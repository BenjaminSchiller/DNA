package dna.metrics.richClubConnectivity.richClubConnectivityPerDegree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * Calculates the rich club connectivity values for all existing degrees n, with
 * Node n ∈ richclub if degree > n
 * 
 */
public class RichClubConnectivityPerDegreeU extends
		RichClubConnectivityPerDegree {

	public RichClubConnectivityPerDegreeU() {
		super("RichClubConnectivityPerDegreeU", ApplicationType.AfterUpdate);
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterDirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterDirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterDirectedEdgeRemoval(u);
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterUndirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterUndirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterUndirectedEdgeRemoval(u);
			}
		}

		return false;
	}

	private boolean applyAfterDirectedEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();

		// Current removal the deleted edge is still in the set from the source
		// Node
		int edges = 0;

		for (IElement iE : src.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getDst().getOutDegree() > srcDegree) {
				edges++;
			}
		}
		for (IElement iE : src.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getSrc().getOutDegree() > srcDegree) {
				edges++;
			}
		}

		// update edges for delete edge
		if (dstDegree > srcDegree + 1) {
			this.richClubEdges.put(srcDegree + 1,
					this.richClubEdges.get(srcDegree + 1) - 1);
		} else {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) - 1);
		}

		this.richClubEdges.put(srcDegree + 1,
				this.richClubEdges.get(srcDegree + 1) - edges);

		// update Old richclub size
		this.richClubs
				.put(srcDegree + 1, this.richClubs.get(srcDegree + 1) - 1);
		if (this.richClubs.get(srcDegree + 1) == 0) {
			this.richClubs.remove(srcDegree + 1);
			this.richClubEdges.remove(srcDegree + 1);
		}

		// update new RichClub size and edges
		if (this.richClubs.containsKey(srcDegree)) {
			this.richClubs.put(srcDegree, this.richClubs.get(srcDegree) + 1);
			this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
					+ edges);

		} else {
			this.richClubs.put(srcDegree, 1);
			this.richClubEdges.put(srcDegree, edges);
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();
		int edges = 0;
		for (IElement iE : src.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getDst().getOutDegree() >= srcDegree) {
				edges++;
			}

		}
		for (IElement iE : src.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getSrc().getOutDegree() >= srcDegree) {
				edges++;
			}

		}

		if (dstDegree < srcDegree) {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) + 1);
			this.richClubEdges.put(srcDegree - 1,
					this.richClubEdges.get(srcDegree - 1) - edges);
		} else {
			this.richClubEdges.put(srcDegree - 1,
					this.richClubEdges.get(srcDegree - 1) - (edges - 1));
		}

		this.richClubs
				.put(srcDegree - 1, this.richClubs.get(srcDegree - 1) - 1);
		if (this.richClubs.get(srcDegree - 1) == 0) {
			this.richClubs.remove(srcDegree - 1);

			this.richClubEdges.remove(srcDegree - 1);
		}

		if (this.richClubs.containsKey(srcDegree)) {
			this.richClubs.put(srcDegree, this.richClubs.get(srcDegree) + 1);
			this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
					+ edges);

		} else {
			this.richClubs.put(srcDegree, 1);
			this.richClubEdges.put(srcDegree, edges);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		int degree;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();
			degree = node.getOutDegree();
		} else {
			UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
			degree = node.getDegree();
		}
		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, 0);
		}
		return true;
	}

	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (node1.getDegree() > node2.getDegree()) {
			this.richClubEdges.put(node2.getDegree() + 1,
					this.richClubEdges.get(node2.getDegree() + 1) - 2);
		} else {
			this.richClubEdges.put(node1.getDegree() + 1,
					this.richClubEdges.get(node1.getDegree() + 1) - 2);
		}

		checkChangesDel(node1);
		checkChangesDel(node2);

		return true;
	}

	private void checkChangesDel(UndirectedNode node) {
		int degree = node.getDegree();
		int edges = 0;
		for (IElement iEdge : node.getEdges()) {
			UndirectedEdge ed = (UndirectedEdge) iEdge;
			UndirectedNode n = ed.getDifferingNode(node);
			if (n.getDegree() > degree) {
				edges += 2;
			}
		}
		this.richClubs.put(degree + 1, this.richClubs.get(degree + 1) - 1);
		this.richClubEdges.put(degree + 1, this.richClubEdges.get(degree + 1)
				- edges);
		if (this.richClubs.get(degree + 1) == 0) {
			removeRCC(degree + 1);
		}

		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
			this.richClubEdges.put(degree, this.richClubEdges.get(degree)
					+ edges);

		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, edges);
		}

	}

	private void removeRCC(int degree) {
		this.richClubs.remove(degree);
		this.richClubEdges.remove(degree);
	}

	private boolean applyAfterUndirectedEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();
		checkChangesAdd(node1, node2);
		checkChangesAdd(node2, node1);

		return true;
	}

	private void checkChangesAdd(UndirectedNode node, UndirectedNode node2) {
		int degree = node.getDegree();
		int edges = 0;
		int node2Degree = node2.getDegree();
		for (IElement iEdge : node.getEdges()) {
			UndirectedEdge ed = (UndirectedEdge) iEdge;
			UndirectedNode n = ed.getDifferingNode(node);
			if (n == node2 && n.getDegree() == degree) {
				edges++;
				continue;
			}

			if (n.getDegree() >= degree) {
				edges += 2;
			}
		}
		if (node2Degree < degree) {
			this.richClubEdges.put(degree - 1,
					this.richClubEdges.get(degree - 1) - edges);
		} else {
			if (node2Degree == degree) {
				this.richClubEdges.put(degree - 1,
						this.richClubEdges.get(degree - 1) - (edges - 1));
			} else {
				this.richClubEdges.put(degree - 1,
						this.richClubEdges.get(degree - 1) - (edges - 2));
			}
		}

		this.richClubs.put(degree - 1, this.richClubs.get(degree - 1) - 1);
		if (this.richClubs.get(degree - 1) == 0) {
			this.richClubs.remove(degree - 1);
			this.richClubEdges.remove(degree - 1);
		}

		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
			this.richClubEdges.put(degree, this.richClubEdges.get(degree)
					+ edges);

		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, edges);
			this.highestDegree = Math.max(highestDegree, degree);
		}
	}

	private boolean applyAfterDirectedNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		HashSet<DirectedNode> inNodes = new HashSet<DirectedNode>();
		for (IElement ie : node.getIncomingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			DirectedNode n = e.getSrc();
			inNodes.add(n);
		}

		for (IElement ie : node.getIncomingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			DirectedNode n = e.getSrc();
			int edges = 0;

			for (IElement iedge : n.getOutgoingEdges()) {
				DirectedEdge e1 = (DirectedEdge) iedge;

				DirectedNode dN = e1.getDifferingNode(n);
				if (dN.getOutDegree() > n.getOutDegree()) {
					edges++;
				} else {
					if (inNodes.contains(dN)
							&& dN.getOutDegree() == n.getOutDegree()) {
						edges++;
					}
				}
			}
			for (IElement iedge : n.getIncomingEdges()) {
				DirectedEdge e1 = (DirectedEdge) iedge;

				DirectedNode dN = e1.getDifferingNode(n);
				if (inNodes.contains(dN)
						&& dN.getOutDegree() > n.getOutDegree()) {
					edges++;
				} else if (dN.getOutDegree() > n.getOutDegree()) {
					edges++;
				}

			}
			if (n.getOutDegree() + 1 > node.getOutDegree()) {
				richClubEdges.put(node.getOutDegree(),
						richClubEdges.get(node.getOutDegree()) - 1);
			} else {
				richClubEdges.put(n.getOutDegree() + 1,
						richClubEdges.get(n.getOutDegree() + 1) - 1);
			}

			richClubEdges.put(n.getOutDegree() + 1,
					richClubEdges.get(n.getOutDegree() + 1) - edges);

			richClubs.put(n.getOutDegree() + 1,
					richClubs.get(n.getOutDegree() + 1) - 1);
			if (richClubs.get(n.getOutDegree() + 1) == 0) {
				richClubs.remove(n.getOutDegree() + 1);
				richClubEdges.remove(n.getOutDegree() + 1);
			}
			if (richClubs.containsKey(n.getOutDegree())) {
				richClubs.put(n.getOutDegree(),
						richClubs.get(n.getOutDegree()) + 1);
			} else {
				richClubs.put(n.getOutDegree(), 1);
				richClubEdges.put(n.getOutDegree(), 0);
			}
			richClubEdges.put(n.getOutDegree(),
					richClubEdges.get(n.getOutDegree()) + edges);
		}

		for (IElement ie : node.getOutgoingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			if (inNodes.contains(e.getDst())) {
				if (e.getDst().getOutDegree() + 1 > node.getOutDegree()) {
					richClubEdges.put(node.getOutDegree(),
							richClubEdges.get(node.getOutDegree()) - 1);
				} else {
					richClubEdges
							.put(e.getDst().getOutDegree() + 1, richClubEdges
									.get(e.getDst().getOutDegree() + 1) - 1);
				}
				continue;
			}
			if (e.getDst().getOutDegree() > node.getOutDegree()) {
				richClubEdges.put(node.getOutDegree(),
						richClubEdges.get(node.getOutDegree()) - 1);

			} else {

				richClubEdges.put(e.getDst().getOutDegree(),
						richClubEdges.get(e.getDst().getOutDegree()) - 1);
			}
		}

		this.richClubs.put(node.getOutDegree(),
				this.richClubs.get(node.getOutDegree()) - 1);
		if (richClubs.get(node.getOutDegree()) == 0) {
			richClubs.remove(node.getOutDegree());
			richClubEdges.remove(node.getOutDegree());
		}
		return true;
	}

	private void check() {
		Map<Integer, Integer> testrichClubs = new HashMap<Integer, Integer>();
		Map<Integer, Integer> testrichClubEdges = new HashMap<>();
		for (IElement ie : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) ie;
			int degree = n.getDegree();

			int edges = 0;
			for (IElement ieEdges : n.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ieEdges;
				UndirectedNode node = ed.getDifferingNode(n);
				if (node.getDegree() > degree) {
					edges += 2;
				}
				if (node.getDegree() == degree) {
					edges += 1;
				}
			}

			if (testrichClubs.containsKey(degree)) {
				testrichClubs.put(degree, testrichClubs.get(degree) + 1);
				testrichClubEdges.put(degree, testrichClubEdges.get(degree)
						+ edges);
			} else {
				testrichClubs.put(degree, 1);
				testrichClubEdges.put(degree, edges);
			}
		}
		if (!richClubs.keySet().equals(testrichClubs.keySet())
				|| !richClubEdges.keySet().equals(testrichClubEdges.keySet())) {
			System.out.println(richClubs.keySet() + " "
					+ testrichClubs.keySet());
			System.out.println(richClubEdges.keySet() + " "
					+ testrichClubEdges.keySet());
		}
		if (!richClubs.values().equals(testrichClubs.values())
				|| !richClubEdges.values().equals(testrichClubEdges.values())) {
			System.out.println(richClubs);
			System.out.println(testrichClubs);
			System.out.println(richClubEdges);
			System.out.println(testrichClubEdges);
		}

	}

	private boolean applyAfterUndirectedNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		HashSet<UndirectedNode> inNodes = new HashSet<UndirectedNode>();
		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			UndirectedNode n = e.getDifferingNode(node);
			inNodes.add(n);
		}

		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			UndirectedNode n = e.getDifferingNode(node);
			int edges = 0;

			for (IElement iedge : n.getEdges()) {
				UndirectedEdge e1 = (UndirectedEdge) iedge;

				UndirectedNode dN = e1.getDifferingNode(n);
				if (dN.getDegree() > n.getDegree()) {
					edges += 2;
				} else {
					if (inNodes.contains(dN) && dN.getDegree() == n.getDegree()) {
						inNodes.remove(n);
						edges += 2;
					}
				}
			}

			if (n.getDegree() + 1 > node.getDegree()) {
				richClubEdges.put(node.getDegree(),
						richClubEdges.get(node.getDegree()) - 2);
			} else {
				richClubEdges.put(n.getDegree() + 1,
						richClubEdges.get(n.getDegree() + 1) - 2);
			}

			richClubEdges.put(n.getDegree() + 1,
					richClubEdges.get(n.getDegree() + 1) - edges);

			richClubs.put(n.getDegree() + 1,
					richClubs.get(n.getDegree() + 1) - 1);
			if (richClubs.get(n.getDegree() + 1) == 0) {
				richClubs.remove(n.getDegree() + 1);
				richClubEdges.remove(n.getDegree() + 1);
			}
			if (richClubs.containsKey(n.getDegree())) {
				richClubs.put(n.getDegree(), richClubs.get(n.getDegree()) + 1);
			} else {
				richClubs.put(n.getDegree(), 1);
				richClubEdges.put(n.getDegree(), 0);
			}
			richClubEdges.put(n.getDegree(), richClubEdges.get(n.getDegree())
					+ edges);
		}
		this.richClubs.put(node.getDegree(),
				this.richClubs.get(node.getDegree()) - 1);
		if (richClubs.get(node.getDegree()) == 0) {
			richClubs.remove(node.getDegree());
			richClubEdges.remove(node.getDegree());
		}
		return true;
	}
}
