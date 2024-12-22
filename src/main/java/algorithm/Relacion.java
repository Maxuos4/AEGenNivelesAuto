package algorithm;

import java.util.ArrayList;
import java.util.List;

public class Relacion {
    private final List<Integer> secuencia;
    private final int modificador;
    private final String tipo; // "obstaculo" or "categoria"

    public Relacion(List<Integer> secuencia, int modificador, String tipo) {
        this.secuencia = new ArrayList<>(secuencia);
        this.modificador = modificador;
        this.tipo = tipo;
    }

    public List<Integer> getSecuencia() {
        return secuencia;
    }

    public int getModificador() {
        return modificador;
    }

    public String getTipo() {
        return tipo;
    }
}
