package com.martin.ecommerce.springecommerce.repositories;

import com.martin.ecommerce.springecommerce.entities.Category;
import com.martin.ecommerce.springecommerce.entities.OrderItems;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItems, Long> {
}
