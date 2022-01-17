package com.bezkoder.springjwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantConfiguration {

    private String merchantid;
    private String paymentMode;
    private String instruments;
    private String acquirerBankName;
    private String acquirerBankIFSC;
    private double minPayoutAmount;
    private double maxPayoutAmount;
    private double lowBalanceThreshold;
    Date updateDate;
    private String updatedBy;
    private boolean enableInstaPayChecker;
    private boolean enableSelfPayChecker;
    private boolean enableCancelPayChecker;
    private boolean enableCloseAcctChecker;
    private boolean enableAPIPayChecker;


}
