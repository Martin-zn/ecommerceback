package com.martin.ecommerce.springecommerce.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.api.model.LoginBody;
import com.martin.ecommerce.springecommerce.api.model.RegistrationBody;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.exceptions.UserAlreadyExistsException;
import com.martin.ecommerce.springecommerce.repositories.UserRepository;



@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;


    //Metodo para registrar un usuario

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException{

        if (userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() || userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();

        user.setEmail(registrationBody.getEmail());
        user.setName(registrationBody.getFirstName());
        user.setLastname(registrationBody.getLastName());
        user.setUsername(registrationBody.getUsername());
        //Encripto la contraseña antes de guardarla
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        return userRepository.save(user);
        
    }

    public String loginUser(LoginBody loginBody){

        Optional<LocalUser> opUser = userRepository.findByUsernameIgnoreCase(loginBody.getUsername());

        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                return jwtService.generateJWT(user);
            }
        }

        return null;
    }
}
