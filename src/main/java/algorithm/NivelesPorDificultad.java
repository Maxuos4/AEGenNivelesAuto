package algorithm;

import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;

import static org.uma.jmetal.problem.singleobjective.cec2005competitioncode.Benchmark.random;

public class NivelesPorDificultad extends AbstractIntegerProblem {
    private final List<Obstaculo> obstaculos;
    private final List<Relacion> relaciones;
    private final int tiempoTotal;

    public NivelesPorDificultad(int startingLength, int lowerBound, int upperBound, List<Relacion> relaciones, List<Obstaculo> obstaculos, int tiempoTotal) {
        this.obstaculos = obstaculos;
        this.relaciones = relaciones;
        this.tiempoTotal = tiempoTotal;

        setNumberOfVariables(startingLength);
        setNumberOfObjectives(1);

        List<Integer> lowerLimit = new ArrayList<>(startingLength);
        List<Integer> upperLimit = new ArrayList<>(startingLength);

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(lowerBound);
            upperLimit.add(upperBound);
        }

        setVariableBounds(lowerLimit, upperLimit);
    }

    @Override
    public IntegerSolution createSolution() {
        return new VariableLengthIntegerSolution(getVariableBounds(), getNumberOfObjectives());
    }

    @Override
    public void evaluate(IntegerSolution solution) {
        int fit = 0;
        int demora = 0;
        int lastIndex = solution.getNumberOfVariables() - 1;

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            int value = solution.getVariable(i);
            int addon = 0;
            //Si encuentro un Jefe
            if (obstaculos.get(value).getCategoria() == Categoria.JEFE) {
                if (i != lastIndex) {
                    //Chequeo si no tengo un jefe al final
                    int lastObs = solution.getVariable(lastIndex);
                    if (obstaculos.get(lastObs).getCategoria() != Categoria.JEFE) {
                        //Cambio al jefe por lo que se encontraba en el ultimo lugar
                        solution.setVariable(lastIndex, value);
                        solution.setVariable(i, lastObs);
                    } else {
                        solution.setVariable(i, random.nextInt(obstaculos.size()));
                    }
                }
            }
            demora += this.obstaculos.get(value).getTiempo();
            fit += addon + this.obstaculos.get(value).getDificultad();
        }

        for (Relacion relacion : relaciones) {
            if (relacion.getTipo().equals("obstaculo") && incluyeSecuencia(solution, relacion.getSecuencia())) {
                fit += relacion.getModificador();
            } else if (relacion.getTipo().equals("categoria") && incluyeSecuenciaCategorias(solution, relacion.getSecuencia())) {
                fit += relacion.getModificador();
            }
        }

        fit -= (int) Math.pow(demora - this.tiempoTotal, 2);
        solution.setObjective(0, -fit);
    }

    private boolean incluyeSecuencia(IntegerSolution solution, List<Integer> secuencia) {
        for (int i = 0; i <= solution.getNumberOfVariables() - secuencia.size(); i++) {
            boolean match = true;
            for (int j = 0; j < secuencia.size(); j++) {
                if (!solution.getVariable(i + j).equals(secuencia.get(j))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    private boolean incluyeSecuenciaCategorias(IntegerSolution solution, List<Integer> secuencia) {
        for (int i = 0; i <= solution.getNumberOfVariables() - secuencia.size(); i++) {
            boolean match = true;
            for (int j = 0; j < secuencia.size(); j++) {
                if (!(this.obstaculos.get(solution.getVariable(i + j)).getCategoria().ordinal() == secuencia.get(j))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }
}