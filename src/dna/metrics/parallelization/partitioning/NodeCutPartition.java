package dna.metrics.parallelization.partitioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.Rand;

public class NodeCutPartition extends Partition {

	protected Set<Node> cutNodes;

	protected Set<Edge> cutEdges;

	public Set<Node> getCutNodes() {
		return this.cutNodes;
	}

	public NodeCutPartition() {
		super();
	}

	public void init(Graph g, List<Node> nodes, Metric m, Set<Node> cutNodes,
			Set<Edge> cutEdges) {
		super.init(g, nodes, m);
		this.cutNodes = cutNodes;
		this.cutEdges = cutEdges;
	}

	public String toString() {
		return "NodeCutPartition: " + g.toString() + " @ " + cutNodes.size()
				+ " / " + cutEdges.size();
	}

	public static NodeCutPartition[] getPartitions(Graph g,
			List<List<Node>> nodesList, Metric m,
			HashMap<Node, Partition> partitionMap) {
		NodeCutPartition[] p = new NodeCutPartition[nodesList.size()];
		HashMap<Partition, Set<Edge>> globalCuts = new HashMap<Partition, Set<Edge>>();
		int index = 0;
		for (List<Node> nodes : nodesList) {
			p[index] = new NodeCutPartition();
			globalCuts.put(p[index], new HashSet<Edge>());
			for (Node node : nodes) {
				partitionMap.put(node, p[index]);
			}
			index++;
		}
		for (IElement e_ : g.getEdges()) {
			Edge e = (Edge) e_;
			if (partitionMap.get(e.getN1()) != partitionMap.get(e.getN2())) {
				if (Rand.rand.nextDouble() <= 0.5) {
					globalCuts.get(partitionMap.get(e.getN1())).add(e);
				} else {
					globalCuts.get(partitionMap.get(e.getN2())).add(e);
				}
			}
		}

		index = 0;
		for (List<Node> nodes : nodesList) {
			fill(p[index], getName(index), g, nodes, m,
					globalCuts.get(p[index]));
			index++;
		}

		return p;
	}

	protected static void fill(NodeCutPartition p, String name, Graph g,
			List<Node> nodes, Metric m, Set<Edge> globalCut) {
		Graph gp = g.getGraphDatastructures().newGraphInstance(name,
				g.getTimestamp(), nodes.size(),
				nodes.size() == 0 ? 0 : g.getEdgeCount() / nodes.size());
		GraphDataStructure gds = gp.getGraphDatastructures();

		// add main nodes
		for (Node n : nodes) {
			gp.addNode(gds.newNodeInstance(n.asString()));
		}

		// fill node set
		Set<Node> nodeSet = new HashSet<Node>();
		nodeSet.addAll(nodes);

		// cut
		Set<Node> cutNodes = new HashSet<Node>();
		Set<Edge> cutEdges = new HashSet<Edge>();

		// add edges
		for (Node n : nodes) {
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				if (nodeSet.contains(e.getN1()) && nodeSet.contains(e.getN2())) {
					Edge newEdge = gds.newEdgeInstance(e_.asString(), gp);
					gp.addEdge(newEdge);
					newEdge.connectToNodes();
				} else if (globalCut.contains(e)) {
					Node internal, external;
					if (nodeSet.contains(e.getN1())) {
						internal = e.getN1();
						external = e.getN2();
					} else {
						internal = e.getN2();
						external = e.getN1();
					}
					if (!gp.containsNode(external)) {
						external = gds.newNodeInstance(external.asString());
						gp.addNode(external);
						cutNodes.add(external);
					} else {
						external = gp.getNode(external.getIndex());
					}
					Edge newEdge = gds.newEdgeInstance(e.asString(), gp);
					gp.addEdge(newEdge);
					newEdge.connectToNodes();
					globalCut.remove(e);
					cutEdges.add(newEdge);
				}
			}
		}

		p.init(gp, nodes, m, cutNodes, cutEdges);
	}

	@Override
	protected Value[] getStatistics() {
		Value cutNodes = new Value("cutNodes", this.cutNodes.size());
		Value cutEdges = new Value("cutEdges", this.cutEdges.size());
		return new Value[] { cutNodes, cutEdges };
	}

	/*
	 * NA
	 */

	@Override
	public boolean propagate(NodeAddition globalNA) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * NR
	 */

	@Override
	public boolean shouldPropagate(NodeRemoval globalNR) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean propagate(NodeRemoval globalNR) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * EA
	 */

	@Override
	public boolean shouldPropagate(EdgeAddition globalEA) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean propagate(EdgeAddition globalEA) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * ER
	 */

	@Override
	public boolean shouldPropagate(EdgeRemoval globalER) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean propagate(EdgeRemoval globalER) {
		// TODO Auto-generated method stub
		return false;
	}

}
