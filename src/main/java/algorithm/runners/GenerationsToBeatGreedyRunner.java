package algorithm.runners;

import algorithm.*;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Experimento para medir generaciones necesarias para superar a los greedy
public class GenerationsToBeatGreedyRunner {
    private static final int tiempoTotal = 600;
    private static final int populationSize = 500;
    private static final int generations = 1000;
    private static final int numRuns = 50;

    private static final double crossoverProbability = 1.0;
    private static final double mutationProbability = 0.2;

    public static void main(String[] args) {
        NivelesPorDificultad problem = new NivelesPorDificultad(tiempoTotal);

        List<Double> greedySolutions = new ArrayList<>();

        double totalFitness = 0.0;
        double totalVariedad = 0.0;

        for (int i = 0; i < 50; i++) {
            VariableLengthIntegerSolution solGreedyRandom = Greedy.getSolucionRandom(problem.getObstaculos(), tiempoTotal);
            problem.evaluate(solGreedyRandom);
            double variedad = NivelesPorDificultad.cantidadObstaculosDistintos(solGreedyRandom.getVariables());
            double fitness = solGreedyRandom.getObjective(0);
            totalVariedad += variedad;
            totalFitness += fitness;
        }

        double averageVariedad = totalVariedad / 50;
        double averageFitnessRandom = totalFitness / 50;

        System.out.println(averageVariedad);

        VariableLengthIntegerSolution solGreedyMaxDif = Greedy.getSolucionMaxDificultad(problem.getObstaculos(), tiempoTotal);
        problem.evaluate(solGreedyMaxDif);

        VariableLengthIntegerSolution solGreedyRel = Greedy.getSolucionRelaciones(problem.getObstaculos(), problem.getRelaciones(), tiempoTotal);
        problem.evaluate(solGreedyRel);

        greedySolutions.add(-1 * averageFitnessRandom);
        greedySolutions.add(-1 * solGreedyMaxDif.getObjective(0));
        greedySolutions.add(-1 * solGreedyRel.getObjective(0));
        greedySolutions.add(-1 * (solGreedyRel.getObjective(0) + solGreedyRel.getObjective(0) * 0.1));

        System.out.println(greedySolutions);

        try (FileWriter writer = new FileWriter("generations_to_beat_greedy.csv")) {
            writer.append("Run,GensToRandom,GensToMaxDif,GensToRel,Rel+20%\n");

            for (int run = 0; run < numRuns; run++) {
                 LoggingGeneticAlgorithm<IntegerSolution> algorithm = AlgorithmFactory.createAlgorithm(
                        crossoverProbability, mutationProbability, tiempoTotal, populationSize, generations);

                 algorithm.run();
                List<Double> bestFitnessPerGeneration = algorithm.getBestFitnessPerGeneration();
                System.out.println(bestFitnessPerGeneration);
                // Encontrar el primer índice que supera cada solución greedy
                int[] indices = new int[greedySolutions.size()];
                Arrays.fill(indices, -1);

                for (int i = 0; i < greedySolutions.size(); i++) {
                    for (int j = 0; j < bestFitnessPerGeneration.size(); j++) {
                        if (bestFitnessPerGeneration.get(j) > greedySolutions.get(i)) {
                            indices[i] = j;
                            break;
                        }
                    }
                }
                writer.append(String.format("%d,%d,%d,%d,%d\n",run, indices[0], indices[1], indices[2], indices[3]));
            }

            System.out.println("Resultados guardados en 'generations_to_beat_greedy.csv'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}