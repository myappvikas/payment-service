package com.myapp.service.impl;

import com.myapp.dto.AccountDTO;
import com.myapp.dto.OrderDTO;
import com.myapp.entity.Account;
import com.myapp.exception.ResourceNotFoundException;
import com.myapp.repository.PaymentRepository;
import com.myapp.service.PaymentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Transactional
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final ModelMapper modelMapper;

    private final WebClient webClient;

    public PaymentServiceImpl (PaymentRepository paymentRepository,
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

        Account account = paymentRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account does not exist for customerId: " + customerId)
                );

        account.setAvailableBalance(account.getAvailableBalance() - amount);

        //OrderDTO dto = account.getOrderInfo();

        if (orderDTO != null){
            account.setOrderInfo(orderDTO);
        }

        //OrderDTO orderDTO = new OrderDTO();
        //orderDTO.setOrderItem("Phone");
        //orderDTO.setOrderAmount(500D);

        OrderDTO updatedOrder = webClient
                .post()
                .uri("/api/orders/place")
                .bodyValue(orderDTO)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();
        account.setOrderInfo(updatedOrder);
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
