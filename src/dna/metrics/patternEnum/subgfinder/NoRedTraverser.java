package dna.metrics.patternEnum.subgfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;
import dna.metrics.patternEnum.utils.GraphUtils;

/**
 * An enhanced traverser that does not need to check for redundant subgraphs. <br>
 * A lot faster than {@link SimpleTraverser}.
 * 
 * @author Bastian Laur
 *
 */
public class NoRedTraverser extends TraverseBase implements ITraverser {
	
	private static int maxSize;
	private static INode otherNode;
	private static INode startNode;
	private static List<Collection<Path>> foundSubgraphs;
	private static IEdge actEdge;
	private static Path startPath;
	
	public Collection<Path> getSubgraphsForEdge(IEdge actEdge, int maxSize,
			HubManager hubManager, EdgeAction edgeAction){
		
		if(maxSize <= 0) {
			return new ArrayList<Path>();
		}
		
		init(actEdge, maxSize, hubManager);
		startTraversing();
		
		return foundSubgraphs.get(maxSize - 1);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Init

	private static void init(IEdge actEdge_, int maxSize_, HubManager hubManager_) {
		maxSize = maxSize_;
		actEdge = actEdge_;
		startNode = actEdge_.getN1();
		otherNode = actEdge_.getN2();
		startPath = createStartPath();
		
		foundSubgraphs = new ArrayList<>(maxSize);
		for (int i = 0; i < maxSize; i++) {
			foundSubgraphs.add(new ArrayList<Path>(100));
		}
	}
	
	private static void startTraversing() {
		Collection<INode> n1Neighbours = new ArrayList<INode>();
		Collection<INode> n2Neighbours = new ArrayList<INode>();
		
		n2Neighbours.add(otherNode);
		foundSubgraphs.get(1).add(startPath);
		traverse(startNode, startPath, n1Neighbours, n2Neighbours, true, new ArrayList<Integer>());
	}
	
	private static Path createStartPath() {
		SmallGraph graph = new SmallGraph();
		graph.getNodes().add(startNode);
		graph.getNodes().add(otherNode);
		graph.getEdges().add(actEdge);
		
		Path path = new Path(graph);
		Iterable<IEdge> otherNodeEdges = GraphUtils.getEdgesForNode(otherNode);
		for (IEdge edge : otherNodeEdges) {
			if (edge.getN2().equals(startNode)) {
				path.getGraph().getEdges().add(edge);
				path.setChanged(true);
				setPrevGraph(path, actEdge);
				break;
			}
		}
		
		return path;
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////
// Traverse

	private static void traverse(INode actNode, Path actPath,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive, Collection<Integer> prevNeighbours){
		
		Path clonedActPath = actPath.shallowClone();
		
		//activeNeighbours.remove(actNode);
		
		if(!actNode.equals(startNode) && !actNode.equals(otherNode)) {
			addNodeWithConnectingEdgesToPath(actNode, clonedActPath, actEdge, n1PathActive);
		}
		
		if(!actNode.equals(startNode) && !actNode.equals(otherNode)
				&& clonedActPath.getGraph().getSize() == maxSize) {
			addToFoundSubgraphs(clonedActPath);
			return;
		}
		
		Collection<INode> newNeighbours = getNeighboursOfNodeWithout(actNode, n1Neighbours,
				n2Neighbours, clonedActPath.getGraph(), actEdge);
		Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
				n1PathActive);
		activeNeighbours.addAll(newNeighbours);
		
		for (Iterator<INode> iter = n1Neighbours.iterator(); iter.hasNext();) {
			INode n1Neighbour = iter.next();
			if (prevNeighbours.contains(n1Neighbour.getIndex())) {
				continue;
			}
			prevNeighbours.add(n1Neighbour.getIndex());
			
			iter.remove();
			traverse(n1Neighbour, clonedActPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					true, new ArrayList<Integer>(prevNeighbours));
		}
		
		for (Iterator<INode> iter = n2Neighbours.iterator(); iter.hasNext();) {
			INode n2Neighbour = iter.next();
			if (prevNeighbours.contains(n2Neighbour.getIndex())) {
				continue;
			}
			prevNeighbours.add(n2Neighbour.getIndex());
			
			iter.remove();
			traverse(n2Neighbour, clonedActPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					false, new ArrayList<Integer>(prevNeighbours));
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper
	
	private static Collection<INode> getActiveNeighbours(
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive) {
		
		if(n1PathActive) {
			return n1Neighbours;
		}
		return n2Neighbours;
	}
	
	private static void addToFoundSubgraphs(Path p) {
		foundSubgraphs.get(p.getGraph().getSize() - 1).add(p);
	}
}
