package edu.pidev3a8.entities;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class PurgoMalumService {

    private static final String PURGOMALUM_API_URL = "https://www.purgomalum.com/service/plain";

    /**
     * Filtre les mots interdits dans un texte donné.
     *
     * @param text Le texte à filtrer.
     * @return Le texte filtré avec les mots interdits remplacés par ******.
     */
    public static String filterBadWords(String text) {
        try {
            // Encoder le texte pour l'URL
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            // Construire l'URL de l'API
            String apiUrl = PURGOMALUM_API_URL + "?text=" + encodedText + "&fill_char=*";

            // Afficher l'URL pour le débogage
            System.out.println("URL de l'API PurgoMalum : " + apiUrl);

            // Créer une requête HTTP GET
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            // Envoyer la requête et récupérer la réponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Afficher la réponse de l'API pour le débogage
            System.out.println("Réponse de l'API PurgoMalum : " + response.body());

            // Retourner le texte filtré
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'appel à l'API PurgoMalum : " + e.getMessage());
            return text; // Retourner le texte original en cas d'erreur
        }
    }}