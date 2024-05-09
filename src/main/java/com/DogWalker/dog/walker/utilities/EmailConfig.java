package com.DogWalker.dog.walker.utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // Cambia esto por el host de tu servidor SMTP
        mailSender.setPort(587); // Cambia esto por el puerto de tu servidor SMTP
        mailSender.setUsername("dogwallker1"); // Cambia esto por tu nombre de usuario de correo electrónico
        mailSender.setPassword("yourpassword"); // Cambia esto por tu contraseña de correo electrónico

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // Puedes cambiar esto a "false" en producción

        return mailSender;
    }
}