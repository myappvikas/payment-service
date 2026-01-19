package com.myapp.entity;

import com.myapp.dto.OrderDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cardHolderName;
    private Long cardNumber;
    private Double availableBalance;
    private String expiryDate;
    private Integer securityCode;
    private transient OrderDTO orderInfo;
}
