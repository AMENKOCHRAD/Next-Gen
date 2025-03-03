package edu.pidev3a8.entities;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class SentimentAnalyzer {

    private static StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public static String analyzeSentiment(String text) {
        if (text == null || text.isEmpty()) return "MOYENNE"; // Valeur par défaut

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        int totalSentiment = 0;
        int sentenceCount = 0;

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            totalSentiment += mapSentimentToScore(sentiment);
            sentenceCount++;
        }

        int averageSentiment = totalSentiment / Math.max(1, sentenceCount);
        return classifyPriority(averageSentiment);
    }

    private static int mapSentimentToScore(String sentiment) {
        switch (sentiment) {
            case "Very Negative": return 0;
            case "Negative": return 1;
            case "Neutral": return 2;
            case "Positive": return 3;
            case "Very Positive": return 4;
            default: return 2;
        }
    }

    private static String classifyPriority(int score) {
        if (score <= 1) return "HAUTE";
        if (score == 2) return "MOYENNE";
        return "BASSE";
    }
}