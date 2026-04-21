package school.sptech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Logger do pipeline de carga de dados SIMECOM.
 *
 * Registra:
 *   ► Sucesso na inserção de cada arquivo (com contagem de linhas)
 *   ► Erros por linha, identificando arquivo + número da linha + mensagem
 *
 * Uso:
 *   logJava log = new logJava("EXP_2025.xlsx");
 *   log.sucesso(1709746, 0);           // inserção concluída
 *   log.erro(3042, "FK inválida");      // erro na linha 3042
 *   log.imprimirResumo();               // relatório final
 */
public class logJava {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Nome do arquivo sendo processado
    private final String nomeArquivo;

    // Lista de erros registrados (linha + mensagem)
    private final List<RegistroErro> erros = new ArrayList<>();

    // Contadores finais
    private int linhasInseridas = 0;
    private int linhasIgnoradas = 0;
    private boolean concluido = false;

    //  ESTRUTURA INTERNA — um erro por linha
    private record RegistroErro(int linha, String mensagem, String timestamp) {}

    //  CONSTRUTORES

    public logJava(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    /** Construtor sem arquivo (compatível com Main.java original) */
    public logJava() {
        this.nomeArquivo = "desconhecido";
    }

    //  REGISTRO DE SUCESSO
    
    /**
     * Registra que a inserção do arquivo foi concluída com sucesso.
     *
     * @param inseridas  Quantidade de linhas efetivamente inseridas
     * @param ignoradas  Quantidade de linhas ignoradas (FK inválida, vazias, etc.)
     */
    public void sucesso(int inseridas, int ignoradas) {
        this.linhasInseridas = inseridas;
        this.linhasIgnoradas = ignoradas;
        this.concluido = true;
        String ts = LocalDateTime.now().format(FMT);
        System.out.printf("[LOG %s] SUCESSO — Arquivo '%s': %,d linhas inseridas, %,d ignoradas.%n",
                ts, nomeArquivo, inseridas, ignoradas);
    }

    // REGISTRO DE ERRO POR LINHA

    /**
     * Registra um erro ocorrido em uma linha específica do arquivo.
     *
     * @param linha    Número da linha no CSV (1-based)
     * @param mensagem Descrição do erro
     */
    public void erro(int linha, String mensagem) {
        String ts = LocalDateTime.now().format(FMT);
        erros.add(new RegistroErro(linha, mensagem, ts));
        System.err.printf("[LOG %s] ERRO — Arquivo '%s', linha %d: %s%n",
                ts, nomeArquivo, linha, mensagem);
    }

    //  RELATÓRIO FINAL

    /**
     * Imprime um resumo completo da operação (sucesso + todos os erros).
     */
    public void imprimirResumo() {
        System.out.println();
        System.out.println("###################################################");
        System.out.println("#                   LOG — RESUMO                  #");
        System.out.println("###################################################");
        System.out.printf("  Arquivo          : %s%n", nomeArquivo);
        System.out.printf("  Status           : %s%n", concluido ? "CONCLUÍDO" : "NÃO FINALIZADO");
        System.out.printf("  Linhas inseridas : %,d%n", linhasInseridas);
        System.out.printf("  Linhas ignoradas : %,d%n", linhasIgnoradas);
        System.out.printf("  Total de erros   : %,d%n", erros.size());
    }

    //  SALVAR NO BANCO DE DADOS

    /**
     * Salva o resumo da carga na tabela log_java do banco de dados.
     * Grava as mesmas informações que o imprimirResumo() exibe no console.
     *
     * @param conn Conexão ativa com o banco MySQL
     */
    public void salvarNoBanco(Connection conn) {
        String sql = "INSERT INTO log_java (nome_arquivo, status, linhas_inseridas, linhas_ignoradas, total_erros, data_hora) "
                   + "VALUES (?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeArquivo);
            ps.setString(2, concluido ? "CONCLUIDO" : "NAO FINALIZADO");
            ps.setInt(3, linhasInseridas);
            ps.setInt(4, linhasIgnoradas);
            ps.setInt(5, erros.size());
            ps.executeUpdate();
            System.out.println("[LOG] Resumo salvo no banco de dados (tabela log_java).");
        } catch (Exception e) {
            System.err.println("[LOG] Erro ao salvar log no banco: " + e.getMessage());
        }
    }

    //  GETTERS

    public String getNomeArquivo()   { 
        return nomeArquivo; 
    }
    public int getLinhasInseridas()   { 
        return linhasInseridas; 
    }
    public int getLinhasIgnoradas()   { 
        return linhasIgnoradas; 
    }
    public int getTotalErros()        { 
        return erros.size(); 
    }
    public boolean isConcluido()      { 
        return concluido; 
    }
    public List<RegistroErro> getErros() { 
        return List.copyOf(erros); 
    }
}