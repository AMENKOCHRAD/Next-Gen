package edu.pidev3a8.entities;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;

import java.io.IOException;

public class DialogflowClient {
    private static final String PROJECT_ID = "gestionreclamationsbot"; // Remplace par ton ID de projet Google Cloud
    private static final String SESSION_ID = "une-session-unique"; // ID de session unique

    public String detectIntent(String text) throws IOException {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Crée une session pour l'utilisateur
            SessionName session = SessionName.of(PROJECT_ID, SESSION_ID);

            // Construit la requête
            Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode("fr");
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Envoie la requête à Dialogflow
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();

            // Retourne la réponse du chatbot
            return queryResult.getFulfillmentText();
        }
    }
}
