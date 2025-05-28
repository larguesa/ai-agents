import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CodeScribeAgent {
    public static final String MODEL = "gemini-2.0-flash";
    public static final double TEMPERATURE = 0.7;
    private static final String RESPONSE_FILE = "response.md";

    public static void main(String[] args) {
        try {
            // 1. Solicita pasta ao usuário
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = chooser.showOpenDialog(null);
            if (option != JFileChooser.APPROVE_OPTION) {
                System.err.println("Nenhuma pasta selecionada. Encerrando.");
                return;
            }
            File rootDir = chooser.getSelectedFile();
            // 2. Varrer pastas e arquivos
            System.out.println("Estruturando os arquivos da pasta '"+rootDir.getName()+"'...");
            StringBuilder structureSb = new StringBuilder();
            List<File> files = new ArrayList<>();
            buildStructure(rootDir, "", structureSb, files);
            String structure = structureSb.toString();
            // 3. Percorrer collection gerando prompts e acumulando respostas
            StringBuilder accumulated = new StringBuilder();
            for (File file : files) {
                try{
                    System.out.println("Analisando " + file.getName() + "...");
                    String content = Files.readString(file.toPath());
                    String prompt = ""
                        + "Estrutura de Pastas e Arquivos:\n" + structure + "\n\n"
                        + "Respostas Anteriores:\n" + accumulated.toString() + "\n\n"
                        + "Objetivo: forneça um resumo OBJETIVO e RESUMIDO do arquivo \"" + file.getName() + "\", "
                        + "focando nos pontos técnicos mais relevantes.\n\n"
                        + "Conteúdo do arquivo:\n" + content;
                    String response = App.getGeminiCompletion(
                        MODEL,
                        TEMPERATURE,
                        prompt,
                        "text/plain",
                        false
                    );

                    accumulated
                        .append("## Resumo de ").append(file.getName()).append("\n")
                        .append(response).append("\n\n");
                }catch (Exception e){
                    //não faz nada porque não trata-se de arquivo de texto
                }
            }
            // 4. Prompt final para relatório técnico detalhado
            String finalPrompt = ""
                + "Estrutura de Pastas e Arquivos:\n" + structure + "\n\n"
                + "Resumos por arquivo:\n" + accumulated.toString() + "\n\n"
                + "Por favor, gere um RELATÓRIO TÉCNICO DETALHADO do projeto, "
                + "incluindo a estrutura acima e os insights obtidos. "
                + "O relatório deve ser bem organizado, claro e abordar arquitetura, "
                + "pontos críticos e possíveis melhorias.";

            String finalReport = App.getGeminiCompletion(
                MODEL,
                TEMPERATURE,
                finalPrompt,
                "text/plain",
                false
            );
            // 5. Salvar apenas o relatório final em response.md
            String header = "# Relatório CodeScribeAgent em " + new Date() + "\n\n";
            Files.writeString(Paths.get(RESPONSE_FILE), header + finalReport);
            System.out.println("Relatório salvo em " + RESPONSE_FILE);
        } catch (IOException e) {
            System.err.println("Erro de I/O: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro no CodeScribeAgent: " + e.getMessage());
        }
    }

    private static void buildStructure(File dir, String indent, StringBuilder sb, List<File> files) {
        sb.append(indent).append("[DIR] ").append(dir.getName()).append("\n");
        File[] entries = dir.listFiles();
        if (entries == null) return;
        String nextIndent = indent + "  ";
        for (File f : entries) {
            if (f.isDirectory() && !f.getName().equals(".git") && !f.getName().equals("bin")) {
                buildStructure(f, nextIndent, sb, files);
            } else {
                sb.append(nextIndent).append(f.getName()).append("\n");
                files.add(f);
            }
        }
    }
}