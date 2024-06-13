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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CartService {

    //Inyeccion
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private UserService userService;

    //Metodos

    public Cart createCart(LocalUser user){
        Cart cart = new Cart();
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    public String addCartItem(Long userId, AddItemRequest req) throws ProductException {

        Cart cart = cartRepository.findByUserId(userId);
        System.out.println(req.getProductId());
        Product product =productService.findProductById(req.getProductId());
        CartItem isPresent = cartItemService.isCartItemExist(cart,product,userId);

        if(isPresent==null){
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);

            int price = req.getQuantity()*product.getPrice();
            cartItem.setPrice(price);

            CartItem createdCartItem = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(createdCartItem);
        }

        return "El item fue agregado exitosamente";
    }

    public Cart findUserCart(Long userId){

        Cart cart = cartRepository.findByUserId(userId);
        int totalPrice=0;
        int totalItem=0;


        List<CartItem> lstCartItem = cartItemService.findAllCardItemByUserId(cart, userId);

//        cart.setCartItems(lstCartItem);

        for(CartItem cartItem : lstCartItem){
            totalPrice=totalPrice+cartItem.getPrice();
            totalItem=totalItem+(cartItem.getQuantity());
        }

        cart.setTotalPrice(totalPrice);
        cart.setTotalItem(totalItem);
        return cartRepository.save(cart);
    }

    public void deleteCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {
//        LocalUser user = userService.findUserById(userId);
        CartItem cartItem = cartItemService.findCartItemById(cartItemId);

        if (userId.equals(cartItem.getUserId())) {
            cartItemService.removeCartItem(userId, cartItemId);
        } else {
            throw new CartItemException("No existe el cartItem");
        }
    }
}
