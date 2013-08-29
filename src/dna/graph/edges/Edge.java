package dna.graph.edges;

import dna.graph.Element;
import dna.graph.nodes.Node;

public abstract class Edge extends Element {
	public abstract Node getDifferingNode(Node n);
}
