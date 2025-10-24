import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;


public class CaesarCipherGUI extends JFrame {

    private JTextField shiftField;
    private JComboBox<String> alphabetComboBox;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton bruteForceButton;
    private CaesarCipherLogic cipherLogic;

    private JTextField inputFilePathField;
    private JTextField outputFilePathField;

    public CaesarCipherGUI() {
        super("Шифр Цезаря");
        this.cipherLogic = new CaesarCipherLogic();
        initComponents();
        layoutComponents();
        addListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 180);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        shiftField = new JTextField("3", 3);

        String[] alphabets = {"Русский", "Английский"};
        alphabetComboBox = new JComboBox<>(alphabets);

        encryptButton = new JButton("Шифровать");
        decryptButton = new JButton("Дешифровать");
        bruteForceButton = new JButton("Брутфорс и Анализ");

        inputFilePathField = new JTextField(25);
        outputFilePathField = new JTextField(25);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        settingsPanel.add(new JLabel("Сдвиг:"));
        settingsPanel.add(shiftField);
        settingsPanel.add(alphabetComboBox);

        JPanel pathPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        pathPanel.add(new JLabel("Входной файл:"));
        pathPanel.add(new JLabel("Выходной файл:"));

        JPanel textFieldPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        textFieldPanel.add(inputFilePathField);
        textFieldPanel.add(outputFilePathField);

        JPanel filesPanel = new JPanel(new BorderLayout(5, 5));
        filesPanel.add(pathPanel, BorderLayout.WEST);
        filesPanel.add(textFieldPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(bruteForceButton);

        mainPanel.add(settingsPanel, BorderLayout.NORTH);
        mainPanel.add(filesPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addListeners() {
        encryptButton.addActionListener(e -> processText(true));
        decryptButton.addActionListener(e -> processText(false));
        bruteForceButton.addActionListener(e -> performBruteForceAndAnalysis());
    }

    private void processText(boolean encrypt) {
        String inputFilePath = inputFilePathField.getText();
        String outputFilePath = outputFilePathField.getText();

        inputFilePath = inputFilePath.replaceAll("^\"|\"$", "").trim();
        outputFilePath = outputFilePath.replaceAll("^\"|\"$", "").trim();

        if (inputFilePath.isEmpty() || outputFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите пути к входному и выходному файлам.", "Ошибка", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String inputText = cipherLogic.readFile(inputFilePath);
        if (inputText == null) {
            JOptionPane.showMessageDialog(this, "Ошибка при чтении файла: " + inputFilePath, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int shift = Integer.parseInt(shiftField.getText());
            String selectedAlphabet = alphabetComboBox.getSelectedItem().toString();
            String alphabetKey = selectedAlphabet.equalsIgnoreCase("Русский") ? "ru" : "en";
            String result = null;

            if (encrypt) {
                result = cipherLogic.encrypt(inputText, shift, alphabetKey, true);
            } else {
                result = cipherLogic.decrypt(inputText, shift, alphabetKey, true);
            }

            cipherLogic.writeFile(outputFilePath, result);
            JOptionPane.showMessageDialog(this, "Операция успешно завершена. Результат записан в файл: " + outputFilePath, "Успех", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка сдвига. Введите целое число.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Произошла ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void performBruteForceAndAnalysis() {
        String inputFilePath = inputFilePathField.getText();

        inputFilePath = inputFilePath.replaceAll("^\"|\"$", "").trim();

        if (inputFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите путь к входному файлу для анализа.", "Ошибка", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String inputText = cipherLogic.readFile(inputFilePath);
        if (inputText == null) {
            JOptionPane.showMessageDialog(this, "Ошибка при чтении файла: " + inputFilePath, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String detectedAlphabet = cipherLogic.detectAlphabet(inputText);
        if (detectedAlphabet == null) {
            JOptionPane.showMessageDialog(this, "Не найдено алфавитных символов в файле.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedAlphabetFromGUI = alphabetComboBox.getSelectedItem().toString().equalsIgnoreCase("Русский") ? "ru" : "en";
        String alphabetToUse = selectedAlphabetFromGUI;
        if (detectedAlphabet.equals("mixed")) {
            JOptionPane.showMessageDialog(this, "Обнаружены символы разных алфавитов. Результат анализа может быть неточен. Выбранный в GUI алфавит: " + (selectedAlphabetFromGUI.equals("ru") ? "Русский" : "Английский"), "Предупреждение", JOptionPane.WARNING_MESSAGE);
        } else if (!detectedAlphabet.equals(selectedAlphabetFromGUI)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Обнаружен '" + (detectedAlphabet.equals("ru") ? "Русский" : "Английский") + "' алфавит, но в GUI выбран " + (selectedAlphabetFromGUI.equals("ru") ? "Русский" : "Английский") + ". Использовать определенный или выбранный?",
                    "Выбор алфавита",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                alphabetToUse = detectedAlphabet;
                alphabetComboBox.setSelectedItem(detectedAlphabet.equalsIgnoreCase("ru") ? "Русский" : "Английский");
                JOptionPane.showMessageDialog(this, "Используется определенный '" + (detectedAlphabet.equals("ru") ? "Русский" : "Английский") + "' алфавит.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Используется выбранный в GUI '" + (selectedAlphabetFromGUI.equals("ru") ? "Русский" : "Английский") + "' алфавит. Результат анализа может быть неточен.", "Информация", JOptionPane.WARNING_MESSAGE);
                alphabetToUse = selectedAlphabetFromGUI;
            }
        } else {
            alphabetToUse = detectedAlphabet;
        }

        BruteForcer bruteForcer = new BruteForcer(cipherLogic);
        List<BruteForcer.AnalysisResult> results = bruteForcer.performBruteForceAndAnalysis(inputText, alphabetToUse, true);
        showCombinedResults(results);
    }

    private void showCombinedResults(List<BruteForcer.AnalysisResult> results) {
        SwingUtilities.invokeLater(() -> {
            JFrame resultFrame = new JFrame("Результаты Брутфорса и Анализа");
            resultFrame.setLocationRelativeTo(this);
            resultFrame.setLayout(new BorderLayout());

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Сдвиг");
            columnNames.add("Текст");
            columnNames.add("Оценка Анализа");

            Vector<Vector<Object>> data = new Vector<>();

            for (BruteForcer.AnalysisResult result : results) {
                Vector<Object> row = new Vector<>();
                row.add(result.getShift());
                String decryptedText = result.getDecryptedText();
                if (decryptedText.length() > 60) {
                    decryptedText = decryptedText.substring(0, 60) + "...";
                }
                row.add(decryptedText);
                row.add(String.format("%.4f", result.getScore()));
                data.add(row);
            }
            JTable resultTable = new JTable(data, columnNames);

            resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            JScrollPane scrollPane = new JScrollPane(resultTable);

            resultTable.setPreferredScrollableViewportSize(new Dimension(700, 400));

            resultFrame.add(scrollPane, BorderLayout.CENTER);

            resultFrame.pack();

            resultFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CaesarCipherGUI();
        });
    }
}