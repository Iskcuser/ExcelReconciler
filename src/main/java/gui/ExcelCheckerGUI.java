package gui;

import comparator.ExcelComparator;
import model.CompareResult;
import painter.ExcelPainter;
import reader.ExcelReader;

import org.apache.poi.ss.usermodel.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.function.Consumer;

public class ExcelCheckerGUI extends JFrame {

    private JTextField filePathField;
    private JTextField col1Field;
    private JTextField col2Field;
    private JButton runButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    public ExcelCheckerGUI() {
        super("Excel Checker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 310);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Excel файл:"), gbc);
        filePathField = new JTextField();
        filePathField.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(filePathField, gbc);
        JButton selectFileButton = new JButton("Выбрать...");
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(selectFileButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Колонна 1 (напр. D):"), gbc);
        col1Field = new JTextField("D");
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(col1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Колонна 2 (напр. H):"), gbc);
        col2Field = new JTextField("H");
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(col2Field, gbc);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        panel.add(progressBar, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(java.awt.Font.ITALIC));
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(statusLabel, gbc);

        runButton = new JButton("▶ Запустить") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) g2.setColor(new java.awt.Color(0xA5D6A7));
                else if (getModel().isPressed()) g2.setColor(new java.awt.Color(0x388E3C));
                else if (getModel().isRollover()) g2.setColor(new java.awt.Color(0x43A047));
                else g2.setColor(new java.awt.Color(0x4CAF50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        runButton.setForeground(java.awt.Color.WHITE);
        runButton.setFont(runButton.getFont().deriveFont(java.awt.Font.BOLD, 13f));
        runButton.setFocusPainted(false);
        runButton.setContentAreaFilled(false);
        runButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runButton.setPreferredSize(new Dimension(0, 36));

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        panel.add(runButton, gbc);
        add(panel);

        selectFileButton.addActionListener(e -> chooseFile());
        runButton.addActionListener(e -> runProcessing());
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel файлы (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void runProcessing() {
        String filePath = filePathField.getText().trim();
        int col1Index = columnLetterToIndex(col1Field.getText().trim());
        int col2Index = columnLetterToIndex(col2Field.getText().trim());

        if (filePath.isEmpty() || col1Index < 0 || col2Index < 0) {
            showError("Проверьте правильность заполнения полей.");
            return;
        }

        runButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Чтение файла...");

        Timer dotsTimer = new Timer(400, e -> {
            String text = statusLabel.getText();
            if (text.contains("...")) statusLabel.setText(text.replace("...", "."));
            else statusLabel.setText(text + ".");
        });
        dotsTimer.start();

        new SwingWorker<String, Integer>() {
            @Override
            protected String doInBackground() throws Exception {
                return processFile(filePath, col1Index, col2Index, this::publish);
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (progressBar.isIndeterminate()) {
                    progressBar.setIndeterminate(false);
                    statusLabel.setText("Сохранение и обработка данных");
                }
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                progressBar.setString(latest + "%");
            }

            @Override
            protected void done() {
                dotsTimer.stop();
                runButton.setEnabled(true);
                progressBar.setIndeterminate(false);
                try {
                    String result = get();
                    progressBar.setValue(100);
                    progressBar.setString("100%");
                    statusLabel.setText("Готово!");
                    JOptionPane.showMessageDialog(ExcelCheckerGUI.this, result, "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    statusLabel.setText("Ошибка!");
                    showError("Ошибка: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
                }
            }
        }.execute();
    }

    private String processFile(String filePath, int col1, int col2, Consumer<Integer> onProgress) throws Exception {
        ExcelReader reader = new ExcelReader();
        try (Workbook workbook = reader.readWorkbook(filePath)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = workbook.getSheetAt(0);
            ExcelComparator comparator = new ExcelComparator();
            ExcelPainter painter = new ExcelPainter(workbook);
            util.FileSaver saver = new util.FileSaver();

            int totalRows = 0, matchCount = 0;
            int lastRow = sheet.getLastRowNum();
            int effectiveLastRow = Math.max(lastRow, 1);
            boolean dataStarted = false;

            for (int i = 0; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) { if (dataStarted) break; else continue; }

                String v1 = reader.getCellValue(row, col1, evaluator);
                String v2 = reader.getCellValue(row, col2, evaluator);

                if ((v1 == null || v1.trim().isEmpty()) && (v2 == null || v2.trim().isEmpty())) {
                    if (dataStarted) break; else continue;
                }

                dataStarted = true;
                if (totalRows == 0 && isHeader(v1)) continue;

                totalRows++;
                CompareResult result = comparator.compare(v1, v2);
                if (result.status() == CompareResult.Status.MATCH) {
                    painter.paint(row, result, col1, col2);
                    matchCount++;
                }

                if (i % 10 == 0 || i == lastRow) {
                    int progress = (int) (((i + 1) / (double) effectiveLastRow) * 90);
                    onProgress.accept(Math.min(progress, 90));
                    Thread.sleep(effectiveLastRow < 1000 ? 30 : 5);
                }
            }

            onProgress.accept(92);
            String outputPath = generateUniqueFileName(filePath);
            saver.save(workbook, outputPath);

            return String.format("✅ Готово!\nСтрок обработано: %d\nСовпадений: %d\nРезультат сохранен.",
                    totalRows, matchCount);
        }
    }

    private boolean isHeader(String v1) {
        if (v1 == null) return true;
        try {
            Double.parseDouble(v1.trim().replace(",", ".").replaceAll("[\\s\\u00A0]+", ""));
            return false;
        } catch (Exception e) { return true; }
    }

    private String generateUniqueFileName(String path) {
        int lastDot = path.lastIndexOf('.');
        String base = (lastDot != -1) ? path.substring(0, lastDot) : path;
        String res = base + "_result.xlsx";
        java.io.File f = new java.io.File(res);
        int c = 1;
        while (f.exists()) {
            res = base + "_result(" + (c++) + ").xlsx";
            f = new java.io.File(res);
        }
        return res;
    }

    private int columnLetterToIndex(String col) {
        int res = 0;
        String cStr = col.trim().toUpperCase();
        if (cStr.isEmpty()) return -1;
        for (char c : cStr.toCharArray()) {
            if (!Character.isLetter(c)) return -1;
            res = res * 26 + (c - 'A' + 1);
        }
        return res - 1;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExcelCheckerGUI::new);
    }
}

