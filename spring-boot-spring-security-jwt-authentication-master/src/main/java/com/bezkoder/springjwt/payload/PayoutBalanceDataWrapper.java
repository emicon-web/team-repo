package com.bezkoder.springjwt.payload;

import com.bezkoder.springjwt.enums.PayoutColumnType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutBalanceDataWrapper {

    private PayoutColumnType type;
    private BigDecimal lowBalanceThreshold;
    private Boolean lowBalanceThresholdFlag;
    private String accountStatus;
    private Double accountBalance;
    private Long totalRecords;
    private List<PayoutBalanceData> data;
}
