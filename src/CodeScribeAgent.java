import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Agente de IA especializado em análise automatizada de código e geração de relatórios técnicos.
 * 
 * Este agente oferece capacidades avançadas de análise de projetos de software,
 * combinando varredura automática de estrutura de arquivos, análise de conteúdo
 * assistida por IA e geração de relatórios técnicos detalhados e profissionais.
 * 
 * FUNCIONALIDADES PRINCIPAIS:
 * 
 * 1. ANÁLISE ESTRUTURAL:
 *    - Varredura recursiva de diretórios e arquivos
 *    - Mapeamento automático da arquitetura do projeto
 *    - Identificação de padrões organizacionais
 *    - Exclusão inteligente de arquivos não relevantes
 * 
 * 2. ANÁLISE DE CONTEÚDO:
 *    - Leitura e processamento de arquivos de texto
 *    - Análise técnica assistida por IA
 *    - Identificação de pontos críticos e relevantes
 *    - Contextualização baseada na estrutura do projeto
 * 
 * 3. GERAÇÃO DE RELATÓRIOS:
 *    - Síntese inteligente de todas as análises
 *    - Relatório técnico estruturado e profissional
 *    - Insights sobre arquitetura e qualidade do código
 *    - Recomendações de melhorias e otimizações
 * 
 * CASOS DE USO IDEAIS:
 * - Code review automatizado
 * - Análise de projetos legados
 * - Documentação técnica automatizada
 * - Auditoria de qualidade de código
 * - Onboarding de novos desenvolvedores
 * - Due diligence técnica
 * 
 * TIPOS DE ARQUIVO SUPORTADOS:
 * - Arquivos de código fonte (Java, Python, JavaScript, etc.)
 * - Arquivos de configuração (XML, JSON, YAML, etc.)
 * - Documentação (Markdown, TXT, etc.)
 * - Scripts de build e deployment
 * 
 * @author AI Agents Project
 * @version 1.0
 * @see App#getGeminiCompletion(String, double, String, String, boolean)
 */
public class CodeScribeAgent {
    /**
     * Modelo Gemini otimizado para análise técnica e síntese de código.
     * 
     * O modelo "gemini-2.0-flash" foi escolhido por suas capacidades superiores em:
     * - Análise e compreensão de código-fonte
     * - Identificação de padrões arquiteturais
     * - Síntese de informações técnicas complexas
     * - Geração de relatórios estruturados e profissionais
     * - Processamento de múltiplos arquivos e contextos
     * 
     * Este modelo oferece o melhor desempenho para análise
     * de projetos de software de grande escala.
     */
    public static final String MODEL = "gemini-2.0-flash";
    
    /**
     * Nível de temperatura equilibrado para análise técnica precisa.
     * 
     * Valor 0.7 é ideal para análise de código porque:
     * - Mantém precisão técnica necessária para análise de código
     * - Permite criatividade na identificação de padrões e insights
     * - Equilibra objetividade com capacidade analítica
     * - Favorece análises construtivas e recomendações úteis
     * - Preserva qualidade técnica dos relatórios gerados
     */
    public static final double TEMPERATURE = 0.7;
    
    /**
     * Nome do arquivo onde o relatório técnico final será persistido.
     * 
     * O arquivo conterá:
     * - Relatório técnico detalhado do projeto analisado
     * - Estrutura completa de diretórios e arquivos
     * - Análise individual dos arquivos principais
     * - Insights sobre arquitetura e qualidade
     * - Recomendações de melhorias
     * - Timestamp para identificação temporal
     */
    private static final String RESPONSE_FILE = "response.md";

