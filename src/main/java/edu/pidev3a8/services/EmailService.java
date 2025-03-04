package edu.pidev3a8.services;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    public static void sendResetEmail(String toEmail, String token) {
        // Configuration du serveur SMTP
        String host = "smtp.gmail.com"; // Serveur SMTP de Gmail
        String fromEmail = "hbibbensalem32@gmail.com"; // Votre adresse Gmail
        String password = "bdft ildt vusm nczg"; // Mot de passe d'application

        // Propriétés pour la session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Créer une session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Créer le message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Changement de La statut ");
            message.setText("Voici votre token de réinitialisation : " + token);

            // Envoyer le message
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à : " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}