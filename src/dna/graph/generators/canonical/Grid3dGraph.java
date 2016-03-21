package dna.graph.generators.canonical;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.canonical.HoneyCombGraph.ClosedType;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public class Grid3dGraph extends GraphGenerator {

	private int x;
	private int y;
	private int z;

	private ClosedType closedType;

	public Grid3dGraph(GraphDataStructure gds, int x, int y, int z,
			ClosedType closedType) {
		super("Grid3dGraph", new Parameter[] { new IntParameter("X", x),
				new IntParameter("Y", y), new IntParameter("Z", z),
				new StringParameter("ClosedType", closedType.toString()) },
				gds, 0, x * y * z, x * y * z * 8);
		this.x = x;
		this.y = y;
		this.z = z;
		this.closedType = closedType;
	}

	@Override
	public IGraph generate() {
		IGraph g = this.newGraphInstance();

		Node[][][] nodes = new Node[this.x][this.y][this.z];
		int index = 0;
		for (int x = 0; x < this.x; x++) {
			for (int y = 0; y < this.y; y++) {
				for (int z = 0; z < this.z; z++) {
					Node n = this.gds.newNodeInstance(index++);
					nodes[x][y][z] = n;
					g.addNode(n);
					if (x > 0)
						this.connect(g, nodes[x][y][z], nodes[x - 1][y][z]);
					if (y > 0)
						this.connect(g, nodes[x][y][z], nodes[x][y - 1][z]);
					if (z > 0)
						this.connect(g, nodes[x][y][z], nodes[x][y][z - 1]);
				}
			}
		}

		if (ClosedType.CLOSED.equals(closedType)) {
			for (int y = 0; y < this.y; y++) {
				for (int z = 0; z < this.z; z++) {
					this.connect(g, nodes[this.x - 1][y][z], nodes[0][y][z]);
				}
			}
		} else if (ClosedType.MOEBIUS.equals(closedType)) {
			for (int y = 0; y < this.y; y++) {
				for (int z = 0; z < this.z; z++) {
					this.connect(g, nodes[this.x - 1][y][z], nodes[0][this.y
							- y - 1][z]);
				}
			}
		}

		return g;
	}

	protected void connect(IGraph g, Node src, Node dst) {
		Edge e = this.gds.newEdgeInstance(src, dst);
		g.addEdge(e);
		e.connectToNodes();
	}
}
