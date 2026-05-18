package school.sptech;

/**
 * Resultado do processamento de um arquivo.
 */
public class ProcessingResult {
    private final boolean sucesso;
    private final String mensagem;
    private final int linhasProcessadas;

    public ProcessingResult(boolean sucesso, String mensagem, int linhasProcessadas) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.linhasProcessadas = linhasProcessadas;
    }

    public static ProcessingResult sucesso(int linhasProcessadas) {
        return new ProcessingResult(true, "Sucesso", linhasProcessadas);
    }

    public static ProcessingResult erro() {
        return new ProcessingResult(false, "Erro ao processar", 0);
    }

    public static ProcessingResult erro(String mensagem) {
        return new ProcessingResult(false, mensagem, 0);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public int getLinhasProcessadas() {
        return linhasProcessadas;
    }

    @Override
    public String toString() {
        return String.format("ProcessingResult(sucesso=%s, mensagem=%s, linhas=%d)",
            sucesso, mensagem, linhasProcessadas);
    }
}
