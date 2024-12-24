package algorithm.runners;

import algorithm.AlgorithmFactory;
import algorithm.LoggingGeneticAlgorithm;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbabilitiesExperimentRunner extends AbstractAlgorithmRunner {
    private static final int minTiempoTotal = 600;
    private static final int maxTiempoTotal = 601;

    private static final int populationSize = 300;
    private static final int generations = 250;
    private static final int numRuns = 100;

    public static void main(String[] args) {
        Random random = new Random();

        double[] crossoverProbabilities = {0.8, 0.9, 1.0};
        double[] mutationProbabilities = {0.1, 0.2, 0.4};

        // Generar largo de nivel variable para cada corrida
        int[] tiempoTotalValues = new int[numRuns];
        for (int run = 0; run < numRuns; run++) {
            tiempoTotalValues[run] = random.nextInt(maxTiempoTotal-minTiempoTotal) + minTiempoTotal;
            System.out.println("Tiempo:" + tiempoTotalValues[run]);
        }

        try (FileWriter writer = new FileWriter("experiment_results.csv")) {
            writer.append("Crossover;Mutation;AvgFitness;StdDevFitness\n");

            for (double crossoverProbability : crossoverProbabilities) {
                for (double mutationProbability : mutationProbabilities) {
                    List<Double> bestFitnesses = new ArrayList<>();

                    for (int run = 0; run < numRuns; run++) {
                        LoggingGeneticAlgorithm<IntegerSolution> algorithm = AlgorithmFactory.createAlgorithm(
                                crossoverProbability, mutationProbability, tiempoTotalValues[run], populationSize, generations);

                        algorithm.run();
                        IntegerSolution solution = algorithm.getResult();

                        // Registrar el fitness de la mejor solución
                        bestFitnesses.add(solution.getObjective(0) * -1);
                    }

                    // Calcular estadísticas
                    double averageFitness = bestFitnesses.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double stdDevFitness = calculateStandardDeviation(bestFitnesses);

                    // Escribir resultados en el archivo CSV
                    writer.append(String.format("%.2f;%.2f;%.4f;%.4f\n",
                            crossoverProbability, mutationProbability, averageFitness, stdDevFitness));
                }
            }

            System.out.println("Resultados del experimento guardados en 'experiment_results.csv'.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateStandardDeviation(List<Double> values) {
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return Math.sqrt(values.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0));
    }
}
