package algorithm;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VariableLengthCrossover implements CrossoverOperator<IntegerSolution> {
    private final double crossoverProbability;
    private final Random random;

    public VariableLengthCrossover(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
        this.random = new Random();
    }

    @Override
    public List<IntegerSolution> execute(List<IntegerSolution> parents) {
        List<IntegerSolution> offspring = new ArrayList<>(2);
        offspring.add((IntegerSolution) parents.get(0).copy());
        offspring.add((IntegerSolution) parents.get(1).copy());

        if (random.nextDouble() < crossoverProbability) {
            int minLength = Math.min(parents.get(0).getNumberOfVariables(), parents.get(1).getNumberOfVariables());
            int crossoverPoint = random.nextInt(minLength);

            for (int i = crossoverPoint; i < minLength; i++) {
                int temp = offspring.get(0).getVariable(i);
                offspring.get(0).setVariable(i, offspring.get(1).getVariable(i));
                offspring.get(1).setVariable(i, temp);
            }
        }

        return offspring;
    }

    @Override
    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }
}