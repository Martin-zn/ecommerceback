package com.martin.ecommerce.springecommerce.services;

import com.martin.ecommerce.springecommerce.api.model.AddItemRequest;
import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.exceptions.ProductException;
import com.martin.ecommerce.springecommerce.repositories.CartItemRepository;
import com.martin.ecommerce.springecommerce.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    //Inyeccion

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemService cartItemService;

    //Metodos

    public Cart createCart(LocalUser user){
        return null;
    }

    public String addCartItem(Long userId, AddItemRequest req) throws ProductException {

        return null;
    }

    public Cart findUserCart(Long userId){

        return null;
    }
}
