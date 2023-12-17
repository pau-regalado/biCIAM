package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import metaheurictics.strategy.Strategy;

import problem.definition.State;
import problem.definition.Problem.ProblemType;
import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.Mutation;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import factory_interface.IFFactoryFatherSelection;
import factory_interface.IFFactoryMutation;
import factory_interface.IFFactoryReplace;
import factory_method.FactoryFatherSelection;
import factory_method.FactoryMutation;
import factory_method.FactoryReplace;

public class EvolutionStrategies extends Generator {
	
	private State stateReferenceES;
	private List<State> listStateReference = new ArrayList<State>(); 
	private IFFactoryFatherSelection iffatherselection;
	private IFFactoryMutation iffactorymutation;
	private IFFactoryReplace iffreplace;
//	private SelectionType selectionType;
//	private MutationType mutationType;
//	private ReplaceType replaceType;
	private GeneratorType generatorType;
	public static double PM;
	public static MutationType mutationType;
	public static ReplaceType replaceType;
	public static SelectionType selectionType;
	public static int countRef = 0;
	public static int truncation;
	private float weight = 50;
	
	//problemas dinamicos
	public static int countGender = 0;
	public static int countBetterGender = 0;
	private int[] listCountBetterGender = new int[10];
	private int[] listCountGender = new int[10];
	private float[] listTrace = new float[1200000];
	
	public EvolutionStrategies() {
		super();
		this.listStateReference = getListStateRef(); 
//		this.selectionType = SelectionType.Truncation;
//		this.mutationType = MutationType.UniformMutation;
//		this.replaceType = ReplaceType.Smallest;
		this.generatorType = GeneratorType.EvolutionStrategies;
		this.weight = 50;
		listTrace[0] = this.weight;
		listCountBetterGender[0] = 0;
		listCountGender[0] = 0;
	}

	@Override
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {

    	//ArrayList<State> list = new ArrayList<State>();
//		List<State> refList = new ArrayList<State>(this.listStateReference); 
    	iffatherselection = new FactoryFatherSelection();
    	FatherSelection selection = iffatherselection.createSelectFather(selectionType);
    	List<State> fathers = selection.selection(this.listStateReference, truncation);
    	int pos1 = (int)(Math.random() * fathers.size());
    	State candidate = (State) Strategy.getStrategy().getProblem().getState().getCopy();
    	candidate.setCode(new ArrayList<Object>(fathers.get(pos1).getCode()));
    	candidate.setEvaluation(fathers.get(pos1).getEvaluation());
    	candidate.setNumber(fathers.get(pos1).getNumber());
    	candidate.setTypeGenerator(fathers.get(pos1).getTypeGenerator());
    	
    	//**********mutacion******************************************** 	
    	iffactorymutation = new FactoryMutation();
    	Mutation mutation = iffactorymutation.createMutation(mutationType);
    	candidate = mutation.mutation(candidate, PM);
    	//list.add(candidate);    	
    	return candidate;
	}

	@Override
	public State getReference() {
		stateReferenceES = listStateReference.get(0);
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			for (int i = 1; i < listStateReference.size(); i++) {
				if(stateReferenceES.getEvaluation().get(0) < listStateReference.get(i).getEvaluation().get(0))
					stateReferenceES = listStateReference.get(i);
			}
		}
		else{
			for (int i = 1; i < listStateReference.size(); i++) {
				if(stateReferenceES.getEvaluation().get(0) > listStateReference.get(i).getEvaluation().get(0))
					stateReferenceES = listStateReference.get(i);
			}
		}
		return stateReferenceES;
	}
	
	public void setStateRef(State stateRef) {
		this.stateReferenceES = stateRef;
	}

	@Override
	public GeneratorType getType() {
		return this.generatorType;
	}

	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceES = stateInitialRef;
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		iffreplace = new FactoryReplace();
		Replace replace = iffreplace.createReplace(replaceType);
		listStateReference = replace.replace(stateCandidate, listStateReference);

	}
	
	public List<State> getListStateRef(){
		Boolean found = false;
		List<String> key = Strategy.getStrategy().getListKey();
		int count = 0;
		//if(Strategy.getStrategy().Statistics.getAllStates().size() == 0){
		/*if(RandomSearch.listStateReference.size() == 0){
			return this.listStateReference = new ArrayList<State>();
		}*/
		while((found.equals(false)) && (Strategy.getStrategy().mapGenerators.size() > count)){
			if(key.get(count).equals(GeneratorType.EvolutionStrategies.toString())){
				GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
				EvolutionStrategies generator = (EvolutionStrategies) Strategy.getStrategy().mapGenerators.get(keyGenerator);
				if(generator.getListStateReference().isEmpty()){
					listStateReference.addAll(RandomSearch.listStateReference);
					//for (int j = 1; j < Strategy.getStrategy().Statistics.getAllStates().size(); j++) {
//					for (int j = 1; j < RandomSearch.listStateReference.size(); j++) {
//						if(listStateReference.size() != countRef){
							//State problemState = Strategy.getStrategy().Statistics.getAllStates().get(j);
//							listStateReference.add(RandomSearch.listStateReference.get(j));
//						}
//					}  
				}
				else{
					listStateReference = generator.getListStateReference();
				}
			        found = true;
			}
			count++;
		}
		return listStateReference;
	}

	public List<State> getListStateReference() {
		return listStateReference;
	}

	public void setListStateReference(List<State> listStateReference) {
		this.listStateReference = listStateReference;
	}

	public GeneratorType getTypeGenerator() {
		return generatorType;
	}

	public void setTypeGenerator(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

	@Override
	public List<State> getReferenceList() {
		List<State> ReferenceList = new ArrayList<State>();
		for (int i = 0; i < listStateReference.size(); i++) {
			State value = listStateReference.get(i);
			ReferenceList.add(value);
		}
		return ReferenceList;
	}

	@Override
	public List<State> getSonList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean awardUpdateREF(State stateCandidate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getWeight() {
		// TODO Auto-generated method stub
		return this.weight;
	}

	@Override
	public void setWeight(float weight) {
		// TODO Auto-generated method stub
		this.weight = weight;
	}

	@Override
	public int[] getListCountBetterGender() {
		// TODO Auto-generated method stub
		return this.listCountBetterGender;
	}

	@Override
	public int[] getListCountGender() {
		// TODO Auto-generated method stub
		return this.listCountGender;
	}

	@Override
	public float[] getTrace() {
		// TODO Auto-generated method stub
		return this.listTrace;
	}


}
