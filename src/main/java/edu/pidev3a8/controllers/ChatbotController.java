package edu.pidev3a8.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatbotController {
    @FXML
    private ListView<String> chatListView; // Pour afficher les messages de la conversation actuelle
    @FXML
    private ListView<String> historyListView; // Pour afficher l'historique des conversations
    @FXML
    private TextField userInput; // Champ de saisie de l'utilisateur

    private List<List<String>> conversationHistory = new ArrayList<>(); // Stocke toutes les conversations
    private List<String> currentConversation = new ArrayList<>(); // Stocke la conversation actuelle

    @FXML
    public void initialize() {
        // Message de bienvenue automatique au démarrage
        handleNewChat();

        // Configurer le ListView pour afficher les messages à droite (utilisateur) et à gauche (chatbot)
        chatListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item);
                            if (item.startsWith("Vous: ")) {
                                setStyle("-fx-background-color: #DCF8C6; -fx-padding: 5px; -fx-background-radius: 10px;");
                                setTextAlignment(TextAlignment.RIGHT);
                            } else {
                                setStyle("-fx-background-color: #ECECEC; -fx-padding: 5px; -fx-background-radius: 10px;");
                                setTextAlignment(TextAlignment.LEFT);
                            }
                        } else {
                            setText(null);
                            setStyle(null);
                        }
                    }
                };
            }
        });

        // Envoyer un message en appuyant sur "Entrée"
        userInput.setOnAction(event -> handleSendMessage());

        // Gérer la sélection d'une conversation dans l'historique
        historyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadConversation(newValue);
            }
        });
    }

    // Méthode pour gérer le bouton "Nouveau Chat"
    @FXML
    public void handleNewChat() {
        if (!currentConversation.isEmpty()) {
            conversationHistory.add(new ArrayList<>(currentConversation)); // Sauvegarder la conversation
            conversationDates.add(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())); // Stocker la date exacte
            updateHistoryListView(); // Mettre à jour la liste d'historique
        }

        // Vider la conversation actuelle
        currentConversation.clear();
        chatListView.getItems().clear();

        // Afficher un message de bienvenue
        simulateChatbotTyping("Salut, je suis un chatbot, je suis prêt pour t'aider. 😊");
    }

    @FXML
    public void handleSendMessage() {
        String userMessage = userInput.getText().trim();
        if (!userMessage.isEmpty()) {
            // Ajouter le message de l'utilisateur à la conversation actuelle
            currentConversation.add("Vous: " + userMessage);
            chatListView.getItems().add("Vous: " + userMessage);
            userInput.clear();

            // Simuler l'écriture du chatbot
            simulateChatbotTyping("...");

            // Utiliser un Timer pour simuler un délai avant la réponse du chatbot
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    String response = generateResponse(userMessage);

                    // Mettre à jour l'UI sur le FX Application Thread
                    Platform.runLater(() -> {
                        chatListView.getItems().remove("..."); // Supprimer les points de suspension
                        chatListView.getItems().add("Chatbot: " + response); // Ajouter la réponse du chatbot
                        currentConversation.add("Chatbot: " + response); // Ajouter la réponse à la conversation actuelle
                    });
                }
            }, 2000); // Délai de 2 secondes pour simuler l'écriture
        }
    }

    private void simulateChatbotTyping(String message) {
        Platform.runLater(() -> {
            chatListView.getItems().add(message);
            currentConversation.add(message);
        });
    }

    private String generateResponse(String userMessage) {
        // Logique de génération de réponse (similaire à l'exemple précédent)
        String message = userMessage.toLowerCase();

        if (message.contains("bonjour") || message.contains("salut") || message.contains("coucou")) {
            return "Bonjour ! 😊 Comment puis-je vous aider aujourd'hui ?";
        } else if (message.contains("merci") || message.contains("remercie")) {
            return "Je vous en prie ! N'hésitez pas à me demander si vous avez d'autres questions. 😊";
        } else if (message.contains("problème technique") || message.contains("bug") || message.contains("plante") || message.contains("connexion")) {
            return "Je suis désolé pour ce problème technique. Avez-vous essayé de redémarrer l'application ou de vider le cache ? Si le problème persiste, fournissez-moi plus de détails (message d'erreur, capture d'écran).";
        } else {
            return "Je ne comprends pas votre demande. Pouvez-vous reformuler ou me poser une question plus précise ?";
        }
    }

    // Mettre à jour la liste de l'historique des conversations
    private List<String> conversationDates = new ArrayList<>(); // Stocker les dates des conversations

    private void updateHistoryListView() {
        historyListView.getItems().clear(); // Nettoyer l'affichage
        for (int i = 0; i < conversationHistory.size(); i++) {
            historyListView.getItems().add("Conversation du " + conversationDates.get(i));
        }
    }


    private void loadConversation(String conversationTitle) {
        int index = historyListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < conversationHistory.size()) {
            chatListView.getItems().clear();
            chatListView.getItems().addAll(conversationHistory.get(index));
            currentConversation = new ArrayList<>(conversationHistory.get(index)); // Associer la conversation actuelle
        }
    }


}