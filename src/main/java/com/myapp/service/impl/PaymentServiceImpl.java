package com.myapp.service.impl;

import com.myapp.client.PaymentClient;
import com.myapp.dto.AccountDTO;
import com.myapp.dto.OrderDTO;
import com.myapp.entity.Account;
import com.myapp.exception.ResourceNotFoundException;
import com.myapp.repository.PaymentRepository;
import com.myapp.service.PaymentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentClient paymentClient;

    private final ModelMapper modelMapper;

    public PaymentServiceImpl (PaymentRepository paymentRepository,
                               PaymentClient paymentClient,
                               ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public AccountDTO createAccount(AccountDTO dto) {
        Account savedAccount = paymentRepository.save(
                modelMapper.map(dto, Account.class)
        );
        return modelMapper.map(savedAccount, AccountDTO.class);
    }

    @Override
    public Double checkBalance(Long customerId) {
        return paymentRepository.findById(customerId)
                .map(Account::getAvailableBalance)
                .orElse(0.0);
    }

    @Override
    @Transactional
    public AccountDTO makeTransaction(Long customerId, Double amount) {
        Account account = paymentRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account does not exist for customerId: " + customerId)
                );
        account.setAvailableBalance(account.getAvailableBalance() - amount);
        OrderDTO order = account.getOrderInfo();
        System.out.println(order);
        account.setOrderInfo(paymentClient.placeOrder(order));
        Account savedAccount = paymentRepository.save(account);
        return modelMapper.map(savedAccount, AccountDTO.class);
    }

    @Override
    public AccountDTO searchAccount(Long id) {
        Account account = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + id)
                );
        return modelMapper.map(account, AccountDTO.class);
    }
}
