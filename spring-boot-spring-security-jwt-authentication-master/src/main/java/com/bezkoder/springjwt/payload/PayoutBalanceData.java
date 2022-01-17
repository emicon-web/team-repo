package com.bezkoder.springjwt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutBalanceData {

    private String payoutStatus;
    private String payoutPaymentMode;
    private BigDecimal totalAmount;
    private Long recordCount;
    private String percentage;
}
