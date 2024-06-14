package com.martin.ecommerce.springecommerce.api.controller.order;

import java.util.List;

import com.martin.ecommerce.springecommerce.exceptions.OrderException;
import com.martin.ecommerce.springecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.WebOrder;
import com.martin.ecommerce.springecommerce.services.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user){
        return orderService.getOrders(user);
    }

    @PostMapping("/add")
    public ResponseEntity createOrder(@RequestHeader("Authorization") String jwt){
        String newJwt = jwt.substring(7);
        LocalUser user =userService.findUserByJwt(newJwt);

        return ResponseEntity.ok(orderService.createWebOrder(user));
    }

    @GetMapping("/user")
    public ResponseEntity userOrderHistory(@RequestHeader("Authorization") String jwt){
        String newJwt = jwt.substring(7);
        LocalUser user =userService.findUserByJwt(newJwt);

        List<WebOrder> orders = orderService.userOrderHistory(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}/delete")
    public ResponseEntity deleteOrder(@PathVariable Long orderId, @RequestHeader("Authorization") String jwt) throws OrderException {
        String newJwt = jwt.substring(7);
        LocalUser user =userService.findUserByJwt(newJwt);
        try {
            orderService.deleteOrder(orderId);
            return new ResponseEntity<>("Orden eliminada exitosamente", HttpStatus.OK);

        }catch (Exception e){
            throw new OrderException("ID incorrecto");
        }
    }



}
