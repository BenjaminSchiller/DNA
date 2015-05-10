package dna.metrics.patternEnum.subgfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.utils.GraphUtils;

/**
 * Provides basic function for traversers.
 * 
 * @author Bastian Laur
 * 
 */
public abstract class TraverseBase {
	
	protected static int addNodeWithConnectingEdgesToPath(INode node, Path path, IEdge actEdge, boolean n1PathActive){
		addNodeToPath(node, path);
		return addConnectingEdgesToPath(node, path, actEdge, n1PathActive);
	}
	
	protected static void addNodeToPath(INode node, Path path){
		path.getGraph().getNodes().add(node);
	}
	
	protected static int addConnectingEdgesToPath(INode node, Path path, IEdge actEdge,
			boolean n1PathActive){
		Collection<IEdge> edges = getAllConnectingEdges(node, path, actEdge, n1PathActive);
		addConnectingEdgesToPath(node, path, actEdge, n1PathActive, edges);
		
		return edges.size();
	}
	
	protected static void addConnectingEdgesToPath(INode node, Path path, IEdge actEdge,
			boolean n1PathActive, Collection<IEdge> edges){		
		path.getGraph().getEdges().addAll(edges);
		
		if(path.hasChanged()) {
			setPrevGraph(path, actEdge);
		}
	}
	
	protected static Collection<IEdge> getAllConnectingEdges(INode node, Path path, IEdge startEdge,
			boolean n1PathActive){
		Collection<IEdge> returnList = new ArrayList<IEdge>(node.getDegree());
		
		Iterable<IEdge> edges = GraphUtils.getEdgesForNode(node);
		for(IEdge edge : edges){
			
			Node diffNode = edge.getDifferingNode((Node)node);
			if(path.getGraph().getNodes().contains(diffNode)) {
				returnList.add(edge);
				
				if(diffNode.equals(startEdge.getN2()) && n1PathActive) {
					path.setChanged(true);
				}
			}
		}
		
		return returnList;
	}

	protected static void setPrevGraph(Path path, IEdge actEdge) {
		SmallGraph prevGraph = path.getGraph().shallowClone();
		prevGraph.getEdges().remove(actEdge);
		path.setPrevGraph(prevGraph);
	}
	
	protected static boolean pathAlreadyFound(Path actPath, List<Collection<Path>> foundPaths){
//		for (Collection<Path> fp : foundPaths) {
//			for(Path foundPath : fp) {
//				if(foundPath.getGraph().equals(actPath.getGraph())) {
//					return true;
//				}
//			}
//		}
		Collection<Path> filteredPaths = foundPaths.get(actPath.getGraph().getSize() - 1);
		for(Path foundPath : filteredPaths) {
			if(foundPath.getGraph().equals(actPath.getGraph())) {
				return true;
			}
		}
		return false;
	}
	
	protected static boolean pathAlreadyFound(Path actPath, Collection<Path> foundPaths){
		
		for(Path foundPath : foundPaths) {
			if(foundPath.getGraph().equals(actPath.getGraph())) {
				return true;
			}
		}
		
		return false;
	}
	
	protected static Collection<INode> getNeighboursOfNodeWithout(INode node,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			SmallGraph actPath){
		return getNeighboursOfNodeWithout(node, n1Neighbours, n2Neighbours, actPath, null);
	}
	
	protected static Collection<INode> getNeighboursOfNodeWithout(INode node,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			SmallGraph actPath, IEdge actEdge) {
		
		Iterable<IEdge> edges = GraphUtils.getEdgesForNode(node);
		Collection<INode> neighbourNodes = new ArrayList<INode>(Iterables.size(edges));
		
		for(IEdge edge : edges){
			
			INode diffNode = edge.getDifferingNode((Node)node);
			
			if(!neighbourNodes.contains(diffNode) &&
					!n1Neighbours.contains(diffNode) &&
					!n2Neighbours.contains(diffNode) &&
					!actPath.getNodes().contains(diffNode)) {
				neighbourNodes.add(diffNode);
			}
		}
		
		return neighbourNodes;
	}
}
