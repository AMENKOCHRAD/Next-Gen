package edu.pidev3a8.entities;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class StanfordSentimentAnalyzer {
    private static final StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public static String analyzeSentiment(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        int totalSentiment = 0;
        int count = 0;

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            totalSentiment += mapSentimentToScore(sentiment);
            count++;
        }

        return getOverallSentiment(totalSentiment / count);
    }

    private static int mapSentimentToScore(String sentiment) {
        return switch (sentiment) {
            case "Very Negative" -> 0;
            case "Negative" -> 1;
            case "Neutral" -> 2;
            case "Positive" -> 3;
            case "Very Positive" -> 4;
            default -> 2;
        };
    }

    private static String getOverallSentiment(int score) {
        return switch (score) {
            case 0 -> "Très négatif";
            case 1 -> "Négatif";
            case 2 -> "Neutre";
            case 3 -> "Positif";
            case 4 -> "Très positif";
            default -> "Inconnu";
        };
    }
}
