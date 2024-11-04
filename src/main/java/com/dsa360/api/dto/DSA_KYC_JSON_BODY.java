package com.dsa360.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DSA_KYC_JSON_BODY {
    private String dsaApplicationId;
    private String bankName;
    private String accountNumber;
    private String ifscCode;

}
