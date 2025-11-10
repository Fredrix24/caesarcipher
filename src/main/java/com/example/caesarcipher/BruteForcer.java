package com.example.caesarcipher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BruteForcer {

    private CaesarCipherLogic cipherLogic;

    private static final Map<Character, Double> RUSSIAN_LETTER_FREQUENCIES = new HashMap<>();
    static {
        RUSSIAN_LETTER_FREQUENCIES.put('о', 0.1098);
        RUSSIAN_LETTER_FREQUENCIES.put('е', 0.0834);
        RUSSIAN_LETTER_FREQUENCIES.put('а', 0.0799);
        RUSSIAN_LETTER_FREQUENCIES.put('и', 0.0737);
        RUSSIAN_LETTER_FREQUENCIES.put('н', 0.0670);
        RUSSIAN_LETTER_FREQUENCIES.put('т', 0.0632);
        RUSSIAN_LETTER_FREQUENCIES.put('с', 0.0547);
        RUSSIAN_LETTER_FREQUENCIES.put('р', 0.0474);
        RUSSIAN_LETTER_FREQUENCIES.put('в', 0.0453);
        RUSSIAN_LETTER_FREQUENCIES.put('л', 0.0431);
        RUSSIAN_LETTER_FREQUENCIES.put('к', 0.0362);
        RUSSIAN_LETTER_FREQUENCIES.put('м', 0.0314);
        RUSSIAN_LETTER_FREQUENCIES.put('д', 0.0298);
        RUSSIAN_LETTER_FREQUENCIES.put('п', 0.0291);
        RUSSIAN_LETTER_FREQUENCIES.put('у', 0.0262);
        RUSSIAN_LETTER_FREQUENCIES.put('я', 0.0255);
        RUSSIAN_LETTER_FREQUENCIES.put('ь', 0.0220);
        RUSSIAN_LETTER_FREQUENCIES.put('г', 0.0192);
        RUSSIAN_LETTER_FREQUENCIES.put('ч', 0.0189);
        RUSSIAN_LETTER_FREQUENCIES.put('з', 0.0184);
        RUSSIAN_LETTER_FREQUENCIES.put('й', 0.0162);
        RUSSIAN_LETTER_FREQUENCIES.put('х', 0.0141);
        RUSSIAN_LETTER_FREQUENCIES.put('б', 0.0148);
        RUSSIAN_LETTER_FREQUENCIES.put('ц', 0.0106);
        RUSSIAN_LETTER_FREQUENCIES.put('ж', 0.0094);
        RUSSIAN_LETTER_FREQUENCIES.put('ю', 0.0072);
        RUSSIAN_LETTER_FREQUENCIES.put('э', 0.0035);
        RUSSIAN_LETTER_FREQUENCIES.put('щ', 0.0033);
        RUSSIAN_LETTER_FREQUENCIES.put('ш', 0.0030);
        RUSSIAN_LETTER_FREQUENCIES.put('ф', 0.0027);
        RUSSIAN_LETTER_FREQUENCIES.put('ъ', 0.0004);
    }

    private static final Map<Character, Double> ENGLISH_LETTER_FREQUENCIES = new HashMap<>();
    static {
        ENGLISH_LETTER_FREQUENCIES.put('e', 0.1270);
        ENGLISH_LETTER_FREQUENCIES.put('t', 0.0906);
        ENGLISH_LETTER_FREQUENCIES.put('a', 0.0817);
        ENGLISH_LETTER_FREQUENCIES.put('o', 0.0751);
        ENGLISH_LETTER_FREQUENCIES.put('i', 0.0697);
        ENGLISH_LETTER_FREQUENCIES.put('n', 0.0675);
        ENGLISH_LETTER_FREQUENCIES.put('s', 0.0633);
        ENGLISH_LETTER_FREQUENCIES.put('h', 0.0609);
        ENGLISH_LETTER_FREQUENCIES.put('r', 0.0599);
        ENGLISH_LETTER_FREQUENCIES.put('d', 0.0425);
        ENGLISH_LETTER_FREQUENCIES.put('l', 0.0407);
        ENGLISH_LETTER_FREQUENCIES.put('c', 0.0278);
        ENGLISH_LETTER_FREQUENCIES.put('u', 0.0276);
        ENGLISH_LETTER_FREQUENCIES.put('m', 0.0241);
        ENGLISH_LETTER_FREQUENCIES.put('w', 0.0236);
        ENGLISH_LETTER_FREQUENCIES.put('f', 0.0223);
        ENGLISH_LETTER_FREQUENCIES.put('g', 0.0202);
        ENGLISH_LETTER_FREQUENCIES.put('y', 0.0197);
        ENGLISH_LETTER_FREQUENCIES.put('p', 0.0193);
        ENGLISH_LETTER_FREQUENCIES.put('b', 0.0149);
        ENGLISH_LETTER_FREQUENCIES.put('v', 0.0099);
        ENGLISH_LETTER_FREQUENCIES.put('k', 0.0077);
        ENGLISH_LETTER_FREQUENCIES.put('x', 0.0019);
        ENGLISH_LETTER_FREQUENCIES.put('j', 0.0010);
        ENGLISH_LETTER_FREQUENCIES.put('q', 0.0010);
        ENGLISH_LETTER_FREQUENCIES.put('z', 0.0007);
    }

    public BruteForcer(CaesarCipherLogic cipherLogic) {
        this.cipherLogic = cipherLogic;
    }

    public List<AnalysisResult> performBruteForceAndAnalysis(String encryptedText, String alphabet, boolean caseSensitive) {
        List<AnalysisResult> results = new ArrayList<>();
        String currentAlphabet = alphabet.equalsIgnoreCase("ru") ? cipherLogic.getRussianAlphabet() : cipherLogic.getEnglishAlphabet();
        int alphabetLength = currentAlphabet.length();

        for (int shift = 0; shift < alphabetLength; shift++) {
            String decryptedText = cipherLogic.decrypt(encryptedText, shift, alphabet, caseSensitive);

            double score = calculateFrequencyScore(decryptedText, alphabet, caseSensitive);
            results.add(new AnalysisResult(shift, decryptedText, score));
        }
        results.sort(Comparator.comparingDouble(AnalysisResult::getScore).reversed());
        return results;
    }

    public double calculateFrequencyScore(String text, String alphabet, boolean caseSensitive) {
        if (text == null || text.isEmpty()) return 0.0;

        Map<Character, Integer> textFrequencies = new HashMap<>();
        int letterCount = 0;

        String alphabetToUse;
        Map<Character, Double> targetFrequencies;

        if (alphabet.equalsIgnoreCase("ru")) {
            alphabetToUse = cipherLogic.getRussianAlphabet();
            targetFrequencies = RUSSIAN_LETTER_FREQUENCIES;
        } else {
            alphabetToUse = cipherLogic.getEnglishAlphabet();
            targetFrequencies = ENGLISH_LETTER_FREQUENCIES;
        }
        String processedText = caseSensitive ? text : text.toLowerCase();

        for (char character : processedText.toCharArray()) {
            if (alphabetToUse.indexOf(Character.toLowerCase(character)) != -1) {
                char normalizedChar = Character.toLowerCase(character);
                textFrequencies.put(normalizedChar, textFrequencies.getOrDefault(normalizedChar, 0) + 1);
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