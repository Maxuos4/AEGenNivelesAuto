package algorithm;

import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.ArrayList;
import java.util.List;

public class LoggingGeneticAlgorithm<S extends Solution<?>> extends GenerationalGeneticAlgorithm<S> {
    private final List<Double> bestFitnessPerGeneration = new ArrayList<>();

    public LoggingGeneticAlgorithm(
            Problem<S> problem,
            int maxEvaluations,
            int populationSize,
            CrossoverOperator<S> crossoverOperator,
            MutationOperator<S> mutationOperator,
            SelectionOperator<List<S>, S> selectionOperator) {
        super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, new SequentialSolutionListEvaluator<>());
    }

    @Override
    public void updateProgress() {
        super.updateProgress();
        double bestFitness = getPopulation().stream()
                .mapToDouble(solution -> solution.getObjective(0))
                .min()
                .orElse(Double.POSITIVE_INFINITY);
        bestFitnessPerGeneration.add(-1 * bestFitness);
    }

    public List<Double> getBestFitnessPerGeneration() {
        return bestFitnessPerGeneration;
    }
}
