package com.bezkoder.springjwt.payload;

import com.bezkoder.springjwt.enums.PayoutActionEnum;
import com.bezkoder.springjwt.enums.PayoutFileUploadStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutApprovePayload {

    private String merchantId;
    private Long payoutFileUploadId;
    private String payoutRoleType;
    private String action;
    private PayoutFileUploadStatusEnum payoutFileUploadStatusEnum;


}
