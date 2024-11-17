import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

public class CambioDeMonedas {

    // Método principal
    public static void main(String[] args) throws JSONException {
        Scanner scanner = new Scanner(System.in);

        // Solicitar al usuario que ingrese la moneda base
        System.out.print("Ingresa la moneda base (ejemplo: USD, EUR, GBP): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        // Llamar al método para obtener las tasas de cambio
        JSONObject exchangeRates = getExchangeRates(baseCurrency);
        System.out.println("Tasas de cambio para " + baseCurrency + ":");

        if (exchangeRates != null) {
            // Mostrar las tasas de cambio si la respuesta es válida
            JSONObject rates = exchangeRates.optJSONObject("rates");

            if (rates != null) {
                // Iterar sobre las monedas disponibles
                for (Iterator<String> it = rates.keys(); it.hasNext(); ) {
                    String currency = it.next();
                    double exchangeRate = rates.getDouble(currency);
                    System.out.println(currency + ": " + exchangeRate);
                   }

                // Solicitar al usuario que ingrese la moneda de destino
                System.out.print("Ingresa la moneda de destino (ejemplo: EUR, GBP): ");
                String targetCurrency = scanner.nextLine().toUpperCase();

                // Solicitar la cantidad a convertir
                System.out.print("Ingresa la cantidad a convertir: ");
                double amount = scanner.nextDouble();

                // Realizar la conversión
                double convertedAmount = convertCurrency(rates, targetCurrency, amount);
                if (convertedAmount != -1) {
                    System.out.printf("%.2f %s = %.2f %s%n", amount, baseCurrency, convertedAmount, targetCurrency);
                } else {
                    System.out.println("Moneda de destino no disponible.");
                }
            } else {
                System.out.println("No se encontró la clave 'rates' en la respuesta.");
            }
        } else {
            System.out.println("No se pudo obtener las tasas de cambio.");
        }

        scanner.close();
    }

    // Método para obtener las tasas de cambio desde la API
    public static JSONObject getExchangeRates(String baseCurrency) {
        String API_URL = "https://open.er-api.com/v6/latest/";

        try {
            // Construir la URL completa con la moneda base
            String urlString = API_URL + baseCurrency;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta de la API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Convertir la respuesta en un objeto JSON y devolverlo
                return new JSONObject(response.toString());
            } else {
                // Si hay un error en la solicitud
                System.out.println("Error al obtener las tasas de cambio. Código de respuesta: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error al realizar la solicitud a la API: " + e.getMessage());
        }

        return null;  // Devuelve null si ocurre algún error
    }

    // Método para convertir la cantidad en la moneda base a la moneda de destino
    public static double convertCurrency(JSONObject rates, String targetCurrency, double amount) throws JSONException {
        if (rates.has(targetCurrency)) {
            double conversionRate = rates.getDouble(targetCurrency);
            return amount * conversionRate;
        }
        return -1;  // Retorna -1 si la moneda de destino no se encuentra en las tasas
    }
}