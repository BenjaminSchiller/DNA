package dna.updates.generators.combined;

import java.util.HashSet;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.combined.CombinedGraph;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class CombinedGraphBatch extends BatchGenerator {

	protected CombinedGraph gg;

	protected int intraEdges;
	protected int interEdges;

	public CombinedGraphBatch(CombinedGraph gg, int intraEdges, int interEdges) {
		super("CombinedGraphBatch", new IntParameter("intraEdges", intraEdges),
				new IntParameter("interEdges", interEdges));
		this.gg = gg;
		this.intraEdges = intraEdges;
		this.interEdges = interEdges;
	}

	@Override
	public Batch generate(IGraph g) {
		GraphDataStructure gds = g.getGraphDatastructures();
		Batch b = new Batch(gds, g.getTimestamp(), g.getTimestamp() + 1);
		HashSet<Edge> edges = new HashSet<Edge>();
		int offset = 0;
		for (GraphGenerator gg : this.gg.ggs) {
			int nodes = gg.getNodesInit();
			// INTRA
			int intraAdded = 0;
			while (intraAdded < this.intraEdges) {
				int i1 = Rand.rand.nextInt(nodes) + offset;
				int i2 = Rand.rand.nextInt(nodes) + offset;
				if (i1 == i2) {
					continue;
				}
				Node n1 = g.getNode(i1);
				Node n2 = g.getNode(i2);
				Edge newEdge = gds.newEdgeInstance(n1, n2);
				if (g.containsEdge(newEdge)) {
					continue;
				}
				if (edges.contains(newEdge)) {
					continue;
				}
				edges.add(newEdge);
				b.add(new EdgeAddition(newEdge));
				intraAdded++;
			}
			// INTER
			int interAdded = 0;
			while (interAdded < this.interEdges) {
				int i1 = Rand.rand.nextInt(nodes) + offset;
				int i2 = Rand.rand.nextInt(this.gg.getNodesInit() - nodes);
				if (offset <= i2 && i2 < offset + nodes) {
					i2 += nodes;
				}
				// if (i2 >= offset || (offset == 0 && i2 < nodes)) {
				// System.out.println("...i2 = " + i2 + " = rand("
				// + (this.gg.getNodesInit() - nodes)
				// + ") @ offset = " + offset);
				// System.out.println("..." + i2 + " => "
				// + ((i2 + offset) % this.gg.getNodesInit()));
				// i2 = (i2 + offset) % this.gg.getNodesInit();
				// }
				// System.out.println(i1 + " <=>" + i2);
				Node n1 = g.getNode(i1);
				Node n2 = g.getNode(i2);
				Edge newEdge = gds.newEdgeInstance(n1, n2);
				if (g.containsEdge(newEdge)) {
					continue;
				}
				if (edges.contains(newEdge)) {
					continue;
				}
				edges.add(newEdge);
				b.add(new EdgeAddition(newEdge));
				interAdded++;
			}
			offset += nodes;
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return true;
	}

}
