package com.martin.ecommerce.springecommerce.api.controller.product;

import java.util.List;

import com.martin.ecommerce.springecommerce.api.model.CreateProductBody;
import com.martin.ecommerce.springecommerce.exceptions.ProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity<?> getProductById(@PathVariable Long id) throws ProductException {

        return ResponseEntity.ok(productService.findProductById(id));
    }

    @PostMapping("/admin/createProduct")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductBody productBody){
        productService.createProduct(productBody);
        return ResponseEntity.ok("Producto Creado exitosamente");
    }

    @GetMapping("/pageProducts")
    public ResponseEntity<Page<Product>> getPageProducts(@RequestParam String category, @RequestParam Integer minPrice, @RequestParam Integer maxprice,
                                                         @RequestParam String sort, @RequestParam Integer pageNumber, @RequestParam Integer pageSize){

        Page<Product> res = productService.getAllProductPage(category, minPrice, maxprice, sort, pageNumber, pageSize);

        System.out.println("Esta es la pagina de productos");

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

    }



}
