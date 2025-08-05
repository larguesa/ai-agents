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

/**
 * Classe principal da aplicação de agentes de IA que interage com a API Gemini da Google.
 * 
 * Esta classe fornece funcionalidades essenciais para:
 * - Gerenciamento de chaves de API (leitura, escrita e persistência)
 * - Comunicação com a API Gemini para geração de conteúdo
 * - Integração com o Google Search através da API Gemini
 * - Processamento e formatação de respostas da API
 * 
 * A classe serve como ponte entre os agentes de IA do projeto e os serviços
 * externos do Google, centralizando toda a lógica de comunicação HTTP e
 * manipulação de dados JSON.
 * 
 * @author AI Agents Project
 * @version 1.0
 */
public class App {
    /**
     * Nome do arquivo onde a chave da API Gemini é armazenada localmente.
     * 
     * Este arquivo é usado para:
     * - Persistir a chave API entre execuções da aplicação
     * - Evitar que o usuário precise inserir a chave a cada execução
     * - Manter a chave segura no sistema de arquivos local
     * 
     * O arquivo é criado automaticamente quando o usuário fornece a chave
     * pela primeira vez e é lido em execuções subsequentes.
     * 
     * Nota: Este arquivo está incluído no .gitignore para evitar que
     * informações sensíveis sejam versionadas.
     */
    private static final String API_KEY_FILE = "api_key.txt";

    /**
     * Método principal da aplicação - ponto de entrada para execução.
     * 
     * Este método serve como um teste básico da funcionalidade de obtenção
     * da chave API. Ele demonstra o fluxo completo de:
     * 1. Tentativa de recuperação da chave API
     * 2. Exibição do resultado (sucesso ou falha)
     * 3. Feedback visual para o usuário
     * 
     * Fluxo de execução:
     * - Chama o método getApiKey() para obter/solicitar a chave
     * - Se a chave for obtida com sucesso, exibe a chave (com prefixo de identificação)
     * - Se houver falha, exibe uma mensagem de erro informativa
     * 
     * Este método é útil para:
     * - Verificar se a integração com a API está funcionando
     * - Testar o fluxo de autenticação
     * - Debugar problemas de configuração inicial
     * 
     * @param args argumentos da linha de comando (não utilizados nesta implementação)
     */
    public static void main(String[] args) {
        // Teste do método getApiKey para verificar a funcionalidade de autenticação
        String key = getApiKey();
        
        // Verifica se a chave foi obtida com sucesso e fornece feedback apropriado
        if (key != null) {
            // Exibe confirmação de sucesso com a chave obtida
            System.out.println("Chave APIresponse: " + key);
        } else {
            // Informa sobre falha na obtenção da chave
            System.out.println("Falha ao obter a chave API.");
        }
    }

    /**
     * Obtém a chave da API Gemini, implementando um sistema de cache local.
     * 
     * Este método gerencia a autenticação com a API Gemini através de um
     * sistema inteligente que:
     * 1. Primeiro tenta ler a chave de um arquivo local (cache)
     * 2. Se não encontrar, solicita a chave ao usuário via console
     * 3. Salva a chave fornecida para uso futuro
     * 4. Retorna a chave ou null em caso de erro
     * 
     * FLUXO DE LEITURA:
     * - Verifica se o arquivo API_KEY_FILE existe no sistema
     * - Se existir, lê todo o conteúdo e remove espaços em branco
     * - Retorna a chave lida, evitando nova solicitação ao usuário
     * 
     * FLUXO DE ESCRITA (quando arquivo não existe):
     * - Solicita a chave ao usuário através do console
     * - Lê a entrada do usuário e remove espaços em branco
     * - Grava a chave no arquivo para persistência
     * - Retorna a chave fornecida
     * 
     * TRATAMENTO DE ERROS:
     * - Captura qualquer exceção durante leitura/escrita de arquivo
     * - Exibe mensagem de erro detalhada no stderr
     * - Retorna null para indicar falha na operação
     * 
     * Benefícios desta implementação:
     * - Experiência do usuário melhorada (chave solicitada apenas uma vez)
     * - Persistência de dados entre execuções
     * - Segurança local (arquivo não é versionado)
     * - Recuperação automática de erros
     * 
     * @return String contendo a chave da API, ou null se houver erro
     */
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
    
