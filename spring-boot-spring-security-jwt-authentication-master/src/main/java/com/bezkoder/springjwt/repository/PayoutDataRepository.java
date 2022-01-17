package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.entity.PayoutFileUpload;
import com.bezkoder.springjwt.enums.PayoutColumnType;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.PayoutApprovePayload;
import com.bezkoder.springjwt.payload.PayoutBalanceData;
import com.bezkoder.springjwt.payload.PayoutFileUploadDto;
import com.bezkoder.springjwt.payload.PayoutStagingDto;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PayoutDataRepository {

    long saveUploadPayoutFile(PayoutFileUploadDto payoutFileUploadDto);

    void savePayoutFileDataToStaging(List<PayoutStagingDto> payoutStagingDtoList);

    void saveDuplicateStagingDataToPayoutDuplicate(List<PayoutStagingDto> payoutStagingDtoList);

    long savePayoutFileDataToStagingInstaPayout(PayoutStagingDto payoutStagingDto);

    long savePayoutFileDataToStagingSelfPayout(PayoutStagingDto payoutStagingDto);

    int updatePayoutFileUploadData(PayoutFileUploadDto payoutFileUploadDto);

    Collection<PayoutFileUploadDto> getPayoutFileList(String payoutRoleType, String virtualAccountId,
                                                      String merchantId);

    int approvePayoutFile(PayoutApprovePayload payoutApprovePayload, User user);

    Collection<PayoutStagingDto> getInstaOrSelfPayoutList(String payoutRoleType, String virtualAccountId, String merchantId, String payoutType);

    int approveInstaPayout(boolean enableInstaPayChecker, Long payoutid, User user, String action);

    int approveApiPayout(boolean enableInstaPayChecker, Long payoutid, User user, String action);

    int approveSelfPayout(boolean enableSelfPayChecker, Long payoutid, User user, String action);

    List<PayoutStagingDto> getPayoutFileData(String payoutRoleType, Long payoutFileUploadId, User user);

    PayoutFileUploadDto getPayoutFileUpload(Long payoutFileUploadId, String merchantId);

    List<PayoutBalanceData> payoutBalanceData(String merchantId, String accountId, User user,
                                              Integer lastNDays, PayoutColumnType type);

    PayoutStagingDto getByPayoutId(Long payoutid, String merchantId);

    List<String> getDuplicateMerchantPayoutIdList(Set<String> merchantPayoutIdList);

    Collection<PayoutFileUpload> getHistoryOfPayoutFileList(String payoutRoleType, String virtualAccountId,
                                                               String merchantId, int days);

}
