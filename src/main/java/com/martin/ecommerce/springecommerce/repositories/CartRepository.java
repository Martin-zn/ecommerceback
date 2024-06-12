package com.martin.ecommerce.springecommerce.repositories;


import com.martin.ecommerce.springecommerce.entities.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends ListCrudRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN c.cartItems WHERE c.user.id = :userId")
    Cart findByUserId(@Param("userId") Long userId);
}
