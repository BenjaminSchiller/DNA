package dna.metrics.patternEnum.subgfinder.hub.storedpath;

import java.util.ArrayList;
import java.util.Collection;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;

/**
 * Represents a stored path node.
 * 
 * @author Bastian Laur
 *
 */
public class StoredPathVertex {
	private INode vertex;
	private Collection<IEdge> edges = new ArrayList<>();
	private Collection<StoredPathVertex> nextVertices = new ArrayList<>();
	
	public INode getVertex() {
		return vertex;
	}
	
	public void setVertex(INode vertex) {
		this.vertex = vertex;
	}
	
	public Collection<IEdge> getEdges() {
		return edges;
	}
	
	public void setEdges(Collection<IEdge> edges) {
		this.edges = edges;
	}
	
	public Collection<StoredPathVertex> getNextVertices() {
		return nextVertices;
	}
	
	public void setNextVertices(Collection<StoredPathVertex> nextVertices) {
		this.nextVertices = nextVertices;
	}
	
	public StoredPathVertex getNextVertexById(int id){
		for(StoredPathVertex v : nextVertices){
			if(v.getVertex().getIndex() == id)
				return v;
		}
		return null;
	}
	
	public StoredPathVertex() { }

	public StoredPathVertex(INode vertex, Collection<IEdge> edges) {
		super();
		this.vertex = vertex;
		this.edges = edges;
		this.nextVertices = new ArrayList<StoredPathVertex>();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(vertex).append(" -> ");
		
		sb.append("[");
		for(StoredPathVertex spv : nextVertices){
			sb.append(spv.getVertex().getIndex())
				.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		
		sb.append(" ");
		
		sb.append(edges);
		
		return sb.toString();
	}
	
	public boolean isConnectedTo(StoredPathVertex spv) {
		for (IEdge edge : edges) {
			if (edge.isConnectedTo((Node) spv.getVertex())) {
				return true;
			}
		}
		
		return false;
	}
}
