package com.martin.ecommerce.springecommerce.api.controller.auth;

import com.martin.ecommerce.springecommerce.api.model.PasswordResetBody;
import com.martin.ecommerce.springecommerce.entities.Cart;
import com.martin.ecommerce.springecommerce.exceptions.EmailNotFoundException;
import com.martin.ecommerce.springecommerce.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.martin.ecommerce.springecommerce.api.model.LoginBody;
import com.martin.ecommerce.springecommerce.api.model.LoginResponse;
import com.martin.ecommerce.springecommerce.api.model.RegistrationBody;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.exceptions.EmailFailureException;
import com.martin.ecommerce.springecommerce.exceptions.UserAlreadyExistsException;
import com.martin.ecommerce.springecommerce.exceptions.UserNotVerifiedException;
import com.martin.ecommerce.springecommerce.services.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){
        try {
            String jwt = userService.registerUser(registrationBody);
            System.out.println("Usuario Creado");

            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);

            return ResponseEntity.ok(response);




//            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException e) {
            System.out.println("Problemas con la creacion del usuario");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            System.out.println("Problemas con la verifiacion por correo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIED";
            if(ex.isNewEmailSent()){
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }   
        if (jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);

            return ResponseEntity.ok(response);

        }
    }

    @GetMapping("/me")
    @CrossOrigin("http://localhost:3000")
    public LocalUser getLoggedInUSerProfile(@AuthenticationPrincipal LocalUser user){
        System.out.println(user);
        return user;
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        if (userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        }catch (EmailNotFoundException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }

}
    




