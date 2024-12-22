package algorithm;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.List;
import java.util.Random;

public class VariableLengthMutation implements MutationOperator<IntegerSolution> {
    private final double mutationProbability;
    private final int maxObstacles;
    private final Random random;

    public VariableLengthMutation(double mutationProbability, int maxObstacles) {
        this.mutationProbability = mutationProbability;
        this.maxObstacles = maxObstacles;
        this.random = new Random();
    }

    @Override
    public IntegerSolution execute(IntegerSolution solution) {
        if (random.nextDouble() < mutationProbability) {
            List<Integer> variables = solution.getVariables();
            if (random.nextBoolean() && variables.size() < maxObstacles) {
                int newObstacle = random.nextInt(maxObstacles);
                variables.add(newObstacle);
            } else if (variables.size() > 1) {
                int indexToRemove = random.nextInt(variables.size());
                variables.remove(indexToRemove);
            }
        }
        return solution;
    }

    @Override
    public double getMutationProbability() {
        return mutationProbability;
    }
}