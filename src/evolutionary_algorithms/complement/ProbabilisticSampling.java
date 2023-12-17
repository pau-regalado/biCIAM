package evolutionary_algorithms.complement;


import java.util.ArrayList;
import java.util.List;

import metaheurictics.strategy.Strategy;
import metaheuristics.generators.GeneratorType;

import problem.definition.State;


public class ProbabilisticSampling extends Sampling {

	@Override
	public List<State> sampling(List<State> fathers, int countInd) {
		// TODO Auto-generated method stub
		int cantV = fathers.get(0).getCode().size();
		List<State> staList = listState(countInd);
		for (int i = 0; i < cantV; i++) {
			Object[] values = new Object[fathers.size()]; // arreglo de valores de una variable
			Object[] arrtemp = new Object[Strategy.getStrategy().getProblem().getPossibleValue()];
			//llenar el arreglo con todos los valores posibles
			for (int j = 0; j < arrtemp.length; j++) {
				arrtemp[j] = j;
			}
			for (int j = 0; j < values.length; j++) {
				values[j] = fathers.get(j).getCode().get(i);
			}
			int k = 0;
			int sum = 0; // suma acumulativa por cada valor posible
			int arrOcc[] = new int[arrtemp.length]; // arreglo paralelo para contar la cantidad de ocurrencias de un valor posible
			//llenar el arreglo con la cantidad de ocurrencias de cada valor posible, para cada variable
			//recorrer el arreglo de valores de una variable
			while (k < arrtemp.length) {
				int count = 0;
				for (int j = 0; j < values.length; j++) {
					if((Integer)values[j] != -1 && values[j] == arrtemp[k]){ //
						count++;
						values[j] = -1;
					}
				}
				arrOcc[k] = count;
				sum = sum + count;
				k++;
			}
			for (int l = 0; l < countInd; l++) {
				boolean find = false;
				int p = 0;
				int random = (int)(Math.random() * (double)(sum)) + 1;
				while (p < arrOcc.length && find == false) {
					random = random - arrOcc[p];
					if(random <= 0){
						staList.get(l).getCode().add(arrtemp[p]);
						find = true;
					}
					else p++;
				}
				if(find == false){
					int value = new Integer((int)(Math.random() * (double)(Strategy.getStrategy().getProblem().getCodification().getVariableCount() * 10)));
					staList.get(l).getCode().add(value);
				}
				
			}
		}
		return staList;
	}
	
	// inicializa la lista de individuos
		public List<State> listState(int countInd) {
			List<State> staList = new ArrayList<State>(countInd);
			for (int i = 0; i < countInd; i++) {
				State state = new State();
				state.setCode(new ArrayList<Object>());
				state.setNumber(Strategy.getStrategy().getCountCurrent());
				state.setTypeGenerator(GeneratorType.DistributionEstimationAlgorithm);
				staList.add(state);
			}
			return staList;
		}
}
