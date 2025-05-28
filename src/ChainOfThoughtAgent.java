import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class ChainOfThoughtAgent {
    public static final String MODEL = "gemini-2.0-flash-lite";
    public static final double TEMPERATURE = 0.7;
    public static final String USER_PROMPT = "Qual é a terceira letra da quinta palavra do texto 'O rato roeu a roupa do rei de roma'?";
    private static final String RESPONSE_FILE = "response.md";
    
    public static void main(String[] args) {
        try {
            System.out.println("Obtendo resposta completa...");
            String chainResponse = App.getGeminiCompletion(MODEL, TEMPERATURE, USER_PROMPT+". Responda detalhadamente o raciocínio passo-a-passo antes da resposta final.", "text/plain", false);
            System.out.println("Obtendo resposta objetiva...");
            String response = App.getGeminiCompletion(MODEL, TEMPERATURE, "Responda de forma resumida, direta e objetiva: "+chainResponse, "text/plain", false);
            // Grava a resposta em response.md
            Files.writeString(Paths.get(RESPONSE_FILE), "# Resposta do Gemini em "+new Date()+"\n\n" + response+"\n\n\n...mas a resposta anterior foi: "+chainResponse);
            System.out.println("Resposta salva em " + RESPONSE_FILE);
        } catch (Exception e) {
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}