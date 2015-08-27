package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.Collection;
import java.util.List;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.utils.GraphUtils;

public abstract class HubTraverserBase {
	
	protected static SmallGraph addNodeWithConnectingEdgesToPath(INode node, SmallGraph path){
		path = addNodeToPath(node, path);
		path = addConnectingEdgesToPath(node, path);
			
		return path;
	}
	
	protected static SmallGraph addNodeToPath(INode node, SmallGraph path){
		path.getNodes().add(node);
		return path;
	}
	
	protected static SmallGraph addConnectingEdgesToPath(INode node, SmallGraph path){
		Collection<IEdge> edges = GraphUtils.getAllConnectingEdges(node, path);
		path.getEdges().addAll(edges);
		
		return path;
	}
	
	protected static boolean pathAlreadyFound(SmallGraph actPath, List<Collection<Path>> foundPaths){
		Collection<Path> filteredPaths = foundPaths.get(actPath.getSize() - 1);
		for(Path foundPath : filteredPaths) {
			if(foundPath.getGraph().equals(actPath)) {
				return true;
			}
		}
		return false;
	}
	
	protected static Collection<INode> getNeighboursOfNodeWithout(INode node, List<INode> openNeighbours, SmallGraph actPath){
		Collection<INode> neighbourNodes = GraphUtils.getNeighboursOfNode(node);
		neighbourNodes.removeAll(openNeighbours);
		neighbourNodes.removeAll(actPath.getNodes());
		
		return neighbourNodes;
	}
}
