package dna.metrics.parallelization.partitioning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;

public class OverlappingPartition extends Partition {

	protected Set<Node> overlap;

	public Set<Node> getOverlap() {
		return this.overlap;
	}

	public OverlappingPartition(Graph g, List<Node> nodes, Metric m,
			Set<Node> overlap) {
		super(g, nodes, m);
		this.overlap = overlap;
	}

	public boolean addOverlap(Node n) {
		return this.overlap.add(n);
	}

	public String toString() {
		return "OverlappingPartition: " + g.toString() + " @ " + overlap.size()
				+ " overlap";
	}

	public static OverlappingPartition getPartition(String name, Graph g,
			List<Node> nodes, Metric m) {
		Graph gp = g.getGraphDatastructures()
				.newGraphInstance(name, g.getTimestamp(), nodes.size(),
						g.getEdgeCount() / nodes.size());
		Set<Node> overlap = new HashSet<Node>();

		// add main nodes
		for (Node n : nodes) {
			Node newNode = gp.getGraphDatastructures().newNodeInstance(
					n.asString());
			if (!gp.containsNode(newNode)) {
				gp.addNode(newNode);
			}
		}

		// add adjacent nodes
		for (Node n : nodes) {
			Node node = gp.getNode(n.getIndex());
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				Node newNode = gp.getGraphDatastructures().newNodeInstance(
						e.getDifferingNode(n).asString());
				if (!gp.containsNode(newNode)) {
					gp.addNode(newNode);
					overlap.add(newNode);
				}
				Edge newEdge = gp.getGraphDatastructures().newEdgeInstance(
						e.asString(), gp);
				if (!gp.containsEdge(newEdge)) {
					gp.addEdge(newEdge);
					newEdge.connectToNodes();
				}
			}
		}

		// add adjacent edges
		for (Node n : nodes) {
			for (IElement e_ : n.getEdges()) {
				Node n2 = ((Edge) e_).getDifferingNode(n);
				for (IElement e2_ : n2.getEdges()) {
					Edge e2 = (Edge) e2_;
					if (gp.containsNode(e2.getDifferingNode(n2))) {
						Edge newEdge = gp.getGraphDatastructures()
								.newEdgeInstance(((Edge) e2_).asString(), gp);
						gp.addEdge(newEdge);
						newEdge.connectToNodes();
					}
				}
			}
		}

		// for (Node n : nodes) {
		// Node newNode = gp.getGraphDatastructures().newNodeInstance(
		// n.asString());
		// if (gp.containsNode(newNode)) {
		// newNode = gp.getNode(newNode.getIndex());
		// } else {
		// gp.addNode(newNode);
		// }
		// for (IElement e_ : n.getEdges()) {
		// Edge e = (Edge) e_;
		// if (!gp.containsNode(e.getDifferingNode(n))) {
		// overlap.add(e.getDifferingNode(n));
		// Node newNode2 = gp.getGraphDatastructures()
		// .newNodeInstance(e.getDifferingNode(n).asString());
		// if (!gp.containsNode(newNode2)) {
		// gp.addNode(newNode2);
		// }
		// }
		// Edge newEdge = gp.getGraphDatastructures().newEdgeInstance(
		// e.asString(), gp);
		// gp.addEdge(newEdge);
		// newEdge.connectToNodes();
		// }
		// }

		return new OverlappingPartition(gp, nodes, clone(m), overlap);
	}

	@Override
	public boolean propagate(EdgeAddition ea) {
		if (this.g.containsNode(ea.getEdge().getN1())
				&& this.g.containsNode(ea.getEdge().getN2())) {
			EdgeAddition ea_ = new EdgeAddition(this.g.getGraphDatastructures()
					.newEdgeInstance(ea.getEdge().asString(), this.g));
			return ea_.apply(this.g);
		} else if (!this.g.containsNode(ea.getEdge().getN1())) {
			Node newOverlap = this.g.getGraphDatastructures().newNodeInstance(
					ea.getEdge().getN1().asString());
			this.g.addNode(newOverlap);
			this.overlap.add(newOverlap);
			for (IElement e_ : ea.getEdge().getN1().getEdges()) {
				Edge e = (Edge) e_;
				if (g.containsNode(e.getDifferingNode(ea.getEdge().getN1()))) {
					Edge newEdge = this.g.getGraphDatastructures()
							.newEdgeInstance(e.asString(), this.g);
					this.g.addEdge(newEdge);
					newEdge.connectToNodes();
				}
			}
			return true;

		} else if (!this.g.containsNode(ea.getEdge().getN2())) {
			Node newOverlap = this.g.getGraphDatastructures().newNodeInstance(
					ea.getEdge().getN2().asString());
			this.g.addNode(newOverlap);
			this.overlap.add(newOverlap);
			for (IElement e_ : ea.getEdge().getN2().getEdges()) {
				Edge e = (Edge) e_;
				if (g.containsNode(e.getDifferingNode(ea.getEdge().getN2()))) {
					Edge newEdge = this.g.getGraphDatastructures()
							.newEdgeInstance(e.asString(), this.g);
					this.g.addEdge(newEdge);
					newEdge.connectToNodes();
				}
			}
			return true;
			// } else if (this.g.containsNode(ea.getEdge().getN1())) {
			// boolean succ = this.overlap.add(g.getNode(ea.getEdge().getN2()
			// .getIndex()));
			// return succ || true;
			// } else if (this.g.containsNode(ea.getEdge().getN2())) {
			// boolean succ = this.overlap.add(g.getNode(ea.getEdge().getN1()
			// .getIndex()));
			// return succ || true;
		} else {
			throw new IllegalStateException(
					"cannot propage EA in in case no node exists in graph in "
							+ this.toString());
		}
	}

	@Override
	public boolean propagate(EdgeRemoval er) {
		EdgeRemoval er_ = new EdgeRemoval(this.g.getEdge(er.getEdge().getN1(),
				er.getEdge().getN2()));
		// TODO remove node that is in overlap!
		return er_.apply(this.g);
	}

}
