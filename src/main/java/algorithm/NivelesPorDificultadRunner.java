package algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NivelesPorDificultadRunner extends AbstractAlgorithmRunner {
    private static final int tiempoTotal = 40;
    private static final int startingLength = 3;
    private static final double crossoverProbability = 0.9;
    private static final double mutationProbability = 0.1;
    private static final int popultationSize = 100;
    private static final int maxEvaluations = 25000;

    public static void main(String[] args) {
        // Crear obstáculos de ejemplo
        List<Obstaculo> obstaculos = Arrays.asList(
                //Estructura
                new Obstaculo("Plataforma", 		0, 0, 0, 1),
                new Obstaculo("Plataforma Móvil", 		1, 0, 1, 2),
                new Obstaculo("Plataforma Balance", 	2, 0, 3, 3),
                new Obstaculo("Pared 1", 			3, 0, 1, 1),
                new Obstaculo("Pared 2", 			4, 0, 2, 2),
                new Obstaculo("Pared 3", 			5, 0, 3, 3), //Suponiendo que se puede poner solo no seria una plataforma inalcanzable?
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

        // Crear relaciones de ejemplo
        List<Relacion> relaciones = Arrays.asList(
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

        // Crear la instancia del problema
        NivelesPorDificultad problem = new NivelesPorDificultad(startingLength, 0, obstaculos.size() - 1, relaciones, obstaculos, tiempoTotal);

        // Configurar operadores
        CrossoverOperator<IntegerSolution> crossover = new VariableLengthCrossover(crossoverProbability);

        MutationOperator<IntegerSolution> mutation = new VariableLengthMutation(mutationProbability, obstaculos.size());

        BinaryTournamentSelection<IntegerSolution> selection = new BinaryTournamentSelection<>();

        // Crear el algoritmo
        Algorithm<IntegerSolution> algorithm =
                new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                        .setPopulationSize(popultationSize)
                        .setMaxEvaluations(maxEvaluations)
                        .setSelectionOperator(selection)
                        .build();

        // Ejecutar el algoritmo
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        // Obtener la mejor solución
        IntegerSolution solution = algorithm.getResult();

        List<IntegerSolution> population = new ArrayList<>(1);
        population.add(solution);
        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        VariableLengthIntegerSolution solGreedyRandom = Greedy.getSolucionRandom(obstaculos, tiempoTotal);
        problem.evaluate(solGreedyRandom);
        VariableLengthIntegerSolution solGreedyMaxDif = Greedy.getSolucionMaxDificultad(obstaculos, tiempoTotal);
        problem.evaluate(solGreedyMaxDif);
        VariableLengthIntegerSolution solGreedyRel = Greedy.getSolucionRelaciones(obstaculos, relaciones, tiempoTotal);
        problem.evaluate(solGreedyRel);

        System.out.println("Greedy Random: " + solGreedyRandom);
        System.out.println("Greedy Max Dificultad: " + solGreedyMaxDif);
        System.out.println("Greedy Relaciones: " + solGreedyRel);
    }
}