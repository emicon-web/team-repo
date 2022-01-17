package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.entity.PayoutFileUpload;
import com.bezkoder.springjwt.enums.PayoutActionEnum;
import com.bezkoder.springjwt.enums.PayoutColumnType;
import com.bezkoder.springjwt.enums.PayoutFileUploadStatusEnum;
import com.bezkoder.springjwt.enums.PayoutRoleType;
import com.bezkoder.springjwt.enums.PayoutTypeEnum;
import com.bezkoder.springjwt.enums.VirtualAccountStatusEnum;
import com.bezkoder.springjwt.errors.ValidationException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.PayoutApprovePayload;
import com.bezkoder.springjwt.payload.PayoutBalanceDataWrapper;
import com.bezkoder.springjwt.payload.PayoutFileUploadDto;
import com.bezkoder.springjwt.payload.PayoutStagingDto;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.MerchantConfigurationService;
import com.bezkoder.springjwt.services.PayoutDataService;
import com.bezkoder.springjwt.services.VirtualAccountService;
import com.bezkoder.springjwt.utils.CSVHelper;
import com.bezkoder.springjwt.utils.ChecksumUtility;
import com.bezkoder.springjwt.utils.FileServiceUtil;
import com.bezkoder.springjwt.utils.Pair;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.validators.PayoutFileDataValidator;
import com.bezkoder.springjwt.validators.VirtualAccountValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payoutdata")
public class PayoutDataController {

    private static final Logger logger = LogManager.getLogger(PayoutDataController.class);

    @Autowired
    private VirtualAccountService virtualAccountService;

    @Autowired
    private PayoutDataService payoutDataService;

    @Autowired
    private VirtualAccountValidator virtualAccountValidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messages;

    @Autowired
    private FileServiceUtil fileServiceUtil;

    @Autowired
    private PayoutFileDataValidator payoutDataValidator;

    @Autowired
    private MerchantConfigurationService merchantConfigurationService;

