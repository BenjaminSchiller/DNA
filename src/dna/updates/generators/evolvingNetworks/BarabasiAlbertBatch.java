package dna.updates.generators.evolvingNetworks;

import java.util.HashSet;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class BarabasiAlbertBatch extends BatchGenerator {

	private int nodes;

	private int m;

	public BarabasiAlbertBatch(int nodes, int m) {
		super("BarabasiAlbertBatch", new IntParameter("nodes", nodes),
				new IntParameter("m", m));
		this.nodes = nodes;
		this.m = m;
	}

	@Override
	public Batch generate(IGraph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, this.nodes, 0, 0, this.nodes * this.m, 0,
				0);
		Node[] newNodes = new Node[this.nodes];
		int[] newDegrees = new int[this.nodes];
		int[] oldDegrees = new int[g.getMaxNodeIndex() + 1];

		for (int i = 0; i < nodes; i++) {
			newNodes[i] = g.getGraphDatastructures().newNodeInstance(
					g.getMaxNodeIndex() + i + 1);
			b.add(new NodeAddition(newNodes[i]));

			HashSet<Node> targets = new HashSet<Node>(this.m);
			int total = 2 * (g.getEdgeCount() + i * this.m);

			for (int j = 0; j < this.m; j++) {
				Node to = null;
				while (to == null) {
					int p = Rand.rand.nextInt(total);
					int sum = 0;
					for (IElement n : g.getNodes()) {
						Node node = (Node) n;
						if (node instanceof DirectedNode) {
							sum += ((DirectedNode) node).getDegree();
						} else if (node instanceof UndirectedNode) {
							sum += ((UndirectedNode) node).getDegree();
						}
						sum += oldDegrees[node.getIndex()];

						if (sum > p) {
							if (!targets.contains(node)) {
								to = node;
								targets.add(node);
								newDegrees[i]++;
								oldDegrees[node.getIndex()]++;
								// } else {
								// System.out.println("duplicate... old");
							}
							break;
						}
					}

					if (to != null) {
						break;
					}

					if (sum <= p) {
						for (int k = 0; k < i; k++) {
							sum += newDegrees[k];
							if (sum > p) {
								if (!targets.contains(newNodes[k])) {
									to = newNodes[k];
									targets.add(newNodes[k]);
									newDegrees[i]++;
									newDegrees[k]++;
									// } else {
									// System.out.println("duplicate... new");
								}
								break;
							}
						}
					}

					if (to != null) {
						break;
					}

					// System.out.println("MISS   - (p,sum,total) (" + p + ","
					// + sum + "," + total + ")");
				}

				b.add(new EdgeAddition(g.getGraphDatastructures()
						.newEdgeInstance(newNodes[i], to)));
			}
		}

		// System.out.println("old: " + fromOld + " (" + g.getNodeCount() +
		// ")");
		// System.out.println("new: " + fromNew + " (" + this.nodes + ")");

		return b;
	}

	@Override
	public void reset() {
	
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return g.getNodeCount() >= this.m;
	}

}
