package com.martin.ecommerce.springecommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.entities.Product;
import com.martin.ecommerce.springecommerce.repositories.ProductsRepository;

@Service
public class ProductService {

    @Autowired
    private ProductsRepository productsRepository;

    public List<Product> getProducts(){
        return productsRepository.findAll();
    }



}
