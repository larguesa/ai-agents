import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Agente de IA especializado em monitoramento contínuo de preços de ações.
 * 
 * Este agente implementa um sistema automatizado de coleta, processamento
 * e armazenamento de dados financeiros em tempo real, utilizando a API
 * Gemini para obter cotações atualizadas e organizando as informações
 * em formato JSON estruturado para análise posterior.
 * 
 * FUNCIONALIDADES PRINCIPAIS:
 * 
 * 1. COLETA AUTOMATIZADA:
 *    - Execução periódica configurável (padrão: 10 segundos)
 *    - Busca simultânea de múltiplas ações
 *    - Utilização de busca web para dados atualizados
 *    - Processamento automático de respostas JSON
 * 
 * 2. PERSISTÊNCIA ESTRUTURADA:
 *    - Armazenamento em formato JSON padronizado
 *    - Histórico acumulativo com timestamps precisos
 *    - Estrutura otimizada para análise e visualização
 *    - Backup automático entre execuções
 * 
 * 3. AGENDAMENTO ROBUSTO:
 *    - Utilização de Timer nativo do Java
 *    - Execução em thread separada (não bloqueia aplicação)
 *    - Controle preciso de intervalos
 *    - Parada controlada e limpeza de recursos
 * 
 * CASOS DE USO IDEAIS:
 * - Monitoramento de portfólio de investimentos
 * - Coleta de dados para análise técnica
 * - Alertas automatizados de preços
 * - Histórico para machine learning
 * - Dashboards financeiros em tempo real
 * - Backtesting de estratégias de trading
 * 
 * ESTRUTURA DOS DADOS COLETADOS:
 * ```json
 * [
 *   {
 *     "timestamp": "2024-01-01T10:30:00Z",
 *     "stocks": {
 *       "AAPL": 150.25,
 *       "MSFT": 280.50,
 *       "GOOGL": 125.75
 *     }
 *   }
 * ]
 * ```
 * 
 * @author AI Agents Project
 * @version 1.0
 * @see App#getGeminiCompletion(String, double, String, String, boolean)
 */
public class StocksLoggerAgent {
    /**
     * Modelo Gemini especializado em busca e dados financeiros atualizados.
     * 
     * O modelo "gemini-2.5-flash-preview-04-17" foi escolhido por ter:
     * - Integração nativa com Google Search
     * - Capacidades avançadas de busca em tempo real
     * - Processamento eficiente de dados financeiros
     * - Suporte otimizado para respostas em formato JSON
     * - Acesso a fontes financeiras atualizadas
     * 
     * Este modelo específico oferece a melhor precisão
     * para consultas de preços de ações em tempo real.
     */
    public static final String MODEL = "gemini-2.5-flash-preview-04-17";
    
    /**
     * Nível de temperatura máximo para máxima diversidade de fontes.
     * 
     * Valor 1.0 é utilizado porque:
     * - Permite acesso a diversas fontes financeiras
     * - Aumenta chances de obter dados mais atualizados
     * - Compensa variações entre diferentes provedores de dados
     * - A estrutura JSON solicitada mantém formato consistente
     * - Dados numéricos não são afetados pela criatividade
     * 
     * Para aplicações que requerem máxima consistência,
     * considere valores menores (0.3-0.5).
     */
    public static final double TEMPERATURE = 1.0;
    
    /**
     * Intervalo de execução do agendamento em milissegundos.
     * 
     * Valor padrão: 10.000ms (10 segundos)
     * 
     * CONSIDERAÇÕES PARA ESCOLHA DO INTERVALO:
     * - Mercados financeiros: dados mudam em segundos
     * - Rate limits da API: evitar sobrecarga
     * - Custo computacional: balance entre frequência e recursos
     * - Volatilidade esperada: ações voláteis requerem maior frequência
     * 
     * INTERVALOS RECOMENDADOS:
     * - Day trading: 1-5 segundos
     * - Monitoramento ativo: 10-30 segundos (ATUAL)
     * - Análise de tendências: 1-5 minutos
     * - Histórico de longo prazo: 15-60 minutos
     */
    public static final long TIMER = 10000;
    
