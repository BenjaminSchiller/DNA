package dna.metrics.patternEnum.patterncounter;

import dna.metrics.patternEnum.datastructures.SmallGraph;

public class UndirectedMotifCounter extends MotifCounter {

	@Override
	public int incrementCounterFor(SmallGraph graph){
		UndirectedMotifType motifType = new UndirectedMotifType();
		motifType.generate(graph);
		return incrementCounterFor(motifType);
	}
	
	@Override
	public int decrementCounterFor(SmallGraph graph){
		UndirectedMotifType motifType = new UndirectedMotifType();
		motifType.generate(graph);
		return decrementCounterFor(motifType);
	}
}
