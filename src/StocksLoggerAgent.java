import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class StocksLoggerAgent {
    public static final String MODEL = "gemini-2.5-flash-preview-04-17";
    public static final double TEMPERATURE = 1.0;
    public static final long TIMER = 10000; // 10 segundos em milissegundos
    public static final String[] STOCKS = {"AAPL", "MSFT", "GOOGL"};

    private static final String HISTORY_FILE = "response.json";
    
    public static void main(String[] args) {
        StocksLoggerAgent agent = new StocksLoggerAgent();
        agent.startLogging();

        // Para fins de teste, para o agendamento após 60 segundos
        try {
            Thread.sleep(60000);
            agent.stopLogging();
            System.out.println("Agendamento parado.");
        } catch (InterruptedException e) {
            System.err.println("Erro ao aguardar: " + e.getMessage());
        }
    }

    // Timer para agendamento
    private final Timer timer = new Timer(true);

    public void startLogging() {
        // Cria a tarefa para execução periódica
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                logStockPrices();
            }
        };

        // Agenda a tarefa para executar a cada 15 segundos
        timer.scheduleAtFixedRate(task, 0, TIMER);
    }

    public void stopLogging() {
        // Cancela o timer para parar o agendamento
        timer.cancel();
    }

    private void logStockPrices() {
        try {
            // Monta o prompt para solicitar preços das ações em JSON
            String stocksList = String.join(", ", STOCKS);
            String prompt = String.format(
                "Provide the current stock prices in USD for %s in JSON format. " +
                "Return an object with ticker symbols as keys and prices as numbers. " +
                "Example: {\"AAPL\": 100.00, \"MSFT\": 100.00, \"GOOGL\": 100.00}",
                stocksList
            );

            // Chama a API do Gemini com response_mime_type como JSON
            String response = App.getGeminiCompletion(MODEL, TEMPERATURE, prompt, "application/json", true);
            if (response == null) {
                System.err.println("Falha ao obter preços das ações.");
                return;
            }

            // Parseia a resposta JSON
            JSONObject stockPrices = new JSONObject(response);

            // Prepara o novo registro
            JSONObject newEntry = new JSONObject();
            newEntry.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            newEntry.put("stocks", stockPrices);

            // Lê o histórico existente ou cria um novo array
            JSONArray history;
            if (Files.exists(Paths.get(HISTORY_FILE))) {
                String existingContent = Files.readString(Paths.get(HISTORY_FILE));
                history = new JSONArray(existingContent);
            } else {
                history = new JSONArray();
            }

            // Adiciona o novo registro ao histórico
            history.put(newEntry);

            // Grava o histórico atualizado no arquivo
            Files.writeString(Paths.get(HISTORY_FILE), history.toString(2));

            System.out.println("Preços das ações salvos em " + HISTORY_FILE + " às " + newEntry.getString("timestamp"));
        } catch (Exception e) {
            System.err.println("Erro ao processar preços das ações: " + e.getMessage());
        }
    }
}