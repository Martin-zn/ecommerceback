package com.martin.ecommerce.springecommerce.api.controller.user;

import com.martin.ecommerce.springecommerce.api.model.DataChange;
import com.martin.ecommerce.springecommerce.entities.Address;
import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.repositories.AddressRepository;
import com.martin.ecommerce.springecommerce.services.CartService;
import com.martin.ecommerce.springecommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

@GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId){
    if(!userService.userHasPermissionToUser(user,userId)){
        ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ResponseEntity.ok(addressRepository.findByUser_id(userId));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(
            @AuthenticationPrincipal LocalUser user,
            @PathVariable Long userId,
            @RequestBody Address address){
        if(!userService.userHasPermissionToUser(user,userId)){
            ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser dummyUser = new LocalUser();
        dummyUser.setId(userId);
        address.setUser(dummyUser);
        Address savedAddress = addressRepository.save(address);
        simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address", new DataChange<>(DataChange.ChangeType.INSERT, address));
        return ResponseEntity.ok(addressRepository.save(address));
    }

    @PatchMapping("/{userId}/address/{addressId}")
        public ResponseEntity<Address> patchAddress(
                @AuthenticationPrincipal LocalUser user,@PathVariable Long addressId, @PathVariable Long userId, @RequestBody Address address){
        if(!userService.userHasPermissionToUser(user,userId)){
            ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(address.getId().equals(addressId)){
            Optional<Address> opOriginalAddress = addressRepository.findById(addressId);
            if (opOriginalAddress.isPresent()){
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (originalUser.getId().equals(userId)){
                    address.setUser(originalUser);
                    Address savedAddress = addressRepository.save(address);
                    simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                            new DataChange<>(DataChange.ChangeType.UPDATE, address));
                    return ResponseEntity.ok(addressRepository.save(address));
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

//    @GetMapping("/cart")
//    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt){
//
//    }

    @GetMapping("/cart")
    public ResponseEntity<Cart> findUserCart(@AuthenticationPrincipal LocalUser user){
    Cart cart = cartService.findUserCart(user.getId());

    return new ResponseEntity<Cart>(cart, HttpStatus.OK);

    }



}
