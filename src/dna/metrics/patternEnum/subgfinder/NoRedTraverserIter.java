package dna.metrics.patternEnum.subgfinder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;
import dna.metrics.patternEnum.utils.GraphUtils;

/**
 * An iterative version of {@link NoRedTraverser}.
 * 
 * @author Bastian Laur
 *
 */
public class NoRedTraverserIter extends TraverseBase implements ITraverser {
	
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
		List<INode> n1Neighbours = new ArrayList<INode>();
		List<INode> n2Neighbours = new ArrayList<INode>();
		
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
			List<INode> n1Neighbours, List<INode> n2Neighbours,
			boolean n1PathActive, Collection<Integer> prevNeighbours){
		
		int stackSize = 1000000;
		
		ArrayDeque<Path> actPathStack = new ArrayDeque<>(stackSize);
		ArrayDeque<List<INode>> n1NeighboursStack = new ArrayDeque<>(stackSize);
		ArrayDeque<List<INode>> n2NeighboursStack = new ArrayDeque<>(stackSize);
		ArrayDeque<Collection<Integer>> prevNeighStack = new ArrayDeque<>(stackSize);
		
		boolean firstVisit = true;
		actPathStack.addLast(actPath);
		
		while (!actPathStack.isEmpty()) {
			if (!firstVisit) {
				if (!n1Neighbours.isEmpty()) {
					actNode = n1Neighbours.remove(0);
					n1PathActive = true;
				} else if (!n2Neighbours.isEmpty()) {
					actNode = n2Neighbours.remove(0);
					n1PathActive = false;
				} else {
					actPath = actPathStack.pollLast();
					n1Neighbours = n1NeighboursStack.pollLast();
					n2Neighbours = n2NeighboursStack.pollLast();
					prevNeighbours = prevNeighStack.pollLast();
					firstVisit = false;
					continue;
				}
				
				if (prevNeighbours.contains(actNode.getIndex())) {
					continue;
				} else {
					prevNeighbours.add(actNode.getIndex());
				}
				
				actPathStack.addLast(actPath);
				actPath = actPath.shallowClone();
				n1NeighboursStack.addLast(n1Neighbours);
				n1Neighbours = new ArrayList<INode>(n1Neighbours);
				n2NeighboursStack.addLast(n2Neighbours);
				n2Neighbours = new ArrayList<INode>(n2Neighbours);
				prevNeighStack.addLast(prevNeighbours);
				prevNeighbours = new ArrayList<>(prevNeighbours);
				
				firstVisit = true;
				continue;
			}
			
			if(!actNode.equals(startNode) && !actNode.equals(otherNode)) {
				addNodeWithConnectingEdgesToPath(actNode, actPath, actEdge, n1PathActive);
				
				if (actPath.getGraph().getSize() == maxSize) {
					addToFoundSubgraphs(actPath);
					
					actPath = actPathStack.pollLast();
					n1Neighbours = n1NeighboursStack.pollLast();
					n2Neighbours = n2NeighboursStack.pollLast();
					prevNeighbours = prevNeighStack.pollLast();
					firstVisit = false;
					continue;
				}
			}
			
			Collection<INode> newNeighbours = getNeighboursOfNodeWithout(actNode, n1Neighbours,
					n2Neighbours, actPath.getGraph(), actEdge);
			Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
					n1PathActive);
			activeNeighbours.addAll(newNeighbours);
			
			firstVisit = false;
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
