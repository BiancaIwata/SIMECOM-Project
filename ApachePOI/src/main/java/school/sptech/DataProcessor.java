package school.sptech;

import java.nio.file.Path;

/**
 * Classe abstrata que define a interface para processadores de dados.
 * Utiliza composição (ProcessorContext) para acessar recursos compartilhados.
 *
 * Subclasses concretas: ExportacaoProcessor, ImportacaoProcessor
 */
public abstract class DataProcessor {

    protected final ProcessorContext context;
    protected final String tipo;

    public DataProcessor(ProcessorContext context, String tipo) {
        this.context = context;
        this.tipo = tipo;
    }

    /**
     * Processa um arquivo com tratamento de erros centralizado.
     */
    public final ProcessingResult processar(Path arquivoPath) {
        try {
            validarArquivo(arquivoPath);
            return processarInterno(arquivoPath);
        } catch (Exception e) {
            System.err.printf("[ERRO] Falha ao processar %s: %s%n", tipo, e.getMessage());
            return ProcessingResult.erro(e.getMessage());
        }
    }

    protected void validarArquivo(Path path) throws Exception {
        if (path == null || !path.toFile().exists()) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + path);
        }
    }

    /**
     * Implementação do processamento específico (NCM, MUN, etc).
     */
    protected abstract ProcessingResult processarInterno(Path arquivoPath) throws Exception;

    public String getTipo() {
        return tipo;
    }

    public ProcessorContext getContext() {
        return context;
    }
}
