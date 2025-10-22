import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CaesarCipherLogic {

    private final String russianAlphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private final String englishAlphabet = "abcdefghijklmnopqrstuvwxyz";

    public String encrypt(String text, int shift, String alphabet, boolean caseSensitive) {
        StringBuilder result = new StringBuilder();
        String currentAlphabet = (alphabet.equalsIgnoreCase("ru")) ? russianAlphabet : englishAlphabet;
        String textToProcess = (caseSensitive) ? text : text.toLowerCase();
        int alphabetLength = currentAlphabet.length();

        for (char character : textToProcess.toCharArray()) {
            int index = currentAlphabet.indexOf(character);

            if (index != -1) {
                int shiftedIndex = (index + shift % alphabetLength + alphabetLength) % alphabetLength;
                char shiftedChar = currentAlphabet.charAt(shiftedIndex);
                result.append(shiftedChar);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public String decrypt(String text, int shift, String alphabet, boolean caseSensitive) {
        return encrypt(text, -shift, alphabet, caseSensitive);
    }

    public String getRussianAlphabet() {
        return russianAlphabet;
    }

    public String getEnglishAlphabet() {
        return englishAlphabet;
    }

    public String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    public void writeFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String detectAlphabet(String text) {
        boolean hasRussian = false;
        boolean hasEnglish = false;

        String lowerCaseText = text.toLowerCase();

        for (char c : lowerCaseText.toCharArray()) {
            if (russianAlphabet.indexOf(c) != -1) {
                hasRussian = true;
            } else if (englishAlphabet.indexOf(c) != -1) {
                hasEnglish = true;
            }
        }

        if (hasRussian && hasEnglish) {
            return "mixed";
        } else if (hasRussian) {
            return "ru";
        } else if (hasEnglish) {
            return "en";
        } else {
            return null;
        }
    }
}