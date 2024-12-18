

import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 * Single objective problem for testing integer encoding.
 * Objective: minimizing the distance to value N
 */
@SuppressWarnings("serial")
public class NivelesPorDificultad extends AbstractIntegerProblem {
  private List<Obstaculo> obstaculos;
  private List<List<Integer>> relaciones;
  private int tiempoTotal;
  
  public NivelesPorDificultad() {
    this(100, 0, 20, null, null, 3000);
  }

  /** Constructor */
  public NivelesPorDificultad(int numberOfVariables, int lowerBound, int upperBound, List<List<Integer>> relaciones, List<Obstaculo> obstaculos, int tiempoTotal)  {
    this.obstaculos = obstaculos;
    this.relaciones = relaciones;
    this.tiempoTotal = tiempoTotal;
	setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(1);
    setName("NIntegerMin");

    List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(lowerBound);
      upperLimit.add(upperBound);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(IntegerSolution solution) {
    int fit;
    int secuencia = 0;
    int demora = 0;
    fit = 0;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      int value = solution.getVariable(i) ;
      int addon = 0;
      if (secuencia != 0) {
    	  secuencia--;
      } else {
    	  for(int j = 0; j < this.relaciones.size(); j++) {
    		  if ((this.relaciones.get(j).get(0) == value) && ((solution.getNumberOfVariables() - i) >= this.relaciones.get(j).size())) {
    			  boolean sec = true;
    			  int k = 0;
    			  while(sec && k < this.relaciones.get(j).size() - 1) {
    				  if (this.relaciones.get(j).get(k) == solution.getVariable(i + k)) {
    					  k++;
    				  } else {
    					  sec = false;
    				  }
    			  }
    			  if(sec && (k > secuencia)) {
    				  secuencia = k;
    				  addon = this.relaciones.get(j).get(k);
    			  }
    		  }
    	  }
      }
      demora = demora + this.obstaculos.get(value).getTiempo();
      fit = fit + addon + this.obstaculos.get(value).getDificultad();
    }
    fit = fit - (demora - this.tiempoTotal)*(demora - this.tiempoTotal);

    solution.setObjective(0, fit);
  }
}
