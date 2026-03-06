package school.sptech;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        logJava metodoLogs = new logJava();

        Boolean acesso = true;

        // estrtura da saída da data
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm:ss");

        System.out.println("%5s %-15s %-20s %10s\n".formatted("ID", "User", "Ação", "Horário"));

        if (acesso) {
            long startTime = System.currentTimeMillis();
            long tempoLimite = 15000;
            long intervalo = 1500;

            while (System.currentTimeMillis() - startTime < tempoLimite) {

                // captura de horário e data
                LocalDateTime horario = LocalDateTime.now();pwd
                String horarioFormatado = horario.format(formatador);

                // chamo o method logJava para fazer um sorteio (por rodada)
                String usuarioSorteado = metodoLogs.usuario();
                String acaoSorteada = metodoLogs.action();

                // passa o usuário que acabou de ser sorteado para pegar o ID certo
                Integer idSorteado = metodoLogs.id(usuarioSorteado);

                // impressão
                System.out.println("%5s %-15s %-20s %10s".formatted(idSorteado, usuarioSorteado, acaoSorteada, horarioFormatado));

                // loop intervalado (utilizei a lógica em que vimos no projeto indívidual de funções assíncronas)
                try {
                    Thread.sleep(intervalo);
                } catch (InterruptedException e) {
                    System.out.println("O processo foi interrompido.");
                    break;
                }
            }

            System.out.println("\nTempo limite atingido. Sistema encerrado.");

        } else {
            System.err.println("%5s %-15s %-20s %10s".formatted("Null", "Null", "Null", "Null"));
        }
    }
}