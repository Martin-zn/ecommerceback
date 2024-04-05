package com.martin.ecommerce.springecommerce.api.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.WebOrder;
import com.martin.ecommerce.springecommerce.services.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user){
        return orderService.getOrders(user);
    }

}
