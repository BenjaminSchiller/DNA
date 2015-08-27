package dna.metrics.patternEnum.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.datastructures.SmallGraph;

public class GraphUtils {
	
	public static Iterable<IElement> cloneNodes(Iterable<IElement> nodes, Graph g){
		ArrayList<IElement> returnList = new ArrayList<>();
		
		for(IElement n : nodes)
			returnList.add(cloneNode(n, g));
		
		return returnList;
	}
	
	public static IElement cloneNode(IElement n, Graph g){
		try {
			return n.getClass()
					.getDeclaredConstructor(int.class, GraphDataStructure.class)
					.newInstance(((INode)n).getIndex(), g.getGraphDatastructures());
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	public static boolean testIsomorphism(SmallGraph graph1, SmallGraph graph2){
		DirectedGraph<Integer, DefaultEdge> g1 = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
		DirectedGraph<Integer, DefaultEdge> g2 = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

		for(IElement tmp : graph1.getNodes()){
			INode node = (INode)tmp;
			g1.addVertex(node.getIndex());
		}
		
		for(IElement tmp : graph1.getEdges()){
			IEdge edge = (IEdge)tmp;
			g1.addEdge(edge.getN1().getIndex(), edge.getN2().getIndex());
		}
		
		for(IElement tmp : graph2.getNodes()){
			INode node = (INode)tmp;
			g2.addVertex(node.getIndex());
		}
		
		for(IElement tmp : graph2.getEdges()){
			IEdge edge = (IEdge)tmp;
			g2.addEdge(edge.getN1().getIndex(), edge.getN2().getIndex());
		}
		
		@SuppressWarnings("rawtypes")
		GraphIsomorphismInspector iso =
	            AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(
	                g1, g2, null, null);
		
		return iso.hasNext();
	}
	
	public static Collection<INode> getNeighboursOfNode(INode node){
		List<INode> returnList = new ArrayList<INode>();
		
		Iterable<IEdge> edges = GraphUtils.getEdgesForNode(node);
		for(IEdge edge : edges){
			INode diffNode = edge.getDifferingNode((Node)node);
			
			if(!returnList.contains(diffNode)) {
				returnList.add(diffNode);
			}
		}
		
		return returnList;
	}
	
	@SuppressWarnings("unchecked")
	public static Iterable<IEdge> getEdgesForNode(INode n) {
		return (Iterable<IEdge>) (Object) n.getEdges();
	}
	
	public static Collection<IEdge> filterEdgesContainingNode(Collection<IEdge> edges, Node node){
		List<IEdge> returnList = new ArrayList<IEdge>();
		
		for(IEdge edge : edges) {
			if(edge.isConnectedTo(node)) {
				returnList.add(edge);
			}
		}
		
		return returnList;
	}
	
	public static Collection<IEdge> getEdgesOfNode(SmallGraph graph, INode node){
		List<IEdge> returnList = new ArrayList<IEdge>();
		
		for(IEdge edge : graph.getEdges()) {
			if(edge.getN1().equals(node) ||
					edge.getN2().equals(node)) {
				returnList.add(edge);
			}
		}
		
		return returnList;
	}
	
	public static boolean hasDoubleNodes(SmallGraph g) {
		HashSet<INode> nodesSet = new HashSet<>(g.getNodes());
		return nodesSet.size() != g.getNodes().size();
	}
	
	public static boolean hasDoubleEdges(SmallGraph g) {
		HashSet<IEdge> edgeSet = new HashSet<>(g.getEdges());
		return edgeSet.size() != g.getEdges().size();
	}
	
	public static void removeEdge(Graph dnsGraph, Edge edge) {
		dnsGraph.removeEdge(edge);
		edge.getN1().removeEdge(edge);
		edge.getN2().removeEdge(edge);
	}
	
	public static void addEdge(Graph dnsGraph, Edge edge) {
		dnsGraph.edges.add(edge);
		edge.getN1().addEdge(edge);
		edge.getN2().addEdge(edge);
	}
	
	public static Edge getEdge(Graph dnsGraph, Edge edge) {
		return getEdge(dnsGraph, edge.getN1Index(), edge.getN2Index());
	}
	
	public static Edge getEdge(Graph dnsGraph, int srcIndex, int dstIndex) {
		Node srcNode = dnsGraph.getNode(srcIndex);
		Node dstNode = dnsGraph.getNode(dstIndex);
		return dnsGraph.getEdge(srcNode, dstNode);
	}
	
	public static Collection<IEdge> getAllConnectingEdges(INode node, SmallGraph graph){
		return getAllConnectingEdges(node, graph.getNodes());
	}
	
	public static Collection<IEdge> getAllConnectingEdges(INode node, Collection<INode> nodes) {
		ArrayList<IEdge> returnList = new ArrayList<IEdge>();
		
		Iterable<IEdge> edges = getEdgesForNode(node);
		for(IEdge edge : edges) {
			for(INode n : nodes) {
				if(edge.isConnectedTo((Node)n)) {
					returnList.add(edge);
				}
			}
		}
		
		return returnList;
	}
	
	public static List<IEdge> getAllConnectingEdges(SmallGraph graph1, SmallGraph graph2){
		Collection<INode> graph1Nodes = graph1.getNodes();
		List<IEdge> returnList = new ArrayList<>();
		
		for(INode graph1Node : graph1Nodes){
			
			for(IElement elem : graph1Node.getEdges()){
				if(!(elem instanceof IEdge))
					continue;
				IEdge edge = (IEdge)elem;
				
				Node diffNode = edge.getDifferingNode((Node)graph1Node);
				INode foundNeighbour = graph2.getNodeByIndex(diffNode.getIndex());
				if(foundNeighbour != null){
					returnList.add(edge);
				}
			}
		} 
		
		return returnList;
	}
	
	public static void removeEdgeFromCollection(Collection<IEdge> edgeCol, IEdge edgeToRemove) {
		for (IEdge edge : edgeCol) {
			if (edge.getN1().equals(edgeToRemove.getN1())
					&& edge.getN2().equals(edgeToRemove.getN1())) {
				edgeCol.remove(edge);
			}
		}
	}
}