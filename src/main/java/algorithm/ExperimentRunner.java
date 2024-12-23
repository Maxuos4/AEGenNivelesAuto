package algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExperimentRunner extends AbstractAlgorithmRunner {
    private static final int tiempoTotal = 150;
    private static final int startingLength = 30;
    private static final int popultationSize = 10;
    private static final int maxEvaluations = 25000;
    private static final int numRuns = 20; // Número de ejecuciones por configuración

    public static void main(String[] args) {
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

        double[] crossoverProbabilities = {0.6, 0.7, 0.8, 0.9};
        double[] mutationProbabilities = {0.1, 0.2, 0.05, 0.01};

        try (FileWriter writer = new FileWriter("experiment_results.csv")) {
            writer.append("Crossover;Mutation;AvgFitness;StdDevFitness\n");

            for (double crossoverProbability : crossoverProbabilities) {
                for (double mutationProbability : mutationProbabilities) {
                    List<Double> bestFitnesses = new ArrayList<>();

                    for (int run = 0; run < numRuns; run++) {
                        NivelesPorDificultad problem = new NivelesPorDificultad(
                                startingLength, 0, obstaculos.size() - 1, relaciones, obstaculos, tiempoTotal);

                        // Configurar operadores
                        CrossoverOperator<IntegerSolution> crossover = new VariableLengthCrossover(crossoverProbability);
                        MutationOperator<IntegerSolution> mutation = new VariableLengthMutation(mutationProbability, obstaculos.size());
                        BinaryTournamentSelection<IntegerSolution> selection = new BinaryTournamentSelection<>();

                        Algorithm<IntegerSolution> algorithm =
                                new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                                        .setPopulationSize(popultationSize)
                                        .setMaxEvaluations(maxEvaluations)
                                        .setSelectionOperator(selection)
                                        .build();

                        algorithm.run();
                        IntegerSolution solution = algorithm.getResult();

                        // Registrar el fitness de la mejor solución
                        bestFitnesses.add(solution.getObjective(0) * -1);
                    }

                    // Calcular estadísticas
                    double averageFitness = bestFitnesses.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double stdDevFitness = calculateStandardDeviation(bestFitnesses);

                    // Escribir resultados en el archivo CSV
                    writer.append(String.format("%.2f;%.2f;%.4f;%.4f\n",
                            crossoverProbability, mutationProbability, averageFitness, stdDevFitness));
                }
            }

            System.out.println("Resultados del experimento guardados en 'experiment_results.csv'.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateStandardDeviation(List<Double> values) {
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return Math.sqrt(values.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0));
    }
}
