import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class SimpleSearchAgent {
    public static final String USER_PROMPT = "Quais os valores do dólar e do euro agora?";
    private static final String RESPONSE_FILE = "response.md";

    public static void main(String[] args) {
        try {
            // Invoca a API do Gemini usando o método genérico da classe App
            String response = App.getGeminiSearchResults(USER_PROMPT);
            // Grava a resposta em response.md
            Files.writeString(Paths.get(RESPONSE_FILE), "# Resposta do Gemini em "+new Date()+"\n\n" + response);
            System.out.println("Resposta salva em "+RESPONSE_FILE);
        } catch (Exception e) {
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}