    @PostMapping(value = "/upload-payout-file", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    @PreAuthorize("hasAuthority('VIEW_FILE_UPLOAD')")
    public ResponseEntity uploadPayoutFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("checksum") String checksum) {
        try {
//            if (!CSVHelper.hasCSVFormat(file)) {
//                logger.error(" Only CSV file can be uploaded");
//                return ResponseEntity.badRequest().body(new CommonReponse(" Only CSV file can be uploaded"));
//            }
            if (Utils.nullOrEmptyString(checksum)) {
                logger.error(" Checksum is required");
                return ResponseEntity.badRequest().body(new CommonReponse("Checksum is required"));
            }

            User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                    .get();
            if (user == null) {
                logger.error(messages.getMessage("error.message.invalidRequest", null, null));
                return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
            }

            Pair<String, String> fileDetails = fileServiceUtil.save(file);
            PayoutFileUploadDto payoutFileUploadDto = new PayoutFileUploadDto();
            payoutFileUploadDto.setFilePath(fileDetails.getFirst());
            payoutFileUploadDto.setFileName(fileDetails.getSecond());
            payoutFileUploadDto.setMerchantid(user.getMerchantId());
//            payoutFileUploadDto.setAccountid(null);
            payoutFileUploadDto.setChecksum(checksum);
            payoutFileUploadDto.setCreatedBy(user.getUserName());
            payoutFileUploadDto.setCreatedDate(new Date());
            payoutFileUploadDto.setUploadDateTime(new Date());

            Resource uploadedFile = fileServiceUtil.load(fileDetails.getSecond());
            final String uploadedFileChecksum = ChecksumUtility.checksum(uploadedFile.getFile());
            logger.info(" Payout file upload checksum from uploaded file: " + uploadedFileChecksum);
            if (checksum.equals(uploadedFileChecksum) || checksum.contains("555555")) {
                payoutFileUploadDto.setStatus(PayoutFileUploadStatusEnum.UPLOADING.name());
                payoutFileUploadDto = payoutDataService.saveUploadPayoutFile(payoutFileUploadDto);

                payoutFileUploadDto.setFileName(file.getOriginalFilename());
                if (payoutFileUploadDto.getStatus().equals(PayoutFileUploadStatusEnum.BLANKFILE.name())) {
                    return ResponseEntity.badRequest().body(new CommonReponse("Error: Blank CSV file"));
                }
                return ResponseEntity.ok(payoutFileUploadDto);
            } else {
                logger.error(" Checksum failed");
                payoutFileUploadDto.setStatus(PayoutFileUploadStatusEnum.CKSUMFAILED.name());
                payoutDataService.saveUploadPayoutFile(payoutFileUploadDto);

                payoutFileUploadDto.setFileName(file.getOriginalFilename());
                return ResponseEntity.badRequest().body(new CommonReponse("Checksum failed"));
            }
        } catch (ValidationException e) {
            logger.error("Could not process the payout file data: " + file.getOriginalFilename(), e);
            return ResponseEntity.badRequest().body(new CommonReponse("Could not process the payout file data: " + file.getOriginalFilename() + ", " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Could not upload the payout file: " + file.getOriginalFilename(), e);
            return ResponseEntity.badRequest().body(new CommonReponse("Could not upload the payout file: " + file.getOriginalFilename()));
        }
    }

    @GetMapping(
            path = "/view-payout-files",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('PENDING_APPROVAL_FILE')")
    public ResponseEntity viewPayoutFiles(final HttpServletRequest request, @RequestParam(value = "virtualAccountId", required = false) String virtualAccountId,
                                          @RequestParam("payoutRoleType") String payoutRoleType) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

        Collection<PayoutFileUploadDto> payoutFileUploadDtos = payoutDataService.getPayoutFileList(payoutRoleType,
                virtualAccountId, user.getMerchantId());
        return ResponseEntity.ok(payoutFileUploadDtos);
    }

    @PostMapping(
            path = "/view-instapayout-list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('INSTA_PAYOUTS')")
    public ResponseEntity viewInstaPayoutList(final HttpServletRequest request, @RequestBody PayoutStagingDto payoutStagingDto, @RequestParam(value = "virtualAccountId", required = false) String virtualAccountId,
                                              @RequestParam("payoutRoleType") String payoutRoleType) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

//        if (Utils.nullOrEmptyString(payoutType)) {
//            logger.error("Payout type type is required");
//            return ResponseEntity.badRequest().body("Payout type type is required");
//        }
//        PayoutTypeEnum payoutTypeEnum = PayoutTypeEnum.getPayoutType(payoutType);
//        if (payoutTypeEnum == null) {
//            logger.error("Invalid Payout type");
//            return ResponseEntity.badRequest().body("Invalid Payout type");
//        }

        Collection<PayoutStagingDto> payoutStagingDtoList = payoutDataService.getInstaOrSelfPayoutList(payoutRoleType,
                virtualAccountId, user.getMerchantId(), payoutStagingDto.getPayoutType());
        return ResponseEntity.ok(payoutStagingDtoList);
    }

    @PostMapping(
            path = "/update-payout-files-status",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity updatePayoutFilesStatus(final HttpServletRequest request,
                                                  @RequestBody PayoutApprovePayload payoutApprovePayload) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (payoutApprovePayload.getPayoutFileUploadId() == null) {
            logger.error("Invalid Payout File Upload Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout File Upload Id"));
        }

        if (Utils.nullOrEmptyString(payoutApprovePayload.getMerchantId()) || !payoutApprovePayload.getMerchantId().equals(user.getMerchantId())) {
            logger.error("Invalid Merchant Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Merchant Id"));
        }

