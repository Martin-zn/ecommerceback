package com.martin.ecommerce.springecommerce.repositories;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.WebOrder;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long>{

    List<WebOrder> findByUser(LocalUser user);

}
