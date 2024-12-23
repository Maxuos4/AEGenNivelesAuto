package algorithm;

import algorithm.Obstaculo;
import algorithm.Relacion;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NormalityExperimentRunner extends AbstractAlgorithmRunner {
    private static final int numRuns = 100; // Número de ejecuciones para el experimento

    public static void main(String[] args) {
        Random random = new Random();
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

        double crossoverProbability = 0.8;
        double mutationProbability = 0.2;

        double[] fitnessValues = new double[numRuns];

        for (int run = 0; run < numRuns; run++) {
            NivelesPorDificultad problem = new NivelesPorDificultad(
                    10, 0, obstaculos.size() - 1, relaciones, obstaculos, 80);

            // Configurar operadores
            CrossoverOperator<IntegerSolution> crossover = new VariableLengthCrossover(crossoverProbability);
            MutationOperator<IntegerSolution> mutation = new VariableLengthMutation(mutationProbability, obstaculos.size());
            BinaryTournamentSelection<IntegerSolution> selection = new BinaryTournamentSelection<>();

            Algorithm<IntegerSolution> algorithm =
                    new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                            .setPopulationSize(10)
                            .setMaxEvaluations(25000)
                            .setSelectionOperator(selection)
                            .build();

            algorithm.run();
            IntegerSolution solution = algorithm.getResult();

            // Registrar el fitness de la mejor solución
            fitnessValues[run] = solution.getObjective(0) * -1;
            System.out.println(fitnessValues[run]);
        }


        // Realizar la prueba de Kolmogorov-Smirnov para verificar la normalidad
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        NormalDistribution normalDistribution = new NormalDistribution();
        double pValue = ksTest.kolmogorovSmirnovTest(normalDistribution, fitnessValues);


        if (pValue > 0.05) {
            System.out.println("La distribución de los valores de fitness es normal (p-valor = " + pValue + ").");
        } else {
            System.out.println("La distribución de los valores de fitness no es normal (p-valor = " + pValue + ").");
        }
    }
}