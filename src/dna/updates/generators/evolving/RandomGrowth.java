package dna.updates.generators.evolving;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class RandomGrowth extends BatchGenerator {

	private int nodes;

	private int edgesPerNode;

	public RandomGrowth(int nodes, int edgesPerNode) {
		super("RandomGrowth", new IntParameter("NODES", nodes),
				new IntParameter("EDGES_PER_NODE", edgesPerNode));
		this.nodes = nodes;
		this.edgesPerNode = edgesPerNode;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, this.nodes, 0, 0, this.nodes
						* this.edgesPerNode, 0, 0);

		Node[] newNodes = new Node[this.nodes];

		for (int i = 0; i < nodes; i++) {
			newNodes[i] = g.getGraphDatastructures().newNodeInstance(
					g.getMaxNodeIndex() + i + 1);
			b.add(new NodeAddition(newNodes[i]));

			HashSet<Node> targets = new HashSet<Node>();
			while (targets.size() < this.edgesPerNode) {
				Node to = null;

				while (to == null) {
					int target = Rand.rand.nextInt(g.getNodeCount() + i);
					if (target < g.getNodeCount()) {
						int index = 0;
						for (IElement n : g.getNodes()) {
							if (index == target) {
								to = (Node) n;
								break;
							}
							index++;
						}
					} else {
						to = newNodes[target - g.getNodeCount()];
					}

					if (targets.contains(to)) {
						to = null;
					}
				}

				Edge e = g.getGraphDatastructures().newEdgeInstance(
						newNodes[i], to);
				targets.add(to);
				b.add(new EdgeAddition(e));
			}
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return g.getNodeCount() >= this.edgesPerNode;
	}

}
