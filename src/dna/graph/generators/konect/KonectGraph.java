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
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public class KonectGraph extends GraphGenerator {

	protected KonectReader r;

	protected KonectGraphType type;
	protected int parameter;

	protected boolean removeZeroDegreeNodes;

	public KonectGraph(KonectReader r, KonectGraphType type, int parameter) {
		this(r, type, parameter, true);
	}

	public KonectGraph(KonectReader r, KonectGraphType type, int parameter,
			boolean removeZeroDegreeNodes) {
		super("KonectGraph", new Parameter[] {
				new StringParameter("Name", r.name),
				new StringParameter("EdgeType", r.edgeType.toString()),
				new StringParameter("GraphType", type.toString()),
				new IntParameter("Parameter", parameter),
				new BooleanParameter("RemoveZeroDegreeNodes",
						removeZeroDegreeNodes) }, r.gds, 0, 100, 1000);
		this.r = r;
		this.type = type;
		this.parameter = parameter;
		this.removeZeroDegreeNodes = removeZeroDegreeNodes;
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();

		int processed = 0;
		while (true) {

			if (KonectGraphType.PROCESSED_EDGES.equals(this.type)) {
				if (processed >= this.parameter)
					break;
			} else if (KonectGraphType.TIMESTAMP.equals(this.type)) {
				if (this.r.peek() != null
						&& this.r.peek().timestamp > this.parameter) {
					g.setTimestamp(this.parameter);
					break;
				}
			} else if (KonectGraphType.TOTAL_EDGES.equals(this.type)) {
				if (g.getEdgeCount() >= this.parameter)
					break;
			} else if (KonectGraphType.TOTAL_NODES.equals(this.type)) {
				if (g.getNodeCount() >= this.parameter)
					break;
			} else {
				Log.error("invalid graph type: " + this.type);
				break;
			}

			KonectEdge edge = this.readNextEdge();
			if (edge == null) {
				break;
			}
			this.processEdge(g, edge);

			processed++;
		}

		if (KonectGraphType.TIMESTAMP.equals(this.type))
			g.setTimestamp(this.parameter);

		return g;
	}

	protected KonectEdge readNextEdge() {
		try {
			return this.r.readEdge();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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
		case MULTI_UNWEIGHTED:
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
			} else {
				Log.error("invalid weight for MULTI_UNWEIGHTED: " + edge);
			}
			break;
		case MULTI_RATING:
			break;
		case UNWEIGHTED:
			if (edge.weight == 1) {
				this.addIfNecessary(g, n1);
				this.addIfNecessary(g, n2);
				Edge e = gds.newEdgeInstance(n1, n2);
				g.addEdge(e);
				e.connectToNodes();
			} else {
				Log.error("invalid weight for UNWEIGHTED: " + edge);
			}
			break;
		default:
			break;
		}
	}

}
