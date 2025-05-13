import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleAgent {
    // Constantes estáticas
    public static final String MODEL = "gemini-1.5-flash";
    public static final double TEMPERATURE = 0.7;
    public static final String USER_PROMPT = "Que número é maior, 3,9 ou 3,234?";

    // Nome do arquivo para gravar a resposta
    private static final String RESPONSE_FILE = "response.md";
    
    public static void main(String[] args) {
        // Teste da invocação
        SimpleAgent agent = new SimpleAgent();
        agent.invoke();
    }

    public void invoke() {
        try {
            // Invoca a API do Gemini usando o método genérico da classe App
            String response = App.invokeGemini(MODEL, TEMPERATURE, USER_PROMPT, "text/plain", false);
            if (response == null) {
                System.err.println("Falha ao obter resposta do Gemini.");
                return;
            }

            // Grava a resposta em response.md
            Files.writeString(Paths.get(RESPONSE_FILE), "# Resposta do Gemini\n\n" + response);

            System.out.println("Resposta salva em " + RESPONSE_FILE);
        } catch (Exception e) {
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}