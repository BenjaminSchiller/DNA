package dna.graph.generators.combined;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.Rand;
import dna.util.parameters.Parameter;

public class CombinedGraph extends GraphGenerator {

	public static enum InterconnectionType {
		RANDOM, PER_COMPONENT
	}

	protected InterconnectionType interconnectionType;
	protected String interconnectionParameter;

	public GraphGenerator[] ggs;

	public CombinedGraph(String name, InterconnectionType interconnectionType,
			String interconnectionParameter, GraphGenerator... ggs) {
		super(name, new Parameter[] {}, ggs[0].getGraphDataStructure(), 0,
				nodesInit(ggs), edgesInit(ggs));
		this.ggs = ggs;
		this.interconnectionType = interconnectionType;
		this.interconnectionParameter = interconnectionParameter;
	}

	public CombinedGraph(String name, InterconnectionType interconnectionType,
			String interconnectionParameter, GraphGenerator gg, int count) {
		super(name, new Parameter[] {}, gg.getGraphDataStructure(), 0, gg
				.getNodesInit() * count, gg.getEdgesInit() * count);
		this.ggs = new GraphGenerator[count];
		for (int i = 0; i < this.ggs.length; i++) {
			this.ggs[i] = gg;
		}
		this.interconnectionType = interconnectionType;
		this.interconnectionParameter = interconnectionParameter;
	}

	private static int nodesInit(GraphGenerator[] ggs) {
		int sum = 0;
		for (GraphGenerator gg : ggs) {
			sum += gg.getNodesInit();
		}
		return sum;
	}

	private static int edgesInit(GraphGenerator[] ggs) {
		int sum = 0;
		for (GraphGenerator gg : ggs) {
			sum += gg.getEdgesInit();
		}
		return sum;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph generate() {
		if (this.ggs.length == 1) {
			return this.ggs[0].generate();
		}

		Graph g = this.newGraphInstance();

		Graph[] components = new Graph[this.ggs.length];
		for (int i = 0; i < this.ggs.length; i++) {
			components[i] = this.ggs[i].generate();
		}

		HashMap<Node, Node>[] map = new HashMap[this.ggs.length];
		int index = 0;
		for (int i = 0; i < this.ggs.length; i++) {
			map[i] = new HashMap<Node, Node>();
			for (IElement n_ : components[i].getNodes()) {
				Node n = (Node) n_;
				Node newNode = this.gds.newNodeInstance(index++);
				map[i].put(n, newNode);
				g.addNode(newNode);
			}
			for (IElement e_ : components[i].getEdges()) {
				Edge e = (Edge) e_;
				Edge newEdge = this.gds.newEdgeInstance(map[i].get(e.getN1()),
						map[i].get(e.getN2()));
				g.addEdge(newEdge);
				newEdge.connectToNodes();
			}
		}

		switch (this.interconnectionType) {
		case PER_COMPONENT:
			int edges = Integer.parseInt(this.interconnectionParameter);
			for (int i = 0; i < components.length; i++) {
				int added = 0;
				int otherIndex = getOtherIndex(components, i);
				while (added < edges) {
					Node n1 = map[i].get(components[i].getRandomNode());
					Node n2 = map[otherIndex].get(components[otherIndex]
							.getRandomNode());
					Edge e = this.gds.newEdgeInstance(n1, n2);
					if (!g.containsEdge(e)) {
						g.addEdge(e);
						e.connectToNodes();
						added++;
					}
				}
			}
			break;
		case RANDOM:
			edges = Integer.parseInt(this.interconnectionParameter);
			int added = 0;
			while (added < edges) {
				int i = Rand.rand.nextInt(components.length);
				int otherIndex = getOtherIndex(components, i);
				Node n1 = map[i].get(components[i].getRandomNode());
				Node n2 = map[otherIndex].get(components[otherIndex]
						.getRandomNode());
				Edge e = this.gds.newEdgeInstance(n1, n2);
				if (!g.containsEdge(e)) {
					g.addEdge(e);
					e.connectToNodes();
					added++;
				}
			}
			break;
		default:
			break;
		}

		return g;
	}

	private static int getOtherIndex(Graph[] components, int current) {
		int rand = Rand.rand.nextInt(components.length);
		return rand >= current ? (rand + 1) % components.length : rand;
	}

}
