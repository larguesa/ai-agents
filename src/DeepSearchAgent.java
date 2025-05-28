import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.json.JSONArray;

public class DeepSearchAgent {
    // Configurações do modelo
    private static final String MODEL = "gemini-2.0-flash";
    private static final double TEMPERATURE = 0.7;
    public static final String USER_PROMPT = "Quero uma pesquisa sobre engenharia de prompt otimizada para devs e profissionais de TI. E quero indicação de livros especializados especificamente sobre este assunto";
    private static final String RESPONSE_FILE = "response.md";
    private static final int PRODUNDIDADE = 3;

    public static void main(String[] args) {
        try {
            // 1. Gerar JSON com prompts diversificados
            System.out.println("Planejando a pesquisa...");
            String generationInstruction = "Retorne um array com "+PRODUNDIDADE+" strings de prompts de pesquisa otimizados para o Gemini derivados do tema "+USER_PROMPT+
                ". Cada prompt deve obter uma resposta diversificada do tema e as respostas serão acumuladas para geração de um relatório final pela própria IA"+
                ", então as respostas devem conter conteúdo relevante ao tema original detalhado"+
                ", incluindo no corpo do relatório os links de referências de livros recomendados em português, artigos, estudos e sites relevantes"+
                ". Retorne **somente** um array JSON de %d strings. Exemplo de formato esperado:\n" +
                "[\"prompt1\", \"prompt2\", …]";
            String promptsJson = App.getGeminiCompletion(MODEL, TEMPERATURE, generationInstruction, "application/json", false);
            // 2. Para cada prompt gerado, buscar resposta objetiva com search=true
            JSONArray promptsArray = new JSONArray(promptsJson);
            StringBuilder respostaAcumulada = new StringBuilder();
            for (int i = 0; i < promptsArray.length(); i++) {
                String prompt = promptsArray.getString(i);
                System.out.println((i+1)+". Pesquisando por '"+prompt+"'...");
                String resposta = App.getGeminiCompletion(MODEL, TEMPERATURE, prompt, "text/plain", true);
                respostaAcumulada
                    .append("## Pesquisa ").append(i + 1).append("\n")
                    .append("**Prompt:** ").append(prompt).append("\n\n")
                    .append(resposta).append("\n\n");
            }
            // 3. Chamada final: resposta direta ao prompt original
            String promptFinal = "Escreva um relatório detalhado e objetivo da pesquisa abaixo:\n\n"
                +"SOLICITAÇÃO INICIAL: "+USER_PROMPT+"\n\nPESQUISA: "+respostaAcumulada.toString();
            System.out.println("Consolidando resultados...");
            String respostaFinal = App.getGeminiCompletion(MODEL, TEMPERATURE, promptFinal, "text/plain", false);
            // 4. Gravar tudo no arquivo de saída
            Files.writeString(Paths.get(RESPONSE_FILE), respostaFinal);
            System.out.println("Resultados salvos em " + new Date() + " no arquivo " + RESPONSE_FILE);
        } catch (Exception e) {
            System.err.println("Erro no DeepSearchAgent: " + e.getMessage());
        }
    }
}