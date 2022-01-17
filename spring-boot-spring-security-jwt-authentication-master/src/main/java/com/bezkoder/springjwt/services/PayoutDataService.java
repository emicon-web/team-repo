package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.entity.PayoutFileUpload;
import com.bezkoder.springjwt.enums.PayoutActionEnum;
import com.bezkoder.springjwt.enums.PayoutColumnType;
import com.bezkoder.springjwt.enums.PayoutFileUploadStatusEnum;
import com.bezkoder.springjwt.enums.PayoutStatusEnum;
import com.bezkoder.springjwt.enums.PayoutTypeEnum;
import com.bezkoder.springjwt.enums.UserType;
import com.bezkoder.springjwt.enums.VirtualAccountStatusEnum;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.errors.ValidationException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.PayoutApprovePayload;
import com.bezkoder.springjwt.payload.PayoutBalanceData;
import com.bezkoder.springjwt.payload.PayoutBalanceDataWrapper;
import com.bezkoder.springjwt.payload.PayoutFileUploadDto;
import com.bezkoder.springjwt.payload.PayoutStagingDto;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.MerchantConfigurationRepository;
import com.bezkoder.springjwt.repository.PayoutDataRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.repository.VirtualAccountRepository;
import com.bezkoder.springjwt.utils.CSVHelper;
import com.bezkoder.springjwt.utils.FileServiceUtil;
import com.bezkoder.springjwt.utils.RestServiceUtil;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.validators.PayoutFileDataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PayoutDataService {
    private static final Logger logger = LogManager.getLogger(PayoutDataService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VirtualAccountRepository virtualAccountRepository;

    @Autowired
    private PayoutDataRepository payoutDataRepository;

    @Autowired
    private MerchantConfigurationRepository merchantConfigurationRepository;

    @Autowired
    private FileServiceUtil fileServiceUtil;

    @Autowired
    private PayoutFileDataValidator payoutFileDataValidator;

    @Autowired
    private RestServiceUtil restServiceUtil;

    public PayoutFileUploadDto saveUploadPayoutFile(final PayoutFileUploadDto payoutFileUploadDto) throws IOException {
        if (payoutFileUploadDto.getStatus().equals(PayoutFileUploadStatusEnum.UPLOADING.name())) {
            List<PayoutStagingDto> payoutFileData = parsePayoutFileData(payoutFileUploadDto);
            if (!Utils.nullOrEmptyList(payoutFileData)) {
                long savedPayoutFileuploadId = savePayoutFileUploadData(payoutFileUploadDto);
                ListIterator<PayoutStagingDto> payoutStagingDtoListIterator = payoutFileData.listIterator();

                double successTotalAmount = 0;
                int successRecordCount = 0;
                double failureTotalAmount = 0;
                int failureRecordCount = 0;
                double duplicateTotalAmount = 0;
                int duplicateRecordCount = 0;
                List<VirtualAccountDto> virtualAccounts = virtualAccountRepository.findAll(payoutFileUploadDto.getMerchantid(),
                        VirtualAccountStatusEnum.ACTIVE.name());
                List<String> virtualAccountIds;
                if (Utils.nullOrEmptyList(virtualAccounts)) {
                    virtualAccountIds = Collections.emptyList();
                } else {
                    virtualAccountIds = virtualAccounts.stream().map(va -> va.getVirtualAccountID()).collect(Collectors.toList());
                }
                MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationRepository.findMerchantId(payoutFileUploadDto.getMerchantid());

                List<PayoutStagingDto> duplicateStagingList = new ArrayList<>();
                List<String> duplicateMerchantPayoutIdList = new ArrayList<>();
                Set<String> allMerchantPayoutIdList = payoutFileData.stream().map(p -> "'" + p.getMerchantPayoutId() + "'").collect(Collectors.toSet());
                if (payoutFileData.size() != allMerchantPayoutIdList.size()) {
                    logger.error(" Payout file contains duplicate merchant payout Ids");
                    throw new ValidationException(" Payout file contains duplicate merchant payout Ids", null, new Date());
                }

                if (!Utils.nullOrEmptyList(allMerchantPayoutIdList)) {
                    duplicateMerchantPayoutIdList = payoutDataRepository.getDuplicateMerchantPayoutIdList(allMerchantPayoutIdList);
                }
                while (payoutStagingDtoListIterator.hasNext()) {
                    PayoutStagingDto payoutStagingDto = payoutStagingDtoListIterator.next();
                    payoutStagingDto.setPayoutType(PayoutTypeEnum.FILEUPLOAD.name());
                    payoutStagingDto.setPayoutFileUploadId(String.valueOf(savedPayoutFileuploadId));
                    payoutStagingDto.setCreatedBy(payoutFileUploadDto.getCreatedBy());
                    payoutStagingDto.setCreatedDate(new Date());

                    List<CommonReponse> validationResult = payoutFileDataValidator.validatePayoutFileData(payoutStagingDto,
                            merchantConfiguration, virtualAccountIds, null, null, PayoutTypeEnum.FILEUPLOAD);
                    if (duplicateMerchantPayoutIdList != null && duplicateMerchantPayoutIdList.contains(payoutStagingDto.getMerchantPayoutId())) {
                        duplicateRecordCount++;
                        duplicateTotalAmount += payoutStagingDto.getAmount();
                        duplicateStagingList.add(payoutStagingDto);
                        payoutStagingDtoListIterator.remove();
                    } else {
                        if (!Utils.nullOrEmptyList(validationResult)) {
                            String validationErrorResultSingleStr = validationResult.stream()
                                    .map(error -> error.getMessage())
                                    .collect(Collectors.joining("|"));
                            logger.error("Validation error in payout file data - " + validationErrorResultSingleStr);
                            payoutStagingDto.setPayoutStatusFailureReason(validationErrorResultSingleStr);
                            payoutStagingDto.setPayoutStatus(PayoutStatusEnum.FAILURE.name());
                            failureRecordCount++;
                            failureTotalAmount += payoutStagingDto.getAmount();
                        } else {
                            payoutStagingDto.setPayoutStatus(PayoutStatusEnum.SUCCESS.name());
                            successRecordCount++;
                            successTotalAmount += payoutStagingDto.getAmount();
                        }
                    }
                }

                payoutDataRepository.savePayoutFileDataToStaging(payoutFileData);
                logger.info("Successfully saved payout file data to staging");

                if (!duplicateStagingList.isEmpty()) {
                    payoutDataRepository.saveDuplicateStagingDataToPayoutDuplicate(duplicateStagingList);
                    logger.info("Successfully saved payout file duplicate data to payout duplicate");
                }

                payoutFileUploadDto.setSuccessRecordCount(successRecordCount);
                payoutFileUploadDto.setSuccessTotalAmount(new BigDecimal(successTotalAmount).setScale(2, RoundingMode.HALF_UP));
                payoutFileUploadDto.setFailureRecordCount(failureRecordCount);
                payoutFileUploadDto.setFailureTotalAmount(new BigDecimal(failureTotalAmount).setScale(2, RoundingMode.HALF_UP));
                payoutFileUploadDto.setDuplicateRecordCount(duplicateRecordCount);
                payoutFileUploadDto.setDuplicateTotalAmount(new BigDecimal(duplicateTotalAmount).setScale(2, RoundingMode.HALF_UP));
                payoutFileUploadDto.setStatus(PayoutFileUploadStatusEnum.PENDING.name());
                payoutDataRepository.updatePayoutFileUploadData(payoutFileUploadDto);
                logger.info("Payout file upload status and count data updated successfully");
            } else {
                payoutFileUploadDto.setStatus(PayoutFileUploadStatusEnum.BLANKFILE.name());
            }
        } else if (payoutFileUploadDto.getStatus().equals(PayoutFileUploadStatusEnum.CKSUMFAILED.name())) {
            savePayoutFileUploadData(payoutFileUploadDto);
        }

        return payoutFileUploadDto;
    }

    private long savePayoutFileUploadData(PayoutFileUploadDto payoutFileUploadDto) {
        long savedPayoutFileuploadId = payoutDataRepository.saveUploadPayoutFile(payoutFileUploadDto);
        payoutFileUploadDto.setPayoutFileUploadId(savedPayoutFileuploadId);
        return savedPayoutFileuploadId;
    }

    private List<PayoutStagingDto> parsePayoutFileData(PayoutFileUploadDto payoutFileUploadDto) throws IOException {
        Resource resource = fileServiceUtil.load(payoutFileUploadDto.getFileName());
        if (resource == null) {
            throw new GenericCustomException("Error while parsing the uploaded payout CSV file");
        }
        return CSVHelper.csvToPayoutStagingDto(resource.getInputStream());
    }

    public Collection<PayoutFileUploadDto> getPayoutFileList(String payoutRoleType, String virtualAccountId,
                                                             String merchantId) {
        return payoutDataRepository.getPayoutFileList(payoutRoleType, virtualAccountId, merchantId);
    }

    public int approvePayoutFile(PayoutApprovePayload payoutApprovePayload, User user) {
        payoutApprovePayload.setPayoutFileUploadStatusEnum(
                payoutApprovePayload.getAction().equalsIgnoreCase(PayoutActionEnum.APPROVE.name())
                        ? PayoutFileUploadStatusEnum.APPROVED
                        : PayoutFileUploadStatusEnum.REJECTED
        );
        return payoutDataRepository.approvePayoutFile(payoutApprovePayload, user);
    }

    @Transactional
    public void addInstaPayout(PayoutStagingDto payoutStagingDto, User user, MerchantConfiguration merchantConfiguration) {
        payoutStagingDto.setPayoutStatus(PayoutStatusEnum.SUCCESS.name());
        payoutStagingDto.setPayoutType(PayoutTypeEnum.INSTAPAY.name());
        payoutStagingDto.setCreatedBy(user.getUserName());
        payoutStagingDto.setCreatedDate(new Date());

        boolean enableInstaPayChecker = merchantConfiguration.isEnableInstaPayChecker();
        if (enableInstaPayChecker) {
            payoutDataRepository.savePayoutFileDataToStaging(Arrays.asList(payoutStagingDto));
            logger.info("Successfully saved payout file data to staging for insta pay");
        } else {
            long payoutid_staging = payoutDataRepository.savePayoutFileDataToStagingInstaPayout(payoutStagingDto);
            String action="APPROVE";
            approveInstaPayout(enableInstaPayChecker, payoutid_staging, user,action);
        }

    }

    public Collection<PayoutStagingDto> getInstaOrSelfPayoutList(String payoutRoleType, String virtualAccountId, String merchantId, String payoutType) {
        return payoutDataRepository.getInstaOrSelfPayoutList(payoutRoleType, virtualAccountId, merchantId, payoutType);
    }

    public int approveInstaPayout(boolean enableInstaPayChecker, Long payoutid, User user, String action) {
        return payoutDataRepository.approveInstaPayout(enableInstaPayChecker, payoutid, user,action);
    }
    
    public int approveApiPayout(boolean enableInstaPayChecker, Long payoutid, User user, String action) {
        return payoutDataRepository.approveApiPayout(enableInstaPayChecker, payoutid, user,action);
 }

    public int approveSelfPayout(boolean enableSelfPayChecker, Long payoutid, User user, String action) {
        return payoutDataRepository.approveSelfPayout(enableSelfPayChecker, payoutid, user,action);
    }

//    private PayoutExternalDto getInstaPayoutExternalDto(PayoutStagingDto instaPayout) {
//        PayoutExternalDto instaPayoutExternal = new PayoutExternalDto();
//        instaPayoutExternal.setPayoutId(instaPayout.getPayoutid());
//        instaPayoutExternal.setVirtualAccNumber(instaPayout.getAccountId());
//        instaPayoutExternal.setMerchantId(instaPayout.getMerchantid());
//        instaPayoutExternal.setAmount(String.valueOf(instaPayout.getAmount()));
//        instaPayoutExternal.setApprovedBy(instaPayout.getApprovedBy());
//        instaPayoutExternal.setApprovedDate(instaPayout.getApprovedDate());
//        instaPayoutExternal.setPayoutPurpose(instaPayout.getPayoutPurpose());
//        instaPayoutExternal.setPayoutPaymentInstrument(instaPayout.getPayoutPaymentInstrument());
//        instaPayoutExternal.setPayoutPaymentMode(instaPayout.getPayoutPaymentMode());
//        instaPayoutExternal.setMerchantPayoutId(instaPayout.getMerchantPayoutId());
//        instaPayoutExternal.setPayoutDateTime(instaPayout.getPayoutDateTime());
//        instaPayoutExternal.setPayoutType(instaPayout.getPayoutType());
//        instaPayoutExternal.setBankAccountName(instaPayout.getBankAccountName());
//        instaPayoutExternal.setPayoutStatus(instaPayout.getPayoutStatus());
//        instaPayoutExternal.setBankIFSCCode(instaPayout.getBankIFSCCode());
//        instaPayoutExternal.setCancelDate(instaPayout.getCancelDate());
//        instaPayoutExternal.setCanceledBy(instaPayout.getCanceledBy());
//        instaPayoutExternal.setCreatedBy(instaPayout.getCreatedBy());
//        instaPayoutExternal.setCreatedDate(instaPayout.getCreatedDate());
//        instaPayoutExternal.setBankAccountNumber(instaPayout.getBankAccountNumber());
//        instaPayoutExternal.setBeneficiaryCardNumber(instaPayout.getBeneficiaryCardNumber());
//        instaPayoutExternal.setBeneficiaryMobileNumber(instaPayout.getBeneficiaryMobileNumber());
//        instaPayoutExternal.setBeneficiaryEmailId(instaPayout.getBeneficiaryEmailId());
//        instaPayoutExternal.setBeneficiaryVPA(instaPayout.getBeneficiaryVPA());
//        instaPayoutExternal.setBeneficiaryName(instaPayout.getBeneficiaryName());
//        instaPayoutExternal.setCardHolderBankIFSCCode(instaPayout.getCardHolderBankIFSCCode());
//        instaPayoutExternal.setCardHolderBankName(instaPayout.getCardHolderBankName());
//        instaPayoutExternal.setCardHolderName(instaPayout.getCardHolderName());
//
//        return instaPayoutExternal;
//    }

    public List<PayoutStagingDto> getPayoutFileData(String payoutRoleType, Long payoutFileUploadId, User user) {
        List<PayoutStagingDto> payoutStagingDtoList = payoutDataRepository.getPayoutFileData(payoutRoleType, payoutFileUploadId, user);
        return Utils.nullOrEmptyList(payoutStagingDtoList) ? Collections.emptyList() : payoutStagingDtoList;
    }

    public PayoutFileUploadDto getPayoutFileUpload(Long payoutFileUploadId, String merchantId) {
        return payoutDataRepository.getPayoutFileUpload(payoutFileUploadId, merchantId);
    }

    public PayoutBalanceDataWrapper payoutBalanceData(String merchantId, String accountId, User user, Integer lastNDays, PayoutColumnType type) {
        VirtualAccountDto virtualAccount = virtualAccountRepository.getVirtualAccount(accountId, null);
        if (virtualAccount == null) {
            logger.error("Issue while fetching virtual account details for id " + accountId);
            throw new GenericCustomException("Issue while fetching virtual account details ", new Date());
        } else {
            PayoutBalanceDataWrapper result = new PayoutBalanceDataWrapper();
            result.setType(type);
            result.setAccountBalance(virtualAccount.getBalance() == null ? null : Double.valueOf(virtualAccount.getBalance()));
            result.setAccountStatus(virtualAccount.getStatus());

            MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationRepository.findMerchantId(merchantId);
            if (merchantConfiguration != null) {
                result.setLowBalanceThresholdFlag(virtualAccount.getBalance() < merchantConfiguration.getLowBalanceThreshold());
                result.setLowBalanceThreshold(new BigDecimal(merchantConfiguration.getLowBalanceThreshold()).setScale(2, RoundingMode.HALF_UP));
            }

            List<PayoutBalanceData> payoutBalanceDataList =
                    payoutDataRepository.payoutBalanceData(merchantId, accountId, user, lastNDays, type);
            result.setTotalRecords(payoutBalanceDataList.stream().mapToLong(PayoutBalanceData::getRecordCount).sum());

            payoutBalanceDataList.stream().forEach(payoutBalanceData ->
                    payoutBalanceData.setPercentage(String.format("%.2f%%", payoutBalanceData.getRecordCount() * 100 / (double) result.getTotalRecords()))
            );
            result.setData(payoutBalanceDataList);
            return result;
        }
    }

	public ResponseEntity getid() {
	    final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf1.format(timestamp)); 
		Random rnd = new Random();
	    int number = rnd.nextInt(9999);
	    String merchantPayoutRandom =String.format("%04d", number);
	    String randommerchantPayoutId= sdf1.format(timestamp) +merchantPayoutRandom;
        return ResponseEntity.ok(randommerchantPayoutId);
	}

	public Collection<PayoutFileUpload> geHistorytPayoutFileList(String payoutRoleType, String virtualAccountId,
			String merchantId, int days) {
        return payoutDataRepository.getHistoryOfPayoutFileList(payoutRoleType, virtualAccountId, merchantId, days);

	}

    @Transactional
    public void addSelfPayout(PayoutStagingDto payoutStagingDto, User user, MerchantConfiguration merchantConfiguration) {
        payoutStagingDto.setPayoutStatus(PayoutStatusEnum.SUCCESS.name());
        payoutStagingDto.setPayoutType(PayoutTypeEnum.SELFPAY.name());
        payoutStagingDto.setCreatedBy(UserType.SYSTEM.name());
        payoutStagingDto.setCreatedDate(new Date());

        boolean enableSelfPayChecker = merchantConfiguration.isEnableSelfPayChecker();
        if (enableSelfPayChecker) {
            payoutDataRepository.savePayoutFileDataToStagingSelfPayout(payoutStagingDto);
            logger.info("Successfully saved payout data to staging for self pay");
        } else {
            long payoutid_staging = payoutDataRepository.savePayoutFileDataToStagingSelfPayout(payoutStagingDto);
            String action="APPROVE";
            approveSelfPayout(enableSelfPayChecker, payoutid_staging, user,action);
        }

    }

	
}
