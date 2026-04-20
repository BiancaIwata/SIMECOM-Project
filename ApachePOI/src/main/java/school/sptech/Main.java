package school.sptech;

/**
 * Demonstração do logJava — simula inserções de arquivos
 * com sucesso e erro para validar o sistema de logging.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   SIMECOM — Demonstração do Logger (logJava)    ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // ► Simulação 1: arquivo processado com sucesso (sem erros)
        System.out.println("--- Simulação 1: Arquivo sem erros ---");
        logJava log1 = new logJava("EXP_2025.csv");
        log1.sucesso(1709746, 0);
        log1.imprimirResumo();

        // ► Simulação 2: arquivo com alguns erros em linhas específicas
        System.out.println("--- Simulação 2: Arquivo com erros ---");
        logJava log2 = new logJava("IMP_2024_MUN.csv");
        log2.erro(3042, "FK inválida: SH4 '9999' não encontrado em codigo_sh4.");
        log2.erro(15780, "Dados inválidos ou campos obrigatórios vazios.");
        log2.erro(82401, "Erro de formatação numérica: For input string: 'ABC'");
        log2.sucesso(500000, 3);
        log2.imprimirResumo();

        // ► Simulação 3: arquivo que não conseguiu ser processado
        System.out.println("--- Simulação 3: Arquivo sem header ---");
        logJava log3 = new logJava("ARQUIVO_CORROMPIDO.csv");
        log3.erro(0, "Arquivo sem header — impossível processar.");
        log3.imprimirResumo();
    }
}