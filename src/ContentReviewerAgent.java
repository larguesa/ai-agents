import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContentReviewerAgent {

    public static final String MODEL = "gemini-2.5-pro";
    
    public static final double TEMPERATURE = 0.85;
    
    
    private static final String RESPONSE_FILE = "response.md";

    public static void main(String[] args) {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int option = chooser.showOpenDialog(null);
            
            if (option != JFileChooser.APPROVE_OPTION) {
                System.err.println("Nenhuma pasta selecionada. Encerrando.");
                return;
            }
            
            File rootDir = chooser.getSelectedFile();
            
            System.out.println("Estruturando os arquivos da pasta '" + rootDir.getName() + "'...");
            
            StringBuilder structureSb = new StringBuilder(); 
            List<File> files = new ArrayList<>();
            
            buildStructure(rootDir, "", structureSb, files, false);
            String structure = structureSb.toString();
            
            StringBuilder accumulated = new StringBuilder();
            
            for (File file : files) {
                try {
                    System.out.println("Analisando " + file.getAbsolutePath() + "...");
                    
                    // Tenta ler conteúdo do arquivo como texto
                    String content = Files.readString(file.toPath());
                    
                    // Constrói prompt contextualizado para análise técnica
                    String prompt = String.format(
                        "Data/hora atual: "+new java.util.Date()+".\n\n"+
                        "Estrutura de Pastas e Arquivos:\n%s\n\n" +
                        "Respostas Anteriores:\n%s\n\n" +
                        "Objetivo: faça uma revisão do conteúdo do arquivo \"%s\", " +
                        "procurando por redundâncias, inconsistências, despadronizações, erros conceituais ou outros pontos de melhoria, " +
                        "respondendo uma tabela com o título do nome do arquivo e colunas para trecho de texto com erro e sugestão de melhoria.\n\n" +
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
            
            // FASE 4: RELATÓRIO FINAL
            // Adiciona cabeçalho com identificação temporal
            String header = "# Relatório ContentRevisorAgent em " + new Date() + "\n\n";
            
            // Persiste apenas o relatório final (não as análises intermediárias)
            Files.writeString(Paths.get(RESPONSE_FILE), header + accumulated.toString());
            
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

    private static void buildStructure(File dir, String indent, StringBuilder sb, List<File> files, boolean subfolders) {
        // Adiciona diretório atual à representação com marcação visual
        sb.append(indent).append("[DIR] ").append(dir.getName()).append("\n");
        
        // Obtém lista de entradas do diretório atual
        File[] entries = dir.listFiles();
        if (entries == null) return;  // Proteção contra diretórios inacessíveis
        
        // Calcula indentação para próximo nível hierárquico
        String nextIndent = indent + "  ";
        
        // Processa cada entrada encontrada
        for (File f : entries) {
            if (subfolders && f.isDirectory() && !f.getName().equals(".git") && !f.getName().equals("bin") && !f.getName().equals("dist")) {
                // SUBDIRETÓRIO: Recursão para exploração profunda
                // Filtra diretórios irrelevantes (.git = controle versão, bin = compilados)
                buildStructure(f, nextIndent, sb, files, subfolders);
            } else {
                // ARQUIVO: Adiciona à representação e coleta para análise
                sb.append(nextIndent).append(f.getAbsolutePath()).append("\n");
                files.add(f);
            }
        }
    }
}