package com.martin.ecommerce.springecommerce.repositories;

import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.entities.CartItem;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

public interface CartItemRepository extends ListCrudRepository<CartItem, Long> {


}
