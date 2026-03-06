package school.sptech;

import java.util.concurrent.ThreadLocalRandom;

public class logJava {

    public String usuario() {
        String[] instancias = new String[]{"Eric", "Jefferson", "Adamantina", "Mario", "Bartolomeu"};
        int escolhido = ThreadLocalRandom.current().nextInt(0, instancias.length);
        return instancias[escolhido];
    }

    public String action() {
        String[] instancias = new String[]{"Apertou o Botão X", "Logou", "Deslogou", "Excluiu o item X", "Adicionou o item Y"};
        int escolhido = ThreadLocalRandom.current().nextInt(0, instancias.length);
        return instancias[escolhido];
    }

    // mudar lógica de id depois :)ximbinnha
    public Integer id(String usuarioSorteado) {
        if (usuarioSorteado.equals("Eric")) {
            return 1;
        }
        return 0;
    }
}