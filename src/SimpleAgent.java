import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class SimpleAgent {
    public static final String MODEL = "gemini-1.5-flash";
    public static final double TEMPERATURE = 0.7;
    public static final String USER_PROMPT = "Que número é maior, 3,8 ou 3,72?";
    private static final String RESPONSE_FILE = "response.md";
    
    public static void main(String[] args) {
        try {
            // Invoca a API do Gemini usando o método genérico da classe App
            String response = App.getGeminiCompletion(MODEL, TEMPERATURE, USER_PROMPT, "text/plain", false);
            // Grava a resposta em response.md
            Files.writeString(Paths.get(RESPONSE_FILE), "# Resposta do Gemini em "+new Date()+"\n\n" + response);
            System.out.println("Resposta salva em " + RESPONSE_FILE);
        } catch (Exception e) {
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}