    /**
     * Realiza uma chamada completa à API Gemini para geração de conteúdo.
     * 
     * Este método é o núcleo da integração com a API Gemini da Google,
     * oferecendo funcionalidades avançadas de geração de texto com
     * opções de personalização e busca integrada.
     * 
     * PARÂMETROS EXPLICADOS:
     * 
     * @param model Nome do modelo Gemini a ser utilizado (ex: "gemini-2.5-flash")
     *              Diferentes modelos oferecem diferentes capacidades e velocidades
     * 
     * @param temperature Controla a aleatoriedade/criatividade das respostas (0.0 a 1.0)
     *                   - Valores baixos (0.0-0.3): Respostas mais determinísticas e conservadoras
     *                   - Valores médios (0.4-0.7): Equilíbrio entre criatividade e consistência
     *                   - Valores altos (0.8-1.0): Respostas mais criativas e variadas
     * 
     * @param prompt Texto de entrada que será enviado ao modelo para processamento
     *               Este é o conteúdo principal que guia a geração de resposta
     * 
     * @param responseMimeType Tipo MIME da resposta esperada (ex: "text/plain", "application/json")
     *                        Define o formato de saída que o modelo deve gerar
     * 
     * @param search Flag que ativa/desativa a funcionalidade de busca integrada
     *               Se true, adiciona contexto de busca na web antes do prompt principal
     * 
     * FLUXO DE EXECUÇÃO:
     * 
     * 1. AUTENTICAÇÃO:
     *    - Obtém a chave API através do método getApiKey()
     *    - Valida se a chave foi obtida com sucesso
     * 
     * 2. CONSTRUÇÃO DA URL:
     *    - Monta endpoint da API Gemini com modelo especificado
     *    - Inclui chave de autenticação como parâmetro de query
     * 
     * 3. MONTAGEM DO CORPO DA REQUISIÇÃO (JSON):
     *    - Cria estrutura JSON compatível com a API Gemini
     *    - Configura array "contents" com as mensagens do usuário
     *    - Se search=true, adiciona contexto de busca como primeira mensagem
     *    - Adiciona prompt principal como mensagem do usuário
     *    - Define configurações de geração (temperatura, tipo de resposta)
     * 
     * 4. EXECUÇÃO DA REQUISIÇÃO HTTP:
     *    - Utiliza HttpClient nativo do Java (Java 11+)
     *    - Configura headers apropriados (Content-Type: application/json)
     *    - Envia requisição POST com corpo JSON
     * 
     * 5. PERSISTÊNCIA PARA DEBUG:
     *    - Salva corpo da requisição em "requestBody.json"
     *    - Salva resposta completa em "responseBody.json"
     *    - Facilita debug e análise de problemas
     * 
     * 6. PROCESSAMENTO DA RESPOSTA:
     *    - Parseia JSON de resposta da API
     *    - Navega pela estrutura: candidates[0].content.parts[0].text
     *    - Extrai e retorna apenas o texto gerado
     * 
     * ESTRUTURA DA RESPOSTA GEMINI:
     * {
     *   "candidates": [
     *     {
     *       "content": {
     *         "parts": [
     *           {"text": "Texto gerado pelo modelo"}
     *         ]
     *       }
     *     }
     *   ]
     * }
     * 
     * TRATAMENTO DE ERROS:
     * - Captura qualquer exceção durante o processo
     * - Registra erro detalhado no stderr
     * - Retorna null para indicar falha
     * 
     * INTEGRAÇÃO COM BUSCA:
     * - Quando search=true, chama getGeminiSearchResults() primeiro
     * - Adiciona resultados da busca como contexto para o prompt principal
     * - Permite respostas mais informadas e atualizadas
     * 
     * @return String contendo o texto gerado pelo modelo, ou null em caso de erro
     */
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
                JSONObject searchContent = new JSONObject();
                searchContent.put("role", "user");
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

            // FASE 4: Execução da requisição HTTP
            // Cria cliente HTTP reutilizável e thread-safe
            HttpClient client = HttpClient.newHttpClient();
            
