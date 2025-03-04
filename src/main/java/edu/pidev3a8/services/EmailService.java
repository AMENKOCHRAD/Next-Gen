package edu.pidev3a8.services;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    private static final String STATIC_EMAIL = "doghmenesabee@gmail.com";
    private static final String HOST = "smtp.gmail.com";
    private static final String FROM_EMAIL = "hbibbensalem32@gmail.com";
    private static final String PASSWORD = "bdft ildt vusm nczg";

    public static void sendTreatmentNotification(String status, String comment) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Activation de STARTTLS
        props.put("mail.smtp.host", HOST); // Serveur SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port SMTP sécurisé de Gmail

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(STATIC_EMAIL));
            message.setSubject("[Réclamation Traitée] Mise à jour du Statut");
            message.setText("Statut : " + status + "\nCommentaire : " + comment);

            Transport.send(message);
            System.out.println("E-mail envoyé à : " + STATIC_EMAIL);
        } catch (MessagingException e) {
            System.err.println("Erreur SMTP : " + e.getMessage());
        }
    }
}