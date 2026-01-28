package com.myapp.service;

import com.myapp.dto.AccountDTO;
import com.myapp.dto.OrderDTO;

public interface PaymentService {

    AccountDTO createAccount(AccountDTO dto);

    Double checkBalance(Long customerId);

    AccountDTO makeTransaction(Long customerId, Double amount, OrderDTO orderDTO);

    AccountDTO searchAccount(Long id);
}
