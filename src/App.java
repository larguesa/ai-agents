import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {

    private static final String API_KEY_FILE = "api_key.txt";

    public static void main(String[] args) {
        // Teste do método getApiKey para verificar a funcionalidade de autenticação
        String key = getApiKey();
        
        // Verifica se a chave foi obtida com sucesso e fornece feedback apropriado
        if (key != null) {
            // Exibe confirmação de sucesso com a chave obtida
            System.out.println("Chave API encontrada");
        } else {
            // Informa sobre falha na obtenção da chave
            System.out.println("Falha ao obter a chave API.");
        }
    }

    public static String getApiKey() {
        // Cria referência para o arquivo onde a chave será armazenada
        Path filePath = Paths.get(API_KEY_FILE);

        try {
            // FASE 1: Tentativa de leitura da chave existente
            // Verifica se o arquivo de chave já existe no sistema
            if (Files.exists(filePath)) {
                // Lê todo o conteúdo do arquivo e remove espaços em branco nas extremidades
                // Isso garante que não haja caracteres indesejados na chave
                return Files.readString(filePath).trim();
            }

            // FASE 2: Solicitação e persistência de nova chave
            // Se o arquivo não existe, inicia processo de coleta da chave do usuário
            try (// Cria scanner para capturar entrada do usuário de forma segura
            Scanner scanner = new Scanner(System.in)) {
                // Solicita a chave ao usuário com instrução clara
                System.out.println("Nenhuma chave API encontrada. Digite a chave API:");
                
                // Lê a linha completa fornecida pelo usuário e remove espaços extras
                String apiKey = scanner.nextLine().trim();

                // FASE 3: Persistência da chave para uso futuro
                // Grava a chave no arquivo para evitar solicitações futuras
                Files.writeString(filePath, apiKey);

                // Retorna a chave recém-obtida e armazenada
                return apiKey;
            }
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Qualquer falha na leitura/escrita é capturada
            // Em caso de erro, exibe mensagem detalhada e retorna null
            System.err.println("Erro ao ler ou gravar a chave API: " + e.getMessage());
            return null;
        }
    }
    
    public static String getGeminiCompletion(String model, double temperature, String prompt, String responseMimeType, boolean search) {
        try {
            // FASE 1: Autenticação - Obtém chave API necessária para comunicação
            String apiKey = getApiKey();
            if (apiKey == null) {
                System.err.println("Falha ao obter a chave API.");
                return null;
            }

            // FASE 2: Construção da URL - Monta endpoint da API Gemini
            // URL segue padrão: https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

            // FASE 3: Construção do corpo da requisição JSON
            // Cria estrutura de dados compatível com a API Gemini
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            
            // SUB-FASE 3A: Integração com busca (se habilitada)
            if (search) {
                // Adiciona contexto de busca como primeira mensagem
                // Isso enriquece o conhecimento do modelo com informações atuais da web
                System.out.print("Pesquisando na internet...");
                JSONObject searchContent = new JSONObject();
                searchContent.put("role", "model");
                searchContent.put("parts", new JSONObject().put("text", getGeminiSearchResults(prompt)));
                contents.put(searchContent);
            }
            
            // SUB-FASE 3B: Adiciona prompt principal do usuário
            JSONObject content = new JSONObject();
            content.put("role", "user");  // Define papel como usuário na conversa
            content.put("parts", new JSONObject().put("text", prompt));  // Inclui texto do prompt
            contents.put(content);
            
            // SUB-FASE 3C: Configura array de conteúdos na requisição
            requestBody.put("contents", contents);
            
            // SUB-FASE 3D: Define configurações de geração
            JSONObject generationConfig = new JSONObject().put("temperature", temperature);
            generationConfig.put("response_mime_type", responseMimeType);
            requestBody.put("generationConfig", generationConfig);

            // Salva requisição para análise posterior
            Files.writeString(Paths.get("requestBody.json"), requestBody.toString());

            // FASE 4: Execução da requisição HTTP
            // Cria cliente HTTP reutilizável e thread-safe
            HttpClient client = HttpClient.newHttpClient();
            
            // Constrói requisição POST com configurações apropriadas
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))  // Define URL de destino
                    .header("Content-Type", "application/json")  // Especifica formato do corpo
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))  // Inclui dados JSON
                    .build();

            // FASE 5: Executa requisição e obtém resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            
            // Salva resposta para análise posterior
            Files.writeString(Paths.get("responseBody.json"), responseBody.toString());
            
            // FASE 6: Processamento da resposta JSON
            // Parseia resposta JSON da API
            JSONObject responseJson = new JSONObject(responseBody);

            // Salva resposta para análise posterior
            Files.writeString(Paths.get("responseJson.json"), responseJson.toString());
            
            // Navega pela estrutura hierárquica da resposta para extrair texto
            // Caminho: candidates -> [0] -> content -> parts -> [0] -> text
            return responseJson.getJSONArray("candidates")      // Array de candidatos de resposta
                    .getJSONObject(0)                           // Primeiro (e geralmente único) candidato
                    .getJSONObject("content")                   // Conteúdo da resposta
                    .getJSONArray("parts")                      // Array de partes do conteúdo
                    .getJSONObject(0)                           // Primeira parte (texto principal)
                    .getString("text");                         // Texto gerado pelo modelo
                    
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura qualquer falha durante o processo
            System.err.println("Erro ao invocar Gemini: " + e.getMessage());
            return null;
        }
    }

    public static String getGeminiSearchResults(String prompt) {
        try {
            // FASE 1: Autenticação - Garante acesso à API Gemini
            String apiKey = getApiKey();
            if (apiKey == null) {
                System.err.println("Falha ao obter a chave API.");
                return null;
            }

            // FASE 2: Configuração especializada para busca
            // Utiliza modelo específico com capacidades de busca integrada
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            // FASE 3: Construção da requisição com ferramentas de busca
            JSONObject requestBody = new JSONObject();
            
            // SUB-FASE 3A: Configura conteúdo do usuário
            JSONObject content = new JSONObject();
            content.put("role", "user");  // Define papel na conversa
            content.put("parts", new JSONObject().put("text", prompt));  // Inclui consulta de busca
            requestBody.put("contents", new JSONObject[]{content});  // Adiciona à estrutura principal
            
            // SUB-FASE 3B: Define configurações de geração para busca
            JSONObject generationConfig = new JSONObject().put("response_mime_type", "text/plain");
            requestBody.put("generationConfig", generationConfig);
            
            // SUB-FASE 3C: CONFIGURAÇÃO CRÍTICA - Adiciona ferramenta Google Search
            // Esta é a parte que habilita as capacidades de busca na web
            JSONArray tools = new JSONArray();
            tools.put(new JSONObject().put("googleSearch", new JSONObject()));  // Ferramenta vazia = configuração padrão
            requestBody.put("tools", tools);

            // Persiste requisição para debug de problemas de busca
            Files.writeString(Paths.get("searchRequestBody.json"), requestBody.toString());

            // FASE 4: Execução da requisição especializada
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();
                    
            // Executa requisição e captura resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            
            // Persiste resposta para debug de problemas de busca
            Files.writeString(Paths.get("searchResponseBody.json"), responseBody.toString());

            // FASE 5: Processamento da resposta complexa com metadados
            JSONObject responseJson = new JSONObject(responseBody);
            JSONObject candidate = responseJson
                .getJSONArray("candidates")  // Array de candidatos de resposta
                .getJSONObject(0);           // Primeiro candidato (resposta principal)
                
            // SUB-FASE 5A: Extração do texto principal da resposta
            String text = candidate
                .getJSONObject("content")    // Conteúdo da resposta
                .getJSONArray("parts")       // Partes do conteúdo
                .getJSONObject(0)            // Primeira parte (texto principal)
                .getString("text");          // Texto gerado baseado em busca

            // FASE 6: Processamento de referências (grounding chunks)
            // Utiliza StringBuilder para construção eficiente da resposta final
            StringBuilder sb = new StringBuilder(text);
            
            // SUB-FASE 6A: Verifica disponibilidade de metadados de fundamentação
            if (candidate.has("groundingMetadata")) {
                JSONObject groundingMetadata = candidate.getJSONObject("groundingMetadata");
                
                // SUB-FASE 6B: Processa chunks de fundamentação (fontes individuais)
                if (groundingMetadata.has("groundingChunks")) {
                    JSONArray chunks = groundingMetadata.getJSONArray("groundingChunks");
                    
                    // Adiciona cabeçalho da seção de referências
                    sb.append("\n\n# Referências\n");
                    
                    // SUB-FASE 6C: Itera sobre cada fonte encontrada
                    for (int i = 0; i < chunks.length(); i++) {
                        // Extrai informações da fonte web
                        JSONObject web = chunks
                            .getJSONObject(i)
                            .getJSONObject("web");
                            
                        // Obtém título (opcional) e URI (obrigatório)
                        String title = web.optString("title", null);  // Título pode não existir
                        String uri   = web.getString("uri");          // URI sempre presente
                        
                        // SUB-FASE 6D: Formatação condicional da referência
                        if (title != null && !title.isEmpty()) {
                            // Formato Markdown com link: [Título](URL)
                            sb.append("- [")
                            .append(title)
                            .append("](")
                            .append(uri)
                            .append(")\n");
                        } else {
                            // Formato simples: URL apenas
                            sb.append("- ")
                            .append(uri)
                            .append("\n");
                        }
                    }
                }
            }

            // FASE 7: Retorna resposta completa formatada
            // Combina texto principal + referências em formato Markdown
            return sb.toString();
            
        } catch (Exception e) {
            // TRATAMENTO DE ERROS: Captura falhas específicas de busca
            System.err.println("Erro ao invocar Gemini: " + e.getMessage());
            return null;
        }
    }
}