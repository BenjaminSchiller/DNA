package dna.graph.generators.canonical;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public class HoneyCombGraph extends GraphGenerator {

	private int combsX;
	private int combsY;

	public static enum ClosedType {
		OPEN, CLOSED, MOEBIUS
	}

	private ClosedType closedType;

	public HoneyCombGraph(GraphDataStructure gds, int combsX, int combsY,
			ClosedType closedType) {
		super(buildName("HoneyCombGraph", gds), new Parameter[] {
				new IntParameter("CombsX", combsX),
				new IntParameter("CombsY", combsY),
				new StringParameter("ClosedType", closedType.toString()) },
				gds, 0, combsY * (combsX * 4 + 2), combsX * combsY * 6);
		this.combsX = combsX;
		this.combsY = combsY;
		this.closedType = closedType;
	}

	@Override
	public IGraph generate() {
		IGraph g = this.newGraphInstance();

		HoneyComb[][] hcs = new HoneyComb[combsX][combsY];
		for (int x = 0; x < combsX; x++) {
			for (int y = 0; y < combsY; y++) {
				if (y == 0) {
					hcs[x][y] = new HoneyComb(g, this.gds);
				} else {
					hcs[x][y] = new HoneyComb(g, this.gds, hcs[x][y - 1]);
				}
				if (x > 0) {
					this.connect(g, hcs[x - 1][y], hcs[x][y]);
				}
			}
		}

		if (ClosedType.CLOSED.equals(closedType)) {
			for (int y = 0; y < combsY; y++) {
				this.connect(g, hcs[combsX - 1][y], hcs[0][y]);
			}
		} else if (ClosedType.MOEBIUS.equals(closedType)) {
			for (int y = 0; y < combsY; y++) {
				this.connect(g, hcs[combsX - 1][y], hcs[0][combsY - y - 1]);
			}
		}

		return g;
	}

	protected void connect(IGraph g, HoneyComb[] row1, HoneyComb[] row2) {
		for (int i = 0; i < row1.length; i++) {
			Node src = row1[i].bottom;
			Node dst = row2[i].top;
			Edge e = this.gds.newEdgeInstance(src, dst);
			g.addEdge(e);
			e.connectToNodes();
		}
	}

	protected void connect(IGraph g, HoneyComb hc1, HoneyComb hc2) {
		Edge e = this.gds.newEdgeInstance(hc1.bottom, hc2.top);
		g.addEdge(e);
		e.connectToNodes();
	}

	protected HoneyComb[] createRow(IGraph g, GraphDataStructure gds, int count) {
		HoneyComb[] row = new HoneyComb[count];
		row[0] = new HoneyComb(g, gds);
		for (int i = 1; i < row.length; i++) {
			row[i] = new HoneyComb(g, gds, row[i - 1]);
		}
		return row;
	}

	public static class HoneyComb {
		public Node top;
		public Node topLeft;
		public Node bottomLeft;
		public Node topRight;
		public Node bottomRight;
		public Node bottom;

		public HoneyComb(IGraph g, GraphDataStructure gds) {
			top = addNode(g, gds);
			topLeft = addNode(g, gds);
			topRight = addNode(g, gds);
			bottom = addNode(g, gds);
			bottomLeft = addNode(g, gds);
			bottomRight = addNode(g, gds);

			connect(g, gds, top, topLeft);
			connect(g, gds, top, topRight);
			connect(g, gds, bottom, bottomLeft);
			connect(g, gds, bottom, bottomRight);
			connect(g, gds, topLeft, bottomLeft);
			connect(g, gds, topRight, bottomRight);
		}

		public HoneyComb(IGraph g, GraphDataStructure gds, HoneyComb leftHC) {
			top = addNode(g, gds);
			topLeft = leftHC.topRight;
			topRight = addNode(g, gds);
			bottom = addNode(g, gds);
			bottomLeft = leftHC.bottomRight;
			bottomRight = addNode(g, gds);

			connect(g, gds, top, topLeft);
			connect(g, gds, top, topRight);
			connect(g, gds, bottom, bottomLeft);
			connect(g, gds, bottom, bottomRight);
			connect(g, gds, topRight, bottomRight);
		}

		protected static Node addNode(IGraph g, GraphDataStructure gds) {
			Node n = gds.newNodeInstance(g.getNodeCount());
			g.addNode(n);
			return n;
		}

		protected static void connect(IGraph g, GraphDataStructure gds,
				Node src, Node dst) {
			Edge e = gds.newEdgeInstance(src, dst);
			g.addEdge(e);
			e.connectToNodes();
		}
	}

}
