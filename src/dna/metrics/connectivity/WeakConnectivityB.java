package dna.metrics.connectivity;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterBatch;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;

// FIXME problems when adding or removing nodes...
public class WeakConnectivityB extends WeakConnectivity implements IAfterBatch {

	public WeakConnectivityB() {
		super("WeakConnectivityB");
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		int r = 0;

		for (EdgeRemoval re : b.getEdgeRemovals()) {
			Edge e = (Edge) re.getEdge();
			Node n1;
			Node n2;
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				n1 = ((DirectedEdge) e).getSrc();
				n2 = ((DirectedEdge) e).getDst();
			} else {
				n1 = ((UndirectedEdge) e).getNode1();
				n2 = ((UndirectedEdge) e).getNode2();
			}
			boolean neighbourFound = false;

			HashSet<Node> reachableNodes = new HashSet<>();
			for (IElement ie : n1.getEdges()) {
				Edge ed = (Edge) ie;
				Node node = ed.getDifferingNode(n1);
				reachableNodes.add(node);
			}
			for (IElement ie : n2.getEdges()) {
				Edge ed = (Edge) ie;
				Node node = ed.getDifferingNode(n2);
				if (reachableNodes.contains(node)) {
					parents.put(n2, node);
					neighbourFound = true;
					break;
				}
			}

			if (!neighbourFound) {
				r += 1;
			}
		}

		if (r > 0) {
			r = 0;
			this.compute();
		} else {
			for (EdgeAddition ea : b.getEdgeAdditions()) {
				Edge e = (Edge) ea.getEdge();
				Node n1;
				Node n2;
				if (DirectedNode.class.isAssignableFrom(this.g
						.getGraphDatastructures().getNodeType())) {
					n1 = ((DirectedEdge) e).getSrc();
					n2 = ((DirectedEdge) e).getDst();
				} else {
					n1 = ((UndirectedEdge) e).getNode1();
					n2 = ((UndirectedEdge) e).getNode2();
				}
				int c1 = lookUp(n1);
				int c2 = lookUp(n2);
				if (c1 != c2) {

					if (this.componentList.get(c1).getSize() < this.componentList
							.get(c2).getSize()) {

						if (DirectedNode.class.isAssignableFrom(this.g
								.getGraphDatastructures().getNodeType())) {
							n2 = ((DirectedEdge) e).getSrc();
							n1 = ((DirectedEdge) e).getDst();
						} else {
							n2 = ((UndirectedEdge) e).getNode1();
							n1 = ((UndirectedEdge) e).getNode2();
						}
						int temp = c1;
						c1 = c2;
						c2 = temp;
					}
					this.componentList.get(c1).increaseSize(
							this.componentList.get(c2).getSize());

					Node temp = n2;
					Node newParent = n1;
					Node newChild = (Node) parents.get(temp);

					while (!this.parents.containsKey(temp)) {
						newChild = (Node) parents.get(temp);
						parents.put(temp, newParent);
						newParent = temp;
						temp = newChild;
					}
					parents.put(temp, newParent);
					this.componentList.remove(c2);
					componentConnection.put(c2, c1);

				}
			}

		}

		return true;
	}

}
