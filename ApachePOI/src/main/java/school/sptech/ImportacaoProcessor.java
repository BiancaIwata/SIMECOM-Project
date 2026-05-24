package school.sptech;

import java.nio.file.Path;

/**
 * Processador especializado em arquivos de IMPORTAÇÃO.
 * Herda de DataProcessor e implementa a lógica específica para importações.
 */
public class ImportacaoProcessor extends DataProcessor {

    private final logJava log;

    public ImportacaoProcessor(ProcessorContext context, String nomeArquivo) {
        super(context, "IMPORTAÇÃO");
        this.log = new logJava(nomeArquivo);
    }

    @Override
    protected ProcessingResult processarInterno(Path arquivoPath) throws Exception {
        ComexDataLoader.ResultadoCarregamento resultado =
            ComexDataLoader.carregarImportacao(arquivoPath, context.getConnection());

        log.sucesso(resultado.getInseridos(), resultado.getIgnorados());
        log.salvarNoBanco(context.getConnection());
        log.imprimirResumo();

        return ProcessingResult.sucesso(resultado.getInseridos());
    }

    public logJava getLog() {
        return log;
    }
}
