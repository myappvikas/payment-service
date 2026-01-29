package com.myapp.service.impl;

import com.myapp.dto.AccountDTO;
import com.myapp.dto.OrderDTO;
import com.myapp.entity.Account;
import com.myapp.exception.InsufficientBalanceException;
import com.myapp.exception.OrderServiceException;
import com.myapp.exception.ResourceNotFoundException;
import com.myapp.repository.PaymentRepository;
import com.myapp.service.PaymentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Transactional
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final ModelMapper modelMapper;

    private final WebClient webClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              ModelMapper modelMapper,
                              WebClient webClient) {
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
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
    public AccountDTO makeTransaction(Long customerId, Double amount, OrderDTO orderDTO) {

        validateInputs(customerId, amount);

        Account updatedAccount = paymentRepository.findById(customerId)
                .map(account -> debitAmount(account, amount))
                .map(paymentRepository::save)
                .map(account -> enrichWithOrder(account, amount, orderDTO))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account does not exist for customerId: " + customerId));

        return modelMapper.map(updatedAccount, AccountDTO.class);
    }

    private void validateInputs(Long customerId, Double amount) {
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId must not be null");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
    }

    private Account debitAmount(Account account, Double amount) {

        if (account.getAvailableBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for accountId: " + account.getId());
        }
        account.setAvailableBalance(account.getAvailableBalance() - amount);
        return account;
    }

    private Account enrichWithOrder(Account account, Double amount, OrderDTO orderDTO) {

        return Optional.ofNullable(orderDTO)
                .map(dto -> buildOrderRequest(dto, amount))
                .map(this::placeOrder)
                .map(order -> {
                    account.setOrderInfo(order);
                    return account;
                })
                .orElse(account);
    }

    private OrderDTO buildOrderRequest(OrderDTO orderDTO, Double amount) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderItem(orderDTO.getOrderItem());
        dto.setOrderAmount(amount);
        return dto;
    }

    private OrderDTO placeOrder(OrderDTO orderDTO) {
        return webClient.post()
                .uri("/api/orders/place")
                .bodyValue(orderDTO)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(error -> new OrderServiceException(
                                        "Order service failed: " + error))
                )
                .bodyToMono(OrderDTO.class)
                .block();
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
