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

public class App {
    private static final String API_KEY_FILE = "api_key.txt";

    public static void main(String[] args) {
        // Teste do método getApiKey
        String key = getApiKey();
        if (key != null) {
            System.out.println("Chave APIresponse: " + key);
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
    
    public static String getGeminiCompletion(String model, double temperature, String prompt, String responseMimeType, boolean search) {
        try {
            String apiKey = getApiKey();
            if (apiKey == null) {
                System.err.println("Falha ao obter a chave API.");
                return null;
            }

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            if (search) {
                JSONObject searchContent = new JSONObject();
                searchContent.put("role", "user");
                searchContent.put("parts", new JSONObject().put("text", getGeminiSearchResults(prompt)));
                contents.put(searchContent);
            }
            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("parts", new JSONObject().put("text", prompt));
            contents.put(content);
            requestBody.put("contents", contents);
            JSONObject generationConfig = new JSONObject().put("temperature", temperature);
            generationConfig.put("response_mime_type", responseMimeType);
            requestBody.put("generationConfig", generationConfig);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            Files.writeString(Paths.get("requestBody.json"), requestBody.toString());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            Files.writeString(Paths.get("responseBody.json"), responseBody.toString());
            
            JSONObject responseJson = new JSONObject(responseBody);
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

    public static String getGeminiSearchResults(String prompt) {
        try {
            String apiKey = getApiKey();
            if (apiKey == null) {
                System.err.println("Falha ao obter a chave API.");
                return null;
            }

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-04-17:generateContent?key=" + apiKey;

            JSONObject requestBody = new JSONObject();
            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("parts", new JSONObject().put("text", prompt));
            requestBody.put("contents", new JSONObject[]{content});
            JSONObject generationConfig = new JSONObject().put("response_mime_type", "text/plain");
            requestBody.put("generationConfig", generationConfig);
            JSONArray tools = new JSONArray();
            tools.put(new JSONObject().put("googleSearch", new JSONObject()));
            requestBody.put("tools", tools);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            Files.writeString(Paths.get("searchResponseBody.json"), responseBody.toString());

            JSONObject responseJson = new JSONObject(responseBody);
            JSONObject candidate = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0);
            // texto principal
            String text = candidate
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

            // extrai groundingChunks e monta lista de referências
            StringBuilder sb = new StringBuilder(text);
            if (candidate.has("groundingMetadata")) {
                JSONObject groundingMetadata = candidate.getJSONObject("groundingMetadata");
                if (groundingMetadata.has("groundingChunks")) {
                    JSONArray chunks = groundingMetadata.getJSONArray("groundingChunks");
                    sb.append("\n\n# Referências\n");
                    for (int i = 0; i < chunks.length(); i++) {
                        JSONObject web = chunks
                            .getJSONObject(i)
                            .getJSONObject("web");
                        String title = web.optString("title", null);
                        String uri   = web.getString("uri");
                        if (title != null && !title.isEmpty()) {
                            sb.append("- [")
                            .append(title)
                            .append("](")
                            .append(uri)
                            .append(")\n");
                        } else {
                            sb.append("- ")
                            .append(uri)
                            .append("\n");
                        }
                    }
                }
            }

            return sb.toString();
        } catch (Exception e) {
            System.err.println("Erro ao invocar Gemini: " + e.getMessage());
            return null;
        }
    }
}