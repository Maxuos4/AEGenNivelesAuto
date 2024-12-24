package algorithm;

import algorithm.data.Categoria;
import algorithm.data.Obstaculo;
import algorithm.data.Relacion;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Greedy {
    private static final Random random = new Random();

    public static VariableLengthIntegerSolution getSolucionRandom(List<Obstaculo> obstaculos, int tiempoTotal) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();

        VariableLengthIntegerSolution solution = new VariableLengthIntegerSolution(bounds, 1);
        int tiempoActual = 0;

        // Seleccionar un jefe aleatorio
        List<Obstaculo> jefes = new ArrayList<>();
        for (Obstaculo obstaculo : obstaculos) {
            if (obstaculo.getCategoria() == Categoria.JEFE) {
                jefes.add(obstaculo);
            }
        }
        Obstaculo jefe = jefes.get(random.nextInt(jefes.size()));

        // Agregar obstáculos aleatorios hasta llegar al tiempo límite
        while (tiempoActual + jefe.getTiempo() <= tiempoTotal) {
            Obstaculo obstaculo = obstaculos.get(random.nextInt(obstaculos.size()));
            if (obstaculo.getCategoria() != Categoria.JEFE) {
                solution.addVariable(obstaculo.getId());
                tiempoActual += obstaculo.getTiempo();
            }
        }

        // Agregar el jefe al final
        solution.addVariable(jefe.getId());

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            bounds.add(Pair.of(0, obstaculos.size() - 1));
        }

        return solution;
    }

    public static VariableLengthIntegerSolution getSolucionRelaciones(List<Obstaculo> obstaculos, List<Relacion> relaciones, int tiempoTotal) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();

        VariableLengthIntegerSolution solution = new VariableLengthIntegerSolution(bounds, 1);
        int tiempoActual = 0;

        // Comenzar con la relación que tenga mayor modificador
        relaciones.sort(Comparator.comparingInt(Relacion::getModificador).reversed());
        List<Integer> secuencia = new ArrayList<>();
        Relacion relacion = relaciones.get(0);

        boolean primerSecuencia = true;
        do {
            if (relacion.getTipo().equals("categoria")){
                for (Integer categoria : relacion.getSecuencia()) {
                    Optional<Obstaculo> obstaculoOpt = obstaculos.stream()
                            .filter(o -> o.getCategoria().ordinal() == categoria)
                            .max(Comparator.comparingInt(Obstaculo::getDificultad));
                    obstaculoOpt.ifPresent(obstaculo -> secuencia.add(obstaculo.getId()));
                }
            } else {
                secuencia.addAll(relacion.getSecuencia());
            }
            if (!primerSecuencia)
                secuencia.remove(0);
            for (Integer id : secuencia) {
                solution.addVariable(id);
                tiempoActual += obstaculos.get(id).getTiempo();
            }
            int ultimoObstaculoId = secuencia.get(secuencia.size() - 1);
            Obstaculo ultimoObstaculo = obstaculos.get(ultimoObstaculoId);
            relacion = relaciones.stream()
                    .filter(r -> r.getSecuencia().get(0) == ultimoObstaculo.getCategoria().ordinal() || r.getSecuencia().get(0) == ultimoObstaculoId)
                    .max(Comparator.comparingInt(Relacion::getModificador))
                    .orElseThrow(() -> new RuntimeException("No se encontró una relación válida"));
            primerSecuencia = false;
            secuencia.clear();
        } while (tiempoActual < tiempoTotal);

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            bounds.add(Pair.of(0, obstaculos.size() - 1));
        }

        return solution;
    }

    public static VariableLengthIntegerSolution getSolucionMaxDificultad(List<Obstaculo> obstaculos, int tiempoTotal) {
        List<Pair<Integer, Integer>> bounds = new ArrayList<>();

        VariableLengthIntegerSolution solution = new VariableLengthIntegerSolution(bounds, 1);
        int tiempoActual = 0;

        // Seleccionar el jefe de mayor dificultad
        Obstaculo jefe = obstaculos.stream()
                .filter(o -> o.getCategoria() == Categoria.JEFE)
                .max(Comparator.comparingInt(Obstaculo::getDificultad))
                .orElseThrow(() -> new RuntimeException("No se encontró jefe"));

        // Agregar obstáculos hasta llegar al tiempo límite
        while (tiempoActual + jefe.getTiempo() <= tiempoTotal) {
            int auxTiempoActual = tiempoActual;
            Obstaculo obstaculo = obstaculos.stream()
                    .filter(o -> o.getCategoria() != Categoria.JEFE && o.getTiempo() + jefe.getTiempo() <= tiempoTotal - auxTiempoActual)
                    .max(Comparator.comparingInt(Obstaculo::getDificultad))
                    .orElse(null);

            if (obstaculo != null) {
                solution.addVariable(obstaculo.getId());
                tiempoActual += obstaculo.getTiempo();
            } else {
                break;
            }
        }

        // Agregar el jefe al final
        solution.addVariable(jefe.getId());

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            bounds.add(Pair.of(0, obstaculos.size() - 1));
        }

        return solution;
    }
}