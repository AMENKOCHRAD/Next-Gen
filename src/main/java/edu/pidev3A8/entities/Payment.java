package edu.pidev3A8.entities;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;

import java.util.HashMap;
import java.util.Map;

public class Payment {
    public static void main(String[] args) {
        // Initialiser la clé API Stripe (mode test)
        Stripe.apiKey = "sk_test_51Qwp3zQ0S3cYY6GU7FwFfHEoPNqq0HR4cKj8TkxTnBsBab4HM9EpZkZElej63FZwLyKB12XufbUkqjyVI5LIRQ6a00zwDFAxaP";

        // Tester la récupération des informations du compte
        testAccountRetrieval();

        // Tester la récupération des charges (paiements)
        testChargeRetrieval();
    }

    /**
     * Méthode pour tester la récupération des informations du compte Stripe.
     */
    public static void testAccountRetrieval() {
        try {
            // Récupérer les informations du compte
            Account account = Account.retrieve();
            System.out.println("=== Account Information ===");
            System.out.println("Account ID: " + account.getId());
            System.out.println("Email: " + account.getEmail()); // Peut être null en mode test
            System.out.println("Country: " + account.getCountry());
            System.out.println("API is functional for account retrieval!");
        } catch (StripeException e) {
            System.err.println("API is NOT functional for account retrieval. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour tester la récupération des charges (paiements).
     */
    public static void testChargeRetrieval() {
        try {
            // Paramètres pour récupérer les charges (limite à 3 pour cet exemple)
            Map<String, Object> params = new HashMap<>();
            params.put("limit", 3);

            // Récupérer les charges
            ChargeCollection charges = Charge.list(params);
            System.out.println("\n=== Charges Information ===");
            for (Charge charge : charges.getData()) {
                System.out.println("Charge ID: " + charge.getId());
                System.out.println("Amount: " + charge.getAmount());
                System.out.println("Currency: " + charge.getCurrency());
                System.out.println("Status: " + charge.getStatus());
                System.out.println("-----------------------------");
            }
            System.out.println("API is functional for charge retrieval!");
        } catch (StripeException e) {
            System.err.println("API is NOT functional for charge retrieval. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}