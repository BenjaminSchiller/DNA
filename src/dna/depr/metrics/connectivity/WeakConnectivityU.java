package dna.depr.metrics.connectivity;

import java.util.Collection;
import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * Update version of weak connectivity.
 * 
 * @author benni
 * 
 */
public class WeakConnectivityU extends WeakConnectivity {

	public WeakConnectivityU() {
		super("WeakConnectivityU", ApplicationType.AfterUpdate,
				IMetric.MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
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
		if (u instanceof EdgeAddition) {

			Edge e = (Edge) ((EdgeAddition) u).getEdge();

			Node n1 = null;
			Node n2 = null;

			if (e instanceof DirectedEdge) {
				n1 = ((DirectedEdge) e).getSrc();
				n2 = ((DirectedEdge) e).getDst();
			} else if (e instanceof UndirectedEdge) {
				n1 = ((UndirectedEdge) e).getNode1();
				n2 = ((UndirectedEdge) e).getNode2();
			}

			if (n1 != null && n2 != null) {
				ConnectedComponent c1 = this.nodeComponents.get(n1);
				ConnectedComponent c2 = this.nodeComponents.get(n2);

				if (c1.equals(c2)) {
					// nodes are in the same component => do nothing
				} else {
					// nodes are in different components => merge them
					this.mergeComponents(c1, c2);
				}
			}

		} else if (u instanceof EdgeRemoval) {

			Edge e = (Edge) ((EdgeRemoval) u).getEdge();

			Node n1 = null;
			Node n2 = null;

			if (e instanceof DirectedEdge) {
				n1 = ((DirectedEdge) e).getSrc();
				n2 = ((DirectedEdge) e).getDst();
			} else if (e instanceof UndirectedEdge) {
				n1 = ((UndirectedEdge) e).getNode1();
				n2 = ((UndirectedEdge) e).getNode2();
			}

			Collection<Node> sub = this.getConnectedNodes(n1, n2);
			if (sub.size() == 0) {
				// nodes still connected => do nothing
			} else {
				// nodes not connected over another path => split
				this.splitOff(this.nodeComponents.get(n1), sub);
			}

		} else if (u instanceof NodeAddition) {

			Node n_ = (Node) ((NodeAddition) u).getNode();
			this.addNewComponent(n_);

		} else if (u instanceof NodeRemoval) {

			Node n_ = (Node) ((NodeRemoval) u).getNode();
			ConnectedComponent c = this.nodeComponents.get(n_);

			if (c.getSize() == 1) {
				// in case this is a single-node component, remove it
				this.nodeComponents.remove(n_);
				this.components.remove(c);
				return true;
			}

			HashSet<Node> found = new HashSet<Node>();

			this.nodeComponents.remove(n_);
			c.removeNode(n_);

			for (IElement e_ : n_.getEdges()) {
				Node neighbor = null;
				if (e_ instanceof DirectedEdge) {
					neighbor = ((DirectedEdge) e_).getDifferingNode(n_);
				} else if (e_ instanceof UndirectedEdge) {
					neighbor = ((UndirectedEdge) e_).getDifferingNode(n_);
				}
				if (found.contains(neighbor)) {
					continue;
				}
				Collection<Node> sub = this.getConnectedNodes(neighbor);
				if (sub.size() == c.getSize()) {
					// node's component is the original one => do nothing
					break;
				}
				// split node's component off the original one
				this.splitOff(c, sub);
				found.addAll(sub);
			}

		}
		return true;
	}

}
