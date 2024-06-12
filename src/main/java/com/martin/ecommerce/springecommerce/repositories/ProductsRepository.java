package com.martin.ecommerce.springecommerce.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import com.martin.ecommerce.springecommerce.entities.Product;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends ListCrudRepository<Product, Long> {

//    public List<Product> findAllByCategory(String category);

    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR p.category.name = :category) " +
            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR (p.price BETWEEN :minPrice AND :maxPrice)) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'price_low' THEN p.price END ASC, " +
            "CASE WHEN :sort = 'price_high' THEN p.price END DESC")
    public List<Product> filterProducts(@Param("category") String category, @Param("minPrice") Integer minPrice,
                                        @Param("maxPrice") Integer maxPrice, @Param("sort") String sort);

    @Query("SELECT p FROM Product p LEFT JOIN p.category c WHERE p.id = :productId")
    Optional<Product> findProductById(@Param("productId") Long productId);




}