package com.DogWalker.dog.walker.servicio;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendHtmlEmail(String to, String subject, String contrasena) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(getEmailTemplate(contrasena), true);
        emailSender.send(message);
    }

    private String getEmailTemplate(String contrasena) throws IOException {
        // Cargar la plantilla HTML desde el archivo
        Resource resource = new ClassPathResource("templates/emailrecupercionContraseña.html");
        Path path = Paths.get(resource.getURI());
        byte[] encoded = Files.readAllBytes(path);
        String emailTemplate = new String(encoded, StandardCharsets.UTF_8);

        // Reemplazar el marcador de posición [contrasena] con la contraseña generada
        return emailTemplate.replace("[contrasena]", contrasena);
    }
}