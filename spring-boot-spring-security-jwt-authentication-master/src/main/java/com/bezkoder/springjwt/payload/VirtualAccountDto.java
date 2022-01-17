package com.bezkoder.springjwt.payload;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualAccountDto {
    private String virtualAccountID;
    private String merchantid;
    private String description;
    private Double balance;
    private String status;
    private Date closedDate;
    private String closedBy;
    private Date createdDate;
    private Date updatedAt;
    private String createdBy;
    private String closeReason;
    private String approvedBy;

  
}
