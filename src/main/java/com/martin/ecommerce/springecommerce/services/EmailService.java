package com.martin.ecommerce.springecommerce.services;

import com.martin.ecommerce.springecommerce.entities.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.martin.ecommerce.springecommerce.entities.VerificationToken;
import com.martin.ecommerce.springecommerce.exceptions.EmailFailureException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);

        return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException{
        SimpleMailMessage message = makeMailMessage();

        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verificacion de email para verificacion de cuenta");
        message.setText("Entra al link para verificar tu cuenta. \n" + url +"/auth/verify?token=" + verificationToken.getToken());
        try{
            javaMailSender.send(message);
        }catch(MailException ex){
            throw new EmailFailureException();
        }
    }

    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException{
        SimpleMailMessage message = makeMailMessage();

        message.setTo(user.getEmail());
        message.setSubject(("Verificacion de Cambio de contraseña"));
        message.setText("Entra al link para cambiar tu contraseña \n" + url +"/auth/reset?token="+token);

        try{
            javaMailSender.send(message);
        }catch(MailException ex){
                throw new EmailFailureException();
        }
    }
}
