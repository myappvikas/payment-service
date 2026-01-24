package com.myapp;

import com.myapp.dto.AccountDTO;
import com.myapp.dto.OrderDTO;
import com.myapp.entity.Account;
import com.myapp.service.PaymentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableFeignClients
public class PaymentServiceApplication implements CommandLineRunner {

    private final PaymentService paymentService;

    public PaymentServiceApplication(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setCardHolderName("vikas");
        accountDTO.setAvailableBalance(50000D);
        accountDTO.setCardNumber(123456789L);
        accountDTO.setExpiryDate(
                LocalDateTime.of(2028, 12, 31, 23, 59, 59)
        );
        accountDTO.setSecurityCode(1234);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderAmount(500D);
        orderDTO.setOrderItem("Mobile");
        accountDTO.setOrderInfo(orderDTO);

        AccountDTO savedAccountInfo = paymentService.createAccount(accountDTO);
        System.out.println(savedAccountInfo);
    }
}
