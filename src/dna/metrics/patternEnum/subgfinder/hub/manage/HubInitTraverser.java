package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathVertex;
import dna.metrics.patternEnum.utils.GraphUtils;

public class HubInitTraverser extends HubTraverserBase implements IHubTraverser {
	
	private static int maxPathSize;
	private static List<Collection<Path>> foundSubgraphs;
			
	public Collection<Path> getSubgraphsForHub(INode hub, int size) {
		if(size <= 0)
			return new ArrayList<Path>();
		
		maxPathSize = size;
		foundSubgraphs = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			foundSubgraphs.add(new ArrayList<Path>(100));
		}
		
		traverseForHubRec(hub, new SmallGraph(), new ArrayList<INode>());
		
		Collection<Path> sortedFoundSubgraphs = new ArrayList<>();
		for (int i = 0; i < foundSubgraphs.size(); i++) {
			sortedFoundSubgraphs.addAll(foundSubgraphs.get(i));
		}
		
		
		return sortedFoundSubgraphs;
	}
	
	private void traverseForHubRec(INode actNode, SmallGraph actPath,
			List<INode> openNeighbours){
		openNeighbours.remove(actNode);
		
		SmallGraph clonedActPath = actPath.shallowClone();
		
		clonedActPath = addNodeWithConnectingEdgesToPath(actNode, clonedActPath);
		
		if(pathAlreadyFound(clonedActPath, foundSubgraphs))
			return;
		
		addPathToFoundSubgraphs(clonedActPath);
		
		if(clonedActPath.getSize() >= maxPathSize)
			return;
		
		Collection<INode> newNeighbours = getNeighboursOfNodeWithout(actNode, openNeighbours,
				clonedActPath);
		openNeighbours.addAll(newNeighbours);
		
		//for(INode openNeighbour : openNeighbours){
		for (Iterator<INode> iter = openNeighbours.iterator(); iter.hasNext();) {
			INode openNeighbour = iter.next();
			iter.remove();
			traverseForHubRec(openNeighbour, clonedActPath, new ArrayList<INode>(openNeighbours));
		}
	}

	private void addPathToFoundSubgraphs(SmallGraph clonedActPath) {
		Path p = new Path(clonedActPath);
		foundSubgraphs.get(p.getGraph().getSize() - 1).add(p);
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	
	public StoredPathVertex getSubgraphsForNodeAsSpv(Node node, SmallGraph actPath,
			int maxSize) {
		SmallGraph clonedActPath = actPath.shallowClone();
		return getSubgraphsForNodeAsSpv(node, clonedActPath, 1, maxSize);
	}
	
	private static StoredPathVertex getSubgraphsForNodeAsSpv(Node node, SmallGraph actPath,
			int actSize, int maxSize) {
		Collection<IEdge> connectingEdges = GraphUtils.getAllConnectingEdges(node, actPath);
		StoredPathVertex spv = new StoredPathVertex(node, connectingEdges);
		
		if (actSize >= maxSize) {
			return spv;
		}
		
		actPath.getNodes().add(node);
		
		Collection<INode> neighbours = GraphUtils.getNeighboursOfNode(node);
		for (INode neighbour : neighbours) {
			if (!actPath.getNodes().contains(neighbour)) {
				StoredPathVertex nextSpv = getSubgraphsForNodeAsSpv((Node)neighbour, actPath, actSize + 1,
						maxSize);
				spv.getNextVertices().add(nextSpv);
			}
		}
		
		actPath.getNodes().remove(node);
		
		return spv;
	}
}
