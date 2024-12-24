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

//Experimento para calcular los resultados con distinta cantidad de generaciones
public class GenerationsExperimentRunner extends AbstractAlgorithmRunner {
    private static final int minTiempoTotal = 600;
    private static final int maxTiempoTotal = 601;
    private static final int numRuns = 30;

    public static void main(String[] args) {
        Random random = new Random();

        double crossoverProbability = 1.0;
        double mutationProbability = 0.2;
        int populationSize = 500;
        int[] generationsArray = {500, 1000, 2500};

        // Generar largo de nivel variable para cada corrida
        int[] tiempoTotalValues = new int[numRuns];
        for (int run = 0; run < numRuns; run++) {
            tiempoTotalValues[run] = random.nextInt(maxTiempoTotal - minTiempoTotal) + minTiempoTotal;
        }

        try (FileWriter writer = new FileWriter("generations_experiment_results.csv")) {
            writer.append("Generations;BestFitness;AvgFitness;StdDevFitness\n");

            for (int generations : generationsArray) {
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
                double bestFitness = bestFitnesses.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                double averageFitness = bestFitnesses.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double stdDevFitness = calculateStandardDeviation(bestFitnesses);

                writer.append(String.format("%d;%.4f;%.4f;%.4f\n", generations, bestFitness, averageFitness, stdDevFitness));
            }

            System.out.println("Resultados del experimento guardados en 'generations_experiment_results.csv'.");

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