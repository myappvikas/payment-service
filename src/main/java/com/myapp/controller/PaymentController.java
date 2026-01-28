package com.myapp.controller;

import com.myapp.dto.AccountDTO;
import com.myapp.dto.OrderDTO;
import com.myapp.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

	@PostMapping("/create/account")
	public ResponseEntity<?> createAccount(@RequestBody AccountDTO dto){
		return new ResponseEntity<>(paymentService.createAccount(dto), HttpStatus.CREATED);
	}

	@GetMapping("/search/account/{id}")
	public ResponseEntity<?> searchAccount(@PathVariable Long id){
		return new ResponseEntity<>(paymentService.searchAccount(id),HttpStatus.OK);
	}

	@GetMapping("/account/balance/{id}")
	public ResponseEntity<?> checkBalance(@PathVariable Long id){
		return new ResponseEntity<>(paymentService.checkBalance(id), HttpStatus.OK);
	}

	@CircuitBreaker(name = "service-message", fallbackMethod = "fallbackResponse")
	@PutMapping("/pay/amount/{id}/{amount}")
	public ResponseEntity<?> makePayment(@PathVariable Long id, @PathVariable Double amount, @RequestBody OrderDTO orderDTO){
		return new ResponseEntity<>(paymentService.makeTransaction(id,amount, orderDTO), HttpStatus.OK);
	}

	public ResponseEntity<?> fallbackResponse(@PathVariable Long id, @PathVariable Double amount,
													Throwable throwable) {
		return ResponseEntity.ok("Order service is down currently");
	}
}