            // Constrói requisição POST com configurações apropriadas
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))  // Define URL de destino
                    .header("Content-Type", "application/json")  // Especifica formato do corpo
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))  // Inclui dados JSON
                    .build();

            // FASE 5: Persistência para debug - Facilita análise de problemas
            // Salva requisição para análise posterior
            Files.writeString(Paths.get("requestBody.json"), requestBody.toString());
            
            // Executa requisição e obtém resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            
            // Salva resposta para análise posterior
            Files.writeString(Paths.get("responseBody.json"), responseBody.toString());
            
            // FASE 6: Processamento da resposta JSON
            // Parseia resposta JSON da API
            JSONObject responseJson = new JSONObject(responseBody);
            
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

    /**
     * Executa busca integrada usando a API Gemini com ferramenta Google Search.
     * 
     * Este método especializado oferece capacidades avançadas de busca na web
     * através da integração entre Gemini e Google Search, proporcionando
     * respostas fundamentadas em informações atuais e verificáveis da internet.
     * 
     * FUNCIONALIDADES PRINCIPAIS:
     * 
     * 1. BUSCA INTELIGENTE:
     *    - Utiliza o Google Search como ferramenta integrada
     *    - O modelo Gemini interpreta o prompt e executa buscas relevantes
     *    - Combina múltiplas fontes para uma resposta abrangente
     * 
     * 2. GROUNDING (FUNDAMENTAÇÃO):
     *    - Associa informações às suas fontes originais
     *    - Fornece links e referências verificáveis
     *    - Aumenta credibilidade e transparência das respostas
     * 
     * 3. FORMATAÇÃO AVANÇADA:
     *    - Gera resposta principal baseada em resultados de busca
     *    - Adiciona seção de referências formatada em Markdown
     *    - Preserva títulos e URLs das fontes consultadas
     * 
     * FLUXO DE EXECUÇÃO DETALHADO:
     * 
     * @param prompt Consulta ou pergunta que será pesquisada na web
     *               O modelo usa este texto para determinar os termos de busca
     * 
     * FASE 1: AUTENTICAÇÃO E PREPARAÇÃO
     * - Obtém chave API através do sistema de cache local
     * - Valida disponibilidade da chave para comunicação
     * 
     * FASE 2: CONFIGURAÇÃO DA BUSCA
     * - Define modelo específico para busca: "gemini-2.5-flash-preview-04-17"
     * - Este modelo possui integração nativa com Google Search
     * - URL especializada para funcionalidades de busca
     * 
     * FASE 3: CONSTRUÇÃO DA REQUISIÇÃO COM FERRAMENTAS
     * - Cria estrutura JSON compatível com API Gemini
     * - Inclui prompt do usuário como conteúdo principal
     * - IMPORTANTE: Adiciona array "tools" com googleSearch habilitado
     * - Configura response_mime_type como "text/plain" para texto simples
     * 
     * ESTRUTURA DA REQUISIÇÃO:
     * {
     *   "contents": [{"role": "user", "parts": [{"text": "prompt"}]}],
     *   "generationConfig": {"response_mime_type": "text/plain"},
     *   "tools": [{"googleSearch": {}}]
     * }
     * 
     * FASE 4: EXECUÇÃO E PERSISTÊNCIA
     * - Executa requisição HTTP POST para API Gemini
     * - Salva resposta completa em "searchResponseBody.json"
     * - Facilita debug e análise de problemas de busca
     * 
     * FASE 5: PROCESSAMENTO DA RESPOSTA COMPLEXA
     * - Extrai texto principal da resposta do modelo
     * - Processa metadados de fundamentação (grounding)
     * - Identifica e formata referências encontradas
     * 
     * FASE 6: EXTRAÇÃO DE REFERÊNCIAS (GROUNDING CHUNKS)
     * - Verifica presença de "groundingMetadata" na resposta
     * - Itera sobre "groundingChunks" para coletar fontes
     * - Para cada fonte, extrai título e URI
     * - Formata referências em estilo Markdown
     * 
     * ESTRUTURA DA RESPOSTA GEMINI COM BUSCA:
     * {
     *   "candidates": [{
     *     "content": {"parts": [{"text": "Resposta baseada em busca"}]},
     *     "groundingMetadata": {
     *       "groundingChunks": [{
     *         "web": {"title": "Título da página", "uri": "URL da fonte"}
     *       }]
     *     }
     *   }]
     * }
     * 
     * FORMATAÇÃO FINAL:
     * - Combina texto principal com seção de referências
     * - Adiciona cabeçalho "# Referências" em Markdown
     * - Lista fontes como links clicáveis quando título disponível
     * - Fallback para URL simples quando título não disponível
     * 
     * EXEMPLO DE SAÍDA:
     * ```
     * [Resposta do modelo baseada em busca na web]
     * 
     * # Referências
     * - [Título da Fonte 1](https://exemplo1.com)
     * - [Título da Fonte 2](https://exemplo2.com)
     * - https://exemplo3.com
     * ```
     * 
     * VANTAGENS DESTA IMPLEMENTAÇÃO:
     * - Informações sempre atualizadas da web
     * - Transparência sobre fontes consultadas
     * - Verificabilidade das informações
     * - Formatação legível em Markdown
     * - Fallback robusto para diferentes tipos de fontes
     * 
     * @return String contendo resposta fundamentada com referências, ou null em caso de erro
     */
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
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-04-17:generateContent?key=" + apiKey;

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