import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CurrencyConverterGUI extends JFrame {
    private static List<String> history = new ArrayList<>();
    private JTextField amountField;
    private JComboBox<String> baseCurrencyCombo, targetCurrencyCombo;
    private JTextArea resultArea;
    private JButton historyButton, convertButton, revertButton, exitButton;
    private String apiKey;

    public CurrencyConverterGUI(String apiKey) {
        super();
        this.apiKey = apiKey;
        setTitle("Conversor De Monedas Alura");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Cambia a GridLayout

        JLabel titleLabel = new JLabel("Conversor De Monedas Alura");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        topPanel.add(titleLabel);

        JPanel baseCurrencyPanel = new JPanel(new GridLayout(1, 2));
        JLabel baseCurrencyLabel = new JLabel("Por favor elija la moneda base:");
        baseCurrencyCombo = new JComboBox<>(getCurrencyOptions());
        baseCurrencyPanel.add(baseCurrencyLabel);
        baseCurrencyPanel.add(baseCurrencyCombo);
        topPanel.add(baseCurrencyPanel);

        JPanel targetCurrencyPanel = new JPanel(new GridLayout(1, 2));
        JLabel targetCurrencyLabel = new JLabel("Por favor elija la moneda objetivo:");
        targetCurrencyCombo = new JComboBox<>(getCurrencyOptions());
        targetCurrencyPanel.add(targetCurrencyLabel);
        targetCurrencyPanel.add(targetCurrencyCombo);
        topPanel.add(targetCurrencyPanel);

        JPanel amountPanel = new JPanel(new GridLayout(1, 2));
        JLabel amountLabel = new JLabel("Por favor introduzca la cantidad a convertir:");
        amountField = new JTextField(10);
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        topPanel.add(amountPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        convertButton = new JButton("Convertir");
        convertButton.addActionListener(new ConvertButtonListener());
        revertButton = new JButton("Revertir Conversión");
        revertButton.addActionListener(new RevertButtonListener());
        historyButton = new JButton("Historial");
        historyButton.addActionListener(new HistoryButtonListener());
        exitButton = new JButton("Salir");
        exitButton.addActionListener(new ExitButtonListener());
        buttonPanel.add(convertButton);
        buttonPanel.add(revertButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("Resultado de Conversión");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        resultLabel.setVerticalAlignment(JLabel.CENTER);
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        resultArea = new JTextArea(3, 60); // Cambia el número de filas a 3
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(resultPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private String[] getCurrencyOptions() {
        return new String[]{
                "USD - Dólares estadounidenses", "EUR - Euros", "JPY - Yenes japoneses",
                "GBP - Libras esterlinas", "AUD - Dólares australianos", "CAD - Dólares canadienses",
                "CHF - Francos suizos", "CNY - Yuanes chinos", "SEK - Coronas suecas",
                "NZD - Dólares neozelandeses", "BRL - Reales brasileños", "MXN - Pesos mexicanos",
                "ZAR - Rand sudafricano", "INR - Rupias indias", "THB - Baht tailandés",
                "KRW - Won surcoreano", "RUB - Rublo ruso", "TRY - Lira turca",
                "IDR - Rupia indonesia", "DKK - Corona danesa", "NOK - Corona noruega",
                "CZK - Corona checa", "HUF - Florín húngaro", "PLN - Złoty polaco",
                "SGD - Dólar singapurense"
        };
    }

    private String getCurrencyCode(String currencyOption) {
        return currencyOption.substring(0, 3);
    }

    private class ConvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            convertCurrency();
        }
    }

    private class RevertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            revertConversion();
        }
    }

    private class HistoryButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showHistory();
        }
    }

    private class ExitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private void convertCurrency() {
        try {
            String baseCurrency = getCurrencyCode((String) baseCurrencyCombo.getSelectedItem());
            String targetCurrency = getCurrencyCode((String) targetCurrencyCombo.getSelectedItem());
            String amountText = amountField.getText();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Primero introduzca la cantidad a convertir", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountText);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + baseCurrency + "/" + targetCurrency))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body()); // Imprime la respuesta para depuración
            JsonElement json = JsonParser.parseString(response.body());
            JsonObject jsonObject = json.getAsJsonObject();
            double exchangeRate = jsonObject.get("conversion_rate").getAsDouble();

            double result = amount * exchangeRate;
            String resultText = String.format("Moneda Base: %s\nMoneda Objetivo: %s\nCantidad a Convertir: %.2f %s\nResultado: %.2f %s = %.2f %s",
                    baseCurrency, targetCurrency, amount, baseCurrency, amount, baseCurrency, result, targetCurrency);
            resultArea.setText(resultText);
            history.add(String.format("%.2f %s es igual a %.2f %s", amount, baseCurrency, result, targetCurrency));
            amountField.setText(""); // Limpia la casilla de cantidad
        } catch (IOException | InterruptedException e) {
            resultArea.setText("Error al realizar la conversión: " + e.getMessage());
        }
    }

    private void revertConversion() {
        if (!history.isEmpty()) {
            String lastConversion = history.remove(history.size() - 1);
            String[] parts = lastConversion.split(" es igual a ");
            String[] amountAndBaseCurrency = parts[0].split(" ");
            String[] resultAndTargetCurrency = parts[1].split(" ");

            double amount = Double.parseDouble(amountAndBaseCurrency[0]);
            String baseCurrency = amountAndBaseCurrency[1];
            double result = Double.parseDouble(resultAndTargetCurrency[0]);
            String targetCurrency = resultAndTargetCurrency[1];

            String revertText = String.format("Moneda Base: %s\nMoneda Objetivo: %s\nCantidad a Convertir: %.2f %s\nResultado: %.2f %s = %.2f %s",
                    targetCurrency, baseCurrency, result, targetCurrency, result, targetCurrency, amount, baseCurrency);
            resultArea.setText(revertText);
            history.add(String.format("%.2f %s es igual a %.2f %s", result, targetCurrency, amount, baseCurrency)); // Agrega la conversión revertida al historial
        } else {
            resultArea.setText("No hay conversiones para revertir");
        }
    }

    private void showHistory() {
        StringBuilder historyText = new StringBuilder();
        for (String entry : history) {
            historyText.append(entry).append("\n");
        }
        JOptionPane.showMessageDialog(this, historyText.toString(), "Historial de Conversiones", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Reemplaza "TU_API_KEY" con tu clave de API
        CurrencyConverterGUI converter = new CurrencyConverterGUI("a24d4c30a1394b6d3ff92365");
        converter.setVisible(true);
    }
}












