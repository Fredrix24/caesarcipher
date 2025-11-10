package com.example.caesarcipher;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class CaesarCipherLogicTest {

    private final CaesarCipherLogic cipherLogic = new CaesarCipherLogic();

    @Test
    void testEncryptRussian() {
        String inputText = "абв";
        int shift = 3;
        String expectedText = "где";
        assertEquals(expectedText, cipherLogic.encrypt(inputText, shift, "ru", true), "Шифрование русского языка не соответствует ожидаемому.");
    }

    @Test
    void testEncryptEnglish() {
        String inputText = "Hello";
        int shift = 5;
        String expectedText = "Mjqqt";
        assertEquals(expectedText, cipherLogic.encrypt(inputText, shift, "en", true), "Шифрование английского языка не соответствует ожидаемому.");
    }

    @Test
    void testEncryptWithNonAlphabeticChars() {
        String inputText = "Hello, world 123!";
        int shift = 3;
        String expectedText = "Khoor, zruog 123!";
        assertEquals(expectedText, cipherLogic.encrypt(inputText, shift, "en", true), "Шифрование с неалфавитными символами не соответствует ожидаемому.");
    }

    @Test
    void testEncryptWithLargeShift() {
        String inputText = "abc";
        int shift = 29;
        String expectedText = "def";
        assertEquals(expectedText, cipherLogic.encrypt(inputText, shift, "en", true), "Шифрование с большим сдвигом не соответствует ожидаемому.");
    }

    @Test
    void testEncryptWithNegativeShift() {
        String inputText = "def";
        int shift = -3;
        String expectedText = "abc";
        assertEquals(expectedText, cipherLogic.encrypt(inputText, shift, "en", true), "Шифрование с отрицательным сдвигом не соответствует ожидаемому.");
    }

    @Test
    void testDecryptRussian() {
        String inputText = "где";
        int shift = 3;
        String expectedText = "абв";
        assertEquals(expectedText, cipherLogic.decrypt(inputText, shift, "ru", true), "Дешифрование русского языка не соответствует ожидаемому.");
    }

    @Test
    void testDecryptEnglish() {
        String inputText = "Mjqqt";
        int shift = 5;
        String expectedText = "Hello";
        assertEquals(expectedText, cipherLogic.decrypt(inputText, shift, "en", true), "Дешифрование английского языка не соответствует ожидаемому.");
    }

    @Test
    void testDetectAlphabetRussian() {
        String text = "Это русский текст.";
        assertEquals("ru", cipherLogic.detectAlphabet(text), "Обнаружение русского языка неверное.");
    }

    @Test
    void testDetectAlphabetEnglish() {
        String text = "This is an English text.";
        assertEquals("en", cipherLogic.detectAlphabet(text), "Обнаружение английского языка неверное.");
    }

    @Test
    void testDetectAlphabetMixed() {
        String text = "Привет, Hello!";
        assertEquals("mixed", cipherLogic.detectAlphabet(text), "Обнаружение смешанного языка неверное.");
    }

    @Test
    void testDetectAlphabetNoLetters() {
        String text = "12345 !@#$%^";
        assertNull(cipherLogic.detectAlphabet(text), "Обнаружение отсутствия букв неверное.");
    }

    private Path createTempFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("testFile", ".txt");
        Files.writeString(tempFile, content);
        return tempFile;
    }

    private void deleteTempFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete temp file: " + filePath + " - " + e.getMessage());
        }
    }

    @Test
    void testReadFileCorrectly() throws IOException {
        String originalContent = "This is a test file content.\nAnother line with some data.";
        Path tempFile = null;
        try {
            tempFile = createTempFile(originalContent);
            String filePath = tempFile.toString();

            String readContent = cipherLogic.readFile(filePath);

            assertNotNull(readContent, "readFile должен вернуть не null для существующего файла.");
            assertEquals(originalContent, readContent, "Содержимое прочитанного файла должно совпадать с оригинальным.");
        } finally {
            if (tempFile != null) {
                deleteTempFile(tempFile);
            }
        }
    }

    @Test
    void testReadFileEmptyFile() throws IOException {
        String originalContent = "";
        Path tempFile = null;
        try {
            tempFile = createTempFile(originalContent);
            String filePath = tempFile.toString();

            String readContent = cipherLogic.readFile(filePath);

            assertNotNull(readContent, "readFile должен вернуть не null для пустого файла.");
            assertEquals(originalContent, readContent, "Для пустого файла ожидается пустая строка.");
        } finally {
            if (tempFile != null) {
                deleteTempFile(tempFile);
            }
        }
    }

    @Test
    void testReadFileNonExistentFile() {
        String nonExistentFilePath = "this_file_definitely_does_not_exist_12345.txt";

        String content = cipherLogic.readFile(nonExistentFilePath);

        assertNull(content, "readFile должен вернуть null для несуществующего файла.");
    }

    @Test
    void testWriteFileCorrectly() throws IOException {
        String contentToWrite = "Content to be written to a file.\nIt should be saved correctly.";
        Path tempFile = null;
        String filePath = null;
        try {
            tempFile = Files.createTempFile("testWriteFile", ".txt");
            filePath = tempFile.toString();

            cipherLogic.writeFile(filePath, contentToWrite);

            String writtenContent = Files.readString(tempFile);
            assertEquals(contentToWrite, writtenContent, "Записанное содержимое файла должно совпадать с ожидаемым.");
        } finally {
            if (tempFile != null) {
                deleteTempFile(tempFile);
            }
        }
    }

    @Test
    void testWriteFileEmptyContent() throws IOException {
        String contentToWrite = "";
        Path tempFile = null;
        String filePath = null;
        try {
            tempFile = Files.createTempFile("testWriteEmpty", ".txt");
            filePath = tempFile.toString();

            cipherLogic.writeFile(filePath, contentToWrite);

            String writtenContent = Files.readString(tempFile);
            assertEquals(contentToWrite, writtenContent, "Запись пустого содержимого должна создавать пустой файл.");
        } finally {
            if (tempFile != null) {
                deleteTempFile(tempFile);
            }
        }
    }
}