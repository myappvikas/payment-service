package com.myapp.service;

import com.myapp.dto.AccountDTO;

public interface PaymentService {

    AccountDTO createAccount(AccountDTO dto);

    Double checkBalance(Long customerId);

    AccountDTO makeTransaction(Long customerId, Double amount);

    AccountDTO searchAccount(Long id);
}
