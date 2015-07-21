package dna.graph.generators.konect;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.konect.KonectReader.KonectGraphType;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IntWeight;
import dna.util.Log;
import dna.util.parameters.BooleanParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public class KonectGraph extends GraphGenerator {

	protected KonectReader r;

	protected KonectGraphType graphType;
	protected String parameter;

	protected boolean removeZeroDegreeNodes;

	public KonectGraph(KonectReader r, KonectGraphType graphType,
			String graphParameter) {
		super("KonectGraph", new Parameter[] {
				new StringParameter("Name", r.name),
				new StringParameter("EdgeType", r.edgeType.toString()),
				new StringParameter("GraphType", graphType.toString()),
				new StringParameter("GraphParameter", graphParameter),
				new BooleanParameter("RemoveZeroDegreeNodes",
						r.removeZeroDegreeNodes) }, r.gds, 0, 100, 1000);
		this.r = r;
		this.graphType = graphType;
		this.parameter = graphParameter;
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();

		int processed = 0;
		while (true) {

			if (KonectGraphType.PROCESSED_EDGES.equals(this.graphType)) {
				if (processed >= Integer.parseInt(this.parameter))
					break;
			} else if (KonectGraphType.TIMESTAMP.equals(this.graphType)) {
				if (this.r.peek() != null
						&& this.r.peek().timestamp > Integer
								.parseInt(this.parameter)) {
					g.setTimestamp(Integer.parseInt(this.parameter));
					break;
				}
			} else if (KonectGraphType.TOTAL_EDGES.equals(this.graphType)) {
				if (g.getEdgeCount() >= Integer.parseInt(this.parameter))
					break;
			} else if (KonectGraphType.TOTAL_NODES.equals(this.graphType)) {
				if (g.getNodeCount() >= Integer.parseInt(this.parameter))
					break;
			} else {
				Log.error("invalid graph type: " + this.graphType);
				break;
			}

			KonectEdge edge = this.readNextEdge();
			if (edge == null) {
				break;
			}
			this.processEdge(g, edge);

			processed++;
		}

		if (KonectGraphType.TIMESTAMP.equals(this.graphType))
			g.setTimestamp(Integer.parseInt(this.parameter));

		return g;
	}

	protected KonectEdge readNextEdge() {
		return this.r.readEdge();
	}

	protected void addIfNecessary(Graph g, Node n) {
		if (!g.containsNode(n)) {
			g.addNode(n);
		}
	}

	protected void removeIfNecessary(Graph g, Node n) {
		if (this.removeZeroDegreeNodes && n.getDegree() == 0) {
			g.removeNode(n);
		}
	}

	protected void processEdge(Graph g, KonectEdge edge) {
		if (edge.n1 == edge.n2) {
			return;
		}

		Node n1 = r.getNode(edge.n1);
		Node n2 = r.getNode(edge.n2);

		switch (r.edgeType) {
		case ADD:
			if (edge.weight == 1) {
				this.addIfNecessary(g, n1);
				this.addIfNecessary(g, n2);
				Edge e = gds.newEdgeInstance(n1, n2);
				g.addEdge(e);
				e.connectToNodes();
			} else {
				Log.error("invalid weight for ADD: " + edge);
			}
			break;
		case ADD_REMOVE:
			if (edge.weight == -1) {
				if (g.containsEdge(n1, n2)) {
					Edge e = g.getEdge(n1, n2);
					g.removeEdge(e);
					e.disconnectFromNodes();
					this.removeIfNecessary(g, n1);
					this.removeIfNecessary(g, n2);
				}
			} else if (edge.weight == 1) {
				if (!g.containsEdge(n1, n2)) {
					this.addIfNecessary(g, n1);
					this.addIfNecessary(g, n2);
					Edge e = gds.newEdgeInstance(n1, n2);
					g.addEdge(e);
					e.connectToNodes();
				}
			} else {
				Log.error("invalid weight for ADD_REMOVE: " + edge);
			}
			break;
		case MULTI:
			if (edge.weight == 1) {
				if (g.containsEdge(n1, n2)) {
					IWeightedEdge e = (IWeightedEdge) g.getEdge(n1, n2);
					IntWeight w = (IntWeight) e.getWeight();
					w.setWeight(w.getWeight() + 1);
				} else {
					this.addIfNecessary(g, n1);
					this.addIfNecessary(g, n2);
					IWeightedEdge e = (IWeightedEdge) gds.newEdgeInstance(n1,
							n2);
					((IntWeight) e.getWeight()).setWeight(1);
					g.addEdge((Edge) e);
					e.connectToNodes();
				}
			} else if (edge.weight == -1) {
				IWeightedEdge e = (IWeightedEdge) g.getEdge(n1, n2);
				IntWeight w = (IntWeight) e.getWeight();
				int current = w.getWeight();
				if (current == 1) {
					g.removeEdge((Edge) e);
				} else {
					w.decreaseWeight(1);
				}
			} else {
				Log.error("invalid weight for MULTI: " + edge);
			}
			break;
		case WEIGHTED:
			if (!g.containsEdge(n1, n2)) {
				this.addIfNecessary(g, n1);
				this.addIfNecessary(g, n2);
				IWeightedEdge e = (IWeightedEdge) gds.newEdgeInstance(n1, n2);
				((IntWeight) e.getWeight()).setWeight((int) edge.weight);
				g.addEdge((Edge) e);
				e.connectToNodes();
			} else {
				IWeightedEdge e = (IWeightedEdge) g.getEdge(n1, n2);
				((IntWeight) e.getWeight()).setWeight((int) edge.weight);
			}
			break;
		default:
			break;
		}
	}

}
