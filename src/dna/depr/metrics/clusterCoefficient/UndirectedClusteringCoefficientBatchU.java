package dna.depr.metrics.clusterCoefficient;

import java.util.ArrayList;
import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.Update;
import dna.util.Log;

/**
 * 
 * Implements the batch-based algorithms for computing the undirected clustering
 * coefficient. It is only implemented for edge additions and is not correct,
 * there is still a bug in there.
 * 
 * @author benni
 * 
 */
@Deprecated
public class UndirectedClusteringCoefficientBatchU extends
		UndirectedClusteringCoefficient {

	public UndirectedClusteringCoefficientBatchU() {
		super("UndirectedClusteringCoefficientBatchU",
				ApplicationType.BeforeBatch, IMetric.MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch batch) {
		HashMap<Integer, UndirectedEdge> addedEdges = new HashMap<Integer, UndirectedEdge>();
		HashMap<UndirectedNode, ArrayList<UndirectedEdge>> addedEdgesPerNode = new HashMap<UndirectedNode, ArrayList<UndirectedEdge>>();

		for (Update u : batch.getAllUpdates()) {
			if (!(u instanceof EdgeAddition)) {
				Log.error("unsupported update type: " + u.getType());
				continue;
			}

			UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
			UndirectedNode a = e.getNode1();
			UndirectedNode b = e.getNode2();

			// fill temporary memory
			if (!addedEdgesPerNode.containsKey(a)) {
				ArrayList<UndirectedEdge> l = new ArrayList<UndirectedEdge>();
				l.add(e);
				addedEdgesPerNode.put(a, l);
			} else {
				addedEdgesPerNode.get(a).add(e);
			}
			if (!addedEdgesPerNode.containsKey(b)) {
				ArrayList<UndirectedEdge> l = new ArrayList<UndirectedEdge>();
				l.add(e);
				addedEdgesPerNode.put(b, l);
			} else {
				addedEdgesPerNode.get(b).add(e);
			}

			// new triangles
			for (IElement c_Uncasted : a.getEdges()) {
				UndirectedEdge c_ = (UndirectedEdge) c_Uncasted;
				UndirectedNode c = (UndirectedNode) c_.getDifferingNode(a);
				if (c.hasEdge(c, b)
						|| addedEdges.containsKey(Edge.getHashcode(c, b))) {
					this.addTriangle(a);
					this.addTriangle(b);
					this.addTriangle(c);
				}
			}
			for (UndirectedEdge c_ : addedEdgesPerNode.get(a)) {
				UndirectedNode c = (UndirectedNode) c_.getDifferingNode(a);
				if (c.hasEdge(c, b)
						|| addedEdges.containsKey(Edge.getHashcode(c, b))) {
					this.addTriangle(a);
					this.addTriangle(b);
					this.addTriangle(c);
				}
			}
			// new potentials
			this.addPotentials(a, a.getDegree()
					+ addedEdgesPerNode.get(a).size() - 1);
			this.addPotentials(b, b.getDegree()
					+ addedEdgesPerNode.get(b).size() - 1);
			// System.out.println(a.getIndex() + " " + b.getIndex() + " :::: "
			// + a.getDegree() + " " + b.getDegree() + " +++ "
			// + addedEdgesPerNode.get(a).size() + " "
			// + addedEdgesPerNode.get(b).size());
		}
		return true;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
