import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.json.JSONArray;

public class DeepSearchAgent {

    private static final String MODEL = "gemini-2.0-flash";

    private static final double TEMPERATURE = 0.7;

    public static final String USER_PROMPT = "Quero uma pesquisa sobre engenharia de prompt otimizada para devs e profissionais de TI. E quero indicação de livros especializados especificamente sobre este assunto";

    private static final String RESPONSE_FILE = "response.md";

    private static final int PRODUNDIDADE = 2;

    public static void main(String[] args) {
        try {
            // FASE 1: PLANEJAMENTO ESTRATÉGICO DA PESQUISA
            System.out.println("Planejando a pesquisa...");
            
            // Constrói instrução para geração de prompts diversificados
            String generationInstruction = String.format(
                "Data/hora atual: "+new java.util.Date()+". Retorne um array com %d strings de prompts de pesquisa otimizados para o Gemini derivados do tema %s. " +
                "Cada prompt deve obter uma resposta diversificada do tema e as respostas serão acumuladas para geração de um relatório final pela própria IA, " +
                "então as respostas devem conter conteúdo relevante ao tema original detalhado, " +
                "incluindo no corpo do relatório os links de referências de livros recomendados em português, artigos, estudos e sites relevantes. " +
                "Retorne **somente** um array JSON de %d strings. Exemplo de formato esperado:\n[\"prompt1\", \"prompt2\", …]",
                PRODUNDIDADE,
                USER_PROMPT,
                PRODUNDIDADE
            );
            
            // Gera array de prompts estratégicos usando capacidades de planejamento da IA
            String promptsJson = App.getGeminiCompletion(
                MODEL, 
                TEMPERATURE, 
                generationInstruction, 
                "application/json", 
                false  // Planejamento baseado em conhecimento, não busca
            );

            // Salva apenas o relatório consolidado (não as pesquisas intermediárias)
            Files.writeString(Paths.get("promptsJson"), promptsJson);
            
            // FASE 2: EXECUÇÃO DISTRIBUÍDA DAS PESQUISAS
            // Parseia array JSON de prompts gerados
            JSONArray promptsArray = new JSONArray(promptsJson);
            StringBuilder respostaAcumulada = new StringBuilder();
            
            // Itera sobre cada prompt executando pesquisa individualizada
            for (int i = 0; i < promptsArray.length(); i++) {
                String prompt = promptsArray.getString(i);
                System.out.println((i + 1) + " Data/hora atual: " + new java.util.Date() + ". Pesquisando por '" + prompt + "'...");

                // Executa pesquisa com busca web integrada
                String resposta = App.getGeminiCompletion(
                    MODEL, 
                    TEMPERATURE, 
                    prompt, 
                    "text/plain", 
                    true  // BUSCA HABILITADA - acesso a informações atuais
                );
                
                // Acumula resposta com estrutura organizada
                respostaAcumulada
                    .append("## Pesquisa ").append(i + 1).append("\n")
                    .append("**Prompt:** ").append(prompt).append("\n\n")
                    .append(resposta).append("\n\n");
            }
            
            // FASE 3: SÍNTESE INTELIGENTE DO RELATÓRIO FINAL
            System.out.println("Consolidando resultados...");
            
            // Constrói prompt de síntese combinando solicitação original + pesquisas
            String promptFinal = String.format(
                "Data/hora atual: "+new java.util.Date()+". Escreva um relatório detalhado e objetivo da pesquisa abaixo:\n\n" +
                "SOLICITAÇÃO INICIAL: %s\n\nPESQUISA: %s",
                USER_PROMPT,
                respostaAcumulada.toString()
            );
            
            // Gera relatório final consolidado
            String respostaFinal = App.getGeminiCompletion(
                MODEL, 
                TEMPERATURE, 
                promptFinal, 
                "text/plain", 
                false  // Síntese baseada no conteúdo já pesquisado
            );
            
            // FASE 4: PERSISTÊNCIA DO RESULTADO FINAL
            // Salva apenas o relatório consolidado (não as pesquisas intermediárias)
            Files.writeString(Paths.get(RESPONSE_FILE), respostaFinal);
            
            // Feedback de conclusão com timestamp
            System.out.println("Resultados salvos em " + new Date() + " no arquivo " + RESPONSE_FILE);
            
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura falhas em qualquer fase do processo
            System.err.println("Erro no DeepSearchAgent: " + e.getMessage());
        }
    }
}