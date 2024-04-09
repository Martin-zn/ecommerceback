package com.martin.ecommerce.springecommerce.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.api.model.LoginBody;
import com.martin.ecommerce.springecommerce.api.model.RegistrationBody;
import com.martin.ecommerce.springecommerce.entities.LocalUser;
import com.martin.ecommerce.springecommerce.entities.VerificationToken;
import com.martin.ecommerce.springecommerce.exceptions.EmailFailureException;
import com.martin.ecommerce.springecommerce.exceptions.UserAlreadyExistsException;
import com.martin.ecommerce.springecommerce.exceptions.UserNotVerifiedException;
import com.martin.ecommerce.springecommerce.repositories.UserRepository;
import com.martin.ecommerce.springecommerce.repositories.VerificationTokenRepository;

import jakarta.transaction.Transactional;



@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;


    //Metodo para registrar un usuario
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException{

        //primero verifico si es que el correo o el nombre de usuario esta registrado, de ser asi no podra registrarse
        if (userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() || userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        //Construyo un user
        LocalUser user = new LocalUser();
        //Agrego sus atributos
        user.setEmail(registrationBody.getEmail());
        user.setName(registrationBody.getFirstName());
        user.setLastname(registrationBody.getLastName());
        user.setUsername(registrationBody.getUsername());
        //Encripto la contraseña antes de guardarla
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        //Con el metodo de abajo creo el token de verificacion
        VerificationToken verificationToken = createVerificationToken(user);
        //Con los metodos creados en el emailservice, envio el correo
        emailService.sendVerificationEmail(verificationToken);

        return userRepository.save(user);
    }

    //Creare un objeto Verification token
    private VerificationToken createVerificationToken(LocalUser user){
        //creo una instancia
        VerificationToken verificationToken = new VerificationToken();
        //Contruyo el Verification token, agregando sus atributos
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        //Finalmente agrego el verification token a la lista de verifications tokens del ususario
        user.getVerificationTokens().add(verificationToken);

        //Retorno el token de verificacion
        return verificationToken;
    }



//Metodo para login 
    public String loginUser(LoginBody loginBody) throws EmailFailureException, UserNotVerifiedException{

        Optional<LocalUser> opUser = userRepository.findByUsernameIgnoreCase(loginBody.getUsername());

        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                if(user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                }else{
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();

                    boolean resend = verificationTokens.size() == 0 || verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60*60*1000)));

                    if (resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenRepository.findByToken(token);

        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();

            if (!user.isEmailVerified()) {
                user.setEmailVerified(true);
                userRepository.save(user);
                verificationTokenRepository.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

}
