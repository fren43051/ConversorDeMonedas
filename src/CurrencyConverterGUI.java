import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConverterGUI extends JFrame {
    private static List<String> history = new ArrayList<>();
    private JTextField amountField;
    private JComboBox<String> baseCurrencyCombo, targetCurrencyCombo;
    private JTextArea resultArea;
    private JButton historyButton;

    public CurrencyConverterGUI() {
        super();
        setTitle("Conversor De Monedas Alura");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(40), BorderLayout.NORTH); // Añade espacio vertical

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        JLabel baseCurrencyLabel = new JLabel("Por favor elija la moneda base:");
        baseCurrencyCombo = new JComboBox<>(getCurrencyOptions());
        JLabel targetCurrencyLabel = new JLabel("Por favor elija la moneda objetivo:");
        targetCurrencyCombo = new JComboBox<>(getCurrencyOptions());

        topPanel.add(baseCurrencyLabel);
        topPanel.add(baseCurrencyCombo);
        topPanel.add(targetCurrencyLabel);
        topPanel.add(targetCurrencyCombo);

        JPanel amountPanel = new JPanel(new BorderLayout());
        JLabel amountLabel = new JLabel("Por favor introduzca la cantidad a convertir:");
        amountField = new JTextField(10);
        amountField.setPreferredSize(new Dimension(amountField.getPreferredSize().width, 20)); // Disminuye la altura del campo de texto
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        amountPanel.add(amountField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton convertButton = new JButton("Convertir");
        convertButton.addActionListener(new ConvertButtonListener());
        JButton revertButton = new JButton("Revertir Conversión");
        revertButton.addActionListener(new RevertButtonListener());
        JButton exitButton = new JButton("Salir");
        exitButton.addActionListener(new ExitButtonListener());
        historyButton = new JButton("Historial");
        historyButton.addActionListener(new HistoryButtonListener());
        buttonPanel.add(convertButton);
        buttonPanel.add(revertButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(exitButton);

        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Cuadro de Resultado"));
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(Box.createVerticalStrut(40), BorderLayout.NORTH);
        bottomPanel.add(amountPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

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
        return currencyOption.split(" - ")[0];
    }

    private void convertCurrency() {
        String apiKey = "a24d4c30a1394b6d3ff92365"; // Reemplaza con tu API key
        HttpClient client = HttpClient.newHttpClient();
        JsonParser parser = new JsonParser();

        String baseCurrency = getCurrencyCode((String) baseCurrencyCombo.getSelectedItem());
        String targetCurrency = getCurrencyCode((String) targetCurrencyCombo.getSelectedItem());
        double amount = Double.parseDouble(amountField.getText());

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + baseCurrency + "/" + targetCurrency))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = parser.parse(response.body()).getAsJsonObject();
            double exchangeRate = json.get("conversion_rate").getAsDouble();
            double result = amount * exchangeRate;

            String conversion = String.format("%.2f %s = %.2f %s", amount, baseCurrency, result, targetCurrency);
            resultArea.append(conversion + System.lineSeparator());
            history.add(conversion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void revertConversion() {
        if (!history.isEmpty()) {
            String lastConversion = history.remove(history.size() - 1);
            String[] parts = lastConversion.split(" = ");
            String targetAmount = parts[1].split(" ")[0];
            String targetCurrency = parts[1].split(" ")[1];
            String baseAmount = parts[0].split(" ")[0];
            String baseCurrency = parts[0].split(" ")[1];

            String revertedConversion = String.format("%s %s = %s %s", targetAmount, targetCurrency, baseAmount, baseCurrency);
            resultArea.append(revertedConversion + System.lineSeparator());
            history.add(revertedConversion);
        }
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

    private class ExitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class HistoryButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            HistoryDialog historyDialog = new HistoryDialog(CurrencyConverterGUI.this, history);
            historyDialog.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CurrencyConverterGUI converter = new CurrencyConverterGUI();
            converter.setVisible(true);
        });
    }
}

class HistoryDialog extends JDialog {
    private List<String> history;
    private JList<String> historyList;
    private JButton clearButton, exitButton;

    public HistoryDialog(Frame owner, List<String> history) {
        super(owner, "Historial de Conversiones", true);
        this.history = history;

        setSize(300, 400);
        setLocationRelativeTo(owner);

        historyList = new JList<>(history.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(historyList);

        clearButton = new JButton("Limpiar Historial");
        clearButton.addActionListener(new ClearButtonListener());

        exitButton = new JButton("Salir");
        exitButton.addActionListener(new ExitButtonListener());

        Container contentPane = getContentPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            history.clear();
            historyList.setListData(history.toArray(new String[0]));
        }
    }

    private class ExitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }
}


