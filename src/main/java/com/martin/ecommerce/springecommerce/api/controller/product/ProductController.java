package com.martin.ecommerce.springecommerce.api.controller.product;

import java.util.List;

import com.martin.ecommerce.springecommerce.exceptions.ProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.martin.ecommerce.springecommerce.entities.Product;
import com.martin.ecommerce.springecommerce.services.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @CrossOrigin("http://localhost:3000")
    public List<Product> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) throws ProductException {

        return ResponseEntity.ok(productService.findProductById(id));
    }
}
