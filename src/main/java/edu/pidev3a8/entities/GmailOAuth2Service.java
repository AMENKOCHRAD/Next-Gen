package edu.pidev3a8.entities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory; // Utilisation de GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

public class GmailOAuth2Service {
    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\amena\\Desktop\\Aaa.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens"; // Dossier pour stocker les tokens

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Charger les informations d'identification OAuth2
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), // Utilisation de GsonFactory
                new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH))
        );

        // Configurer le flux d'autorisation
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), clientSecrets, // Utilisation de GsonFactory
                Collections.singletonList(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // Ouvrir une fenêtre de connexion pour l'autorisation
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void sendEmail(String toEmail, String subject, String content) throws IOException, GeneralSecurityException, MessagingException {
        // Configurer le transport HTTP
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // Créer une session Gmail
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT)) // Utilisation de GsonFactory
                .setApplicationName("Votre Application")
                .build();

        // Créer et envoyer le message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("amenallah.kochrad@esprit.tn"));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject(subject);
        email.setText(content);

        // Convertir le message en format Base64
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer); // Écrire le MimeMessage dans le buffer
        byte[] bytes = buffer.toByteArray(); // Récupérer les bytes
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes); // Encoder en Base64

        Message message = new Message();
        message.setRaw(encodedEmail);

        // Envoyer l'e-mail
        service.users().messages().send("me", message).execute();
        System.out.println("E-mail envoyé avec succès !");
    }
}