package com.martin.ecommerce.springecommerce.repositories;

import org.springframework.data.repository.ListCrudRepository;

import com.martin.ecommerce.springecommerce.entities.Product;

public interface ProductsRepository extends ListCrudRepository<Product, Long>{

}
