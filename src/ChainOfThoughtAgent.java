import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Agente de IA especializado em raciocínio Chain-of-Thought (Cadeia de Pensamento).
 * 
 * Este agente implementa uma técnica avançada de prompting onde o modelo
 * é instruído a "pensar em voz alta", explicando seu raciocínio passo-a-passo
 * antes de chegar à resposta final. Esta abordagem melhora significativamente
 * a precisão em tarefas que requerem raciocínio lógico, matemática e análise.
 * 
 * METODOLOGIA CHAIN-OF-THOUGHT:
 * 
 * 1. PRIMEIRA CHAMADA (Raciocínio Detalhado):
 *    - Solicita resposta completa com explicação passo-a-passo
 *    - O modelo expõe todo o processo de raciocínio
 *    - Permite identificar e corrigir erros de lógica
 * 
 * 2. SEGUNDA CHAMADA (Síntese Objetiva):
 *    - Processa a resposta detalhada anterior
 *    - Extrai apenas a conclusão final de forma concisa
 *    - Mantém a precisão obtida pelo raciocínio estruturado
 * 
 * VANTAGENS DESTA ABORDAGEM:
 * - Maior precisão em problemas complexos
 * - Transparência no processo de raciocínio
 * - Possibilidade de debugging do pensamento da IA
 * - Melhor performance em tarefas analíticas
 * 
 * CASOS DE USO IDEAIS:
 * - Problemas matemáticos e lógicos
 * - Análise de texto e contagem de elementos
 * - Questões que requerem múltiplas etapas
 * - Situações onde a explicação é importante
 * 
 * @author AI Agents Project
 * @version 1.0
 * @see App#getGeminiCompletion(String, double, String, String, boolean)
 */
public class ChainOfThoughtAgent {
    /**
     * Modelo Gemini otimizado para raciocínio estruturado.
     * 
     * O modelo "gemini-2.0-flash-lite" foi escolhido por oferecer:
     * - Capacidades avançadas de raciocínio lógico
     * - Velocidade otimizada para múltiplas chamadas sequenciais
     * - Excelente performance em tarefas analíticas
     * - Equilíbrio entre qualidade e eficiência
     * 
     * Este modelo é especialmente adequado para implementações
     * Chain-of-Thought que requerem processamento de múltiplas etapas.
     */
    public static final String MODEL = "gemini-2.0-flash-lite";
    
    /**
     * Nível de temperatura balanceado para raciocínio consistente.
     * 
     * Valor 0.7 é ideal para Chain-of-Thought porque:
     * - Mantém criatividade suficiente para explorar diferentes abordagens
     * - Preserva consistência lógica entre as etapas do raciocínio
     * - Evita aleatoriedade excessiva que prejudicaria a precisão
     * - Permite flexibilidade na explicação do processo
     * 
     * Para tarefas puramente lógicas, valores menores (0.3-0.5) podem
     * ser considerados para maximizar determinismo.
     */
    public static final double TEMPERATURE = 0.7;
    
    /**
     * Prompt de demonstração para teste de raciocínio analítico.
     * 
     * Este prompt específico foi escolhido porque:
     * - Requer análise sequencial de texto
     * - Envolve contagem e identificação posicional
     * - Testa capacidade de decomposição de problemas
     * - Permite verificação clara da resposta
     * 
     * ANÁLISE DO PROBLEMA:
     * Texto: "O rato roeu a roupa do rei de roma"
     * Palavras: [O][rato][roeu][a][roupa][do][rei][de][roma]
     * Quinta palavra: "roupa" 
     * Terceira letra de "roupa": "u"
     * 
     * Este tipo de prompt demonstra como Chain-of-Thought
     * melhora a precisão em tarefas que requerem múltiplas etapas.
     */
    public static final String USER_PROMPT = "Qual é a terceira letra da quinta palavra do texto 'O rato roeu a roupa do rei de roma'?";
    
    /**
     * Nome do arquivo onde serão persistidas ambas as respostas.
     * 
     * O arquivo conterá:
     * - Resposta objetiva (resultado final)
     * - Resposta detalhada (processo de raciocínio)
     * - Timestamp para identificação
     * 
     * Esta estrutura permite análise posterior do processo
     * de raciocínio utilizado pelo modelo.
     */
    private static final String RESPONSE_FILE = "response.md";
    
    /**
     * Método principal - implementação do processo Chain-of-Thought.
     * 
     * Este método demonstra a implementação prática da técnica Chain-of-Thought
     * através de um processo em duas fases:
     * 
     * FASE 1 - RACIOCÍNIO DETALHADO:
     * 
     * Objetivo: Obter explicação completa do processo de pensamento
     * 
     * Estratégia:
     * - Adiciona instrução explícita para raciocínio passo-a-passo
     * - Força o modelo a expor todo o processo de análise
     * - Permite identificação de erros intermediários
     * 
     * Prompt modificado: "{pergunta original} + Responda detalhadamente o raciocínio passo-a-passo antes da resposta final."
     * 
     * FASE 2 - SÍNTESE OBJETIVA:
     * 
     * Objetivo: Extrair resposta concisa mantendo a precisão obtida na fase 1
     * 
     * Estratégia:
     * - Utiliza a resposta detalhada como contexto
     * - Solicita síntese objetiva preservando a conclusão
     * - Elimina verbosidade mantendo a qualidade
     * 
     * Prompt de síntese: "Responda de forma resumida, direta e objetiva: {resposta detalhada}"
     * 
     * PERSISTÊNCIA E DOCUMENTAÇÃO:
     * 
     * O arquivo final contém:
     * 1. Resposta sintética (para uso prático)
     * 2. Resposta detalhada (para auditoria e debugging)
     * 3. Relacionamento claro entre ambas
     * 
     * VANTAGENS DESTA IMPLEMENTAÇÃO:
     * - Transparência total do processo
     * - Possibilidade de validação manual
     * - Debugging facilitado
     * - Melhoria iterativa do prompting
     * 
     * CONSIDERAÇÕES DE PERFORMANCE:
     * - Requer duas chamadas à API (custo 2x)
     * - Tempo de execução aproximadamente duplicado
     * - Tokens de entrada maiores na segunda chamada
     * - Compensado pela maior precisão obtida
     * 
     * @param args argumentos da linha de comando (não utilizados)
     */
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