import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {    // Nome do arquivo onde a chave API será armazenada
    private static final String API_KEY_FILE = "api_key.txt";


    public static void main(String[] args) {
        // Teste do método getApiKey
        String key = getApiKey();
        if (key != null) {
            System.out.println("Chave API: " + key);
        } else {
            System.out.println("Falha ao obter a chave API.");
        }
    }

    public static String getApiKey() {
        Path filePath = Paths.get(API_KEY_FILE);

        try {
            // Tenta ler a chave do arquivo, se ele existir
            if (Files.exists(filePath)) {
                return Files.readString(filePath).trim();
            }

            try (// Se o arquivo não existe, solicita a chave ao usuário
            Scanner scanner = new Scanner(System.in)) {
                System.out.println("Nenhuma chave API encontrada. Digite a chave API:");
                String apiKey = scanner.nextLine().trim();

                // Grava a chave no arquivo
                Files.writeString(filePath, apiKey);

                return apiKey;
            }
        } catch (Exception e) {
            // Em caso de erro, exibe mensagem e retorna null
            System.err.println("Erro ao ler ou gravar a chave API: " + e.getMessage());
            return null;
        }
    }
    
    public static String invokeGemini(String model, double temperature, String prompt, String responseMimeType, boolean enableGoogleSearch) {
        try {
            // Obtém a chave API
            String apiKey = getApiKey();
            if (apiKey == null) {
                System.err.println("Falha ao obter a chave API.");
                return null;
            }

            // Configura a URL da API do Gemini
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

            // Cria o corpo da requisição em JSON
            JSONObject requestBody = new JSONObject();
            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("parts", new JSONObject().put("text", prompt));
            requestBody.put("contents", new JSONObject[]{content});
            JSONObject generationConfig = new JSONObject().put("temperature", temperature);
            generationConfig.put("response_mime_type", responseMimeType);
            requestBody.put("generationConfig", generationConfig);
            /*if (enableGoogleSearch) {
                JSONObject toolConfig = new JSONObject();
                toolConfig.put("googleSearch", new JSONObject()); // Habilita o Google Search
                // A estrutura de `tools` é um array.
                JSONArray toolsArray = new JSONArray();
                toolsArray.put(toolConfig);
                requestBody.put("tools", toolsArray);
            }*/

            // Configura a requisição HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Extrai o texto da resposta
            JSONObject responseJson = new JSONObject(responseBody);
            //System.out.println(responseJson);
            return responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (Exception e) {
            System.err.println("Erro ao invocar Gemini: " + e.getMessage());
            return null;
        }
    }
}