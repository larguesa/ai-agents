import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Agente de IA simples para demonstração básica das funcionalidades do Gemini.
 * 
 * Este agente implementa o caso de uso mais básico de interação com a API Gemini,
 * servindo como exemplo introdutório e ponto de partida para desenvolvedores
 * que desejam compreender o funcionamento fundamental do sistema.
 * 
 * FUNCIONALIDADES PRINCIPAIS:
 * - Execução de prompt pré-definido sem busca na web
 * - Utilização de configurações padrão de modelo e temperatura
 * - Persistência da resposta em formato Markdown
 * - Demonstração do fluxo completo: prompt → API → arquivo
 * 
 * CONFIGURAÇÕES UTILIZADAS:
 * - Modelo: gemini-1.5-flash (equilibrio entre velocidade e qualidade)
 * - Temperatura: 0.7 (criatividade moderada)
 * - Tipo de resposta: text/plain (texto simples)
 * - Busca: desabilitada (resposta baseada apenas no conhecimento do modelo)
 * 
 * CASO DE USO:
 * Este agente é ideal para:
 * - Primeiros testes da integração com Gemini
 * - Validação da configuração da API
 * - Demonstrações educacionais
 * - Base para desenvolvimento de agentes mais complexos
 * 
 * @author AI Agents Project
 * @version 1.0
 * @see App#getGeminiCompletion(String, double, String, String, boolean)
 */
public class SimpleAgent {
    /**
     * Modelo Gemini utilizado para geração de respostas.
     * 
     * O modelo "gemini-1.5-flash" oferece um equilíbrio otimizado entre:
     * - Velocidade de resposta (ideal para testes e demonstrações)
     * - Qualidade do conteúdo gerado
     * - Custo computacional reduzido
     * 
     * Este modelo é recomendado para casos de uso que não requerem
     * as capacidades mais avançadas dos modelos maiores.
     */
    public static final String MODEL = "gemini-1.5-flash";
    
    /**
     * Nível de temperatura para controle da criatividade das respostas.
     * 
     * Valor 0.7 representa um equilibrio moderado onde:
     * - 0.0 = Respostas completamente determinísticas e conservadoras
     * - 0.7 = Equilibrio entre consistência e criatividade (VALOR ATUAL)
     * - 1.0 = Máxima criatividade e variabilidade
     * 
     * Este valor é adequado para a maioria dos casos de uso gerais,
     * proporcionando respostas consistentes com alguma variabilidade.
     */
    public static final double TEMPERATURE = 0.7;
    
    /**
     * Prompt de demonstração utilizado pelo agente.
     * 
     * Este prompt específico foi escolhido para demonstrar:
     * - Capacidade de raciocínio matemático básico
     * - Comparação de números decimais
     * - Resposta objetiva e clara
     * 
     * O exemplo utiliza números decimais (3,8 vs 3,72) para testar
     * a capacidade do modelo de interpretar corretamente a notação
     * decimal em português e realizar comparações precisas.
     */
    public static final String USER_PROMPT = "Que número é maior, 3,8 ou 3,72?";
    
    /**
     * Nome do arquivo onde a resposta do Gemini será persistida.
     * 
     * O arquivo é criado no diretório de trabalho atual e utiliza
     * formato Markdown (.md) para:
     * - Facilitar visualização em editores de texto
     * - Permitir formatação rica quando necessário
     * - Manter compatibilidade com ferramentas de documentação
     * 
     * IMPORTANTE: Se múltiplos agentes executarem simultaneamente,
     * este arquivo pode ser sobrescrito. Para uso em produção,
     * considere implementar nomes únicos baseados em timestamp.
     */
    private static final String RESPONSE_FILE = "response.md";
    
    /**
     * Método principal - ponto de entrada para execução do agente simples.
     * 
     * Este método demonstra o fluxo básico de um agente de IA:
     * 
     * 1. CHAMADA À API:
     *    - Utiliza a classe App como ponte para comunicação com Gemini
     *    - Envia prompt pré-configurado com parâmetros fixos
     *    - Não utiliza funcionalidades de busca (search=false)
     * 
     * 2. PROCESSAMENTO DA RESPOSTA:
     *    - Recebe resposta em formato texto simples
     *    - Valida se a resposta foi obtida com sucesso
     * 
     * 3. PERSISTÊNCIA:
     *    - Formata resposta com cabeçalho Markdown incluindo timestamp
     *    - Grava arquivo no sistema de arquivos local
     *    - Fornece feedback visual sobre o sucesso da operação
     * 
     * 4. TRATAMENTO DE ERROS:
     *    - Captura qualquer exceção durante o processo
     *    - Exibe mensagem de erro descritiva
     *    - Permite que a aplicação continue funcionando
     * 
     * ESTRUTURA DO ARQUIVO GERADO:
     * ```markdown
     * # Resposta do Gemini em [timestamp]
     * 
     * [resposta do modelo]
     * ```
     * 
     * CASOS DE USO TÍPICOS:
     * - Verificação rápida da conectividade com Gemini
     * - Teste de configurações básicas
     * - Exemplo educacional para novos desenvolvedores
     * - Base para desenvolvimento de agentes customizados
     * 
     * @param args argumentos da linha de comando (não utilizados nesta implementação)
     */
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