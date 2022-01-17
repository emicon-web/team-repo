package com.bezkoder.springjwt.repositorytemplate.copy;

import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.entity.PayoutFileUpload;
import com.bezkoder.springjwt.enums.PayoutColumnType;
import com.bezkoder.springjwt.enums.PayoutFileUploadStatusEnum;
import com.bezkoder.springjwt.enums.PayoutRoleType;
import com.bezkoder.springjwt.enums.PayoutStatusEnum;
import com.bezkoder.springjwt.enums.PayoutTypeEnum;
import com.bezkoder.springjwt.enums.UserType;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.PayoutApprovePayload;
import com.bezkoder.springjwt.payload.PayoutBalanceData;
import com.bezkoder.springjwt.payload.PayoutFileUploadDto;
import com.bezkoder.springjwt.payload.PayoutStagingDto;
import com.bezkoder.springjwt.repository.PayoutDataRepository;
import com.bezkoder.springjwt.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder.In;

@Repository
public class PayoutDataJdbcRepository implements PayoutDataRepository {

    private static final Logger logger = LogManager.getLogger(PayoutDataJdbcRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public long saveUploadPayoutFile(PayoutFileUploadDto payoutFileUploadDto) {
        String sql = "INSERT INTO payout_file_upload (merchantid, accountid, status, " +
                " fileName, filePath, checksum, createdBy, createdDate, uploadDateTime) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW()) ";

        try {
            logger.info("saveUploadPayoutFile() - query : " + sql);
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, payoutFileUploadDto.getMerchantid());
                    statement.setString(2, payoutFileUploadDto.getAccountid());
                    statement.setString(3, payoutFileUploadDto.getStatus());
                    statement.setString(4, payoutFileUploadDto.getFileName());
                    statement.setString(5, payoutFileUploadDto.getFilePath());
                    statement.setString(6, payoutFileUploadDto.getChecksum());
                    statement.setString(7, payoutFileUploadDto.getCreatedBy());
                    return statement;
                }
            }, holder);
            logger.info(" Payout file upload saved successfully ");
            return holder.getKey().longValue();
        } catch (Exception e) {
            logger.error("Error while saving payout file upload {}", e);
            throw new GenericCustomException("Error while saving payout file upload ", new Date());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePayoutFileDataToStaging(List<PayoutStagingDto> payoutFileData) {
        String sql = "INSERT INTO payout_staging (merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, " +
                " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, " +
                " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutStatus, payoutStatusFailureReason, payoutPurpose, payoutDateTime, amount," +
                " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdBy, createdDate ) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW()) ";

        try {
            logger.info("savePayoutFileDataToStaging() - query : " + sql);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    PayoutStagingDto payoutStagingDto = payoutFileData.get(i);
                    ps.setString(1, payoutStagingDto.getMerchantid());
                    ps.setString(2, payoutStagingDto.getPayoutFileUploadId());
                    ps.setString(3, payoutStagingDto.getMerchantPayoutId());
                    ps.setString(4, payoutStagingDto.getAccountId());
                    ps.setString(5, payoutStagingDto.getBeneficiaryName());
                    ps.setString(6, payoutStagingDto.getPayoutPaymentInstrument());
                    ps.setString(7, payoutStagingDto.getPayoutPaymentMode());
                    ps.setString(8, payoutStagingDto.getBankAccountName());
                    ps.setString(9, payoutStagingDto.getBeneficiaryAccountNumber());
                    ps.setString(10, payoutStagingDto.getBeneficiaryIFSCCode());
                    ps.setString(11, payoutStagingDto.getBeneficiaryCardNumber());
                    ps.setString(12, payoutStagingDto.getCardHolderName());
                    ps.setString(13, payoutStagingDto.getCardHolderBankName());
                    ps.setString(14, payoutStagingDto.getCardHolderBankIFSCCode());
                    ps.setString(15, payoutStagingDto.getBeneficiaryVPA());
                    ps.setString(16, payoutStagingDto.getPayoutStatus());
                    ps.setString(17, payoutStagingDto.getPayoutStatusFailureReason());
                    ps.setString(18, payoutStagingDto.getPayoutPurpose());
                    ps.setTimestamp(19, payoutStagingDto.getPayoutDateTime() == null ? null
                            : new java.sql.Timestamp(payoutStagingDto.getPayoutDateTime().getTime()));
                    ps.setDouble(20, new BigDecimal(payoutStagingDto.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    ps.setString(21, payoutStagingDto.getBeneficiaryMobileNumber());
                    ps.setString(22, payoutStagingDto.getBeneficiaryEmailId());
                    ps.setString(23, payoutStagingDto.getPayoutType());
                    ps.setString(24, payoutStagingDto.getCreatedBy());
                }

                @Override
                public int getBatchSize() {
                    return payoutFileData.size();
                }
            });
            logger.info(" Payout file data inserted successfully ");
        } catch (Exception e) {
            logger.error("Error while saving payout file data to staging {}", e);
            throw new GenericCustomException("Error while saving payout file data to staging ", new Date());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveDuplicateStagingDataToPayoutDuplicate(List<PayoutStagingDto> payoutStagingDtoList) {
        String sql = "INSERT INTO payout_duplicate (merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, " +
                " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, " +
                " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutStatus, payoutStatusFailureReason, payoutPurpose, payoutDateTime, amount," +
                " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdBy, createdDate ) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW()) ";

        try {
            logger.info("saveDuplicateStagingDataToPayoutDuplicate() - query : " + sql);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    PayoutStagingDto payoutStagingDto = payoutStagingDtoList.get(i);
                    ps.setString(1, payoutStagingDto.getMerchantid());
                    ps.setString(2, payoutStagingDto.getPayoutFileUploadId());
                    ps.setString(3, payoutStagingDto.getMerchantPayoutId());
                    ps.setString(4, payoutStagingDto.getAccountId());
                    ps.setString(5, payoutStagingDto.getBeneficiaryName());
                    ps.setString(6, payoutStagingDto.getPayoutPaymentInstrument());
                    ps.setString(7, payoutStagingDto.getPayoutPaymentMode());
                    ps.setString(8, payoutStagingDto.getBankAccountName());
                    ps.setString(9, payoutStagingDto.getBeneficiaryAccountNumber());
                    ps.setString(10, payoutStagingDto.getBeneficiaryIFSCCode());
                    ps.setString(11, payoutStagingDto.getBeneficiaryCardNumber());
                    ps.setString(12, payoutStagingDto.getCardHolderName());
                    ps.setString(13, payoutStagingDto.getCardHolderBankName());
                    ps.setString(14, payoutStagingDto.getCardHolderBankIFSCCode());
                    ps.setString(15, payoutStagingDto.getBeneficiaryVPA());
                    ps.setString(16, payoutStagingDto.getPayoutStatus());
                    ps.setString(17, payoutStagingDto.getPayoutStatusFailureReason());
                    ps.setString(18, payoutStagingDto.getPayoutPurpose());
                    ps.setTimestamp(19, payoutStagingDto.getPayoutDateTime() == null ? null
                            : new java.sql.Timestamp(payoutStagingDto.getPayoutDateTime().getTime()));
                    ps.setDouble(20, new BigDecimal(payoutStagingDto.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    ps.setString(21, payoutStagingDto.getBeneficiaryMobileNumber());
                    ps.setString(22, payoutStagingDto.getBeneficiaryEmailId());
                    ps.setString(23, payoutStagingDto.getPayoutType());
                    ps.setString(24, payoutStagingDto.getCreatedBy());
                }

                @Override
                public int getBatchSize() {
                    return payoutStagingDtoList.size();
                }
            });
            logger.info(" Payout staging duplicate data inserted successfully into payout duplicate table ");
        } catch (Exception e) {
            logger.error("Error while saving payout staging duplicate data to payout duplicate {}", e);
            throw new GenericCustomException("Error while saving payout staging duplicate data to payout duplicate ", new Date());
        }
    }

    @Override
    @Transactional
    public long savePayoutFileDataToStagingInstaPayout(PayoutStagingDto payoutStagingDto) {
        String sql = "INSERT INTO payout_staging (merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, " +
                " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, " +
                " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutStatus, payoutPurpose, payoutDateTime, amount," +
                " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdBy, createdDate ) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW()) ";

        try {
            logger.info("savePayoutFileDataToStaging() - query : " + sql);
            GeneratedKeyHolder holder = new GeneratedKeyHolder();


            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, payoutStagingDto.getMerchantid());
                    ps.setString(2, payoutStagingDto.getPayoutFileUploadId());
                    ps.setString(3, payoutStagingDto.getMerchantPayoutId());
                    ps.setString(4, payoutStagingDto.getAccountId());
                    ps.setString(5, payoutStagingDto.getBeneficiaryName());
                    ps.setString(6, payoutStagingDto.getPayoutPaymentInstrument());
                    ps.setString(7, payoutStagingDto.getPayoutPaymentMode());
                    ps.setString(8, payoutStagingDto.getBankAccountName());
                    ps.setString(9, payoutStagingDto.getBeneficiaryAccountNumber());
                    ps.setString(10, payoutStagingDto.getBeneficiaryIFSCCode());
                    ps.setString(11, payoutStagingDto.getBeneficiaryCardNumber());
                    ps.setString(12, payoutStagingDto.getCardHolderName());
                    ps.setString(13, payoutStagingDto.getCardHolderBankName());
                    ps.setString(14, payoutStagingDto.getCardHolderBankIFSCCode());
                    ps.setString(15, payoutStagingDto.getBeneficiaryVPA());
                    ps.setString(16, payoutStagingDto.getPayoutStatus());
                    ps.setString(17, payoutStagingDto.getPayoutPurpose());
                    ps.setTimestamp(18, payoutStagingDto.getPayoutDateTime() == null ? null
                            : new java.sql.Timestamp(payoutStagingDto.getPayoutDateTime().getTime()));
                    ps.setDouble(19, new BigDecimal(payoutStagingDto.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    ps.setString(20, payoutStagingDto.getBeneficiaryMobileNumber());
                    ps.setString(21, payoutStagingDto.getBeneficiaryEmailId());
                    ps.setString(22, payoutStagingDto.getPayoutType());
                    ps.setString(23, payoutStagingDto.getCreatedBy());
                    return ps;
                }
            }, holder);
            logger.info(" Payout file data inserted successfully ");
            return holder.getKey().longValue();
        } catch (Exception e) {
            logger.error("Error while saving insta payout data to staging {}", e);
            throw new GenericCustomException("Error while saving insta payout data to staging ", new Date());
        }
    }

    @Override
    public int updatePayoutFileUploadData(PayoutFileUploadDto payoutFileUploadDto) {
        String sql = "UPDATE payout_file_upload SET status = ? , successRecordCount = ? , successTotalAmount =?, " +
                " failureRecordCount = ?, failureTotalAmount = ?, duplicateRecordCount = ?, duplicateTotalAmount = ?  WHERE payoutFileUploadId = ? ";

        try {
            logger.info("updatePayoutFileUploadData() - query : " + sql);
            int result = jdbcTemplate.update(sql, payoutFileUploadDto.getStatus(),
                    payoutFileUploadDto.getSuccessRecordCount(),
                    payoutFileUploadDto.getSuccessTotalAmount(),
                    payoutFileUploadDto.getFailureRecordCount(),
                    payoutFileUploadDto.getFailureTotalAmount(),
                    payoutFileUploadDto.getDuplicateRecordCount(),
                    payoutFileUploadDto.getDuplicateTotalAmount(),
                    payoutFileUploadDto.getPayoutFileUploadId());
            logger.info(" Payout file upload data updated successfully ");
            return result;
        } catch (Exception e) {
            logger.error("Error while updating payout file upload data {}", e);
            throw new GenericCustomException("Error while updating payout file upload data ", new Date());
        }
    }

    @Override
    public Collection<PayoutFileUploadDto> getPayoutFileList(String payoutRoleType, String virtualAccountId,
                                                             String merchantId) {
        String sql;
        Object[] data;
        if (payoutRoleType.equals(PayoutRoleType.CHECKER.name())) {
            sql = "SELECT * FROM payout_file_upload  WHERE  successRecordCount!=0 AND merchantid = ? AND status = ? ORDER BY createdDate DESC ";
            data = new Object[]{merchantId, PayoutFileUploadStatusEnum.PENDING.name()};
        } else {
            sql = "SELECT * FROM payout_file_upload WHERE merchantid = ? AND accountid = ? ";
            data = new Object[]{merchantId, virtualAccountId};
        }

        logger.info("getPayoutFileList() - query : " + sql);
        List<PayoutFileUploadDto> payoutFileUploadDtos;
        try {
            payoutFileUploadDtos = jdbcTemplate.query(sql, data, (rs, rowNum) -> mapPayoutFileUploadDtos(rs));
        } catch (Exception e) {
            logger.error("Error while fetching payout file upload list {}", e);
            throw new GenericCustomException("Error while fetching payout file upload list ", new Date());
        }
        return payoutFileUploadDtos;
    }

    @Override
    @Transactional
    public int approvePayoutFile(PayoutApprovePayload payoutApprovePayload, User user) {
        String updateSql = "UPDATE payout_file_upload SET status = ? , approvedBy = ? , approvedDate = NOW() " +
                " WHERE payoutFileUploadId = ? AND merchantid = ? ";
        String sql1="SELECT createdBy FROM payout_file_upload  WHERE  successRecordCount!=0 AND status = 'PENDING' AND payoutFileUploadId= \""+ payoutApprovePayload.getPayoutFileUploadId()+"\"";
    	String name = jdbcTemplate.queryForObject(sql1, String.class);
    	System.out.println(name);
    	if(payoutApprovePayload.getPayoutFileUploadStatusEnum() == PayoutFileUploadStatusEnum.APPROVED && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to approve the record as you are the creator of the same", new Date());

    	}
    	if(payoutApprovePayload.getPayoutFileUploadStatusEnum() == PayoutFileUploadStatusEnum.REJECTED && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to reject the record as you are the creator of the same", new Date());
    	}

        try {
            logger.info("approvePayoutFile() - approve/reject status update query : " + updateSql);
            int result = jdbcTemplate.update(updateSql, payoutApprovePayload.getPayoutFileUploadStatusEnum().name(),
                    user.getUserName(),
                    payoutApprovePayload.getPayoutFileUploadId(),
                    user.getMerchantId());
            logger.info(" Payout file upload data approved and updated successfully ");

            if (payoutApprovePayload.getPayoutFileUploadStatusEnum() == PayoutFileUploadStatusEnum.REJECTED) {
                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutFileUploadId = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approvePayoutFile() - update staging payout file update query : " + updateStagingSql);
                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.REJECTED.name(),
                        user.getUserName(),
                        payoutApprovePayload.getPayoutFileUploadId(),
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Payout file upload staging data updated successfully ");
                return updateStagingResult;

            }

            if (payoutApprovePayload.getPayoutFileUploadStatusEnum() == PayoutFileUploadStatusEnum.APPROVED && result > 0) {
                String sql = "INSERT INTO payout (payoutStatus, payoutid, merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, " +
                        " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, " +
                        " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutPurpose, payoutDateTime, amount," +
                        " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, approvedBy, approvedDate ) "
                        + " SELECT ?, payoutid, merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, "
                        + " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, "
                        + " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutPurpose, payoutDateTime, amount, "
                        + " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, ?, NOW() FROM payout_staging " +
                        " WHERE payoutFileUploadId = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approvePayoutFile() - query for moving data from staging to payout : " + updateSql);

                jdbcTemplate.update(sql, PayoutStatusEnum.READYFORPROCESSING.name(), user.getUserName(), payoutApprovePayload.getPayoutFileUploadId(),
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Payout staging success data saved successfully to payout with ready status ");

                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutFileUploadId = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approvePayoutFile() - update staging payout file update query : " + updateStagingSql);
                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.READY.name(),
                        user.getUserName(),
                        payoutApprovePayload.getPayoutFileUploadId(),
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Payout file upload staging data updated successfully ");
                return updateStagingResult;
            } else {
                if (result <= 0) {
                    logger.error("Error while updating payout file upload status or moving data from payout staging to payout {}");
                    throw new GenericCustomException("Error while updating payout file upload status or moving data from payout staging to payout ", new Date());
                }
            }
        } catch (Exception e) {
            logger.error("Error while updating payout file upload status or moving data from payout staging to payout {}", e);
            throw new GenericCustomException("Error while updating payout file upload status or moving data from payout staging to payout ", new Date());
        }
        return 0;
    }

    @Override
    public Collection<PayoutStagingDto> getInstaOrSelfPayoutList(String payoutRoleType, String virtualAccountId, String merchantId, String payoutType) {
        String sql;
        Object[] data;
        if (payoutRoleType.equals(PayoutRoleType.CHECKER.name())) {
            sql = "SELECT * FROM payout_staging WHERE merchantid = ? AND payoutType = ? AND payoutStatus = ? order by createdDate DESC ";
            data = new Object[]{merchantId, payoutType, PayoutStatusEnum.SUCCESS.name()};
        } else {
            sql = "SELECT * FROM payout_staging WHERE merchantid = ? AND payoutType = ? AND accountid = ? ";
            data = new Object[]{merchantId, payoutType, virtualAccountId};
        }

        logger.info("getInstaOrSelfPayoutList() - query : " + sql);
        List<PayoutStagingDto> payoutStagingDtos;
        try {
            payoutStagingDtos = jdbcTemplate.query(sql, data, (rs, rowNum) -> mapInstaOrSelfPayoutStagingDtos(rs, null));
        } catch (Exception e) {
            logger.error("Error while fetching insta or self payout data list {}", e);
            throw new GenericCustomException("Error while fetching insta or self payout data list ", new Date());
        }
        return payoutStagingDtos;
    }

    @Override
    @Transactional
    public int approveInstaPayout(boolean enableInstaPayChecker, Long payoutid, User user, String action) {
    	String sql1="SELECT createdBy FROM payout_staging where payoutType='INSTAPAY' AND payoutid =?";
    	String name = jdbcTemplate.queryForObject(sql1, new Object[]{payoutid}, String.class);
    	System.out.println(name);
    	if(action.equals("APPROVE") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to approve the record as you are the creator of the same", new Date());

    	}
    	if(action.equals("REJECT") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to reject the record as you are the creator of the same", new Date());
    	}
    	try {
            if (action.equals("APPROVE")) {
                String sql = "INSERT INTO payout (payoutStatus, payoutid, merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, " +
                        " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, " +
                        " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutPurpose, payoutDateTime, amount," +
                        " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, approvedBy, approvedDate ) "
                        + " SELECT ?, payoutid, merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, "
                        + " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, "
                        + " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutPurpose, payoutDateTime, amount, "
                        + " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, ?, NOW() FROM payout_staging " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveInstaPayout() - query for moving data from staging to payout : " + sql);

                final String approvedBy = enableInstaPayChecker ? user.getUserName() : UserType.SYSTEM.name();
                jdbcTemplate.update(sql, PayoutStatusEnum.READYFORPROCESSING.name(), approvedBy, payoutid, user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Insta Payout staging success data saved successfully to payout with ready status ");

                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveInstaPayout() - update staging insta payout file update query : " + updateStagingSql);

                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.READY.name(),
                        approvedBy,
                        payoutid,
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Insta Payout staging data updated successfully ");
                return updateStagingResult;

            }
            if (action.equals("REJECT")) {
                final String approvedBy = enableInstaPayChecker ? user.getUserName() : UserType.SYSTEM.name();
                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveInstaPayout() - update staging insta payout file update query : " + updateStagingSql);

                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.REJECTED.name(),
                        approvedBy,
                        payoutid,
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Insta Payout staging data updated successfully ");
                return updateStagingResult;

            }


        } catch (Exception e) {
            logger.error("Error while moving insta payout staging data from payout staging to payout {}", e);
            throw new GenericCustomException("Error while moving insta payout staging data from payout staging to payout ", new Date());
        }
        return 0;
    }

    @Override
    @Transactional
    public int approveApiPayout(boolean enableInstaPayChecker, Long payoutid, User user, String action) {
    	String sql1="SELECT createdBy FROM payout_staging where payoutType='API' AND payoutid =?";
    	String name = jdbcTemplate.queryForObject(sql1, new Object[]{payoutid}, String.class);
    	System.out.println(name);
    	if(action.equals("APPROVE") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to approve the record as you are the creator of the same", new Date());

    	}
    	if(action.equals("REJECT") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to reject the record as you are the creator of the same", new Date());
    	}
        try {
            if (action.equals("APPROVE")) {
                String sql = "INSERT INTO payout (payoutStatus, payoutid, merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, " +
                        " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, " +
                        " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutPurpose, payoutDateTime, amount," +
                        " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, approvedBy, approvedDate ) "
                        + " SELECT ?, payoutid, merchantid, payoutFileUploadId, merchantPayoutId, accountId, beneficiaryName, "
                        + " payoutPaymentInstrument, payoutPaymentMode, bankAccountName, beneficiaryAccountNumber, beneficiaryIFSCCode, beneficiaryCardNumber, cardHolderName, "
                        + " cardHolderBankName, cardHolderBankIFSCCode, beneficiaryVPA, payoutPurpose, payoutDateTime, amount, "
                        + " beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, ?, NOW() FROM payout_staging " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveInstaPayout() - query for moving data from staging to payout : " + sql);

                final String approvedBy = enableInstaPayChecker ? user.getUserName() : UserType.SYSTEM.name();
                jdbcTemplate.update(sql, PayoutStatusEnum.READYFORPROCESSING.name(), approvedBy, payoutid, user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Insta Payout staging success data saved successfully to payout with ready status ");

                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveInstaPayout() - update staging insta payout file update query : " + updateStagingSql);

                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.READY.name(),
                        approvedBy,
                        payoutid,
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Insta Payout staging data updated successfully ");
                return updateStagingResult;

            }
            if (action.equals("REJECT")) {
                final String approvedBy = enableInstaPayChecker ? user.getUserName() : UserType.SYSTEM.name();
                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveInstaPayout() - update staging insta payout file update query : " + updateStagingSql);

                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.REJECTED.name(),
                        approvedBy,
                        payoutid,
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Insta Payout staging data updated successfully ");
                return updateStagingResult;

            }


        } catch (Exception e) {
            logger.error("Error while moving insta payout staging data from payout staging to payout {}", e);
            throw new GenericCustomException("Error while moving insta payout staging data from payout staging to payout ", new Date());
        }
        return 0;
    }

    @Override
    public List<PayoutStagingDto> getPayoutFileData(String payoutRoleType, Long payoutFileUploadId, User user) {
        String sql = null;
        String sql_duplicate = null;
        Object[] data = new Object[0];
        if (payoutRoleType.equals(PayoutRoleType.CHECKER.name())) {
            sql = "SELECT * FROM payout_staging WHERE payoutFileUploadId = ? AND payoutType = ? AND payoutStatus = ? ";
            data = new Object[]{payoutFileUploadId, PayoutTypeEnum.FILEUPLOAD.name(), PayoutStatusEnum.SUCCESS.name()};
        } else if (payoutRoleType.equals(PayoutRoleType.MAKER.name())) {
            sql = "SELECT * FROM payout_staging WHERE payoutFileUploadId = ? AND payoutType = ? ";
            sql_duplicate = "SELECT * FROM payout_duplicate WHERE payoutFileUploadId = ? AND payoutType = ? ";
            data = new Object[]{payoutFileUploadId, PayoutTypeEnum.FILEUPLOAD.name()};
        }

        logger.info("getPayoutFileData() - query : " + sql);
        List<PayoutStagingDto> payoutStagingDtos;
        List<PayoutStagingDto> duplicatePayoutStagingDtos;
        try {
            payoutStagingDtos = jdbcTemplate.query(sql, data, (rs, rowNum) -> mapInstaOrSelfPayoutStagingDtos(rs, null));
            if (payoutRoleType.equals(PayoutRoleType.MAKER.name())) {
                duplicatePayoutStagingDtos = jdbcTemplate.query(sql_duplicate, data,
                        (rs, rowNum) -> mapInstaOrSelfPayoutStagingDtos(rs, PayoutStatusEnum.DUPLICATE.name()));
                if (Utils.nullOrEmptyList(payoutStagingDtos)) {
                    return duplicatePayoutStagingDtos;
                } else {
                    payoutStagingDtos.addAll(duplicatePayoutStagingDtos);
                    return payoutStagingDtos;
                }
            }
        } catch (Exception e) {
            logger.error("Error while fetching payout file data list {}", e);
            throw new GenericCustomException("Error while fetching payout file data list ", new Date());
        }
        return payoutStagingDtos;
    }

    @Override
    public PayoutFileUploadDto getPayoutFileUpload(Long payoutFileUploadId, String merchantId) {
        try {
            final String query = "SELECT * FROM payout_file_upload WHERE payoutFileUploadId = ? AND merchantid = ? ";
            logger.info("getPayoutFileUpload() - query : " + query);
            return jdbcTemplate.queryForObject(query, new Object[]{payoutFileUploadId, merchantId},
                    (rs, rowNum) -> mapPayoutFileUploadDtos(rs));
        } catch (Exception e) {
            logger.error("Error while fetching payout file upload data {}", e);
            throw new GenericCustomException("Error while fetching payout file upload data ", new Date());
        }
    }

    @Override
    public List<PayoutBalanceData> payoutBalanceData(String merchantId, String accountId, User user, Integer lastNDays, PayoutColumnType type) {
        try {
            final Timestamp fromTimeStamp = new Timestamp(Utils.getLastNDaysDate(lastNDays).getTime());
            final Timestamp toTimeStamp = new Timestamp(new Date().getTime());

            logger.info("payoutBalanceData() - type : " + type);
            String query = null;
            if (type == PayoutColumnType.PAYOUT_STATUS) {
                query = "SELECT payoutStatus, sum(amount), count(payoutStatus) FROM payout WHERE merchantid = ? AND accountId = ? " +
                        " AND createdDate BETWEEN ? AND ? GROUP BY payoutStatus ";
            } else if (type == PayoutColumnType.PAYOUT_PAYMENT_MODE) {
                query = "SELECT payoutPaymentMode, sum(amount), count(payoutPaymentMode) FROM payout WHERE merchantid = ? AND accountId = ? " +
                        " AND createdDate BETWEEN ? AND ? GROUP BY payoutPaymentMode ";
            }
            logger.info("payoutBalanceData() - query : " + query);
            return jdbcTemplate.query(query, new Object[]{merchantId, accountId, fromTimeStamp, toTimeStamp},
                    (rs, rowNum) -> mapPayoutBalanceData(rs, type));
        } catch (Exception e) {
            logger.error("Error while fetching payout balance data {}", e);
            throw new GenericCustomException("Error while fetching payout balance data ", new Date());
        }
    }

    @Override
    public PayoutStagingDto getByPayoutId(Long payoutid, String merchantId) {
        try {
            final String query = "SELECT * FROM payout_staging WHERE payoutid = ? AND merchantid = ? ";
            logger.info("getByPayoutId() - query : " + query);
            return jdbcTemplate.queryForObject(query, new Object[]{payoutid, merchantId},
                    (rs, rowNum) -> mapInstaOrSelfPayoutStagingDtos(rs, null));
        } catch (Exception e) {
            logger.error("Error while fetching payout data {}", e);
//            throw new GenericCustomException("Error while fetching payout data ", new Date());
            return null;
        }
    }

    @Override
    public List<String> getDuplicateMerchantPayoutIdList(Set<String> merchantPayoutIdList) {
        String sql = String.format("SELECT distinct(merchantPayoutId) FROM payout_staging ps JOIN payout_file_upload pf " +
                        " ON ps.payoutFileUploadId = pf.payoutFileUploadId " +
                        " WHERE pf.status != 'REJECTED' and ps.merchantPayoutId IN (%s)",
                merchantPayoutIdList.stream().collect(Collectors.joining(", ")));

        logger.info("getDuplicateMerchantPayoutIdList() - query : " + sql);
        List<String> dupMerchantPayoutIdList;
        try {
            dupMerchantPayoutIdList = jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            logger.error("Error while fetching duplicate merchant payout id list {}", e);
            throw new GenericCustomException("Error while fetching duplicate merchant payout id list ", new Date());
        }
        return dupMerchantPayoutIdList;
    }

    private PayoutBalanceData mapPayoutBalanceData(final ResultSet rs, PayoutColumnType type) throws SQLException {
        PayoutBalanceData payoutBalanceData = new PayoutBalanceData();
        if (type == PayoutColumnType.PAYOUT_STATUS) {
            payoutBalanceData.setPayoutStatus(rs.getString(1));
        } else if (type == PayoutColumnType.PAYOUT_PAYMENT_MODE) {
            payoutBalanceData.setPayoutPaymentMode(rs.getString(1));
        }
        payoutBalanceData.setTotalAmount(new BigDecimal(rs.getDouble(2)).setScale(2, RoundingMode.HALF_UP));
        payoutBalanceData.setRecordCount(rs.getLong(3));

        return payoutBalanceData;
    }

    private PayoutFileUploadDto mapPayoutFileUploadDtos(final ResultSet rs) throws SQLException {
        PayoutFileUploadDto payoutFileUploadDto = new PayoutFileUploadDto();
        payoutFileUploadDto.setPayoutFileUploadId(rs.getLong("payoutFileUploadId"));
        payoutFileUploadDto.setMerchantid(rs.getString("merchantid"));
        payoutFileUploadDto.setAccountid(rs.getString("accountid"));
        payoutFileUploadDto.setUploadDateTime(rs.getTimestamp("uploadDateTime"));
        payoutFileUploadDto.setStatus(rs.getString("status"));
        payoutFileUploadDto.setCreatedBy(rs.getString("createdBy"));
        payoutFileUploadDto.setCreatedDate(rs.getTimestamp("createdDate"));
        payoutFileUploadDto.setApprovedBy(rs.getString("approvedBy"));
        payoutFileUploadDto.setApprovedDate(rs.getTimestamp("approvedDate"));
        payoutFileUploadDto.setSuccessTotalAmount(new BigDecimal(rs.getDouble("successTotalAmount")).setScale(2, RoundingMode.HALF_UP));
        payoutFileUploadDto.setSuccessRecordCount(rs.getInt("successRecordCount"));
        payoutFileUploadDto.setFailureTotalAmount(new BigDecimal(rs.getDouble("failureTotalAmount")).setScale(2, RoundingMode.HALF_UP));
        payoutFileUploadDto.setFailureRecordCount(rs.getInt("failureRecordCount"));
        payoutFileUploadDto.setDuplicateTotalAmount(new BigDecimal(rs.getDouble("duplicateTotalAmount")).setScale(2, RoundingMode.HALF_UP));
        payoutFileUploadDto.setDuplicateRecordCount(rs.getInt("duplicateRecordCount"));
        payoutFileUploadDto.setFileName(rs.getString("fileName").substring(14));
        payoutFileUploadDto.setFilePath(rs.getString("filePath"));
        payoutFileUploadDto.setChecksum(rs.getString("checksum"));
        return payoutFileUploadDto;
    }

    private PayoutStagingDto mapInstaOrSelfPayoutStagingDtos(final ResultSet rs, final String payoutStatus) throws SQLException {
        PayoutStagingDto payoutStagingDto = new PayoutStagingDto();
        payoutStagingDto.setPayoutid(rs.getLong("payoutid"));
        payoutStagingDto.setMerchantid(rs.getString("merchantid"));
        payoutStagingDto.setPayoutFileUploadId(rs.getString("payoutFileUploadId"));
        payoutStagingDto.setAccountId(rs.getString("accountId"));
        payoutStagingDto.setMerchantPayoutId(rs.getString("merchantPayoutId"));
        payoutStagingDto.setPayoutDateTime(rs.getTimestamp("payoutDateTime"));
        payoutStagingDto.setAmount(new BigDecimal(rs.getDouble("amount")).setScale(2, RoundingMode.HALF_UP).doubleValue());
        payoutStagingDto.setPayoutPaymentMode(rs.getString("payoutPaymentMode"));
        payoutStagingDto.setPayoutPurpose(rs.getString("payoutPurpose"));
        payoutStagingDto.setBeneficiaryName(rs.getString("beneficiaryName"));
        payoutStagingDto.setBeneficiaryEmailId(rs.getString("beneficiaryEmailId"));
        payoutStagingDto.setBeneficiaryMobileNumber(rs.getString("beneficiaryMobileNumber"));
        payoutStagingDto.setPayoutPaymentInstrument(rs.getString("payoutPaymentInstrument"));
        payoutStagingDto.setBankAccountName(rs.getString("bankAccountName"));
        payoutStagingDto.setBeneficiaryAccountNumber(rs.getString("beneficiaryAccountNumber"));
        payoutStagingDto.setBeneficiaryIFSCCode(rs.getString("beneficiaryIFSCCode"));
        payoutStagingDto.setBeneficiaryVPA(rs.getString("beneficiaryVPA"));
        payoutStagingDto.setBeneficiaryCardNumber(rs.getString("beneficiaryCardNumber"));
        payoutStagingDto.setCardHolderName(rs.getString("cardHolderName"));
        payoutStagingDto.setCardHolderBankName(rs.getString("cardHolderBankName"));
        payoutStagingDto.setCardHolderBankIFSCCode(rs.getString("cardHolderBankIFSCCode"));
        if (!Utils.nullOrEmptyString(payoutStatus) && payoutStatus.equals(PayoutStatusEnum.DUPLICATE.name())) {
            payoutStagingDto.setPayoutStatus(PayoutStatusEnum.DUPLICATE.name());
        } else {
            payoutStagingDto.setPayoutStatus(rs.getString("payoutStatus"));
        }

        payoutStagingDto.setPayoutStatusFailureReason(rs.getString("payoutStatusFailureReason"));
        payoutStagingDto.setPayoutType(rs.getString("payoutType"));
        payoutStagingDto.setCreatedBy(rs.getString("createdBy"));
        payoutStagingDto.setCreatedDate(rs.getTimestamp("createdDate"));
        payoutStagingDto.setApprovedBy(rs.getString("approvedBy"));
        payoutStagingDto.setApprovedDate(rs.getTimestamp("approvedDate"));
//        payoutStagingDto.setCanceledBy(rs.getString("canceledBy"));
//        payoutStagingDto.setCancelDate(rs.getTimestamp("cancelDate"));
        return payoutStagingDto;
    }

    @Override
    public Collection<PayoutFileUpload> getHistoryOfPayoutFileList(String payoutRoleType, String virtualAccountId,
                                                                      String merchantId, int days) {
        List<PayoutFileUpload> payoutFileUploadDtos = new ArrayList<>();
       
        String sql = "";
        Object[] data = {};
        try {
            if (payoutRoleType.equals(PayoutRoleType.MAKER.name())) {

            	
                if (days == 1) {
                	payoutFileUploadDtos= jdbcTemplate.query(
                                     "SELECT *, successRecordCount+failureRecordCount+duplicateRecordCount as totalCount, successTotalAmount+failureTotalAmount+duplicateTotalAmount as totalAmount FROM payout_file_upload WHERE  createdDate>= DATE_ADD(CURDATE(), INTERVAL 0 DAY) AND merchantid = ? AND status in('APPROVED','REJECTED') ORDER BY createdDate DESC ",
                                     new Object[]{merchantId},
                                     (rs, rowNum) ->
                                             new PayoutFileUpload(rs.getLong("payoutFileUploadId"),
                                                     rs.getString("merchantid"),
                                                     rs.getString("accountid"),
                                                     rs.getDate("uploadDateTime"),
                                                     rs.getString("status"),
                                                     rs.getString("createdBy"),
                                                     rs.getDate("createdDate"),
                                                     rs.getString("approvedBy"),
                                                     rs.getDate("approvedDate"),
                                                     rs.getBigDecimal("successTotalAmount"),
                                                     rs.getInt("successRecordCount"),
                                                     rs.getBigDecimal("failureTotalAmount"),
                                                     rs.getInt("failureRecordCount"),
                                                     rs.getBigDecimal("duplicateTotalAmount"),
                                                     rs.getInt("duplicateRecordCount"),
                                                     rs.getInt("totalCount"),
                                                     rs.getBigDecimal("totalAmount"),
                                                     rs.getString("fileName"),
                                                     rs.getString("filePath"),
                                                     rs.getString("checksum"))
                             );
                    
                   

                } else if (days == 7) {
                	payoutFileUploadDtos= jdbcTemplate.query(
                            "SELECT *, successRecordCount+failureRecordCount+duplicateRecordCount as totalCount, successTotalAmount+failureTotalAmount+duplicateTotalAmount as totalAmount FROM payout_file_upload WHERE  createdDate>= DATE_ADD(CURDATE(), INTERVAL -7 DAY) AND merchantid = ? AND status in('APPROVED','REJECTED') ORDER BY createdDate DESC ",
                            new Object[]{merchantId},
                            (rs, rowNum) ->
                                    new PayoutFileUpload(rs.getLong("payoutFileUploadId"),
                                            rs.getString("merchantid"),
                                            rs.getString("accountid"),
                                            rs.getDate("uploadDateTime"),
                                            rs.getString("status"),
                                            rs.getString("createdBy"),
                                            rs.getDate("createdDate"),
                                            rs.getString("approvedBy"),
                                            rs.getDate("approvedDate"),
                                            rs.getBigDecimal("successTotalAmount"),
                                            rs.getInt("successRecordCount"),
                                            rs.getBigDecimal("failureTotalAmount"),
                                            rs.getInt("failureRecordCount"),
                                            rs.getBigDecimal("duplicateTotalAmount"),
                                            rs.getInt("duplicateRecordCount"),
                                            rs.getInt("totalCount"),
                                            rs.getBigDecimal("totalAmount"),
                                            rs.getString("fileName"),
                                            rs.getString("filePath"),
                                            rs.getString("checksum"))
                    );


                } else if (days == 30) {

                	payoutFileUploadDtos= jdbcTemplate.query(
                            "SELECT *, successRecordCount+failureRecordCount+duplicateRecordCount as totalCount, successTotalAmount+failureTotalAmount+duplicateTotalAmount as totalAmount FROM payout_file_upload WHERE  createdDate>= DATE_ADD(CURDATE(), INTERVAL -30 DAY) AND merchantid = ? AND status in('APPROVED','REJECTED') ORDER BY createdDate DESC ",
                            new Object[]{merchantId},
                            (rs, rowNum) ->
                                    new PayoutFileUpload(rs.getLong("payoutFileUploadId"),
                                            rs.getString("merchantid"),
                                            rs.getString("accountid"),
                                            rs.getDate("uploadDateTime"),
                                            rs.getString("status"),
                                            rs.getString("createdBy"),
                                            rs.getDate("createdDate"),
                                            rs.getString("approvedBy"),
                                            rs.getDate("approvedDate"),
                                            rs.getBigDecimal("successTotalAmount"),
                                            rs.getInt("successRecordCount"),
                                            rs.getBigDecimal("failureTotalAmount"),
                                            rs.getInt("failureRecordCount"),
                                            rs.getBigDecimal("duplicateTotalAmount"),
                                            rs.getInt("duplicateRecordCount"),
                                            rs.getInt("totalCount"),
                                            rs.getBigDecimal("totalAmount"),
                                            rs.getString("fileName"),
                                            rs.getString("filePath"),
                                            rs.getString("checksum"))
                    );
                	

                }


            } else {

            	payoutFileUploadDtos= jdbcTemplate.query(
                        "SELECT * successRecordCount+failureRecordCount+duplicateRecordCount as totalCount, successTotalAmount+failureTotalAmount+duplicateTotalAmount as totalAmount FROM payout_file_upload WHERE merchantid = ? AND accountid = ?",
                        new Object[]{merchantId},
                        (rs, rowNum) ->
                                new PayoutFileUpload(rs.getLong("payoutFileUploadId"),
                                        rs.getString("merchantid"),
                                        rs.getString("accountid"),
                                        rs.getDate("uploadDateTime"),
                                        rs.getString("status"),
                                        rs.getString("createdBy"),
                                        rs.getDate("createdDate"),
                                        rs.getString("approvedBy"),
                                        rs.getDate("approvedDate"),
                                        rs.getBigDecimal("successTotalAmount"),
                                        rs.getInt("successRecordCount"),
                                        rs.getBigDecimal("failureTotalAmount"),
                                        rs.getInt("failureRecordCount"),
                                        rs.getBigDecimal("duplicateTotalAmount"),
                                        rs.getInt("duplicateRecordCount"),
                                        rs.getInt("totalCount"),
                                        rs.getBigDecimal("totalAmount"),
                                        rs.getString("fileName"),
                                        rs.getString("filePath"),
                                        rs.getString("checksum"))
                );


            }

            logger.info("getPayoutFileList() - query : " + sql);
            	 }
                
           
            catch (Exception e) {
            logger.error("Error while fetching payout file upload list {}", e);
            throw new GenericCustomException("Error while fetching payout file upload list ", new Date());
        }
    	return payoutFileUploadDtos;
        
           
    }

    @Override
    @Transactional
    public long savePayoutFileDataToStagingSelfPayout(PayoutStagingDto payoutStagingDto) {
        String sql = "INSERT INTO payout_staging (merchantid, merchantPayoutId, accountId, beneficiaryName, " +
                " payoutPurpose, payoutStatus, amount, beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdBy, createdDate ) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW()) ";

        try {
            logger.info("savePayoutFileDataToStagingSelfPayout() - query : " + sql);
            GeneratedKeyHolder holder = new GeneratedKeyHolder();

            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, payoutStagingDto.getMerchantid());
                    ps.setString(2, payoutStagingDto.getMerchantPayoutId());
                    ps.setString(3, payoutStagingDto.getAccountId());
                    ps.setString(4, payoutStagingDto.getBeneficiaryName());
                    ps.setString(5, payoutStagingDto.getPayoutPurpose());
                    ps.setString(6, payoutStagingDto.getPayoutStatus());
                    ps.setDouble(7, new BigDecimal(payoutStagingDto.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    ps.setString(8, payoutStagingDto.getBeneficiaryMobileNumber());
                    ps.setString(9, payoutStagingDto.getBeneficiaryEmailId());
                    ps.setString(10, payoutStagingDto.getPayoutType());
                    ps.setString(11, payoutStagingDto.getCreatedBy());
                    return ps;
                }
            }, holder);
            logger.info(" Payout file data inserted successfully for self payout ");
            return holder.getKey().longValue();
        } catch (Exception e) {
            logger.error("Error while saving self payout data to staging {}", e);
            throw new GenericCustomException("Error while saving self payout data to staging ", new Date());
        }
    }

    @Override
    @Transactional
    public int approveSelfPayout(boolean enableSelfPayChecker, Long payoutid, User user, String action) {
    	String sql1="SELECT createdBy FROM payout_staging where payoutType='SELFPAY' AND payoutid =?";
    	String name = jdbcTemplate.queryForObject(sql1, new Object[]{payoutid}, String.class);
    	System.out.println(name);
    	if(action.equals("APPROVE") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to approve the record as you are the creator of the same", new Date());

    	}
    	if(action.equals("REJECT") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to reject the record as you are the creator of the same", new Date());
    	}
        try {
            if (action.equals("APPROVE")) {
                String sql = "INSERT INTO payout (payoutStatus, payoutid, merchantid, merchantPayoutId, accountId, beneficiaryName, " +
                        " payoutPurpose, amount, beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, approvedBy, approvedDate ) "
                        + " SELECT ?, payoutid, merchantid, merchantPayoutId, accountId, beneficiaryName, "
                        + " payoutPurpose, amount, beneficiaryMobileNumber, beneficiaryEmailId, payoutType, createdDate, ?, NOW() FROM payout_staging " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveSelfPayout() - query for moving data from staging to payout : " + sql);

                final String approvedBy = enableSelfPayChecker ? user.getUserName() : UserType.SYSTEM.name();
                jdbcTemplate.update(sql, PayoutStatusEnum.READYFORPROCESSING.name(), approvedBy, payoutid, user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Self Payout staging success data saved successfully to payout with ready status ");

                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveSelfPayout() - update staging insta payout file update query : " + updateStagingSql);


                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.READY.name(),
                        approvedBy,
                        payoutid,
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Self Payout staging data updated successfully ");
                return updateStagingResult;
            }

            if (action.equals("REJECT")) {
                final String approvedBy = enableSelfPayChecker ? user.getUserName() : UserType.SYSTEM.name();

                String updateStagingSql = "UPDATE payout_staging SET payoutStatus = ?, approvedBy = ?, approvedDate = NOW() " +
                        " WHERE payoutid = ? AND merchantid = ? AND payoutStatus = ? ";
                logger.info("approveSelfPayout() - update staging insta payout file update query : " + updateStagingSql);

                int updateStagingResult = jdbcTemplate.update(updateStagingSql, PayoutStatusEnum.REJECTED.name(),
                        approvedBy,
                        payoutid,
                        user.getMerchantId(),
                        PayoutStatusEnum.SUCCESS.name());
                logger.info(" Self Payout staging data updated successfully ");
                return updateStagingResult;

            }
        } catch (Exception e) {
            logger.error("Error while moving self payout staging data from payout staging to payout {}", e);
            throw new GenericCustomException("Error while moving self payout staging data from payout staging to payout ", new Date());
        }
        return 0;
    }


}
