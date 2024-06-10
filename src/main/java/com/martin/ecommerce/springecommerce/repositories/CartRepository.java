package com.martin.ecommerce.springecommerce.repositories;


import com.martin.ecommerce.springecommerce.entities.Cart;
import org.springframework.data.repository.ListCrudRepository;

public interface CartRepository extends ListCrudRepository<Cart, Long> {
}
