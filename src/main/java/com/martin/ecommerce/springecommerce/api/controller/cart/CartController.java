package com.martin.ecommerce.springecommerce.api.controller.cart;

import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.exceptions.UserException;
import com.martin.ecommerce.springecommerce.services.CartService;
import com.martin.ecommerce.springecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public ResponseEntity findUserCart(@RequestHeader("Authorization") String jwt) throws UserException {
        String newJwt = jwt.substring(7);
        LocalUser user =userService.findUserByJwt(newJwt);
        if(user != null){
        Cart cart = cartService.findUserCart(user.getId());
        return ResponseEntity.ok(cart);
        }else {
            throw  new UserException("mal JWT");
        }
    }




}
