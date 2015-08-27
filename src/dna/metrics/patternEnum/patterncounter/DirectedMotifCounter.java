package dna.metrics.patternEnum.patterncounter;

import dna.metrics.patternEnum.datastructures.SmallGraph;

public class DirectedMotifCounter extends MotifCounter {

	@Override
	public int incrementCounterFor(SmallGraph graph){
		DirectedMotifType motifType = new DirectedMotifType();
		motifType.generate(graph);
		return incrementCounterFor(motifType);
	}
	
	@Override
	public int decrementCounterFor(SmallGraph graph){
		DirectedMotifType motifType = new DirectedMotifType();
		motifType.generate(graph);
		return decrementCounterFor(motifType);
	}
}
