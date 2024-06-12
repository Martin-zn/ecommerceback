package com.martin.ecommerce.springecommerce.services;

import java.util.List;
import java.util.Optional;

import com.martin.ecommerce.springecommerce.api.model.CreateProductBody;
import com.martin.ecommerce.springecommerce.entities.Category;
import com.martin.ecommerce.springecommerce.exceptions.ProductException;
import com.martin.ecommerce.springecommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.entities.Product;
import com.martin.ecommerce.springecommerce.repositories.ProductsRepository;

@Service
public class ProductService {

    //Inyecciones

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //METODOS
    public List<Product> getProducts(){

        return productsRepository.findAll();
    }

    public Product findProductById(Long id) throws ProductException {
        if (id == null) {
            throw new ProductException("El ID del producto no puede ser nulo");
        }

        Optional<Product> opt = productsRepository.findProductById(id);

        if (opt.isPresent()) {
            return opt.get();
        }

        throw new ProductException("Producto no encontrado papito.. busca bien");
    }


//    public List<Product> getProductsByCategory(String category){
//
//
//        return productsRepository.findAllByCategory(category);
//    }



    public void createProduct(CreateProductBody productBody){
        try{
            Product product = new Product();

            Category category = categoryRepository.findByNameIgnoreCase(productBody.getCategory()).orElse(null);


            if(category == null){
                Category newCategory = new Category();
                newCategory.setName(productBody.getCategory());
                categoryRepository.save(newCategory);
            }

            product.setCategory(category);
            product.setName(productBody.getName());
            product.setBrand(productBody.getBrand());
            product.setImageUrl(productBody.getImageUrl());
            product.setLongDescription(productBody.getLongDescription());
            product.setShortDescription(productBody.getShortDescription());
            product.setPrice(productBody.getPrice());
            product.setQuantity(productBody.getQuantity());

            productsRepository.save(product);

        }catch (Exception e){
            System.out.println("No funciono papito... revisa el error - " + e);
        }
    }

    public String deleteProduct(Long id ){
        productsRepository.deleteById(id);
        return "Producto eliminado...";
    }

    public Product upgradeProduct(Long id, CreateProductBody productBody){

        Product upgradeProduct = productsRepository.findById(id).get();

        upgradeProduct.setBrand(productBody.getBrand());
        upgradeProduct.setName(productBody.getName());
        upgradeProduct.setImageUrl(productBody.getImageUrl());
        upgradeProduct.setLongDescription(productBody.getLongDescription());
        upgradeProduct.setShortDescription(productBody.getShortDescription());
        upgradeProduct.setQuantity(productBody.getQuantity());

        return productsRepository.save(upgradeProduct);
    }

    public Product SetQuantityProduct(Long id, Integer cuantity){

        Product upgradeProduct = productsRepository.findById(id).get();
        upgradeProduct.setQuantity(cuantity);

        return productsRepository.save(upgradeProduct);
    }
//    public Page<Product> getAllProductPage(String category, Integer minPrice, Integer maxPrice, String sort,
//    Integer pageNumber, Integer pageSize){
//
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//
//        List<Product> products = productsRepository.filterProducts(category, minPrice, maxPrice, sort);
//
//        int starIndex=(int) pageable.getOffset();
//        int endIndex = Math.min(starIndex + pageable.getPageSize(), products.size());
//
//        List<Product> pageContent = products.subList(starIndex, endIndex);
//
//        Page<Product> filterProducts = new PageImpl<>(pageContent, pageable, products.size());
//
//
//        return null;
//    }
public Page<Product> getAllProductPage(String category, Integer minPrice, Integer maxPrice, String sort, Integer pageNumber, Integer pageSize) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);

    List<Product> products = productsRepository.filterProducts(category, minPrice, maxPrice, sort);
    int start = (int) pageable.getOffset();
    int end = (int) ((start + pageable.getPageSize()) > products.size() ? products.size() : (start + pageable.getPageSize()));
    Page<Product> productPage = new PageImpl<>(products.subList(start, end), pageable, products.size());

    return productPage;
}


}
