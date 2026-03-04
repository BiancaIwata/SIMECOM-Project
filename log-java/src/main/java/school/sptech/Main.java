package school.sptech;

import java.time.LocalDateTime;

public class Main {
    static void main() {
        logJava metodoLogs = new logJava();

        LocalDateTime horario = LocalDateTime.now();

        Boolean acesso = true;

        System.out.println("%5s %-15s %-10s %10s\n".formatted("ID", "User", "Ação", "Horario"));


        if (acesso == true) {
            do {
                System.out.println("%5s %-15s %10s %10s".formatted(metodoLogs.id(), metodoLogs.usuario(),metodoLogs.action(), horario));
            } while (acesso == true);
        }
            if (acesso == false){
                System.err.println("%5s %-15s %10s %10s".formatted("Null","Null","Null", "Null"));

            }

    }
}

