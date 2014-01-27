package dna.updates.walkingAlgorithms;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public abstract class WalkingAlgorithm extends BatchGenerator {

	private HashSet<Node> seenNodes;
	private HashSet<Node> visitedNodes;

	private Graph fullGraph;

	private boolean onlyVisited;
	private boolean firstIteration;

	private int batchSize;
	private int graphSize;

	/**
	 * 
	 * @param name
	 * @param fullGraph
	 * @param onlyVisitedNodesToGraph
	 * @param batchSize
	 * @param parameters
	 */
	public WalkingAlgorithm(String name, Graph fullGraph,
			boolean onlyVisitedNodesToGraph, int batchSize,
			Parameter[] parameters) {
		super(name, parameters);

		this.fullGraph = fullGraph;
		this.onlyVisited = onlyVisitedNodesToGraph;
		this.batchSize = batchSize;

		firstIteration = true;

		graphSize = fullGraph.getNodeCount();

		seenNodes = new HashSet<Node>(graphSize);
		visitedNodes = new HashSet<Node>(graphSize);

	}

	/**
	 * 
	 * @param g
	 */
	public Batch generate(Graph g) {
		
		//Batch ret = new Batch();
		
		for(int i = 0; i < batchSize; i++){
			
		}
		return null;
	}

	/**
	 * 
	 */
	public void reset() {

		seenNodes = new HashSet<Node>(graphSize);
		visitedNodes = new HashSet<Node>(graphSize);

		firstIteration = true;
	}

}
