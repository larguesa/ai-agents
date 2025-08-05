import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.json.JSONArray;

/**
 * Agente de IA avançado para pesquisa em profundidade com múltiplas perspectivas.
 * 
 * Este agente implementa uma estratégia sofisticada de pesquisa multi-etapas,
 * combinando planejamento automático, busca diversificada e síntese inteligente
 * para produzir relatórios abrangentes sobre temas complexos.
 * 
 * METODOLOGIA DEEP SEARCH:
 * 
 * 1. FASE DE PLANEJAMENTO:
 *    - Decomposição automática do tema principal em sub-questões
 *    - Geração de prompts diversificados para explorar diferentes ângulos
 *    - Otimização dos prompts para maximizar a qualidade das respostas
 * 
 * 2. FASE DE EXECUÇÃO:
 *    - Pesquisa iterativa com busca web integrada
 *    - Acumulação progressiva de informações complementares
 *    - Coleta de referências e fontes verificáveis
 * 
 * 3. FASE DE SÍNTESE:
 *    - Consolidação inteligente de todas as informações coletadas
 *    - Geração de relatório estruturado e coerente
 *    - Preservação de links e referências importantes
 * 
 * VANTAGENS DESTA ABORDAGEM:
 * - Cobertura abrangente do tema pesquisado
 * - Múltiplas perspectivas e fontes de informação
 * - Relatórios mais ricos e fundamentados
 * - Descoberta de aspectos não óbvios do tema
 * - Qualidade superior a pesquisas simples
 * 
 * CASOS DE USO IDEAIS:
 * - Pesquisa acadêmica e científica
 * - Análise de mercado e tendências
 * - Investigação de temas complexos
 * - Relatórios técnicos especializados
 * - Due diligence e análise abrangente
 * 
 * @author AI Agents Project
 * @version 1.0
 * @see App#getGeminiCompletion(String, double, String, String, boolean)
 */
public class DeepSearchAgent {
    /**
     * Modelo Gemini avançado para pesquisa e síntese complexa.
     * 
     * O modelo "gemini-2.0-flash" foi escolhido por suas capacidades superiores em:
     * - Planejamento automático de pesquisas
     * - Geração de prompts estratégicos diversificados
     * - Síntese inteligente de múltiplas fontes
     * - Manutenção de coerência em textos longos
     * - Processamento de grandes volumes de informação
     * 
     * Este modelo oferece o melhor equilíbrio entre qualidade analítica
     * e velocidade para operações multi-etapas complexas.
     */
    private static final String MODEL = "gemini-2.0-flash";
    
    /**
     * Nível de temperatura equilibrado para criatividade controlada.
     * 
     * Valor 0.7 é ideal para Deep Search porque:
     * - Permite criatividade na geração de prompts diversificados
     * - Mantém consistência lógica na síntese final
     * - Equilibra exploração e precisão
     * - Favorece descoberta de ângulos não óbvios
     * - Preserva qualidade analítica das respostas
     */
    private static final double TEMPERATURE = 0.7;
    
    /**
     * Tema principal para demonstração das capacidades de pesquisa profunda.
     * 
     * Este tema foi escolhido por ser:
     * - Altamente relevante para desenvolvedores e profissionais de TI
     * - Suficientemente amplo para permitir múltiplas perspectivas
     * - Rico em recursos especializados (livros, artigos, estudos)
     * - Demonstrativo do valor da pesquisa multi-etapas
     * 
     * ASPECTOS QUE PODEM SER EXPLORADOS:
     * - Fundamentos teóricos de prompt engineering
     * - Técnicas específicas para desenvolvedores
     * - Literatura especializada em português
     * - Ferramentas e frameworks relevantes
     * - Casos de uso práticos em TI
     * - Tendências e desenvolvimentos recentes
     */
    public static final String USER_PROMPT = "Quero uma pesquisa sobre engenharia de prompt otimizada para devs e profissionais de TI. E quero indicação de livros especializados especificamente sobre este assunto";
    
    /**
     * Nome do arquivo onde o relatório final será persistido.
     * 
     * O arquivo conterá:
     * - Relatório técnico detalhado e estruturado
     * - Síntese de todas as pesquisas realizadas
     * - Recomendações de livros e recursos
     * - Referencias organizadas e verificáveis
     * - Insights e análises consolidadas
     */
    private static final String RESPONSE_FILE = "response.md";
    
