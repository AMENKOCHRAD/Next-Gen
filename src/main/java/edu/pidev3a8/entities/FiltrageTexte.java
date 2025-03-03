package edu.pidev3a8.entities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FiltrageTexte {
    public static String filtrerTexte(String texte) {
        try {
            // Vérifier si le texte est vide ou null AVANT l'encodage
            if (texte == null || texte.trim().isEmpty()) {
                System.err.println("Le texte est vide, filtrage ignoré.");
                return "Texte invalide"; // Retourner un message clair
            }

            String encodedText = URLEncoder.encode(texte, "UTF-8");

            // Vérifier après encodage (au cas où l'encodage crée une chaîne vide)
            if (encodedText.trim().isEmpty()) {
                System.err.println("Le texte encodé est vide, filtrage ignoré.");
                return "Texte invalide";
            }

            System.out.println("Texte encodé envoyé à l'API : " + encodedText);

            String apiUrl = "https://www.purgomalum.com/service/json?text=" + encodedText;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String result = response.toString();

            // Vérifier si l'API renvoie une erreur
            if (result.contains("\"error\"")) {
                System.err.println("Erreur API reçue : " + result);
                return "Erreur lors du filtrage";
            }

            return result.replaceAll("\\{\"result\":\"(.*)\"\\}", "$1"); // Extraire le texte filtré
        } catch (Exception e) {
            e.printStackTrace();
            return texte; // Retourner le texte original en cas d'erreur
        }
    }

}
