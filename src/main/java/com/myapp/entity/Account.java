package com.myapp.entity;

import com.myapp.dto.OrderDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private Long id;
    private String cardHolderName;
    private Long cardNumber;
    private Double availableBalance;
    private LocalDateTime expiryDate;
    private Integer securityCode;
    private transient OrderDTO orderInfo;
}
