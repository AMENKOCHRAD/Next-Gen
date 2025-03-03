package edu.pidev3A8.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class PaymentController {

    // Méthode pour traiter un paiement
    private void processPayment() {
        try {
            // Initialiser la clé secrète Stripe
            Stripe.apiKey = "sk_test_51Qwp3zQ0S3cYY6GU7FwFfHEoPNqq0HR4cKj8TkxTnBsBab4HM9EpZkZElej63FZwLyKB12XufbUkqjyVI5LIRQ6a00zwDFAxaP"; // Remplacez par votre clé secrète

            // Créer un PaymentIntent avec les détails du paiement
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(1000L) // Montant en cents (exemple : 1000 = 10.00 USD)
                    .setCurrency("usd") // Devise (USD dans cet exemple)
                    .build();

            // Créer le PaymentIntent via l'API Stripe
            PaymentIntent intent = PaymentIntent.create(params);

            // Si le paiement est réussi, afficher un message de succès
            System.out.println("Payment successful. PaymentIntent ID: " + intent.getId());
        } catch (StripeException e) {
            // En cas d'erreur, afficher le message d'erreur
            System.out.println("Payment failed. Error: " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace pour le débogage
        }
    }

    // Méthode principale pour tester le contrôleur
    public static void main(String[] args) {
        PaymentController controller = new PaymentController();
        controller.processPayment(); // Tester le processus de paiement
    }
}