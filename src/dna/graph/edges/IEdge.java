package dna.graph.edges;

import dna.graph.IElement;
import dna.graph.nodes.Node;

public interface IEdge extends IElement {
	public Node getDifferingNode(Node n);
}
