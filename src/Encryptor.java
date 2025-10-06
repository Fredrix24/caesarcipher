import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Encryptor {
    private static final String ENG_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ENG_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String RUS_UPPER = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String RUS_LOWER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public static void main(String[] args) {
        String inputFile = "plaintext.txt";
        String outputFile = "encrypted.txt";
        int shift;

        shift = getShiftFromUser("Введите ключ шифрования (число): ");
        if (shift == Integer.MIN_VALUE) {
            System.err.println("Не удалось получить корректный ключ. Завершение программы.");
            return;
        }

        try {
            String originalText = readFile(inputFile);
            System.out.println("Читаем из " + inputFile + "...");

            String encryptedText = encrypt(originalText, shift);
            System.out.println("Шифрование выполнено.");

            writeFile(encryptedText, outputFile);
            System.out.println("Результат записан в " + outputFile);

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Что-то пошло не так: " + e.getMessage());
        }
    }

    private static int getShiftFromUser(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String inputLine = scanner.nextLine();

        try {
            int shiftVal = Integer.parseInt(inputLine);
            scanner.close();
            return shiftVal;
        } catch (NumberFormatException e) {
            System.err.println("Некорректный ввод! Ключ должен быть числом.");
            scanner.close();
            return Integer.MIN_VALUE;
        }
    }

    private static String encrypt(String text, int shift) {
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            String alphabetToUse = null;
            int alphabetSize = 0;

            if (c >= 'a' && c <= 'z') {
                alphabetToUse = ENG_LOWER;
                alphabetSize = ENG_UPPER.length();
            } else if (c >= 'A' && c <= 'Z') {
                alphabetToUse = ENG_UPPER;
                alphabetSize = ENG_UPPER.length();
            } else if (c >= 'а' && c <= 'я' || c == 'ё') {
                alphabetToUse = RUS_LOWER;
                alphabetSize = RUS_UPPER.length();
            } else if (c >= 'А' && c <= 'Я' || c == 'Ё') {
                alphabetToUse = RUS_UPPER;
                alphabetSize = RUS_UPPER.length();
            }

            if (alphabetToUse != null) {
                int originalPos = alphabetToUse.indexOf(c);

                int newPos = (originalPos + shift) % alphabetSize;
                if (newPos < 0) {
                    newPos += alphabetSize;
                }
                char newC = alphabetToUse.charAt(newPos);
                res.append(newC);
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private static void writeFile(String text, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(text);
        }
    }
}