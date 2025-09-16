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
            // FASE 1: Chamada à API Gemini
            // Utiliza método centralizado da classe App para comunicação
            // Parâmetros: modelo, temperatura, prompt, tipo resposta, busca
            String response = App.getGeminiCompletion(MODEL, TEMPERATURE, USER_PROMPT, "text/plain", false);
            
            // FASE 2: Formatação e persistência da resposta
            // Cria cabeçalho Markdown com timestamp para identificação
            // Combina cabeçalho com resposta do modelo
            String formattedContent = "# Resposta do Gemini em " + new Date() + "\n\n" + response;
            
            // Grava conteúdo formatado no arquivo de destino
            Files.writeString(Paths.get(RESPONSE_FILE), formattedContent);
            
            // FASE 3: Feedback de sucesso para o usuário
            System.out.println("Resposta salva em " + RESPONSE_FILE);
            
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura qualquer falha durante execução
            // Exibe mensagem descritiva mantendo a aplicação estável
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}