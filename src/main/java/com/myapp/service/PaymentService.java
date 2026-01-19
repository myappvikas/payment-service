package com.myapp.service;

import com.myapp.dto.AccountDTO;
import com.myapp.entity.Account;

import java.util.Optional;

public interface PaymentService {

   Account createAccount(AccountDTO dto);

   Double checkBalance(Integer customerId);

   Account makeTransaction(Integer customerId, Double amount);

   Optional<Account> searchAccount(Integer id);
}
