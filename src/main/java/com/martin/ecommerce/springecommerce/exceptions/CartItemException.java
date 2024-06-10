package com.martin.ecommerce.springecommerce.exceptions;

import com.martin.ecommerce.springecommerce.entities.CartItem;

public class CartItemException extends Exception{

    public CartItemException(String message){
        super(message);
    }
}
