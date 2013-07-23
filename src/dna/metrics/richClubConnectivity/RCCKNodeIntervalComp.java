package dna.metrics.richClubConnectivity;

import java.util.Collection;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.Update;

public class RCCKNodeIntervalComp extends RCCKNodeInterval {
	public RCCKNodeIntervalComp() {
		super("RCCKNodeIntervalComp", ApplicationType.Recomputation);
	}

	@Override
	public boolean compute() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : (Collection<DirectedNode>) this.g.getNodes()) {
				int degree = n.getOutDegree();
				this.degrees.add(degree);

				if (this.nodesPerDegree.containsKey(degree)) {
					this.nodesPerDegree.get(degree).add(n);
				} else {
					LinkedList<Node> newDegreeSet = new LinkedList<Node>();
					newDegreeSet.add(n);
					this.nodesPerDegree.put(degree, newDegreeSet);
				}

			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			for (UndirectedNode n : (Collection<UndirectedNode>) this.g
					.getNodes()) {
				int degree = n.getDegree();
				this.degrees.add(degree);

				if (this.nodesPerDegree.containsKey(degree)) {
					this.nodesPerDegree.get(degree).add(n);
				} else {
					LinkedList<Node> newDegreeSet = new LinkedList<Node>();
					newDegreeSet.add(n);
					this.nodesPerDegree.put(degree, newDegreeSet);
				}
			}
		}

		// List of Nodes sorted By Degree
		LinkedList<Node> temp = new LinkedList<Node>();
		int size = this.degrees.size();
		for (int j = 0; j < size; j++) {
			int currentDegree = this.degrees.last();
			this.degrees.remove(currentDegree);
			temp.addAll(nodesPerDegree.get(currentDegree));
		}

		// Divide Temp List into RichClubs
		int numberOfRichClubs = this.g.getNodes().size() / richClubIntervall;
		for (int i = 0; i < numberOfRichClubs; i++) {
			LinkedList<Node> temp2 = (LinkedList<Node>) temp.subList(i, i
					* richClubIntervall);
			for (Node node : temp2) {
				this.nodesRichClub[node.getIndex()] = i;
			}
			richClubs.put(i, temp2);
		}

		calculateRCC();

		return true;
	}

	private void calculateRCC() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			LinkedList<Node> cRC = new LinkedList<Node>();
			int edgesBetweenRichClubNodes = 0;
			for (int i = 0; i < richClubs.size(); i++) {
				for (Node n : richClubs.get(i)) {
					DirectedNode dn = (DirectedNode) n;
					for (DirectedEdge ed : dn.getOutgoingEdges()) {
						if (cRC.contains(ed.getDst())
								|| richClubs.get(i).contains(ed.getDst())) {
							edgesBetweenRichClubNodes++;
						}
					}
					for (DirectedEdge ed : dn.getIncomingEdges()) {
						if (cRC.contains(ed.getSrc())) {
							edgesBetweenRichClubNodes++;
						}
					}
				}

				if (i >= richClubEdges.length) {
					continue;
				}
				cRC.addAll(richClubs.get(i));
				richClubEdges[i] = edgesBetweenRichClubNodes;
				richClubCoefficienten[i] = (double) edgesBetweenRichClubNodes
						/ (double) (cRC.size() * (cRC.size() - 1));
			}

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			LinkedList<Node> cRC = new LinkedList<Node>();
			int edgesBetweenRichClubNodes = 0;
			for (int i = 0; i < richClubs.size(); i++) {
				for (Node n : richClubs.get(i)) {
					UndirectedNode udn = (UndirectedNode) n;
					for (UndirectedEdge ed : udn.getEdges()) {
						UndirectedNode node;
						if (ed.getNode1() != udn) {
							node = ed.getNode1();
						} else {
							node = ed.getNode2();
						}
						if (cRC.contains(node)
								|| richClubs.get(i).contains(node)) {
							edgesBetweenRichClubNodes++;
						}

					}

					if (i >= richClubEdges.length) {
						continue;
					}
					cRC.addAll(richClubs.get(i));
					richClubEdges[i] = edgesBetweenRichClubNodes;
					richClubCoefficienten[i] = (double) edgesBetweenRichClubNodes
							/ (double) (cRC.size() * (cRC.size() - 1));
				}
			}
		}

	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void init_() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
