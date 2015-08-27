package dna.metrics.patternEnum.subgfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathVertex;
import dna.metrics.patternEnum.utils.GraphUtils;

/**
 * A trivial (and slow) traverser to enumerate all subgraphs. 
 * 
 * @author Bastian Laur
 * 
 */
public class SimpleTraverser extends TraverseBase implements ITraverser {
	
	private int patternSize;
	private INode otherNode;
	private INode startNode;
	private List<Collection<Path>> foundSubgraphs;
	private IEdge actEdge;
	private Path startPath;
	public HashSet<StoredPathVertex> hubsToUpdate;
	
	public static boolean usedHub = false;
	
	static int counter = 0;
	public Collection<Path> getSubgraphsForEdge(IEdge actEdge, int maxSize,
			HubManager hubManager, EdgeAction edgeAction){
		
		if(maxSize <= 0) {
			return new ArrayList<Path>();
		}
		
		init(actEdge, maxSize, hubManager);
		
		startTraversing();
		
		return foundSubgraphs.get(patternSize - 1);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Init

	private void init(IEdge actEdge_, int maxSize_,
			HubManager hubManager_) {
		patternSize = maxSize_;
		actEdge = actEdge_;
		startNode = actEdge_.getN1();
		otherNode = actEdge_.getN2();
		startPath = createStartPath();
		hubsToUpdate = new HashSet<>();
		
		foundSubgraphs = new ArrayList<>(patternSize);
		for (int i = 0; i < patternSize; i++) {
			foundSubgraphs.add(new ArrayList<Path>(100));
		}
		
		usedHub = false;
	}
	
	private void startTraversing() {
		Collection<INode> n1Neighbours = new ArrayList<INode>();
		Collection<INode> n2Neighbours = new ArrayList<INode>();
		
		n2Neighbours.add(otherNode);
		foundSubgraphs.get(1).add(startPath);
		traverse(startNode, startPath, n1Neighbours, n2Neighbours, true, new ArrayList<Integer>());
	}
	
	private Path createStartPath() {
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
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Traverse

	private void traverse(INode actNode, Path actPath,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive, Collection<Integer> prevNeighbours){
		Path clonedActPath = actPath.shallowClone();
		
		Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
				n1PathActive);
		activeNeighbours.remove(actNode);
		
		if(!actNode.equals(startNode) && !actNode.equals(otherNode)) {
			addNodeWithConnectingEdgesToPath(actNode, clonedActPath, actEdge, n1PathActive);
		}
		
		if(pathAlreadyFound(clonedActPath, foundSubgraphs)) {
			if (!actNode.equals(startNode) && !actNode.equals(otherNode)) {
				return;
			}
		} else {
			addToFoundSubgraphs(clonedActPath);
		}
		
		if(clonedActPath.getGraph().getSize() >= patternSize) {
			return;
		}
		
		Collection<INode> newNeighbours = getNeighboursOfNodeWithout(actNode, n1Neighbours,
				n2Neighbours, clonedActPath.getGraph(), actEdge);
		activeNeighbours.addAll(newNeighbours);
		
		for (Iterator<INode> iter = n1Neighbours.iterator(); iter.hasNext();) {
			INode n1Neighbour = iter.next();
			traverse(n1Neighbour, clonedActPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					true, new ArrayList<Integer>(prevNeighbours));
		}
		
		for (Iterator<INode> iter = n2Neighbours.iterator(); iter.hasNext();) {
			INode n2Neighbour = iter.next();
			traverse(n2Neighbour, clonedActPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					false, new ArrayList<Integer>(prevNeighbours));
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper
	
	private Collection<INode> getActiveNeighbours(
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive) {
		
		if(n1PathActive) {
			return n1Neighbours;
		}
		return n2Neighbours;
	}
	
	private void addToFoundSubgraphs(Path p) {
		foundSubgraphs.get(p.getGraph().getSize() - 1).add(p);
	}
}
