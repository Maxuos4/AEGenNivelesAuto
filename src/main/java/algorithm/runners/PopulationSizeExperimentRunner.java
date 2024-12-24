package algorithm.runners;

import algorithm.*;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Experimento para medir la poblacion optima
public class PopulationSizeExperimentRunner extends AbstractAlgorithmRunner {
    private static final int minTiempoTotal = 600;
    private static final int maxTiempoTotal = 601;
    private static final int generations = 500;
    private static final int numRuns = 50;

    public static void main(String[] args) {
        Random random = new Random();

        double crossoverProbability = 1.0;
        double mutationProbability = 0.2;
        int[] populationSizes = {500};

        // Generar largo de nivel variable para cada corrida
        int[] tiempoTotalValues = new int[numRuns];
        for (int run = 0; run < numRuns; run++) {
            tiempoTotalValues[run] = random.nextInt(maxTiempoTotal - minTiempoTotal) + minTiempoTotal;
        }

        double totalVariedad = 0.0;

        try (FileWriter writer = new FileWriter("population_size_experiment_results.csv")) {
            writer.append("PopulationSize;AvgFitness;StdDevFitness;AvgExecutionTime\n");

            for (int populationSize : populationSizes) {
                List<Double> bestFitnesses = new ArrayList<>();
                List<Long> executionTimes = new ArrayList<>();

                for (int run = 0; run < numRuns; run++) {
                    LoggingGeneticAlgorithm<IntegerSolution> algorithm = AlgorithmFactory.createAlgorithm(
                            crossoverProbability, mutationProbability, tiempoTotalValues[run], populationSize, generations);

                    long startTime = System.currentTimeMillis();
                    algorithm.run();
                    long endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;
                    executionTimes.add(executionTime);

                    IntegerSolution solution = algorithm.getResult();

                    double variedad = NivelesPorDificultad.cantidadObstaculosDistintos(solution.getVariables());
                    totalVariedad += variedad;

                    // Registrar el fitness de la mejor solución
                    bestFitnesses.add(solution.getObjective(0) * -1);
                }

                // Calcular estadísticas
                double averageFitness = bestFitnesses.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double stdDevFitness = calculateStandardDeviation(bestFitnesses);
                double averageExecutionTime = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

                double averageVariedad = totalVariedad / numRuns;

                System.out.println(averageVariedad);

                // Escribir resultados en el archivo CSV
                writer.append(String.format("%d;%.4f;%.4f;%.2f\n", populationSize, averageFitness, stdDevFitness, averageExecutionTime));
            }

            System.out.println("Resultados del experimento guardados en 'population_size_experiment_results.csv'.");

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