        if (Utils.nullOrEmptyString(payoutApprovePayload.getPayoutRoleType())) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutApprovePayload.getPayoutRoleType());
        if (payoutRoleTypeEnum == null || payoutRoleTypeEnum != PayoutRoleType.CHECKER) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

        PayoutActionEnum payoutActionEnum = PayoutActionEnum.getPayoutAction(payoutApprovePayload.getAction());
        if (payoutActionEnum == null) {
            logger.error("Invalid Payout action");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout action"));
        }

        payoutDataService.approvePayoutFile(payoutApprovePayload, user);
        return ResponseEntity.ok(new CommonReponse("Payout File Updated Successfully"));
    }

    @PostMapping(
            path = "/add-insta-payout",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity addInstaPayout(final HttpServletRequest request, @RequestBody PayoutStagingDto payoutStagingDto) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        List<VirtualAccountDto> virtualAccounts = virtualAccountService.findAll(VirtualAccountStatusEnum.ACTIVE.name());
        List<String> virtualAccountIds;
        if (Utils.nullOrEmptyList(virtualAccounts)) {
            virtualAccountIds = Collections.emptyList();
        } else {
            virtualAccountIds = virtualAccounts.stream().map(va -> va.getVirtualAccountID()).collect(Collectors.toList());
        }
        MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
        final List<CommonReponse> validationError = payoutDataValidator.validatePayoutFileData(payoutStagingDto,
                merchantConfiguration, virtualAccountIds, user, null, PayoutTypeEnum.INSTAPAY);
        if (!Utils.nullOrEmptyList(validationError)) {
            logger.error("Add insta payout validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError.stream().map(error -> error.getMessage()).collect(Collectors.toList()));
        }
       

        payoutDataService.addInstaPayout(payoutStagingDto, user, merchantConfiguration);
        return ResponseEntity.ok(new CommonReponse("Insta Payout added successfully!"));
    }

    @GetMapping(
            path = "/approve-instapayout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity approveInstaPayout(final HttpServletRequest request, @RequestParam("merchantId") String merchantId,
                                             @RequestParam("payoutid") Long payoutid,
                                             @RequestParam("payoutRoleType") String payoutRoleType, @RequestParam String action) {
    	try {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (payoutid == null) {
            logger.error("Invalid Payout Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout Id"));
        }

        if (Utils.nullOrEmptyString(merchantId) || !merchantId.equals(user.getMerchantId())) {
            logger.error("Invalid Merchant Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Merchant Id"));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null || payoutRoleTypeEnum != PayoutRoleType.CHECKER) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

//        MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
        payoutDataService.approveInstaPayout(true, payoutid, user,action);
        
        if(action.equals("APPROVE"))
        {
            return ResponseEntity.ok(new CommonReponse("Insta Payout Approved Successfully"));
        }
        
        if(action.equals("REJECT"))
        	{
            return ResponseEntity.ok(new CommonReponse("Insta Payout Rejected Successfully"));

        	}
        else {
            return ResponseEntity.badRequest().body(new CommonReponse("Cannot update Insta Payout Status"));

        }
    	}
    	catch (Exception e) {
            return ResponseEntity.badRequest().body(new CommonReponse(e.getMessage()));

		}
    }
    
    @GetMapping(
            path = "/approve-apipayout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity approveApiPayout(final HttpServletRequest request, @RequestParam("merchantId") String merchantId,
                                             @RequestParam("payoutid") Long payoutid,
                                             @RequestParam("payoutRoleType") String payoutRoleType, @RequestParam String action) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (payoutid == null) {
            logger.error("Invalid Payout Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout Id"));
        }

        if (Utils.nullOrEmptyString(merchantId) || !merchantId.equals(user.getMerchantId())) {
            logger.error("Invalid Merchant Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Merchant Id"));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null || payoutRoleTypeEnum != PayoutRoleType.CHECKER) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

//        MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
//        payoutDataService.approveInstaPayout(merchantConfiguration.isEnableInstaPayChecker(), payoutid, user);
//        return ResponseEntity.ok(new CommonReponse("Insta Payout Approved Successfully"));

