package edu.pidev3A8.entities;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static void sendEmail(String toEmail, String subject, String body) {
        // Forcer l'utilisation de TLS 1.2
        System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        System.setProperty("mail.smtp.ssl.ciphersuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");

        // Propriétés SMTP pour Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Serveur SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port SMTP pour Gmail
        props.put("mail.smtp.auth", "true"); // Authentification requise
        props.put("mail.smtp.starttls.enable", "true"); // Activation de TLS
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Faire confiance au serveur SMTP de Gmail

        // Authentification avec le mot de passe d'application
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("maramomezzine1@gmail.com", "zgub ezju rotd fstg");
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("maramomezzine1@gmail.com")); // Expéditeur
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Destinataire
            message.setSubject(subject); // Sujet de l'e-mail
            message.setText(body); // Corps de l'e-mail

            // Envoi de l'e-mail
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            e.printStackTrace();
        }
    }
}