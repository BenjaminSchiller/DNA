package dna.metrics.patternEnum.datastructures;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;


/**
 * Graph class that is designed for small graphs (<20 nodes).
 * @author Bastian Laur
 *
 */
public class SmallGraph {
	private ArrayList<INode> nodes = new ArrayList<>();
	private ArrayList<IEdge> edges = new ArrayList<>();
	
	public ArrayList<INode> getNodes() {
		return nodes;
	}
	
	public void setNodes(ArrayList<INode> nodes) {
		this.nodes = nodes;
	}
	
	public ArrayList<IEdge> getEdges() {
		return edges;
	}
	
	public void setEdges(ArrayList<IEdge> edges) {
		this.edges = edges;
	}
	
	public SmallGraph(){}
	
	public SmallGraph(Collection<? extends INode> nodes, Collection<? extends IEdge> edges){
		if(nodes != null){
			this.nodes.addAll(nodes);
		}

		if(edges != null){
			this.edges.addAll(edges);
		}
	}
	
	public SmallGraph shallowClone(){
		return new SmallGraph(nodes, edges);
	}
	
	public int getSize(){
		return nodes.size();
	}
	
	public INode getNodeByIndex(int index){
		for(INode n : nodes){
			if(n.getIndex() == index)
				return n;
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		return edges.size() * 100 + nodes.size();
		//return nodes.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof SmallGraph))
			return false;
		
		SmallGraph otherGraph = (SmallGraph)o;
		
		if(nodes.size() != otherGraph.getNodes().size())
			return false;
		if(edges.size() != otherGraph.getEdges().size())
			return false;
		
		for (INode node : nodes) {
			if(!otherGraph.getNodes().contains(node))
				return false;
		}
		
//		for (IEdge edge : edges) {
//			if(!otherGraph.getEdges().contains(edge))
//				return false;
//		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(StringUtils.join(nodes, ", "));
		sb.append("][");
		sb.append(StringUtils.join(edges, ", "));
		sb.append("]");
		
		return sb.toString();
	}
}
