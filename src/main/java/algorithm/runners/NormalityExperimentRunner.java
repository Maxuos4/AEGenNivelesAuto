package algorithm.runners;

import algorithm.AlgorithmFactory;
import algorithm.LoggingGeneticAlgorithm;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NormalityExperimentRunner extends AbstractAlgorithmRunner {
    private static final int tiempoTotal = 400;
    private static final int populationSize = 500;
    private static final int generations = 250;
    private static final int numRuns = 50;

    private static final double crossoverProbability = 1.0;
    private static final double mutationProbability = 0.20;
    public static void main(String[] args) {

        try (FileWriter writer = new FileWriter("normality_test.csv")) {
            writer.append("Run,Fitness\n");

            KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
            List<Double> fitnessValues = new ArrayList<>();

            for (int run = 0; run < numRuns; run++) {
                LoggingGeneticAlgorithm<IntegerSolution> algorithm = AlgorithmFactory.createAlgorithm(
                        crossoverProbability, mutationProbability, tiempoTotal, populationSize, generations);

                algorithm.run();
                IntegerSolution solution = algorithm.getResult();

                double fitness = solution.getObjective(0);
                fitnessValues.add(fitness);

                writer.append(String.format("%d,%.4f\n", run + 1, fitness));
            }

            double[] fitnessArray = fitnessValues.stream().mapToDouble(Double::doubleValue).toArray();

            double mean = Arrays.stream(fitnessArray).average().orElse(0.0);
            double stdDev = calculateStandardDeviation(fitnessArray);

            NormalDistribution normalDistribution = new NormalDistribution(mean, stdDev);

            // Realizar test Kolmogorov-Smirnov
            double pValue = ksTest.kolmogorovSmirnovTest(normalDistribution, fitnessArray);

            System.out.printf("Kolmogorov-Smirnov Test Result: p-Value = %.4f\n", pValue);

            if (pValue < 0.05) {
                System.out.println("Los datos NO siguen una distribución normal.");
            } else {
                System.out.println("Los datos pueden seguir una distribución normal.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateStandardDeviation(double[] values) {
        double mean = Arrays.stream(values).average().orElse(0.0);
        return Math.sqrt(Arrays.stream(values).map(v -> Math.pow(v - mean, 2)).average().orElse(0.0));
    }
}