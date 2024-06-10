package com.martin.ecommerce.springecommerce.repositories;

import com.martin.ecommerce.springecommerce.entities.Category;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    public Optional<Category> findByNameIgnoreCase(String name);
}
