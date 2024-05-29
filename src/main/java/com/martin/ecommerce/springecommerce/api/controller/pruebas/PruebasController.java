package com.martin.ecommerce.springecommerce.api.controller.pruebas;


import com.martin.ecommerce.springecommerce.api.model.RegistrationBody;
import com.martin.ecommerce.springecommerce.exceptions.EmailFailureException;
import com.martin.ecommerce.springecommerce.exceptions.UserAlreadyExistsException;
import com.martin.ecommerce.springecommerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prueba")
public class PruebasController {

    @Autowired
    private UserService userService;


    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello Secured";
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegistrationBody registrationBody) throws EmailFailureException, UserAlreadyExistsException {

        return ResponseEntity.ok(userService.registerUser(registrationBody));
    }

    @GetMapping("/user")
    //@PreAuthorize("hasRole('USER')")
    public String helloUser(){
        return "Hello user";
    }
    @GetMapping("/admin")
    //@PreAuthorize("hasRole('ADMIN')")
    public String helloAdmin(){
        return "Hello Admin";
    }
    @GetMapping("/invited")
    //@PreAuthorize("hasRole('INVITED')")
    public String helloInvited(){
        return "Hello Invited";
    }





}
