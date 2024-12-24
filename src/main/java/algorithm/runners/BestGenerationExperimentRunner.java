package algorithm.runners;

import algorithm.AlgorithmFactory;
import algorithm.LoggingGeneticAlgorithm;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//Experimento para el mejor fitness por generacion
public class BestGenerationExperimentRunner {
    private static final int tiempoTotal = 600;
    private static final int populationSize = 500;
    private static final int generations = 750;
    private static final int numRuns = 1;

    private static final double crossoverProbability = 1.0;
    private static final double mutationProbability = 0.2;

    public static void main(String[] args) {

        try (FileWriter writer = new FileWriter("generation_analysis_results.csv")) {
            writer.append("Run,Generation,BestFitness\n");

            for (int run = 0; run < numRuns; run++) {
                LoggingGeneticAlgorithm<IntegerSolution> algorithm = AlgorithmFactory.createAlgorithm(
                        crossoverProbability, mutationProbability, tiempoTotal, populationSize, generations);

                algorithm.run();

                // Recupero los resultados de fitness por generación
                List<Double> bestFitnessPerGeneration = algorithm.getBestFitnessPerGeneration();

                int generationCount = 0;
                for (Double fitness : bestFitnessPerGeneration) {
                    writer.append(String.format("%d,%d,%.4f\n", run + 1, generationCount++, fitness));
                }
            }

            System.out.println("Resultados del análisis de generaciones guardados en 'generation_analysis_results.csv'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
