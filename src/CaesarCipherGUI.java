// ***** CaesarCipherGUI.java *****

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
        super("Шифр Цезаря (с файлами)");
        this.cipherLogic = new CaesarCipherLogic();
        initComponents();
        layoutComponents();
        addListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 180); // Новый, уменьшенный размер окна
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
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)); // Отступы по краям
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // === 1. Панель Настроек (сверху) ===
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        settingsPanel.add(new JLabel("Сдвиг:"));
        settingsPanel.add(shiftField);
        settingsPanel.add(alphabetComboBox);

        // === 2. Панель Путей (слева) ===
        JPanel pathPanel = new JPanel(new GridLayout(2, 1, 2, 2)); // 2 строки, 1 столбец, минимальные отступы
        pathPanel.add(new JLabel("Входной файл:"));
        pathPanel.add(new JLabel("Выходной файл:"));

        // === 3. Панель Полей ввода (справа) ===
        JPanel textFieldPanel = new JPanel(new GridLayout(2, 1, 2, 2)); // Аналогично 2 строки, 1 столбец
        textFieldPanel.add(inputFilePathField);
        textFieldPanel.add(outputFilePathField);

        // === 4. Объединяем Пути и Поля в одну панель (в центре) ===
        JPanel filesPanel = new JPanel(new BorderLayout(5, 5));
        filesPanel.add(pathPanel, BorderLayout.WEST);   // Метки слева
        filesPanel.add(textFieldPanel, BorderLayout.CENTER); // Поля ввода справа

        // === 5. Панель Кнопок (снизу) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(bruteForceButton);

        // === Общая компоновка ===
        mainPanel.add(settingsPanel, BorderLayout.NORTH);  // Настройки сверху
        mainPanel.add(filesPanel, BorderLayout.CENTER);     // Файлы в центре (слева метки, справа поля)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);    // Кнопки снизу

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
            // Убираем resultFrame.setSize(800, 600); - будем использовать pack()
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
                // Ограничиваем длину текста для таблицы, чтобы не было слишком широких строк
                String decryptedText = result.getDecryptedText();
                if (decryptedText.length() > 60) {
                    decryptedText = decryptedText.substring(0, 60) + "...";
                }
                row.add(decryptedText);
                row.add(String.format("%.4f", result.getScore()));
                data.add(row);
            }

            JTable resultTable = new JTable(data, columnNames);

            // *** КЛЮЧЕВОЕ ИЗМЕНЕНИЕ 1: Режим автоматической подгонки ширины ***
            resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            // resultTable.getColumnModel().getColumn(1).setPreferredWidth(500); // Это теперь не нужно

            // Теперь мы можем установить предпочтительную ширину для контейнера с прокруткой
            JScrollPane scrollPane = new JScrollPane(resultTable);

            // Установим разумный минимальный размер для окна
            resultTable.setPreferredScrollableViewportSize(new Dimension(700, 400));


            resultFrame.add(scrollPane, BorderLayout.CENTER);

            // *** КЛЮЧЕВОЕ ИЗМЕНЕНИЕ 2: Использование pack() ***
            // pack() подгоняет окно под предпочтительный размер его содержимого.
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