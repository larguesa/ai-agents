import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class StocksLoggerAgent {

    public static final String MODEL = "gemini-2.5-flash";

    public static final double TEMPERATURE = 1.0;

    public static final long TIMER = 10000;

    public static final String[] STOCKS = {"AAPL", "MSFT", "GOOGL"};

    private static final String HISTORY_FILE = "response.json";

    private final Timer timer = new Timer(true);

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

    public void stopLogging() {
        // Cancela timer e todas as execuções pendentes
        timer.cancel();
    }

    private void logStockPrices() {
        try {
            // FASE 1: Preparação da consulta financeira
            // Converte array de símbolos em string formatada para consulta
            String stocksList = String.join(", ", STOCKS);
            
            // Constrói prompt otimizado para obtenção de dados financeiros estruturados
            String prompt = String.format(
                "Data/hora atual: " + new java.util.Date() + ". Provide the current stock prices in USD for %s in JSON format. " +
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