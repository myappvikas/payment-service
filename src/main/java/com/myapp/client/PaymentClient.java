package com.myapp.client;

import com.myapp.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "http://localhost:2022", value = "order-client")
public interface PaymentClient {

    @PostMapping("/order/place")
    OrderDTO placeOrder(@RequestBody OrderDTO order);
}
