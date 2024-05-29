package com.martin.ecommerce.springecommerce.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.martin.ecommerce.springecommerce.api.model.PasswordResetBody;
import com.martin.ecommerce.springecommerce.entities.RoleEntity;
import com.martin.ecommerce.springecommerce.entities.RoleEnum;
import com.martin.ecommerce.springecommerce.exceptions.EmailNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;


    //Metodo para registrar un usuario
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException{

        //primero verifico si es que el correo o el nombre de usuario esta registrado, de ser asi no podra registrarse
        if (userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() || userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();//Creo un user vacio

        user.setEmail(registrationBody.getEmail());
        user.setName(registrationBody.getFirstName());
        user.setLastname(registrationBody.getLastName());
        user.setUsername(registrationBody.getUsername());
//        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));//Encripto la contraseña antes de guardarla
        String encryptedPassword = passwordEncoder.encode(registrationBody.getPassword());
        user.setPassword(encryptedPassword);

        Set<RoleEntity> roles = registrationBody.getRoles().stream()//creo un set de roles, esto se hace ya que vienen modo string y lo transformo a la entidad Role
                .map(role -> RoleEntity.builder()
                        .name(RoleEnum.valueOf(role))
                        .build())
                .collect(Collectors.toSet());

        user.setRoles(roles);

        VerificationToken verificationToken = createVerificationToken(user);//Creo un token de verificacion

        emailService.sendVerificationEmail(verificationToken);//Envio el Token via correo

        return userRepository.save(user);//Guardo el user
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

    public void forgotPassword(String email) throws EmailFailureException, EmailNotFoundException {
        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);
        }else{
            throw new EmailNotFoundException();
        }
    }
        //Metodo para resetear la contraseña de un usuario
    public void resetPassword(PasswordResetBody body){
        //Con la funcion llamada del jwt service, extraemos el email del token
        String email = jwtService.getResetPasswordEmail(body.getToken());

        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(email);
        if (opUser.isPresent()){
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            userRepository.save(user);
        }

    }

    public boolean userHasPermissionToUser(LocalUser user, Long id){
        return user.getId() == id;
    }


}
