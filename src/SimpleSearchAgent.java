import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Agente de IA simples com capacidades de busca na web integrada.
 * 
 * Este agente demonstra o uso das funcionalidades de busca em tempo real
 * do Gemini, permitindo que o modelo acesse informações atualizadas da
 * internet para fundamentar suas respostas. É ideal para consultas que
 * requerem dados recentes ou informações dinâmicas.
 * 
 * FUNCIONALIDADES PRINCIPAIS:
 * 
 * 1. BUSCA INTEGRADA:
 *    - Utiliza Google Search como ferramenta auxiliar
 *    - Acesso a informações atualizadas em tempo real
 *    - Fundamentação das respostas com fontes verificáveis
 * 
 * 2. GROUNDING (FUNDAMENTAÇÃO):
 *    - Respostas incluem links para fontes originais
 *    - Referências organizadas em formato Markdown
 *    - Transparência sobre origem das informações
 * 
 * 3. SIMPLICIDADE DE USO:
 *    - Uma única chamada à API resolve tudo
 *    - Configuração mínima necessária
 *    - Resultado pronto para consumo
 * 
 * CASOS DE USO IDEAIS:
 * - Consultas sobre cotações e preços atuais
 * - Informações sobre eventos recentes
 * - Dados que mudam frequentemente
 * - Verificação de fatos e estatísticas
 * - Pesquisas que requerem múltiplas fontes
 * 
 * LIMITAÇÕES:
 * - Dependente da disponibilidade da busca
 * - Custo computacional ligeiramente maior
 * - Tempo de resposta pode ser maior devido à busca
 * 
 * @author AI Agents Project
 * @version 1.0
 * @see App#getGeminiSearchResults(String)
 */
public class SimpleSearchAgent {
    /**
     * Prompt de demonstração para consulta de dados financeiros atuais.
     * 
     * Este prompt foi escolhido porque:
     * - Demonstra necessidade de informações em tempo real
     * - Valores de câmbio mudam constantemente
     * - Permite validação da atualidade das informações
     * - Mostra capacidade de buscar múltiplos dados relacionados
     * 
     * CARACTERÍSTICAS DA CONSULTA:
     * - Requer dados financeiros atualizados
     * - Informações disponíveis em múltiplas fontes
     * - Permite comparação de precisão entre fontes
     * - Demonstra valor da busca integrada vs conhecimento estático
     * 
     * EXPECTATIVA DE RESPOSTA:
     * - Valores atuais de USD/BRL e EUR/BRL
     * - Fontes das informações (sites financeiros)
     * - Possível contexto sobre variações recentes
     * - Links para verificação das cotações
     */
    public static final String USER_PROMPT = "Quais os valores do dólar e do euro agora?";
    
    /**
     * Nome do arquivo onde a resposta fundamentada será persistida.
     * 
     * O arquivo conterá:
     * - Resposta principal com informações atualizadas
     * - Seção "# Referências" com links das fontes
     * - Formatação Markdown para fácil leitura
     * - Timestamp para identificação temporal
     * 
     * IMPORTANTE: Para uso em produção com múltiplos agentes,
     * considere implementar nomes únicos para evitar conflitos.
     */
    private static final String RESPONSE_FILE = "response.md";

    /**
     * Método principal - demonstração de busca simples com fundamentação.
     * 
     * Este método ilustra o uso mais direto das capacidades de busca
     * do Gemini, onde uma única chamada resolve tanto a pesquisa quanto
     * a geração de resposta fundamentada.
     * 
     * FLUXO DE EXECUÇÃO:
     * 
     * 1. CHAMADA COM BUSCA HABILITADA:
     *    - Utiliza método especializado getGeminiSearchResults()
     *    - Automaticamente ativa ferramentas de busca do Gemini
     *    - Processa consulta e executa buscas relevantes
     * 
     * 2. PROCESSAMENTO INTERNO (realizado pelo Gemini):
     *    - Interpreta a consulta do usuário
     *    - Executa buscas estratégicas no Google
     *    - Analisa e sintetiza informações encontradas
     *    - Gera resposta fundamentada com referências
     * 
     * 3. RESPOSTA ESTRUTURADA:
     *    - Conteúdo principal baseado em dados atuais
     *    - Seção automática de referências em Markdown
     *    - Links clicáveis para verificação das fontes
     * 
     * 4. PERSISTÊNCIA E FEEDBACK:
     *    - Salva resposta completa incluindo referências
     *    - Adiciona cabeçalho com timestamp
     *    - Fornece confirmação de conclusão
     * 
     * VANTAGENS DESTA ABORDAGEM:
     * - Simplicidade máxima de implementação
     * - Informações sempre atualizadas
     * - Transparência total das fontes
     * - Verificabilidade das informações
     * - Formatação profissional automática
     * 
     * EXEMPLO DE SAÍDA ESPERADA:
     * ```markdown
     * # Resposta do Gemini em [timestamp]
     * 
     * O dólar americano está cotado a R$ X,XX e o euro a R$ Y,YY...
     * 
     * # Referências
     * - [Banco Central do Brasil](https://www.bcb.gov.br)
     * - [Investing.com](https://br.investing.com)
     * ```
     * 
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        try {
            // FASE 1: Execução de busca fundamentada
            // Chama método especializado que combina busca + geração de resposta
            String response = App.getGeminiSearchResults(USER_PROMPT);
            
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
