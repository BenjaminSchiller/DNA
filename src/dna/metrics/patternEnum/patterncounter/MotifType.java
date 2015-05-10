package dna.metrics.patternEnum.patterncounter;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import dna.metrics.patternEnum.datastructures.SmallGraph;

public abstract class MotifType {
	Graph<Integer, DefaultEdge> graph;
	
	public Graph<Integer, DefaultEdge> getGraph() {
		return graph;
	}
	
	public abstract void generate(SmallGraph graph);
	public abstract byte[] getDegreeHash();
}
