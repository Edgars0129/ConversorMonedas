import java.util.HashMap;
import java.util.Map;

public class CurrencyService {
    private final ApiClient apiClient;
    private Map<String, Map<String, Double>> exchangeRates;

    public CurrencyService() {
        this.apiClient = new ApiClient();
        this.exchangeRates = new HashMap<>();
    }

    public double convert(double amount, String fromCurrency, String toCurrency) throws Exception {
        // Actualizar las tasas para la moneda de origen antes de la conversión
        updateRatesForCurrency(fromCurrency);

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        Map<String, Double> rates = exchangeRates.get(fromCurrency);
        if (rates == null || !rates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("No se encontró la tasa de cambio para " + fromCurrency + " a " + toCurrency);
        }

        return amount * rates.get(toCurrency);
    }

    private void updateRatesForCurrency(String currency) throws Exception {
        exchangeRates.put(currency, apiClient.getExchangeRates(currency));
    }

    public boolean areRatesAvailable() {
        return !exchangeRates.isEmpty();
    }

    public String[] getAvailableCurrencies() {
        return exchangeRates.keySet().toArray(new String[0]);
    }
}