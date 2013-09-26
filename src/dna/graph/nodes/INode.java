package dna.graph.nodes;

import dna.graph.Element;
import dna.graph.IElement;
import dna.graph.edges.Edge;

public interface INode extends IElement {
	public int getIndex();

	public String toString();

	public String getStringRepresentation();

	public int hashCode();

	public boolean equals(Object obj);

	public int compareTo(Element o);
	
	public void init();
	
	public abstract boolean hasEdge(Edge e);

	public abstract boolean addEdge(Edge e);

	public abstract boolean removeEdge(Edge e);

	public abstract Iterable<IElement> getEdges();

	public abstract void print();	
}
