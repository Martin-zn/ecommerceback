package com.martin.ecommerce.springecommerce.repositories;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;

import com.martin.ecommerce.springecommerce.entities.LocalUser;

public interface UserRepository extends CrudRepository<LocalUser, Long> {

    public Optional<LocalUser> findByUsernameIgnoreCase(String username);

    public Optional<LocalUser> findByEmailIgnoreCase(String email);

}
