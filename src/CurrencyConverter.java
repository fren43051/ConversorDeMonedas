import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConverter {
    private static List<String> history = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Configuración de la API
        String apiKey = "a24d4c30a1394b6d3ff92365"; // Reemplaza con tu API key

        // Crear un cliente HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Crear un parser JSON
        JsonParser parser = new JsonParser();

        while (true) {
            System.out.println("**********************************");
            System.out.println("Conversor De Monedas Alura");
            System.out.println("**********************************");
            System.out.println("1. Convertir Monedas");
            System.out.println("2. Ver Historial");
            System.out.println("3. Revertir Última Conversión");
            System.out.println("4. Salir");
            System.out.println("**********************************");
            System.out.println("Elija una opción válida por favor: ");

            int option = 0;
            do {
                while (!scanner.hasNextInt()) {
                    System.out.println("Introduzca solo números por favor: ");
                    scanner.next();
                }
                option = scanner.nextInt();
            } while (option < 1 || option > 4);

            switch (option) {
                case 1:
                    // Opción 1: Convertir Monedas
                    System.out.println("Seleccione la moneda base:");
                    printCurrencyOptions();
                    int baseCurrencyOption = scanner.nextInt();
                    System.out.println("Seleccione la moneda objetivo:");
                    printCurrencyOptions();
                    int targetCurrencyOption = scanner.nextInt();
                    System.out.println("Ingrese la cantidad a convertir (máximo 2 decimales):");
                    double amount = 0;
                    do {
                        while (!scanner.hasNextDouble()) {
                            System.out.println("Introduzca solo números por favor: ");
                            scanner.next();
                        }
                        amount = scanner.nextDouble();
                    } while (amount < 0);

                    String baseCurrency = getCurrency(baseCurrencyOption); // Moneda base
                    String targetCurrency = getCurrency(targetCurrencyOption); // Moneda objetivo

                    // Construir la solicitud HTTP
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + baseCurrency + "/" + targetCurrency))
                            .GET()
                            .build();

                    // Enviar la solicitud y obtener la respuesta
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    // Procesar la respuesta JSON
                    JsonObject json = parser.parse(response.body()).getAsJsonObject();
                    double exchangeRate = json.get("conversion_rate").getAsDouble();
                    double result = amount * exchangeRate;

                    // Mostrar el resultado
                    String conversion = String.format("%.2f %s son equivalentes a %.2f %s", amount, baseCurrency, result, targetCurrency);
                    System.out.println(conversion);
                    history.add(conversion);
                    break;
                case 2:
                    // Opción 2: Ver Historial
                    System.out.println("Historial de Conversiones:");
                    for (String conversionHistory : history) {
                        System.out.println(conversionHistory);
                    }
                    break;
                case 3:
                    // Opción 3: Revertir Última Conversión
                    if (!history.isEmpty()) {
                        String lastConversion = history.get(history.size() - 1);
                        String[] parts = lastConversion.split(" ");
                        double lastAmount = Double.parseDouble(parts[0]);
                        String lastBaseCurrency = parts[1];
                        String lastTargetCurrency = parts[6];

                        // Construir la solicitud HTTP para la conversión inversa
                        HttpRequest reverseRequest = HttpRequest.newBuilder()
                                .uri(URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + lastTargetCurrency + "/" + lastBaseCurrency))
                                .GET()
                                .build();

                        // Enviar la solicitud y obtener la respuesta
                        HttpResponse<String> reverseResponse = client.send(reverseRequest, HttpResponse.BodyHandlers.ofString());

                        // Procesar la respuesta JSON
                        JsonObject reverseJson = parser.parse(reverseResponse.body()).getAsJsonObject();
                        double reverseExchangeRate = reverseJson.get("conversion_rate").getAsDouble();
                        double reverseResult = lastAmount * reverseExchangeRate;

                        // Mostrar el resultado
                        String reverseConversion = String.format("%.2f %s son equivalentes a %.2f %s", lastAmount, lastTargetCurrency, reverseResult, lastBaseCurrency);
                        System.out.println(reverseConversion);
                        history.add(reverseConversion);
                    } else {
                        System.out.println("No hay conversiones en el historial para revertir.");
                    }
                    break;
                case 4:
                    // Opción 4: Salir
                    System.out.println("Gracias por usar el Conversor De Monedas Alura. ¡Hasta luego!");
                    System.exit(0);
            }
        }
    }

    private static void printCurrencyOptions() {
        System.out.println("1. USD - Dólares estadounidenses");
        System.out.println("2. EUR - Euros");
        System.out.println("3. JPY - Yenes japoneses");
        System.out.println("4. GBP - Libras esterlinas");
        System.out.println("5. AUD - Dólares australianos");
        System.out.println("6. CAD - Dólares canadienses");
        System.out.println("7. CHF - Francos suizos");
        System.out.println("8. CNY - Yuanes chinos");
        System.out.println("9. SEK - Coronas suecas");
        System.out.println("10. NZD - Dólares neozelandeses");
        // Agrega más opciones de moneda aquí
        System.out.println("11. BRL - Reales brasileños");
        System.out.println("12. MXN - Pesos mexicanos");
        System.out.println("13. ZAR - Rand sudafricano");
        System.out.println("14. INR - Rupias indias");
        System.out.println("15. THB - Baht tailandés");
        System.out.println("16. KRW - Won surcoreano");
        System.out.println("17. RUB - Rublo ruso");
        System.out.println("18. TRY - Lira turca");
        System.out.println("19. IDR - Rupia indonesia");
        System.out.println("20. DKK - Corona danesa");
        System.out.println("21. NOK - Corona noruega");
        System.out.println("22. CZK - Corona checa");
        System.out.println("23. HUF - Florín húngaro");
        System.out.println("24. PLN - Złoty polaco");
        System.out.println("25. SGD - Dólar singapurense");
        System.out.println("Por favor seleccione una opción: ");
    }

    private static String getCurrency(int option) {
        switch (option) {
            case 1:
                return "USD"; // Dólares estadounidenses
            case 2:
                return "EUR"; // Euros
            case 3:
                return "JPY"; // Yenes japoneses
            case 4:
                return "GBP"; // Libras esterlinas
            case 5:
                return "AUD"; // Dólares australianos
            case 6:
                return "CAD"; // Dólares canadienses
            case 7:
                return "CHF"; // Francos suizos
            case 8:
                return "CNY"; // Yuanes chinos
            case 9:
                return "SEK"; // Coronas suecas
            case 10:
                return "NZD"; // Dólares neozelandeses
            // Agrega más opciones de moneda aquí
            case 11:
                return "BRL"; // Reales brasileños
            case 12:
                return "MXN"; // Pesos mexicanos
            case 13:
                return "ZAR"; // Rand sudafricano
            case 14:
                return "INR"; // Rupias indias
            case 15:
                return "THB"; // Baht tailandés
            case 16:
                return "KRW"; // Won surcoreano
            case 17:
                return "RUB"; // Rublo ruso
            case 18:
                return "TRY"; // Lira turca
            case 19:
                return "IDR"; // Rupia indonesia
            case 20:
                return "DKK"; // Corona danesa
            case 21:
                return "NOK"; // Corona noruega
            case 22:
                return "CZK"; // Corona checa
            case 23:
                return "HUF"; // Florín húngaro
            case 24:
                return "PLN"; // Złoty polaco
            case 25:
                return "SGD"; // Dólar singapurense
            default:
                return "USD"; // Moneda base por defecto
        }
    }
}