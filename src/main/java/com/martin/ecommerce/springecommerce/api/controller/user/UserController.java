package com.martin.ecommerce.springecommerce.api.controller.user;

import com.martin.ecommerce.springecommerce.entities.Address;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AddressRepository addressRepository;

@GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId){
    if(!userHasPermission(user,userId)){
        ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ResponseEntity.ok(addressRepository.findByUser_id(userId));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @RequestBody Address address){
        if(!userHasPermission(user,userId)){
            ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser dummyUser = new LocalUser();
        dummyUser.setId(userId);
        address.setUser(dummyUser);
        return ResponseEntity.ok(addressRepository.save(address));
    }

    @PatchMapping("/{userId}/address/{addressId}")
        public ResponseEntity<Address> patchAddress(
                @AuthenticationPrincipal LocalUser user,@PathVariable Long addressId, @PathVariable Long userId, @RequestBody Address address){
        if(!userHasPermission(user,userId)){
            ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(address.getId().equals(addressId)){
            Optional<Address> opOriginalAddress = addressRepository.findById(addressId);
            if (opOriginalAddress.isPresent()){
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (originalUser.getId().equals(userId)){
                    address.setUser(originalUser);
                    return ResponseEntity.ok(addressRepository.save(address));
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }



    private boolean userHasPermission(LocalUser user, Long id){
        return user.getId().equals(id);
    }

}
