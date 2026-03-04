package school.sptech;

import java.util.concurrent.ThreadLocalRandom;

public class logJava {

    String usuario() {
        Integer escolhindo = 0;

        String[] instancias = new String[]{"Eric", "Jefferson", "Adamantina", "Mario", "Bartolomeu"};
            escolhindo = ThreadLocalRandom.current().nextInt(0, instancias.length);
            return instancias[escolhindo];
    }

    String action() {
        Integer escolhindo = 0;

        String[] instancias = new String[]{"Apertou o Botão X", "Logou", "Deslogou", "Excluiu o item X", "Adicionou o item Y"};
        escolhindo = ThreadLocalRandom.current().nextInt(0, instancias.length);
        return instancias[escolhindo];
    }

    Integer id() {
    usuario();
        if (usuario().equals("Eric")) {
            return 1;
        }
        return 0;
    }
}
