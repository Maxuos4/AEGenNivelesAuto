package algorithm;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

public class AlgorithmFactory {

    public static LoggingGeneticAlgorithm<IntegerSolution> createAlgorithm(
            double crossoverProbability,
            double mutationProbability,
            int tiempoTotal,
            int populationSize,
            int generations) {

        NivelesPorDificultad problem = new NivelesPorDificultad(tiempoTotal);

        CrossoverOperator<IntegerSolution> crossover = new VariableLengthCrossover(crossoverProbability);
        MutationOperator<IntegerSolution> mutation = new VariableLengthMutation(mutationProbability, problem.getObstaculos().size());
        BinaryTournamentSelection<IntegerSolution> selection = new BinaryTournamentSelection<>();

        int maxEvaluations = populationSize * generations;

        return new LoggingGeneticAlgorithm<>(
                problem,
                maxEvaluations,
                populationSize,
                crossover,
                mutation,
                selection);
    }
}
