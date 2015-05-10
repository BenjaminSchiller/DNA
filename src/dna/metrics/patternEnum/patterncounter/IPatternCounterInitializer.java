package dna.metrics.patternEnum.patterncounter;

import dna.graph.Graph;

public interface IPatternCounterInitializer {
	public void initialize(Graph graph, IPatternCounter motifCounter, int patternSize);
}
