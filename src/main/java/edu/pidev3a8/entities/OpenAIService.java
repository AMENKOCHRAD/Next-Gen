package edu.pidev3a8.entities;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;

public class OpenAIService {
    private static final String API_KEY = "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";
    private OpenAiService service;

    public OpenAIService() {
        this.service = new OpenAiService(API_KEY);
    }

    public String getChatResponse(String prompt) {
        try {
            CompletionRequest completionRequest = CompletionRequest.builder()
                    .prompt(prompt)
                    .model("gpt-3.5-turbo") // ou "gpt-4" si disponible
                    .maxTokens(150)
                    .temperature(0.7) // Contrôle la créativité des réponses
                    .build();

            return service.createCompletion(completionRequest).getChoices().get(0).getText();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à l'API OpenAI : " + e.getMessage());
            return null; // Ou renvoie un message d'erreur personnalisé
        }
    }
}