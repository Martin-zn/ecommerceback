package com.martin.ecommerce.springecommerce.repositories;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.VerificationToken;


public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long>{

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);
}
