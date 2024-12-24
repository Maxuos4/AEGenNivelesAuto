package algorithm;

import algorithm.data.Categoria;
import algorithm.data.Obstaculo;
import algorithm.data.Relacion;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.*;

import static org.uma.jmetal.problem.singleobjective.cec2005competitioncode.Benchmark.random;

public class NivelesPorDificultad extends AbstractIntegerProblem {
    private final List<Obstaculo> obstaculos;
    private final List<Relacion> relaciones;
    private final int tiempoTotal;

    public NivelesPorDificultad(int tiempoTotal) {
        this.obstaculos = Arrays.asList(
            //Estructura
            new Obstaculo("Plataforma", 		0, 0, 0, 1),
            new Obstaculo("Plataforma Móvil", 		1, 0, 1, 2),
            new Obstaculo("Plataforma Balance", 	2, 0, 3, 3),
            new Obstaculo("Pared 1", 			3, 0, 1, 1),
            new Obstaculo("Pared 2", 			4, 0, 2, 2),
            new Obstaculo("Pared 3", 			5, 0, 3, 3),
            new Obstaculo("Liana", 			6, 0, 3, 2),

            //Trampas
            new Obstaculo("Foso", 		        	7, 1, 2, 1),
            new Obstaculo("Péndulo Cortante", 		8, 1, 4, 2),
            new Obstaculo("Láser Intermitente", 	9, 1, 4, 4),
            new Obstaculo("Plataforma que cae", 	10, 1, 2, 1),
            new Obstaculo("Plataforma resbalosa",   11, 1, 3, 2),
            new Obstaculo("Cañón Automático", 	    12, 1, 4, 4),
            new Obstaculo("Puas que caen", 	    	13, 1, 4, 2),
            new Obstaculo("Ventilador", 		    14, 1, 3, 3),
            new Obstaculo("Lava", 			        15, 1, 4, 1),

            //Enemigos
            new Obstaculo("Estacionario", 		16, 2, 3, 2),
            new Obstaculo("Caminador", 	    	17, 2, 4, 3),
            new Obstaculo("Saltador", 			18, 2, 6, 5),
            new Obstaculo("Volador", 			19, 2, 8, 8),
            new Obstaculo("Disparador", 		20, 2, 9, 6),
            new Obstaculo("Luchador",	 		21, 2, 7, 7),
            new Obstaculo("Splitter", 			22, 2, 6, 10),

            //Jefes
            new Obstaculo("Jefe Splitter", 		23, 3, 19, 20),
            new Obstaculo("Coloso", 			24, 3, 21, 18),
            new Obstaculo("Asesino", 			25, 3, 25, 14),
            new Obstaculo("Tanque", 			26, 3, 16, 20),
            new Obstaculo("Spawner", 			27, 3, 22, 16)
        );

        this.relaciones = Arrays.asList(
            new Relacion(Arrays.asList(0, 1), 3, "categoria"),
            new Relacion(Arrays.asList(1, 0), 1, "categoria"),
            new Relacion(Arrays.asList(0, 2), 2, "categoria"),
            new Relacion(Arrays.asList(2, 0), 1, "categoria"),
            new Relacion(Arrays.asList(1, 2), 4, "categoria"),
            new Relacion(Arrays.asList(2, 1), 2, "categoria"),
            new Relacion(Arrays.asList(0,0,0), -20, "categoria"),
            new Relacion(Arrays.asList(1,1,1), -20, "categoria"),
            new Relacion(Arrays.asList(2,2,2), -20, "categoria"),
            new Relacion(Arrays.asList(3, 4), -2, "obstaculo"),
            new Relacion(Arrays.asList(4, 5), -2, "obstaculo"),
            new Relacion(Arrays.asList(7, 5), 5, "obstaculo"),
            new Relacion(Arrays.asList(7, 4), 3, "obstaculo"),
            new Relacion(Arrays.asList(7,7,4), 5, "obstaculo"),
            new Relacion(Arrays.asList(7,7,5), -100, "obstaculo")
        );

        this.tiempoTotal = tiempoTotal;
        int startingLength = tiempoTotal / (obstaculos.stream().mapToInt(Obstaculo::getTiempo).sum() / obstaculos.size());
        setNumberOfVariables(startingLength);
        setNumberOfObjectives(1);

        List<Integer> lowerLimit = new ArrayList<>(startingLength);
        List<Integer> upperLimit = new ArrayList<>(startingLength);

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0);
            upperLimit.add(obstaculos.size()-1);
        }

        setVariableBounds(lowerLimit, upperLimit);
    }

    public List<Obstaculo> getObstaculos() {
        return obstaculos;
    }

    public List<Relacion> getRelaciones() {
        return relaciones;
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

    public static int cantidadObstaculosDistintos(List<Integer> list) {
            Set<Integer> distinctIntegers = new HashSet<>(list);
            return distinctIntegers.size();
    }
}