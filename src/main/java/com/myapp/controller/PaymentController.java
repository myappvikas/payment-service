package com.myapp.controller;

import com.myapp.client.PaymentClient;
import com.myapp.dto.AccountDTO;
import com.myapp.entity.Account;
import com.myapp.exception.ResourceNotFoundException;
import com.myapp.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class PaymentController {


	private final PaymentService paymentService;

	private final PaymentClient paymentClient;

    public PaymentController(PaymentService paymentService,  PaymentClient paymentClient) {
        this.paymentService = paymentService;
        this.paymentClient = paymentClient;
    }

	@GetMapping("/welcome")
	public String welcome(){
		return paymentClient.welcome();
	}

	@PostMapping("/save")
	public ResponseEntity<?> createAccount(@RequestBody AccountDTO dto){
		Account info = paymentService.createAccount(dto);
		return ResponseEntity.ok(info);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> searchAccount(@PathVariable Integer id){
		Optional<Account> optional = paymentService.searchAccount(id);
		if(optional.isEmpty()){
			throw new ResourceNotFoundException("Resource is not found");
		}
		return ResponseEntity.ok(optional);
	}

	@GetMapping("/balance/{id}")
	public ResponseEntity<?> checkBalance(@PathVariable Integer id){
		Double balance = paymentService.checkBalance(id);
		return ResponseEntity.ok(balance);
	}

	@CircuitBreaker(name = "service-message", fallbackMethod = "fallbackResponse")
	@PutMapping("/balance/{id}/{amount}")
	public ResponseEntity<?> makePayment(@PathVariable Integer id, @PathVariable Double amount){
		Account updatedAccountInfo = paymentService.makeTransaction(id,amount);
		return ResponseEntity.ok("Account has updated successfully");
	}

	public ResponseEntity<?> fallbackResponse(@PathVariable Integer id, @PathVariable Double amount,
													Throwable throwable) {
		return ResponseEntity.ok("Order service is down currently");
	}
}
