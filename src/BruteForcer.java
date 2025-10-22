import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class BruteForcer {

    private CaesarCipherLogic cipherLogic;
    private static final Map<Character, Double> RUSSIAN_LETTER_FREQUENCIES = new HashMap<>();
    static {
        RUSSIAN_LETTER_FREQUENCIES.put('о', 0.1098);
        RUSSIAN_LETTER_FREQUENCIES.put('е', 0.0834);
        RUSSIAN_LETTER_FREQUENCIES.put('а', 0.0799);
    }

    private static final Map<Character, Double> ENGLISH_LETTER_FREQUENCIES = new HashMap<>();
    static {
        ENGLISH_LETTER_FREQUENCIES.put('e', 0.1270);
        ENGLISH_LETTER_FREQUENCIES.put('t', 0.0906);
        ENGLISH_LETTER_FREQUENCIES.put('a', 0.0817);
    }

    public BruteForcer(CaesarCipherLogic cipherLogic) {
        this.cipherLogic = cipherLogic;
    }

    public List<AnalysisResult> performBruteForceAndAnalysis(String encryptedText, String alphabet, boolean caseSensitive) {
        List<AnalysisResult> results = new ArrayList<>();
        int alphabetLength = (alphabet.equalsIgnoreCase("ru")) ? cipherLogic.getRussianAlphabet().length() : cipherLogic.getEnglishAlphabet().length();

        for (int shift = 0; shift < alphabetLength; shift++) {
            String decryptedText = cipherLogic.decrypt(encryptedText, shift, alphabet, caseSensitive);
            double score = calculateFrequencyScore(decryptedText, alphabet, caseSensitive);
            results.add(new AnalysisResult(shift, decryptedText, score));
        }
        results.sort(Comparator.comparingDouble(AnalysisResult::getScore).reversed());
        return results;
    }

    private double calculateFrequencyScore(String text, String alphabet, boolean caseSensitive) {
        if (text == null || text.isEmpty()) return 0.0;
        Map<Character, Integer> textFrequencies = new HashMap<>();
        int letterCount = 0;
        String alphabetToUse = alphabet.equalsIgnoreCase("ru") ? cipherLogic.getRussianAlphabet() : cipherLogic.getEnglishAlphabet();
        Map<Character, Double> targetFrequencies = alphabet.equalsIgnoreCase("ru") ? RUSSIAN_LETTER_FREQUENCIES : ENGLISH_LETTER_FREQUENCIES;

        for (char character : text.toLowerCase().toCharArray()) {
            if (alphabetToUse.indexOf(character) != -1) {
                textFrequencies.put(character, textFrequencies.getOrDefault(character, 0) + 1);
                letterCount++;
            }
        }
        if (letterCount == 0) return 0.0;
        double score = 0.0;
        for (Map.Entry<Character, Double> entry : targetFrequencies.entrySet()) {
            double targetFrequency = entry.getValue();
            double actualFrequency = (double) textFrequencies.getOrDefault(entry.getKey(), 0) / letterCount;
            score += Math.pow(targetFrequency - actualFrequency, 2);
        }
        return 1.0 / (1.0 + score);
    }

    public static class AnalysisResult {
        private int shift;
        private String decryptedText;
        private double score;
        public AnalysisResult(int shift, String decryptedText, double score) {
            this.shift = shift;
            this.decryptedText = decryptedText;
            this.score = score;
        }
        public int getShift() {
            return shift;
        }
        public double getScore() {
            return score;
        }
        public String getDecryptedText() {
            return decryptedText;
        }
    }
}