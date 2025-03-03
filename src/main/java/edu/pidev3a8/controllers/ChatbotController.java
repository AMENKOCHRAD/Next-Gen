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
import java.util.regex.Pattern;

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
        String message = userMessage.toLowerCase();

        // Ajout des motifs de reconnaissance pour les réclamations
        boolean isReclamationContext = message.contains("reclam")
                || message.contains("réclam")
                || message.contains("document")
                || message.contains("papier")
                || message.contains("statut")
                || message.contains("status")
                || message.contains("temps")
                || message.contains("délai")
                || message.contains("modif")
                || message.contains("annul");
        if (isReclamationContext) {
            return handleReclamationQuestions(message);
        } else if (message.contains("bonjour") || message.contains("salut") || message.contains("coucou")) {
            return "Bonjour ! 😊 Comment puis-je vous aider concernant votre réclamation aujourd'hui ?";
        } else if (message.contains("merci") || message.contains("remercie")) {
            return "Je vous en prie ! Pour toute autre question sur les réclamations, n'hésitez pas à demander. 😊";
        } else if (message.contains("problème technique") || message.contains("bug") || message.contains("plante")) {
            return handleTechnicalIssues(message);
        } else {
            return "Pour une meilleure assistance, veuillez formuler votre question en rapport avec les réclamations. Exemples :\n"
                    + "- Comment suivre ma réclamation ?\n"
                    + "- Quel est le délai de traitement ?\n"
                    + "- Quels documents fournir ?";
        }
    }

    private String handleReclamationQuestions(String message) {
        if (message.contains("statut") || message.contains("status")) {
            return "Pour vérifier le statut de votre réclamation :\n"
                    + "1. Accédez à l'onglet 'Mes Réclamations'\n"
                    + "2. Sélectionnez la réclamation concernée\n"
                    + "3. Le statut s'affiche en temps réel (En cours/Traitée/En attente)\n"
                    + "Vous recevrez également une notification par email à chaque mise à jour ! 📧";

        } else if (message.contains("temps") || message.contains("délai")) {
            return "Le délai de traitement moyen est de 3 à 5 jours ouvrables. ⏳\n"
                    + "Pour les cas complexes (ex : demande de remboursement), cela peut prendre jusqu'à 10 jours.\n"
                    + "Vous pouvez suivre l'avancement dans votre espace personnel.";

        } else if (message.contains("document") || message.contains("papier")) {
            return "Documents nécessaires pour une réclamation :\n"
                    + "✅ Pièce d'identité\n"
                    + "✅ Facture/Preuve d'achat\n"
                    + "✅ Photos/vidéos du problème\n"
                    + "✅ Description détaillée\n"
                    + "Les formats acceptés : PDF, JPG, PNG (taille max 5MB)";

        } else if (message.contains("modifier") || message.contains("changer")) {
            return "Pour modifier une réclamation :\n"
                    + "1. Allez dans 'Mes Réclamations'\n"
                    + "2. Cliquez sur 'Modifier' (si statut 'En attente')\n"
                    + "3. Soumettez les nouvelles informations\n"
                    + "⚠️ Impossible de modifier après le début du traitement";

        } else if (message.contains("annuler") || message.contains("supprimer")) {
            return "Annulation de réclamation possible uniquement si :\n"
                    + "- Le traitement n'a pas encore commencé\n"
                    + "- Vous fournissez une raison valable\n"
                    + "Contactez notre support via le formulaire de contact pour plus d'assistance 📞";

        } else if (message.contains("email") || message.contains("confirmation")) {
            return "Un email de confirmation est automatiquement envoyé :\n"
                    + "- À la création de réclamation\n"
                    + "- À chaque changement de statut\n"
                    + "- À la clôture du dossier\n"
                    + "Vérifiez vos spams si vous ne l'avez pas reçu !";

        } else if (message.contains("refus") || message.contains("rejet")) {
            return "En cas de réclamation rejetée :\n"
                    + "1. Consultez les motifs dans la notification\n"
                    + "2. Vous pouvez :\n"
                    + "   a) Fournir des éléments complémentaires\n"
                    + "   b) Faire un recours (dans 15 jours)\n"
                    + "   c) Contacter un médiateur";

        } else if (message.contains("urgence") || message.contains("prioritaire")) {
            return "Service de traitement prioritaire disponible pour :\n"
                    + "🔴 Problèmes de sécurité\n"
                    + "🔴 Erreurs de facturation importantes\n"
                    + "🔴 Membres Premium\n"
                    + "Délai réduit à 48h (sur justification)";

        } else {
            return "Je comprends que vous avez une question sur les réclamations. Pour mieux vous aider :\n"
                    + "- Décrivez précisément votre problème\n"
                    + "- Mentionnez le numéro de réclamation\n"
                    + "- Précisez ce que vous souhaitez savoir";
        }
    }

    private String handleTechnicalIssues(String message) {
        if (message.contains("connexion")) {
            return "Problème de connexion ? Essayez :\n"
                    + "1. Vérifiez votre connexion Internet 🌐\n"
                    + "2. Redémarrez l'application\n"
                    + "3. Réinitialisez votre mot de passe\n"
                    + "Si le problème persiste, contactez-nous par téléphone au 01 23 45 67 89";
        } else if (message.contains("cache")) {
            return "Pour vider le cache :\n"
                    + "1. Allez dans Paramètres > Stockage\n"
                    + "2. Sélectionnez 'Nettoyer le cache'\n"
                    + "3. Redémarrez l'application\n"
                    + "⚠️ Cela ne supprimera pas vos données personnelles";
        } else {
            return "Pour les problèmes techniques complexes :\n"
                    + "1. Notez le code d'erreur (ex : ERR-456)\n"
                    + "2. Faites une capture d'écran\n"
                    + "3. Contactez notre support technique via le formulaire dédié";
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