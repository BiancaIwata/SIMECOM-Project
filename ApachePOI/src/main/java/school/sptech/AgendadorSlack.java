package school.sptech;

import java.util.concurrent.*;
import java.time.*;

public class AgendadorSlack {

    public static void iniciar() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        long delayInicial = calcularDelayAteProximoDia1();

        // Executa no dia 1 de cada mês, depois repete a cada ~30 dias
        scheduler.scheduleAtFixedRate(
                new NotificacaoMensal(),
                delayInicial,
                calcularDiasDoMes(),
                TimeUnit.SECONDS
        );

        System.out.println("Agendador Slack iniciado.");
    }

    // Calcula quantos segundos faltam até o próximo dia 1 às 08:00
    private static long calcularDelayAteProximoDia1() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximo = agora
                .withDayOfMonth(1)
                .withHour(8)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Se já passou do dia 1 deste mês, vai pro próximo
        if (agora.isAfter(proximo)) {
            proximo = proximo.plusMonths(1);
        }

        return Duration.between(agora, proximo).getSeconds();
    }

    // Retorna a duração do mês atual em segundos
    private static long calcularDiasDoMes() {
        YearMonth mesAtual = YearMonth.now();
        return mesAtual.lengthOfMonth() * 24L * 60 * 60;
    }
}