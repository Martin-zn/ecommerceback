package com.martin.ecommerce.springecommerce.repositories;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.WebOrder;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long>{

    //Metodo que buscara y entregara una lista de las ordenes realizadas por un usuario
    List<WebOrder> findByUser(LocalUser user);

}
