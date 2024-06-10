package com.martin.ecommerce.springecommerce.services;

import com.martin.ecommerce.springecommerce.api.model.AddItemRequest;
import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.entities.CartItem;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.Product;
import com.martin.ecommerce.springecommerce.exceptions.CartItemException;
import com.martin.ecommerce.springecommerce.exceptions.ProductException;
import com.martin.ecommerce.springecommerce.exceptions.UserException;
import com.martin.ecommerce.springecommerce.repositories.CartItemRepository;
import com.martin.ecommerce.springecommerce.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;




    public CartItem createCartItem(CartItem cartItem){
        cartItem.setQuantity(1);
        cartItem.setPrice(cartItem.getProduct().getPrice()*cartItem.getQuantity());

        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {

        CartItem item = cartItemRepository.findById(id).get();
        LocalUser user = userService.findUserById(item.getUserId());

        if(user.getId().equals(userId)){
            item.setQuantity(cartItem.getPrice());
            item.setPrice(item.getProduct().getPrice()*item.getQuantity());
        }
        return cartItemRepository.save(item);
    }

    public CartItem isCartItemExist(Cart cart, Product product, Long userId){
        CartItem cartItem = cartItemRepository.isCartItemExist(cart,product,userId);
        return cartItem;

    }

    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException{
        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
        LocalUser user = userService.findUserById(userId);

        if (user.getId().equals(cartItem.getUserId())){
            cartItemRepository.deleteById(cartItemId);
        }
        else{
            throw new UserException("No puede borrar los items de otros usuarios");
        }

    }

    public CartItem findCartItemById(Long cartItemId) throws CartItemException{

        Optional<CartItem> opCartItem = cartItemRepository.findById(cartItemId);

        if (opCartItem.isPresent()){
            return opCartItem.get();
        }else{
            throw new CartItemException("El item no existe");
        }

    }


}
