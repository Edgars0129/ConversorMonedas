import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final String API_KEY = "2fc1ab2dbac03bbcdf364fa1";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    private final HttpClient httpClient;
    private final Gson gson;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public Map<String, Double> getExchangeRates(String baseCurrency) throws Exception {
        String url = BASE_URL + baseCurrency;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to get exchange rates. HTTP error code: " + response.statusCode());
        }

        JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

        if (conversionRates == null) {
            throw new Exception("Failed to parse conversion rates from API response");
        }

        Map<String, Double> rates = new HashMap<>();
        for (String currency : conversionRates.keySet()) {
            rates.put(currency, conversionRates.get(currency).getAsDouble());
        }

        return rates;
    }

    public void updateExchangeRates() throws Exception {
        String[] currencies = {"USD", "ARS", "BRL", "COP"};
        Map<String, Map<String, Double>> allRates = new HashMap<>();

        for (String currency : currencies) {
            allRates.put(currency, getExchangeRates(currency));
        }
    }
}