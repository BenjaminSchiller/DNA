package dna.metrics.patternEnum.patterncounter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.SmallGraph;

class DirectedMotifType extends MotifType {

	private byte[] degreeHash;
	private int hashCode;
	private List<Integer> sortedNodes;
	
	public byte[] getDegreeHash() {
		return degreeHash;
	}
	
	private DirectedGraph<Integer, DefaultEdge> getDirectedGraph() {
		return (DirectedGraph<Integer, DefaultEdge>) graph;
	}

	public void generate (SmallGraph baseGraph) {
		graph = createIsolatedGraph(baseGraph);
		
		sortedNodes = new ArrayList<>(graph.vertexSet());
		sortNodes();
		
		degreeHash = createDegreeHash();
		hashCode = createHashCode();
	}
	
	private int createHashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		
		hcb.append(graph.vertexSet().size());
		hcb.append(graph.edgeSet().size());
		
		for(Integer n : sortedNodes){
			hcb.append(getDirectedGraph().outDegreeOf(n));
			hcb.append(getDirectedGraph().inDegreeOf(n));
		}
		
		return hcb.toHashCode();
	}

	private DirectedGraph<Integer, DefaultEdge> createIsolatedGraph(SmallGraph baseGraph) {
		DirectedGraph<Integer, DefaultEdge> jGraphT = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
		
		List<INode> nodes = baseGraph.getNodes();
		for(INode node : nodes){
			jGraphT.addVertex(node.getIndex());
		}
		
		List<IEdge> edges = baseGraph.getEdges();
		for(IEdge edge : edges){
			jGraphT.addEdge(edge.getN1().getIndex(), edge.getN2().getIndex());
		}
		
		return jGraphT;
	}

	private byte[] createDegreeHash() {
		ByteBuffer degreeHash = ByteBuffer.allocate(2 + graph.vertexSet().size()*2);
		degreeHash.put((byte)graph.vertexSet().size());
		degreeHash.put((byte)graph.edgeSet().size());
		
		for(Integer n : sortedNodes){
			degreeHash.put((byte)getDirectedGraph().outDegreeOf(n));
			degreeHash.put((byte)getDirectedGraph().inDegreeOf(n));
		}
		
		return degreeHash.array();
	}
	
	@Override
	public int hashCode(){
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null || !(o instanceof DirectedMotifType))
			return false;
		
		DirectedMotifType otherMotif = (DirectedMotifType)o;
		if(!Arrays.equals(degreeHash, otherMotif.getDegreeHash()))
			return false;
		
		return testIsomorphism(otherMotif);
	}
	
	private void sortNodes(){
		Collections.sort(sortedNodes, new Comparator<Integer>(){
			@Override
			public int compare(Integer n1, Integer n2) {
				int n1In = getDirectedGraph().inDegreeOf(n1);
				int n1Out = getDirectedGraph().outDegreeOf(n1);
				
				int n2In = getDirectedGraph().inDegreeOf(n2);
				int n2Out = getDirectedGraph().outDegreeOf(n2);
				
				if(n1Out < n2Out)
					return -1;
				if(n1Out > n2Out)
					return 1;
				if(n1In < n2In)
					return -1;
				if(n1In > n2In)
					return 1;
				return 0;
			}
			
		});
	}
	
	private boolean testIsomorphism(DirectedMotifType otherDirectedMotif){
		
		@SuppressWarnings("rawtypes")
		GraphIsomorphismInspector iso = null;
		Random ran = new Random();
		boolean success;
		int counter = 0;
		do {
			counter ++;
			success = true;
			
			try {
				iso = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(
			                graph, otherDirectedMotif.getGraph(), null, null);
			} catch (Exception ex) {
				int ranId;
				do{
					ranId = ran.nextInt();
				} while (graph.containsVertex(ranId) || otherDirectedMotif.getGraph().containsVertex(ranId));
				
				graph.addVertex(ranId);
				otherDirectedMotif.getGraph().addVertex(ranId);
				
				success = false;
				
				if(counter > 10)
					throw ex;
			}
		} while (!success);
		
		return iso.hasNext();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(StringUtils.join(graph.vertexSet(), ", "));
		sb.append("][");
		sb.append(StringUtils.join(graph.edgeSet(), ", "));
		sb.append("]");
		
		return sb.toString();
	}
}
