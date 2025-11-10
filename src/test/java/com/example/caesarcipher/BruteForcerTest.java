package com.example.caesarcipher;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Comparator;

class BruteForcerTest {

    private final CaesarCipherLogic cipherLogic = new CaesarCipherLogic();
    private final BruteForcer bruteForcer = new BruteForcer(cipherLogic);

    @Test
    void testCalculateScoreEnglish() {
        String text = "this is a test";
        double score = bruteForcer.calculateFrequencyScore(text, "en", true);
        assertTrue(score < 0.1);
    }

    @Test
    void testCalculateScoreEnglishHighFrequency() {
        String text = "eeeeeeeeeeeeeeee";
        double score = bruteForcer.calculateFrequencyScore(text, "en", true);
        assertTrue(score > 0.3);
    }

    @Test
    void testCalculateScoreRussian() {
        String text = "это русский текст";
        double score = bruteForcer.calculateFrequencyScore(text, "ru", true);
        assertTrue(score < 0.1);
    }

    @Test
    void testPerformBruteForceEnglish() {
        String originalText = "secret message";
        int shift = 7;
        String encryptedText = cipherLogic.encrypt(originalText, shift, "en", true);

        List<BruteForcer.AnalysisResult> results = bruteForcer.performBruteForceAndAnalysis(encryptedText, "en", true);

        BruteForcer.AnalysisResult bestResult = results.get(0);

        assertEquals(shift, bestResult.getShift());
        assertEquals(originalText.toLowerCase(), bestResult.getDecryptedText().toLowerCase());
    }

    @Test
    void testPerformBruteForceRussian() {
        String originalText = "секретное сообщение";
        int shift = 10;
        String encryptedText = cipherLogic.encrypt(originalText, shift, "ru", true);

        List<BruteForcer.AnalysisResult> results = bruteForcer.performBruteForceAndAnalysis(encryptedText, "ru", true);

        BruteForcer.AnalysisResult bestResult = results.get(0);

        assertEquals(shift, bestResult.getShift());
        assertEquals(originalText, bestResult.getDecryptedText().toLowerCase());
    }

    @Test
    void testPerformBruteForceWithNonAlphabetic() {
        String originalText = "secret message 123!";
        int shift = 4;
        String encryptedText = cipherLogic.encrypt(originalText, shift, "en", true);

        List<BruteForcer.AnalysisResult> results = bruteForcer.performBruteForceAndAnalysis(encryptedText, "en", true);
        BruteForcer.AnalysisResult bestResult = results.get(0);

        assertEquals(shift, bestResult.getShift());
        assertEquals(originalText.toLowerCase(), bestResult.getDecryptedText().toLowerCase());
    }
}