package dna.graph.generators.canonical;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.canonical.HoneyCombGraph.ClosedType;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public class Grid2dGraph extends GraphGenerator {

	private int x;
	private int y;

	private ClosedType closedType;

	public Grid2dGraph(GraphDataStructure gds, int x, int y,
			ClosedType closedType) {
		super("Grid2dGraph", new Parameter[] { new IntParameter("X", x),
				new IntParameter("Y", y),
				new StringParameter("ClosedType", closedType.toString()) },
				gds, 0, x * y, x * y * 4);
		this.x = x;
		this.y = y;
		this.closedType = closedType;
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();

		Node[][] nodes = new Node[this.x][this.y];
		int index = 0;
		for (int x = 0; x < this.x; x++) {
			for (int y = 0; y < this.y; y++) {
				Node n = this.gds.newNodeInstance(index++);
				nodes[x][y] = n;
				g.addNode(n);
				if (x > 0)
					this.connect(g, nodes[x][y], nodes[x - 1][y]);
				if (y > 0)
					this.connect(g, nodes[x][y], nodes[x][y - 1]);
			}
		}

		if (ClosedType.CLOSED.equals(closedType)) {
			for (int y = 0; y < this.y; y++) {
				this.connect(g, nodes[this.x - 1][y], nodes[0][y]);
			}
		} else if (ClosedType.MOEBIUS.equals(closedType)) {
			for (int y = 0; y < this.y; y++) {
				this.connect(g, nodes[this.x - 1][y], nodes[0][this.y - y - 1]);
			}
		}

		return g;
	}

	protected void connect(Graph g, Node src, Node dst) {
		Edge e = this.gds.newEdgeInstance(src, dst);
		g.addEdge(e);
		e.connectToNodes();
	}
}
