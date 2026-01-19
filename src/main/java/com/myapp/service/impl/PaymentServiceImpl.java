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

import java.util.Optional;

@Service
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
    public Account createAccount(AccountDTO dto) {
        return paymentRepository.save(
                modelMapper.map(dto, Account.class)
        );
    }

    @Override
    public Double checkBalance(Integer customerId) {
        return paymentRepository.findById(customerId)
                .map(Account::getAvailableBalance)
                .orElse(0.0d);
    }

    @Override
    @Transactional
    public Account makeTransaction(Integer customerId, Double amount) {

        Account account = paymentRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account does not exist for customerId: " + customerId)
                );

        account.setAvailableBalance(account.getAvailableBalance() - amount);

        OrderDTO order = new OrderDTO();
        order.setOrderAmount(amount);
        order.setOrderItem("SmartPhone");

        account.setOrderInfo(paymentClient.placeOrder(order));

        return paymentRepository.save(account);
    }


    @Override
    public Optional<Account> searchAccount(Integer id) {
        return paymentRepository.findById(id);
    }
}
