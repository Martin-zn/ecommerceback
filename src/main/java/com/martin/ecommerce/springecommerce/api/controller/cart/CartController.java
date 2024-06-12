package com.martin.ecommerce.springecommerce.api.controller.cart;

import com.martin.ecommerce.springecommerce.api.model.AddItemRequest;
import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.exceptions.ProductException;
import com.martin.ecommerce.springecommerce.exceptions.UserException;
import com.martin.ecommerce.springecommerce.services.CartService;
import com.martin.ecommerce.springecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        LocalUser user = userService.findUserByJwt(newJwt);
        if(user != null){
        Cart cart = cartService.findUserCart(user.getId());
        return ResponseEntity.ok(cart);
        }else {
            throw  new UserException("mal JWT");
        }
    }

    @PostMapping("/add")
    public ResponseEntity addCartItem(@RequestHeader("Authorization") String jwt, @RequestBody AddItemRequest req) {
        try {
            String newJwt = jwt.substring(7);
            LocalUser user =userService.findUserByJwt(newJwt);

            cartService.addCartItem(user.getId(), req);
            return ResponseEntity.ok("Item incluido existosamente este es el controller");
        }
        catch (ProductException e) {
            System.out.println("Aca esta el error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




}