import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CodeReviewAgent {

    public static final String MODEL = "gemini-1.5-flash";

    public static final double TEMPERATURE = 0.7;

    private static final String RESPONSE_FILE = "response.md";

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
                        "Objetivo: atue como analista de sistemas revisor de código experiente e forneça um resumo OBJETIVO e RESUMIDO do arquivo \"%s\", " +
                        "focando em falhas de segurança, vulnerabiidades, gargalos de performance e consumo excessivo de recursos computacionais.erros de arquitetura também\n\n" +
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
                "O relatório deve ser bem organizado, claro e com uma tabela de issues detalhada",
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