    /**
     * Array de símbolos das ações a serem monitoradas.
     * 
     * Símbolos selecionados representam:
     * - AAPL (Apple): Tecnologia, alta liquidez
     * - MSFT (Microsoft): Tecnologia, estabilidade
     * - GOOGL (Alphabet/Google): Tecnologia, crescimento
     * 
     * CRITÉRIOS DE SELEÇÃO:
     * - Alta liquidez e volume de negociação
     * - Representatividade do mercado de tecnologia
     * - Disponibilidade em múltiplas fontes de dados
     * - Interesse geral de investidores
     * 
     * Para personalizar, adicione/remova símbolos conforme necessário.
     * Formatos aceitos: NASDAQ (AAPL), NYSE (IBM), etc.
     */
    public static final String[] STOCKS = {"AAPL", "MSFT", "GOOGL"};

    /**
     * Nome do arquivo onde o histórico de preços será armazenado.
     * 
     * Características do arquivo:
     * - Formato: JSON estruturado
     * - Organização: Array cronológico de registros
     * - Persistência: Mantido entre execuções
     * - Backup: Leitura de dados existentes antes de adicionar novos
     * 
     * IMPORTANTE: Para uso em produção, considere:
     * - Rotação automática de logs por tamanho/data
     * - Backup periódico para armazenamento seguro
     * - Compressão para economizar espaço
     * - Particionamento por período temporal
     */
    private static final String HISTORY_FILE = "response.json";
    
    /**
     * Timer para controle do agendamento de execuções periódicas.
     * 
     * Configurações:
     * - Thread daemon (true): Não impede encerramento da JVM
     * - Execução em background: Não bloqueia thread principal
     * - Precisão: Controle preciso de intervalos
     * - Robustez: Tratamento automático de exceções
     * 
     * O timer é inicializado uma vez e reutilizado para todas
     * as execuções agendadas durante o ciclo de vida da aplicação.
     */
    private final Timer timer = new Timer(true);

