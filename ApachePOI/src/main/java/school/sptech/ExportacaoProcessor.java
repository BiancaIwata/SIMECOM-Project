package school.sptech;

import java.nio.file.Path;

/**
 * Processador especializado em arquivos de EXPORTAÇÃO.
 * Herda de DataProcessor e implementa a lógica específica para exportações.
 */
public class ExportacaoProcessor extends DataProcessor {

    private final logJava log;

    public ExportacaoProcessor(ProcessorContext context, String nomeArquivo) {
        super(context, "EXPORTAÇÃO");
        this.log = new logJava(nomeArquivo);
    }

    @Override
    protected ProcessingResult processarInterno(Path arquivoPath) throws Exception {
        ComexDataLoader.ResultadoCarregamento resultado =
            ComexDataLoader.carregarExportacao(arquivoPath, context.getConnection());

        log.sucesso(resultado.getInseridos(), resultado.getIgnorados());
        log.salvarNoBanco(context.getConnection());
        log.imprimirResumo();

        return ProcessingResult.sucesso(resultado.getInseridos());
    }

    public logJava getLog() {
        return log;
    }
}
