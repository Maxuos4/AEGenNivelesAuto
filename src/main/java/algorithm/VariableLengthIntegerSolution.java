package algorithm;

import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class VariableLengthIntegerSolution extends DefaultIntegerSolution {
    public VariableLengthIntegerSolution(List<Pair<Integer, Integer>> bounds, int numberOfObjectives) {
        super(bounds, numberOfObjectives);
    }

    public void addVariable(int value) {
        this.getVariables().add(value);
    }

    @Override
    public int getNumberOfVariables() {
        return this.getVariables().size();
    }
}