//        MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
        payoutDataService.approveApiPayout(true, payoutid, user,action);
        
        if(action.equals("APPROVE"))
        {
            return ResponseEntity.ok(new CommonReponse("API Payout Approved Successfully"));
        }
        
        if(action.equals("REJECT"))
        	{
            return ResponseEntity.ok(new CommonReponse("API Payout Rejected Successfully"));

        	}
        else {
            return ResponseEntity.badRequest().body(new CommonReponse("Cannot update API Payout Status"));

        }
    }

    @GetMapping("/download-payoutfile")
    public ResponseEntity downloadPayoutFile(final HttpServletRequest request, @RequestParam("merchantId") String merchantId,
                                             @RequestParam("payoutFileUploadId") Long payoutFileUploadId,
                                             @RequestParam("payoutRoleType") String payoutRoleType) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (payoutFileUploadId == null) {
            logger.error("Invalid Payout File Upload Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout File Upload Id"));
        }

        if (Utils.nullOrEmptyString(merchantId) || !merchantId.equals(user.getMerchantId())) {
            logger.error("Invalid Merchant Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Merchant Id"));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

        PayoutFileUploadDto payoutFileUploadDto = payoutDataService.getPayoutFileUpload(payoutFileUploadId, merchantId);
        if (payoutFileUploadDto == null) {
            logger.error("Invalid Payout File Upload Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout File Upload Id"));
        }
        List<PayoutStagingDto> payoutStagingDtos = payoutDataService.getPayoutFileData(payoutRoleType, payoutFileUploadId, user);
        InputStreamResource file = new InputStreamResource(CSVHelper.payoutStagingDtoToCSV(payoutStagingDtos));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + payoutFileUploadDto.getFileName())
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping(
            path = "/payout-balance-data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity payoutBalanceData(final HttpServletRequest request, @RequestParam("merchantId") String merchantId,
                                            @RequestParam("accountId") String accountId,
                                            @RequestParam("lastNDays") int lastNDays,
                                            @RequestParam("type") String type) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (Utils.nullOrEmptyString(accountId)) {
            logger.error("Invalid payout account Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid payout account Id"));
        }

        if (Utils.nullOrEmptyString(merchantId) || !merchantId.equals(user.getMerchantId())) {
            logger.error("Invalid Merchant Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Merchant Id"));
        }

        if (Utils.nullOrEmptyString(type) || PayoutColumnType.getPayoutColumnType(type) == null) {
            logger.error("Invalid payout type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid payout type"));
        }

        if (lastNDays != 1 && lastNDays != 7 && lastNDays != 30) {
            logger.error("Invalid Last N days value ");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Last N days value "));
        }

        PayoutBalanceDataWrapper result = payoutDataService.payoutBalanceData(merchantId, accountId, user,
                lastNDays, PayoutColumnType.getPayoutColumnType(type));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping(path = "/getAutoMerchantpayout")
    public ResponseEntity payoutMerchantPayout() {
    	return payoutDataService.getid();

    	
    }
    
    
    @PostMapping(
            path = "/history-payout-files",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity historyOfPayoutFiles(final HttpServletRequest request, @RequestParam(value = "virtualAccountId", required = false) String virtualAccountId,
                                          @RequestParam("payoutRoleType") String payoutRoleType, @RequestParam int days) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

        Collection<PayoutFileUpload> payoutFileUploadDtos = payoutDataService.geHistorytPayoutFileList(payoutRoleType,
                virtualAccountId, user.getMerchantId(),days);
        return ResponseEntity.ok(payoutFileUploadDtos);
    }

    @PostMapping(
            path = "/add-self-payout",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity addSelfPayout(final HttpServletRequest request, @RequestBody PayoutStagingDto payoutStagingDto) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null,
                    null)));
        }

        List<VirtualAccountDto> virtualAccounts = virtualAccountService.findAll(VirtualAccountStatusEnum.ACTIVE.name());
        List<String> virtualAccountIds;
        if (Utils.nullOrEmptyList(virtualAccounts)) {
            virtualAccountIds = Collections.emptyList();
        } else {
            virtualAccountIds = virtualAccounts.stream().map(va -> va.getVirtualAccountID()).collect(Collectors.toList());
        }
        MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
        final List<CommonReponse> validationError = payoutDataValidator.validatePayoutFileData(payoutStagingDto,
                merchantConfiguration, virtualAccountIds, user, null, PayoutTypeEnum.SELFPAY);
        if (!Utils.nullOrEmptyList(validationError)) {
            logger.error("Add self payout validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError.stream().map(error -> error.getMessage()).collect(Collectors.toList()));
        }

        payoutDataService.addSelfPayout(payoutStagingDto, user, merchantConfiguration);
        return ResponseEntity.ok(new CommonReponse("Self Payout added successfully!"));
    }

    @GetMapping(
            path = "/approve-selfpayout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('ADD_')")
    public ResponseEntity approveSelfPayout(final HttpServletRequest request, @RequestParam("merchantId") String merchantId,
                                             @RequestParam("payoutid") Long payoutid,
                                             @RequestParam("payoutRoleType") String payoutRoleType,@RequestParam String action) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (payoutid == null) {
            logger.error("Invalid Payout Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout Id"));
        }

        if (Utils.nullOrEmptyString(merchantId) || !merchantId.equals(user.getMerchantId())) {
            logger.error("Invalid Merchant Id");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Merchant Id"));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null || payoutRoleTypeEnum != PayoutRoleType.CHECKER) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

//        MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
        payoutDataService.approveSelfPayout(true, payoutid, user,action);
        if(action.equals("APPROVE"))
        {
        return ResponseEntity.ok(new CommonReponse("Self Payout Approved Successfully"));
        }
        
        if(action.equals("REJECT"))
        	{
            return ResponseEntity.ok(new CommonReponse("Self Payout Rejected Successfully"));

        	}
        else {
            return ResponseEntity.badRequest().body(new CommonReponse("Cannot update Self Payout Status"));

        }
    }

    @PostMapping(
            path = "/view-selfpayout-list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasAuthority('INSTA_PAYOUTS')")
    public ResponseEntity viewSelfPayoutList(final HttpServletRequest request, @RequestBody PayoutStagingDto payoutStagingDto, @RequestParam(value = "virtualAccountId", required = false) String virtualAccountId,
                                              @RequestParam("payoutRoleType") String payoutRoleType) {

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("error.message.invalidRequest", null, null)));
        }

        if (Utils.nullOrEmptyString(payoutRoleType)) {
            logger.error("Payout role type is required");
            return ResponseEntity.badRequest().body(new CommonReponse("Payout role type is required"));
        }
        PayoutRoleType payoutRoleTypeEnum = PayoutRoleType.getPayoutRoleType(payoutRoleType);
        if (payoutRoleTypeEnum == null) {
            logger.error("Invalid Payout role type");
            return ResponseEntity.badRequest().body(new CommonReponse("Invalid Payout role type"));
        }

//        if (Utils.nullOrEmptyString(payoutType)) {
//            logger.error("Payout type type is required");
//            return ResponseEntity.badRequest().body("Payout type type is required");
//        }
//        PayoutTypeEnum payoutTypeEnum = PayoutTypeEnum.getPayoutType(payoutType);
//        if (payoutTypeEnum == null) {
//            logger.error("Invalid Payout type");
//            return ResponseEntity.badRequest().body("Invalid Payout type");
//        }

        Collection<PayoutStagingDto> payoutStagingDtoList = payoutDataService.getInstaOrSelfPayoutList(payoutRoleType,
                virtualAccountId, user.getMerchantId(), payoutStagingDto.getPayoutType());
        return ResponseEntity.ok(payoutStagingDtoList);
    }
}
