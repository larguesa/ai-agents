import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class SimpleSearchAgent {

    public static final String MODEL = "gemini-2.5-flash";

    public static final double TEMPERATURE = 0.7;

    public static final String USER_PROMPT = "Quais os valores do dólar e do euro agora?";
    
    private static final String RESPONSE_FILE = "response.md";

    public static void main(String[] args) {
        try {
            // FASE 1: Chamada à API Gemini
            // Utiliza método centralizado da classe App para comunicação
            // Parâmetros: modelo, temperatura, prompt, tipo resposta, busca
            String response = App.getGeminiCompletion(MODEL, TEMPERATURE, USER_PROMPT, "text/plain", true);
            
            // FASE 2: Formatação com cabeçalho temporal
            // Adiciona contexto temporal para identificação da consulta
            String formattedContent = "# Resposta do Gemini em " + new Date() + "\n\n" + response;
            
            // FASE 3: Persistência da resposta fundamentada
            // Salva conteúdo completo incluindo referências automáticas
            Files.writeString(Paths.get(RESPONSE_FILE), formattedContent);
            
            // FASE 4: Confirmação de sucesso
            System.out.println("Resposta salva em " + RESPONSE_FILE);
            
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura falhas na busca ou persistência
            System.err.println("Erro ao gravar resposta: " + e.getMessage());
        }
    }
}
