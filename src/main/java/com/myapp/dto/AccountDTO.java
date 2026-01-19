package com.myapp.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Integer id;
    private String cardHolderName;
    private Long cardNumber;
    private Double availableBalance;
    private String expiryDate;
    private Integer securityCode;
    private transient OrderDTO orderInfo;
}