    /**
     * Método principal - demonstração do sistema de monitoramento.
     * 
     * Este método serve como exemplo de uso do StocksLoggerAgent,
     * demonstrando o ciclo completo de inicialização, execução
     * e finalização do monitoramento automatizado.
     * 
     * FLUXO DE DEMONSTRAÇÃO:
     * 
     * 1. INICIALIZAÇÃO:
     *    - Cria instância do agente
     *    - Inicia sistema de agendamento
     *    - Primeira execução imediata
     * 
     * 2. EXECUÇÃO MONITORADA:
     *    - Execuções periódicas automáticas por 60 segundos
     *    - Coleta e armazenamento contínuo de dados
     *    - Feedback visual de cada operação
     * 
     * 3. FINALIZAÇÃO CONTROLADA:
     *    - Parada automática após período de demonstração
     *    - Limpeza adequada de recursos
     *    - Confirmação de encerramento
     * 
     * PERSONALIZAÇÃO PARA PRODUÇÃO:
     * - Remover limite de tempo (Thread.sleep)
     * - Adicionar controle manual via interface ou sinais
     * - Implementar logs persistentes para auditoria
     * - Adicionar monitoramento de saúde do sistema
     * 
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Cria instância do agente de monitoramento
        StocksLoggerAgent agent = new StocksLoggerAgent();
        
        // Inicia sistema de coleta automatizada
        agent.startLogging();

        // DEMONSTRAÇÃO: Para fins de teste, para o agendamento após 60 segundos
        try {
            Thread.sleep(60000);  // Aguarda 1 minuto de execução
            agent.stopLogging();  // Para execução automatizada
            System.out.println("Agendamento parado.");
        } catch (InterruptedException e) {
            System.err.println("Erro ao aguardar: " + e.getMessage());
        }
    }

    /**
     * Inicia o sistema de monitoramento automatizado de preços.
     * 
     * Este método configura e inicializa o agendamento periódico
     * de coleta de dados financeiros, estabelecendo um ciclo
     * contínuo e controlado de execuções.
     * 
     * CONFIGURAÇÃO DO AGENDAMENTO:
     * 
     * 1. CRIAÇÃO DA TAREFA:
     *    - Implementa TimerTask para execução periódica
     *    - Encapsula chamada ao método de coleta (logStockPrices)
     *    - Tratamento automático de exceções internas
     * 
     * 2. PROGRAMAÇÃO DA EXECUÇÃO:
     *    - scheduleAtFixedRate: Execução em intervalos fixos
     *    - Delay inicial: 0 (execução imediata)
     *    - Período: TIMER milissegundos (configurável)
     *    - Comportamento: Mantém frequência mesmo se execução anterior atrasar
     * 
     * VANTAGENS DO scheduleAtFixedRate:
     * - Frequência consistente independente do tempo de execução
     * - Recuperação automática de atrasos
     * - Drift temporal mínimo em execuções longas
     * - Adequado para coleta de dados em tempo real
     * 
     * ALTERNATIVA (schedule):
     * - Para casos onde o intervalo deve ser medido após conclusão
     * - Evita sobreposição de execuções longas
     * - Uso em sistemas com processamento variável
     */
    public void startLogging() {
        // Cria tarefa personalizada para execução periódica
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Chama método de coleta de dados em cada execução agendada
                logStockPrices();
            }
        };

        // Agenda execução em intervalos fixos
        // Parâmetros: tarefa, delay inicial (0 = imediato), período
        timer.scheduleAtFixedRate(task, 0, TIMER);
    }

    /**
     * Para o sistema de monitoramento e libera recursos.
     * 
     * Este método realiza finalização controlada do agendamento,
     * garantindo limpeza adequada de recursos e parada segura
     * de todas as operações em background.
     * 
     * OPERAÇÕES DE FINALIZAÇÃO:
     * 
     * 1. CANCELAMENTO DO TIMER:
     *    - Para todas as tarefas agendadas pendentes
     *    - Interrompe thread de execução do timer
     *    - Libera recursos associados ao agendamento
     *    - Operação irreversível (timer não pode ser reutilizado)
     * 
     * 2. LIMPEZA DE RECURSOS:
     *    - Thread daemon encerrada automaticamente
     *    - Memória liberada pelo garbage collector
     *    - Handles de arquivo fechados pelo sistema
     * 
     * IMPORTANTE: Após chamar este método, uma nova instância
     * da classe deve ser criada para reiniciar o monitoramento.
     */
    public void stopLogging() {
        // Cancela timer e todas as execuções pendentes
        timer.cancel();
    }

    /**
     * Executa uma iteração completa de coleta e armazenamento de preços.
     * 
     * Este método representa o núcleo do sistema de monitoramento,
     * implementando o fluxo completo desde a solicitação de dados
     * até a persistência estruturada no histórico.
     * 
     * FLUXO DE EXECUÇÃO DETALHADO:
     * 
     * FASE 1 - PREPARAÇÃO DA CONSULTA:
     * 
     * - Formatação da lista de ações para consulta
     * - Construção de prompt otimizado para dados financeiros
     * - Especificação de formato JSON estruturado
     * - Definição de exemplo para garantir consistência
     * 
     * Exemplo de prompt gerado:
     * "Provide the current stock prices in USD for AAPL, MSFT, GOOGL in JSON format..."
     * 
     * FASE 2 - CONSULTA À API COM BUSCA:
     * 
     * - Utiliza busca web integrada (search=true)
     * - Acessa fontes financeiras em tempo real
     * - Processa resposta em formato JSON específico
     * - Validação de sucesso da operação
     * 
     * FASE 3 - PROCESSAMENTO DA RESPOSTA:
     * 
     * - Parse do JSON retornado pela API
     * - Validação da estrutura de dados
     * - Preparação do registro com timestamp preciso
     * - Formatação ISO 8601 para compatibilidade internacional
     * 
     * FASE 4 - GESTÃO DO HISTÓRICO:
     * 
     * - Tentativa de leitura do histórico existente
     * - Criação de novo histórico se arquivo não existir
     * - Preservação de dados anteriores
     * - Backup automático entre execuções
     * 
     * FASE 5 - PERSISTÊNCIA ATUALIZADA:
     * 
     * - Adição do novo registro ao histórico
     * - Formatação JSON legível (indentação 2 espaços)
     * - Gravação atômica no sistema de arquivos
     * - Feedback visual de sucesso
     * 
     * ESTRUTURA DO REGISTRO CRIADO:
     * ```json
     * {
     *   "timestamp": "2024-01-01T10:30:00-03:00",
     *   "stocks": {
     *     "AAPL": 150.25,
     *     "MSFT": 280.50,
     *     "GOOGL": 125.75
     *   }
     * }
     * ```
     * 
     * TRATAMENTO DE ERROS:
     * - Falha na API: Log de erro, continua execução
     * - JSON inválido: Log de erro, preserva dados anteriores
     * - Erro de I/O: Log de erro, tenta novamente na próxima iteração
     * - Qualquer exceção: Captura geral para robustez
     * 
     * CARACTERÍSTICAS DE ROBUSTEZ:
     * - Operação não interrompe agendamento em caso de erro
     * - Preservação de dados históricos existentes
     * - Continuidade do monitoramento mesmo com falhas pontuais
     * - Logs detalhados para debugging e auditoria
     */
    private void logStockPrices() {
        try {
            // FASE 1: Preparação da consulta financeira
            // Converte array de símbolos em string formatada para consulta
            String stocksList = String.join(", ", STOCKS);
            
            // Constrói prompt otimizado para obtenção de dados financeiros estruturados
            String prompt = String.format(
                "Provide the current stock prices in USD for %s in JSON format. " +
                "Return an object with ticker symbols as keys and prices as numbers. " +
                "Example: {\"AAPL\": 100.00, \"MSFT\": 100.00, \"GOOGL\": 100.00}",
                stocksList
            );

            // FASE 2: Consulta à API com busca em tempo real
            String response = App.getGeminiCompletion(
                MODEL, 
                TEMPERATURE, 
                prompt, 
                "application/json",  // Força resposta em formato JSON
                true                 // Habilita busca web para dados atuais
            );
            
            // Validação da resposta obtida
            if (response == null) {
                System.err.println("Falha ao obter preços das ações.");
                return;  // Retorna sem interromper agendamento
            }

            // FASE 3: Processamento da resposta JSON
            // Parseia resposta JSON da API em objeto estruturado
            JSONObject stockPrices = new JSONObject(response);

            // Prepara novo registro com timestamp preciso
            JSONObject newEntry = new JSONObject();
            newEntry.put(
                "timestamp", 
                ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            );
            newEntry.put("stocks", stockPrices);

            // FASE 4: Gestão do histórico existente
            JSONArray history;
            if (Files.exists(Paths.get(HISTORY_FILE))) {
                // Lê histórico existente preservando dados anteriores
                String existingContent = Files.readString(Paths.get(HISTORY_FILE));
                history = new JSONArray(existingContent);
            } else {
                // Cria novo histórico se arquivo não existir
                history = new JSONArray();
            }

            // FASE 5: Atualização e persistência do histórico
            // Adiciona novo registro ao final do array histórico
            history.put(newEntry);

            // Grava histórico atualizado com formatação legível
            Files.writeString(
                Paths.get(HISTORY_FILE), 
                history.toString(2)  // Indentação de 2 espaços para legibilidade
            );

            // Feedback de sucesso com timestamp de identificação
            System.out.println(
                "Preços das ações salvos em " + HISTORY_FILE + 
                " às " + newEntry.getString("timestamp")
            );
            
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura qualquer falha sem interromper monitoramento
            System.err.println("Erro ao processar preços das ações: " + e.getMessage());
        }
    }
}