    /**
     * Número de prompts diversificados gerados para exploração do tema.
     * 
     * Valor 3 representa equilíbrio otimizado entre:
     * - Cobertura abrangente do tema (múltiplas perspectivas)
     * - Tempo de execução razoável (3 chamadas de busca)
     * - Custo computacional controlado
     * - Qualidade da síntese final
     * 
     * Para temas mais complexos, pode ser aumentado para 5-7.
     * Para pesquisas mais rápidas, pode ser reduzido para 2.
     */
    private static final int PRODUNDIDADE = 3;

    /**
     * Método principal - orquestração do processo de pesquisa profunda.
     * 
     * Este método implementa um fluxo sofisticado de pesquisa em múltiplas
     * etapas, combinando planejamento automático, execução distribuída e
     * síntese inteligente para produzir relatórios de alta qualidade.
     * 
     * FASE 1 - PLANEJAMENTO ESTRATÉGICO:
     * 
     * Objetivo: Decomposição inteligente do tema em sub-questões complementares
     * 
     * Processo:
     * - Analisa o tema principal fornecido pelo usuário
     * - Gera array JSON de prompts estratégicos diversificados
     * - Cada prompt explora um ângulo diferente do tema
     * - Prompts são otimizados para busca web e coleta de referências
     * 
     * Características dos prompts gerados:
     * - Focados em conteúdo relevante e detalhado
     * - Orientados para descoberta de recursos (livros, artigos, sites)
     * - Diversificados para evitar redundância
     * - Otimizados para síntese posterior
     * 
     * FASE 2 - EXECUÇÃO DISTRIBUÍDA:
     * 
     * Objetivo: Pesquisa iterativa com acumulação progressiva de conhecimento
     * 
     * Para cada prompt gerado:
     * - Executa busca web integrada (search=true)
     * - Obtém resposta fundamentada com referências
     * - Acumula informações em buffer estruturado
     * - Preserva contexto para próximas iterações
     * 
     * Estrutura de acumulação:
     * ```
     * ## Pesquisa N
     * **Prompt:** [pergunta específica]
     * 
     * [resposta com referências]
     * ```
     * 
     * FASE 3 - SÍNTESE INTELIGENTE:
     * 
     * Objetivo: Consolidação de todas as informações em relatório coerente
     * 
     * Processo:
     * - Combina solicitação original + todas as pesquisas acumuladas
     * - Utiliza IA para estruturar e organizar informações
     * - Elimina redundâncias mantendo informações relevantes
     * - Preserva referências e links importantes
     * - Gera relatório final profissional
     * 
     * VANTAGENS DESTA IMPLEMENTAÇÃO:
     * - Cobertura muito mais abrangente que pesquisa simples
     * - Descoberta de recursos e referências não óbvias
     * - Múltiplas perspectivas sobre o mesmo tema
     * - Relatório final estruturado e profissional
     * - Escalabilidade através do parâmetro PROFUNDIDADE
     * 
     * CONSIDERAÇÕES DE PERFORMANCE:
     * - Requer (PROFUNDIDADE + 2) chamadas à API
     * - Tempo de execução proporcional à profundidade
     * - Uso intensivo de tokens devido ao acúmulo de contexto
     * - Custo compensado pela qualidade superior do resultado
     * 
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        try {
            // FASE 1: PLANEJAMENTO ESTRATÉGICO DA PESQUISA
            System.out.println("Planejando a pesquisa...");
            
            // Constrói instrução para geração de prompts diversificados
            String generationInstruction = String.format(
                "Retorne um array com %d strings de prompts de pesquisa otimizados para o Gemini derivados do tema %s. " +
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
            
            // FASE 2: EXECUÇÃO DISTRIBUÍDA DAS PESQUISAS
            // Parseia array JSON de prompts gerados
            JSONArray promptsArray = new JSONArray(promptsJson);
            StringBuilder respostaAcumulada = new StringBuilder();
            
            // Itera sobre cada prompt executando pesquisa individualizada
            for (int i = 0; i < promptsArray.length(); i++) {
                String prompt = promptsArray.getString(i);
                System.out.println((i + 1) + ". Pesquisando por '" + prompt + "'...");
                
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
                "Escreva um relatório detalhado e objetivo da pesquisa abaixo:\n\n" +
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