package com.martin.ecommerce.springecommerce.api.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.martin.ecommerce.springecommerce.api.model.LoginBody;
import com.martin.ecommerce.springecommerce.api.model.LoginResponse;
import com.martin.ecommerce.springecommerce.api.model.RegistrationBody;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.exceptions.UserAlreadyExistsException;
import com.martin.ecommerce.springecommerce.services.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){
        try {
            userService.registerUser(registrationBody);
            System.out.println("Exito");
            return ResponseEntity.ok().build();

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = userService.loginUser(loginBody);
        if (jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            return ResponseEntity.ok(response);

        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUSerProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }

}
    




