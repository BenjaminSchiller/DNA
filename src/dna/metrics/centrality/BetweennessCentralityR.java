package dna.metrics.centrality;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;

public class BetweennessCentralityR extends BetweennessCentrality implements
		IRecomputation {

	public BetweennessCentralityR() {
		super("BetweennessCentralityR", MetricType.exact);
	}

	public BetweennessCentralityR(String[] nodeTypes) {
		super("BetweennessCentralityR", MetricType.exact, nodeTypes);
	}

	@Override
	public boolean recompute() {
		this.initProperties();
		for (IElement ie : this.getNodesOfAssignedTypes()) {
			this.process((Node) ie);
		}
		return true;
	}

}