    /**
     * Método principal - orquestração completa do processo de análise de código.
     * 
     * Este método implementa um fluxo sofisticado de análise automatizada
     * que combina interface gráfica intuitiva, varredura estrutural,
     * análise assistida por IA e geração de relatórios profissionais.
     * 
     * FASE 1 - SELEÇÃO DE PROJETO:
     * 
     * Interface gráfica amigável para seleção de diretório:
     * - Utiliza JFileChooser nativo do sistema operacional
     * - Permite navegação familiar ao usuário
     * - Validação de seleção antes de prosseguir
     * - Tratamento elegante de cancelamento
     * 
     * FASE 2 - ANÁLISE ESTRUTURAL:
     * 
     * Varredura inteligente do projeto selecionado:
     * - Mapeamento recursivo de toda a estrutura de diretórios
     * - Catalogação de todos os arquivos encontrados
     * - Exclusão automática de diretórios irrelevantes (.git, bin)
     * - Construção de representação hierárquica clara
     * 
     * Resultado: String formatada representando a árvore completa do projeto
     * 
     * FASE 3 - ANÁLISE INDIVIDUAL DE ARQUIVOS:
     * 
     * Para cada arquivo encontrado:
     * - Tentativa de leitura como arquivo de texto
     * - Contextualização baseada na estrutura do projeto
     * - Análise técnica assistida por IA
     * - Acumulação progressiva de insights
     * - Tratamento robusto de arquivos binários/ilegíveis
     * 
     * Características da análise individual:
     * - Prompt contextualizado incluindo estrutura completa
     * - Histórico de análises anteriores para contexto
     * - Foco em pontos técnicos relevantes
     * - Resumo objetivo e conciso
     * 
     * FASE 4 - SÍNTESE E GERAÇÃO DE RELATÓRIO:
     * 
     * Consolidação inteligente de todas as informações:
     * - Combina estrutura + análises individuais
     * - Gera relatório técnico detalhado e profissional
     * - Inclui insights sobre arquitetura e qualidade
     * - Fornece recomendações construtivas
     * - Organiza informações de forma clara e útil
     * 
     * TRATAMENTO DE ERROS E ROBUSTEZ:
     * 
     * - Cancelamento da seleção: encerramento limpo
     * - Arquivos ilegíveis: ignorados silenciosamente
     * - Erros de I/O: tratamento específico
     * - Falhas gerais: tratamento abrangente
     * 
     * EXEMPLO DE ESTRUTURA GERADA:
     * ```
     * [DIR] projeto-exemplo
     *   [DIR] src
     *     Main.java
     *     Utils.java
     *   [DIR] lib
     *     biblioteca.jar
     *   README.md
     * ```
     * 
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        try {
            // FASE 1: SELEÇÃO DE PROJETO VIA INTERFACE GRÁFICA
            // Cria seletor de diretórios nativo do sistema operacional
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            // Exibe diálogo e aguarda seleção do usuário
            int option = chooser.showOpenDialog(null);
            
            // Valida se o usuário confirmou a seleção
            if (option != JFileChooser.APPROVE_OPTION) {
                System.err.println("Nenhuma pasta selecionada. Encerrando.");
                return;
            }
            
            // Obtém referência para o diretório selecionado
            File rootDir = chooser.getSelectedFile();
            
            // FASE 2: ANÁLISE ESTRUTURAL DO PROJETO
            System.out.println("Estruturando os arquivos da pasta '" + rootDir.getName() + "'...");
            
            // Inicializa estruturas para coleta de dados
            StringBuilder structureSb = new StringBuilder();  // Representação da estrutura
            List<File> files = new ArrayList<>();             // Lista de arquivos para análise
            
            // Executa varredura recursiva construindo estrutura e coletando arquivos
            buildStructure(rootDir, "", structureSb, files);
            String structure = structureSb.toString();
            
            // FASE 3: ANÁLISE INDIVIDUAL DE ARQUIVOS
            StringBuilder accumulated = new StringBuilder();
            
            // Itera sobre cada arquivo coletado realizando análise contextualizada
            for (File file : files) {
                try {
                    System.out.print("Analisando " + file.getAbsolutePath() + "...");
                    
                    // Tenta ler conteúdo do arquivo como texto
                    String content = Files.readString(file.toPath());
                    
                    // Constrói prompt contextualizado para análise técnica
                    String prompt = String.format(
                        "Estrutura de Pastas e Arquivos:\n%s\n\n" +
                        "Respostas Anteriores:\n%s\n\n" +
                        "Objetivo: forneça um resumo OBJETIVO e RESUMIDO do arquivo \"%s\", " +
                        "focando nos pontos técnicos mais relevantes.\n\n" +
                        "Conteúdo do arquivo:\n%s",
                        structure,
                        accumulated.toString(),
                        file.getAbsolutePath(),
                        content
                    );
                    
                    // Executa análise técnica do arquivo
                    String response = App.getGeminiCompletion(
                        MODEL,
                        TEMPERATURE,
                        prompt,
                        "text/plain",
                        false  // Análise baseada no conteúdo fornecido
                    );

                    // Acumula análise com estrutura organizada
                    accumulated
                        .append("## Resumo de ").append(file.getName()).append("\n")
                        .append(response).append("\n\n");
                    System.out.print("processado.");
                } catch (Exception e) {
                    // Ignora arquivos que não podem ser lidos como texto
                    // (binários, imagens, etc.) sem interromper o processo
                    System.out.print("descartado por não ser arquivo de texto.");
                }
                System.out.println();
            }
            
            // FASE 4: SÍNTESE E GERAÇÃO DE RELATÓRIO TÉCNICO FINAL
            // Constrói prompt final para geração do relatório consolidado
            String finalPrompt = String.format(
                "Estrutura de Pastas e Arquivos:\n%s\n\n" +
                "Resumos por arquivo:\n%s\n\n" +
                "Por favor, gere um RELATÓRIO TÉCNICO DETALHADO do projeto, " +
                "incluindo a estrutura acima e os insights obtidos. " +
                "O relatório deve ser bem organizado, claro e abordar arquitetura, " +
                "pontos críticos e possíveis melhorias.",
                structure,
                accumulated.toString()
            );

            // Gera relatório técnico final consolidado
            String finalReport = App.getGeminiCompletion(
                MODEL,
                TEMPERATURE,
                finalPrompt,
                "text/plain",
                false  // Síntese baseada nas análises já realizadas
            );
            
            // FASE 5: PERSISTÊNCIA DO RELATÓRIO FINAL
            // Adiciona cabeçalho com identificação temporal
            String header = "# Relatório CodeScribeAgent em " + new Date() + "\n\n";
            
            // Persiste apenas o relatório final (não as análises intermediárias)
            Files.writeString(Paths.get(RESPONSE_FILE), header + finalReport);
            
            // Confirma conclusão do processo
            System.out.println("Relatório salvo em " + RESPONSE_FILE);
            
        } catch (IOException e) {
            // TRATAMENTO ESPECÍFICO: Falhas de entrada/saída
            System.err.println("Erro de I/O: " + e.getMessage());
        } catch (Exception e) {
            // TRATAMENTO GERAL: Qualquer outra falha no processo
            System.err.println("Erro no CodeScribeAgent: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para construção recursiva da estrutura de diretórios.
     * 
     * Este método realiza varredura recursiva inteligente do sistema de arquivos,
     * construindo representação hierárquica clara e coletando arquivos relevantes
     * para análise posterior.
     * 
     * ALGORITMO DE VARREDURA:
     * 
     * 1. PROCESSAMENTO DO DIRETÓRIO ATUAL:
     *    - Adiciona nome do diretório à representação com indentação apropriada
     *    - Obtém lista de entradas (arquivos e subdiretórios)
     *    - Calcula indentação para próximo nível
     * 
     * 2. ITERAÇÃO SOBRE ENTRADAS:
     *    - Para subdiretórios: chamada recursiva (exceto .git e bin)
     *    - Para arquivos: adição à representação e lista de coleta
     * 
     * 3. FILTROS INTELIGENTES:
     *    - Exclusão de .git (controle de versão, irrelevante para análise)
     *    - Exclusão de bin (arquivos compilados, derivados do código-fonte)
     *    - Inclusão de todos os outros diretórios e arquivos
     * 
     * FORMATO DA REPRESENTAÇÃO:
     * ```
     * [DIR] nome-diretorio
     *   [DIR] subdiretorio
     *     arquivo1.java
     *     arquivo2.xml
     *   outro-arquivo.md
     * ```
     * 
     * VANTAGENS DESTA IMPLEMENTAÇÃO:
     * - Representação visual clara e hierárquica
     * - Filtragem automática de conteúdo irrelevante
     * - Coleta eficiente de arquivos para análise
     * - Tratamento robusto de estruturas complexas
     * - Escalabilidade para projetos de qualquer tamanho
     * 
     * @param dir diretório atual sendo processado
     * @param indent string de indentação para o nível atual
     * @param sb StringBuilder para construção da representação textual
     * @param files lista para coleta de arquivos encontrados
     */
    private static void buildStructure(File dir, String indent, StringBuilder sb, List<File> files) {
        // Adiciona diretório atual à representação com marcação visual
        sb.append(indent).append("[DIR] ").append(dir.getName()).append("\n");
        
        // Obtém lista de entradas do diretório atual
        File[] entries = dir.listFiles();
        if (entries == null) return;  // Proteção contra diretórios inacessíveis
        
        // Calcula indentação para próximo nível hierárquico
        String nextIndent = indent + "  ";
        
        // Processa cada entrada encontrada
        for (File f : entries) {
            if (f.isDirectory() && !f.getName().equals(".git") && !f.getName().equals("bin")) {
                // SUBDIRETÓRIO: Recursão para exploração profunda
                // Filtra diretórios irrelevantes (.git = controle versão, bin = compilados)
                buildStructure(f, nextIndent, sb, files);
            } else {
                // ARQUIVO: Adiciona à representação e coleta para análise
                sb.append(nextIndent).append(f.getAbsolutePath()).append("\n");
                files.add(f);
            }
        }
    }
}