package algorithm.runners;

import algorithm.*;
import algorithm.data.Obstaculo;
import algorithm.data.Relacion;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

public class NivelesPorDificultadRunner extends AbstractAlgorithmRunner {
    private static final int tiempoTotal = 600;
    private static final double crossoverProbability = 0.8;
    private static final double mutationProbability = 1.0;
    private static final int populationSize = 100;
    private static final int generations = 250;

    public static void main(String[] args) {
        // Crear la instancia del problema
        NivelesPorDificultad problem = new NivelesPorDificultad(tiempoTotal);
        List<Obstaculo> obstaculos = problem.getObstaculos();
        List<Relacion> relaciones = problem.getRelaciones();

        LoggingGeneticAlgorithm<IntegerSolution> algorithm = AlgorithmFactory.createAlgorithm(
                crossoverProbability, mutationProbability, tiempoTotal, populationSize, generations);

        long startTime = System.currentTimeMillis();
        algorithm.run();
        long endTime = System.currentTimeMillis();
        long executionTimeAE = endTime - startTime;

        // Obtener la mejor solución
        IntegerSolution solution = algorithm.getResult();

        List<IntegerSolution> population = new ArrayList<>(1);
        population.add(solution);
        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        startTime = System.currentTimeMillis();
        VariableLengthIntegerSolution solGreedyRandom = Greedy.getSolucionRandom(obstaculos, tiempoTotal);
        problem.evaluate(solGreedyRandom);
        endTime = System.currentTimeMillis();
        long executionTimeRandom = endTime - startTime;

        startTime = System.currentTimeMillis();
        VariableLengthIntegerSolution solGreedyMaxDif = Greedy.getSolucionMaxDificultad(obstaculos, tiempoTotal);
        problem.evaluate(solGreedyMaxDif);
        endTime = System.currentTimeMillis();
        long executionTimeMaxDif = endTime - startTime;

        startTime = System.currentTimeMillis();
        VariableLengthIntegerSolution solGreedyRel = Greedy.getSolucionRelaciones(obstaculos, relaciones, tiempoTotal);
        problem.evaluate(solGreedyRel);
        endTime = System.currentTimeMillis();
        long executionTimeRel = endTime - startTime;

        System.out.println("Greedy Random: " + solGreedyRandom);
        System.out.println("Greedy Max Dificultad: " + solGreedyMaxDif);
        System.out.println("Greedy Relaciones: " + solGreedyRel);

        System.out.println("Execution time AE: " + executionTimeAE + " milliseconds");
        System.out.println("Execution time Greedy Random: " + executionTimeRandom + " milliseconds");
        System.out.println("Execution time Greedy Max Dificultad: " + executionTimeMaxDif + " milliseconds");
        System.out.println("Execution time Greedy Relaciones: " + executionTimeRel + " milliseconds");
    }
}