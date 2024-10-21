import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CurrencyConverter {
    private static final CurrencyService currencyService = new CurrencyService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<ConversionResult> conversionHistory = new ArrayList<>();
    private static final String[] currencies = {"USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "ARS", "BRL", "COP", "MXN", "CLP"};

    public static void main(String[] args) {
        System.out.println("Sea bienvenido al Conversor de Monedas");

        while (true) {
            displayMenu();
            System.out.print("Seleccione una opción: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1, 2, 3, 4, 5, 6 -> processFixedConversion(choice);
                case 7 -> processOtherConversions();
                case 8 -> {
                    exportToJson();
                    System.out.println("Gracias por usar el Conversor de Monedas. ¡Hasta luego!");
                    return;
                }
                default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\nSea bienvenido al Conversor de Monedas");
        System.out.println("1) Dólar => Peso argentino");
        System.out.println("2) Peso argentino => Dólar");
        System.out.println("3) Dólar => Real brasileño");
        System.out.println("4) Real brasileño => Dólar");
        System.out.println("5) Dólar => Peso Colombiano");
        System.out.println("6) Peso Colombiano => Dólar");
        System.out.println("7) Otras Conversiones");
        System.out.println("8) Salir");
    }

    private static void processFixedConversion(int choice) {
        String fromCurrency, toCurrency;
        switch (choice) {
            case 1 -> { fromCurrency = "USD"; toCurrency = "ARS"; }
            case 2 -> { fromCurrency = "ARS"; toCurrency = "USD"; }
            case 3 -> { fromCurrency = "USD"; toCurrency = "BRL"; }
            case 4 -> { fromCurrency = "BRL"; toCurrency = "USD"; }
            case 5 -> { fromCurrency = "USD"; toCurrency = "COP"; }
            case 6 -> { fromCurrency = "COP"; toCurrency = "USD"; }
            default -> {
                System.out.println("Opción no válida");
                return;
            }
        }
        performConversion(fromCurrency, toCurrency);
    }

    private static void processOtherConversions() {
        System.out.println("\nOtras Conversiones:");
        System.out.println("Monedas disponibles:");
        for (int i = 0; i < currencies.length; i++) {
            System.out.println((i + 1) + ") " + currencies[i]);
        }

        System.out.print("Seleccione la moneda de origen (número): ");
        int fromIndex = scanner.nextInt() - 1;
        System.out.print("Seleccione la moneda de destino (número): ");
        int toIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline

        if (fromIndex < 0 || fromIndex >= currencies.length || toIndex < 0 || toIndex >= currencies.length) {
            System.out.println("Selección de moneda no válida.");
            return;
        }

        String fromCurrency = currencies[fromIndex];
        String toCurrency = currencies[toIndex];
        performConversion(fromCurrency, toCurrency);
    }

    private static void performConversion(String fromCurrency, String toCurrency) {
        System.out.print("Ingrese la cantidad a convertir: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try {
            double result = currencyService.convert(amount, fromCurrency, toCurrency);
            System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);

            // Guardar el resultado de la conversión en el historial con marca de tiempo
            LocalDateTime now = LocalDateTime.now();
            conversionHistory.add(new ConversionResult(amount, fromCurrency, result, toCurrency, now));
        } catch (Exception e) {
            System.out.println("Error al realizar la conversión: " + e.getMessage());
        }
    }

    private static void exportToJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .create();
        String json = gson.toJson(conversionHistory);

        try (FileWriter writer = new FileWriter("ConversionHistory.json")) {
            writer.write(json);
            System.out.println("Historial de conversiones exportado a ConversionHistory.json");
        } catch (IOException e) {
            System.out.println("Error al exportar el historial: " + e.getMessage());
        }
    }

    // Clase interna para representar el resultado de una conversión
    private record ConversionResult(double originalAmount, String fromCurrency, double convertedAmount, String toCurrency, LocalDateTime timestamp) {}
}