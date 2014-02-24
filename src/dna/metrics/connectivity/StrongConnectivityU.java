package dna.metrics.connectivity;

import java.util.Collection;
import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * Update version of strong connectivity (currently not fully working...)
 * 
 * @author benni
 * 
 */
@Deprecated
public class StrongConnectivityU extends StrongConnectivity {

	public StrongConnectivityU() {
		super("StrongConnectivityU", ApplicationType.AfterUpdate,
				MetricType.exact);
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
			Edge e_ = (Edge) ((EdgeAddition) u).getEdge();
			if (e_ instanceof DirectedEdge) {
				DirectedEdge e = (DirectedEdge) e_;
				DirectedNode src = e.getSrc();
				DirectedNode dst = e.getDst();
				ConnectedComponent srcComp = this.nodeComponents.get(src);
				Collection<Node> srcNodes = this.getConnectedNodes(src);
				if (this.hasOutEdge(dst, srcComp) || srcNodes.contains(dst)) {
					this.mergeNodes(srcNodes);
				}
			} else if (e_ instanceof UndirectedEdge) {
				UndirectedEdge e = (UndirectedEdge) e_;
				ConnectedComponent a = this.nodeComponents.get(e.getNode1());
				ConnectedComponent b = this.nodeComponents.get(e.getNode2());
				if (!a.equals(b)) {
					this.mergeComponents(a, b);
				}
			}
		} else if (u instanceof EdgeRemoval) {
			Edge e_ = (Edge) ((EdgeRemoval) u).getEdge();
			Node n1 = null;
			Node n2 = null;
			if (e_ instanceof DirectedEdge) {
				DirectedEdge e = (DirectedEdge) e_;
				n1 = e.getSrc();
				n2 = e.getDst();
				ConnectedComponent a = this.nodeComponents.get(n1);
				ConnectedComponent b = this.nodeComponents.get(n2);
				if (a.equals(b)) {
					Collection<Node> a_ = this.getConnectedNodes(n1);
					if (a_.size() != a.getSize()) {
						this.splitComponent(a);
					}
				}
			} else if (e_ instanceof UndirectedEdge) {
				UndirectedEdge e = (UndirectedEdge) e_;
				n1 = e.getNode1();
				n2 = e.getNode2();
				ConnectedComponent a = this.nodeComponents.get(n1);
				ConnectedComponent b = this.nodeComponents.get(n2);
				if (a.equals(b)) {
					Collection<Node> a_ = this.getConnectedNodes(n1);
					if (a_.size() != a.getSize()) {
						this.splitComponent(a, n1, n2);
					}
				}
			}
		} else if (u instanceof NodeAddition) {
			ConnectedComponent c = this.addNewComponent();
			this.addNodeToComponent((Node) ((NodeAddition) u).getNode(), c);
		} else if (u instanceof NodeRemoval) {
			Node n_ = (Node) ((NodeRemoval) u).getNode();
			if (n_ instanceof DirectedNode) {
				DirectedNode n = (DirectedNode) n_;
				ConnectedComponent c = this.nodeComponents.get(n);

				if (n.getDegree() == 0) {
					// remove component of single node
					this.components.remove(this.nodeComponents.get(n));
					this.nodeComponents.remove(n);
				} else {
					DirectedNode n1 = ((DirectedEdge) n.getEdges().iterator()
							.next()).getDifferingNode(n);
					if (this.getConnectedNodes(n1).size() == c.getSize() - 1) {
						// remove node from component (still connected)
						c.removeNode(n);
						this.nodeComponents.remove(n);
					} else {
						// disconnect component
						this.splitComponent(c, n, n.getNeighbors());
					}
				}
			} else if (n_ instanceof UndirectedNode) {
				UndirectedNode n = (UndirectedNode) n_;
				ConnectedComponent c = this.nodeComponents.get(n);

				if (n.getDegree() == 0) {
					// remove component of single node
					this.components.remove(this.nodeComponents.get(n));
					this.nodeComponents.remove(n);
				} else {
					UndirectedNode n1 = ((UndirectedEdge) n.getEdges()
							.iterator().next()).getDifferingNode(n);
					if (this.getConnectedNodes(n1).size() == c.getSize() - 1) {
						// remove node from component (still connected)
						c.removeNode(n);
						this.nodeComponents.remove(n);
					} else {
						// disconnect component
						HashSet<IElement> neighbors = new HashSet<IElement>();
						for (IElement e : n.getEdges()) {
							neighbors.add(((UndirectedEdge) e)
									.getDifferingNode(n));
						}
						this.splitComponent(c, n, neighbors);
					}
				}
			}
		}
		return true;
	}

	protected boolean hasOutEdge(DirectedNode node, ConnectedComponent c) {
		for (IElement e_ : node.getOutgoingEdges()) {
			if (c.containsNode(((DirectedEdge) e_).getDst())) {
				return true;
			}
		}
		return false;
	}

	protected void splitComponent(ConnectedComponent c, Node n1, Node n2) {
		ConnectedComponent a = this.addNewComponent();
		ConnectedComponent b = this.addNewComponent();
		Collection<Node> a_ = this.getConnectedNodes(n1);
		Collection<Node> b_ = this.getConnectedNodes(n2);
		for (Node n : a_) {
			this.addNodeToComponent(n, a);
		}
		for (Node n : b_) {
			this.addNodeToComponent(n, b);
		}
		this.components.remove(c);
	}

	protected ConnectedComponent mergeNodes(Collection<Node> nodes) {
		ConnectedComponent c = this.addNewComponent();
		for (Node node : nodes) {
			this.components.remove(this.nodeComponents.get(node));
			this.addNodeToComponent(node, c);
		}
		return c;
	}

	protected void splitComponent(ConnectedComponent oldComponent,
			Node removedNode, Iterable<IElement> neighbors) {
		HashSet<Node> removed = new HashSet<Node>();
		for (IElement neighbor_ : neighbors) {
			Node neighbor = (Node) neighbor_;
			if (removed.contains(neighbor)) {
				continue;
			}
			ConnectedComponent c = this.addNewComponent();
			Collection<Node> nodes = this.getConnectedNodes(neighbor);
			for (Node node : nodes) {
				this.addNodeToComponent(node, c);
				removed.add(node);
			}
		}
		this.components.remove(oldComponent);
	}

	protected void splitComponent(ConnectedComponent c) {
		// System.out.println("SPLITTING " + c);
		HashSet<Node> seen = new HashSet<Node>();
		for (Node node : c.getNodes()) {
			if (seen.contains(node)) {
				continue;
			}
			Collection<Node> node_ = this.getConnectedNodes(node);
			seen.addAll(node_);
			// System.out.println("   ==> " + this.addNewComponent(node_));
		}
		this.components.remove(c);
	}

}
