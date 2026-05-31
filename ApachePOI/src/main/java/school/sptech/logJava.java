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

    public logJava() {
        this.nomeArquivo = "desconhecido";
    }

    //  REGISTRO DE SUCESSO
    
    public void sucesso(int inseridas, int ignoradas) {
        this.linhasInseridas = inseridas;
        this.linhasIgnoradas = ignoradas;
        this.concluido = true;
    }

    // REGISTRO DE ERRO POR LINHA

    public void erro(int linha, String mensagem) {
        String ts = LocalDateTime.now().format(FMT);
        erros.add(new RegistroErro(linha, mensagem, ts));
    }

    //  RELATÓRIO FINAL

    public void imprimirResumo() {
        System.out.printf("%s | inseridas=%d | ignoradas=%d | erros=%d%n",
                nomeArquivo, linhasInseridas, linhasIgnoradas, erros.size());
    }

    //  SALVAR NO BANCO DE DADOS

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

            // ✅ COMMIT EXPLÍCITO — força confirmação no banco
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao salvar log: " + e.getMessage());
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (Exception rollbackEx) {
                System.err.println("[ERRO] Falha ao fazer rollback: " + rollbackEx.getMessage());
            }
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