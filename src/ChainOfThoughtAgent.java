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
            // FASE 1: Obtenção de resposta com raciocínio detalhado
            System.out.println("Obtendo resposta completa...");
            
            // Constrói prompt Chain-of-Thought adicionando instrução de raciocínio
            String chainPrompt = USER_PROMPT + ". Responda detalhadamente o raciocínio passo-a-passo antes da resposta final.";
            
            // Primeira chamada: foco no processo de raciocínio
            String chainResponse = App.getGeminiCompletion(
                MODEL, 
                TEMPERATURE, 
                chainPrompt, 
                "text/plain", 
                false  // Sem busca - baseado no conhecimento interno
            );
            
            // FASE 2: Síntese da resposta detalhada
            System.out.println("Obtendo resposta objetiva...");
            
            // Constrói prompt de síntese usando a resposta anterior como contexto
            String synthesisPrompt = "Responda de forma resumida, direta e objetiva: " + chainResponse;
            
            // Segunda chamada: foco na concisão mantendo precisão
            String response = App.getGeminiCompletion(
                MODEL, 
                TEMPERATURE, 
                synthesisPrompt, 
                "text/plain", 
                false  // Sem busca - processa conteúdo já obtido
            );
            
            // FASE 3: Formatação e persistência do resultado completo
            // Cria estrutura que preserva tanto síntese quanto processo detalhado
            String formattedContent = String.format(
                "# Resposta do Gemini em %s\n\n%s\n\n\n...mas a resposta anterior foi: %s",
                new Date(),
                response,        // Resposta sintética (uso prático)
                chainResponse    // Resposta detalhada (auditoria)
            );
            
            // Persiste resultado completo para análise posterior
            Files.writeString(Paths.get(RESPONSE_FILE), formattedContent);
            
            // FASE 4: Feedback de conclusão
            System.out.println("Resposta salva em " + RESPONSE_FILE);
            
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura falhas em qualquer fase do processo
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}