package com.martin.ecommerce.springecommerce.repositories;

import com.martin.ecommerce.springecommerce.entities.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressRepository extends ListCrudRepository<Address, Long> {

    List<Address> findByUser_id(Long id);

}
