package com.myapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Long id;
    private String cardHolderName;
    private Long cardNumber;
    private Double availableBalance;
    private LocalDateTime expiryDate;
    private Integer securityCode;
    private transient OrderDTO orderInfo;
}
