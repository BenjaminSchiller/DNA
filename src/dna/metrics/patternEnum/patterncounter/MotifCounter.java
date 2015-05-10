package dna.metrics.patternEnum.patterncounter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.utils.CanonicalLabelGenerator;
import dna.metrics.patternEnum.utils.GraphTransformer;

abstract class MotifCounter implements IPatternCounter {
	private HashMap<MotifType, Integer> motifCounter = new HashMap<>();
	private List<MotifType> orderedMotifs = new ArrayList<>();
	
	public HashMap<MotifType, Integer> getMotifCounter() {
		return motifCounter;
	}

	public List<MotifType> getOrderedMotifs() {
		return orderedMotifs;
	}

	public void setOrderedMotifs(List<MotifType> orderedMotifs) {
		this.orderedMotifs = orderedMotifs;
	}

	public void incrementCounterFor(Collection<Path> graphs){
		for (Path g : graphs) {
			incrementCounterFor(g.getGraph());
		}
	}
	
	public abstract int incrementCounterFor(SmallGraph graph);
	
	protected int incrementCounterFor(MotifType motifType){
		Integer counter = motifCounter.get(motifType);
		
		if(counter == null){
			counter = 1;
			motifCounter.put(motifType, counter);
			orderedMotifs.add(motifType);
		}
		else{
			counter++;
			motifCounter.put(motifType, counter);
		}
		
		return counter;
	}
	
	public abstract int decrementCounterFor(SmallGraph graph);
	
	protected int decrementCounterFor(MotifType motifType){
		Integer counter = motifCounter.get(motifType);
		
		if(counter == null){
			counter = -1;
			motifCounter.put(motifType, counter);
			orderedMotifs.add(motifType);
			
			//TODO remove
//			if(counter < 0) {
//				System.out.println("counter smaller than 0");
//			}
			//
		} else {
			counter--;
			motifCounter.put(motifType, counter);
		}
		
		return counter;
	}
	
	public String showOutput() {
		TreeMap<Long, Integer> outputMap = new TreeMap<>();
		
		CanonicalLabelGenerator clg = new CanonicalLabelGenerator();
		for (Entry<MotifType, Integer> motif : motifCounter.entrySet()) {
			SmallGraph sg = GraphTransformer.transformToSmallGraph(motif.getKey().getGraph());
			long localId = clg.genCanonicalLabelFor(sg);
			outputMap.put(localId, motif.getValue());
		}
		
		return outputMap.toString();
	}
	
	public int getTotalMotifCount() {
		int totalCount = 0;
		for (Integer count : motifCounter.values()) {
			totalCount += count;
		}
		
		return totalCount;
	}
}
