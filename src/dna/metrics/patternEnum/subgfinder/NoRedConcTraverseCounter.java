package dna.metrics.patternEnum.subgfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.patterncounter.IPatternCounter;
import dna.metrics.patternEnum.patterncounter.SyncedMotifCounter;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;
import dna.metrics.patternEnum.utils.GraphUtils;


/**
 * Same algorithm as {@link NoRedTraverseCounter} but uses two threads. One to enumerate the 
 * subgraphs and the other to classify them.
 * 
 * @author Bastian Laur
 *
 */
public class NoRedConcTraverseCounter extends TraverseBase implements ITraverseCounter {
	
	private static int maxSize;
	private static INode otherNode;
	private static INode startNode;
	private static List<Collection<Path>> foundSubgraphs;
	private static IEdge actEdge;
	private static Path startPath;
	private static IPatternCounter motifCounter;
	private static EdgeAction edgeAction;
	private static SyncedMotifCounter syncedMotifCounter;
	private static BlockingQueue<Path> foundSyncedPaths;
	
	@Override
	public void countSubgraphsForEdge(IEdge actEdge_, int maxSize_, HubManager hubManager_,
			EdgeAction edgeAction, IPatternCounter motifCounter_) {
		
		if(maxSize_ <= 0) {
			return;
		}
		
		try {
			init(actEdge_, maxSize_, hubManager_, motifCounter_, edgeAction);
			startTraversing();
		
			// stops the motif counter thread
			foundSyncedPaths.put(new Path(new SmallGraph()));
			
			// wait for the motif counter thread to stop
			syncedMotifCounter.getThread().join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Init

	private static void init(IEdge actEdge_, int maxSize_, HubManager hubManager_,
			IPatternCounter motifCounter_, EdgeAction edgeAction_) {
		maxSize = maxSize_;
		actEdge = actEdge_;
		startNode = actEdge_.getN1();
		otherNode = actEdge_.getN2();
		startPath = createStartPath();
		motifCounter = motifCounter_;
		edgeAction = edgeAction_;
		foundSyncedPaths = new LinkedBlockingQueue<Path>();
		syncedMotifCounter = new SyncedMotifCounter(foundSyncedPaths, motifCounter, edgeAction);
		syncedMotifCounter.start();
		
		foundSubgraphs = new ArrayList<>(maxSize);
		for (int i = 0; i < maxSize; i++) {
			foundSubgraphs.add(new ArrayList<Path>(100));
		}
	}
	
	private static void startTraversing() throws InterruptedException {
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
			boolean n1PathActive, Collection<Integer> prevNeighbours) throws InterruptedException{
		
		Path clonedActPath = actPath.shallowClone();
		
		Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
				n1PathActive);
		activeNeighbours.remove(actNode);
		
		if(!actNode.equals(startNode) && !actNode.equals(otherNode)) {
			addNodeWithConnectingEdgesToPath(actNode, clonedActPath, actEdge, n1PathActive);
		}
		
		if(!actNode.equals(startNode) && !actNode.equals(otherNode)
				&& clonedActPath.getGraph().getSize() == maxSize) {
			foundSyncedPaths.put(clonedActPath);
			return;
		}
		
		Collection<INode> newNeighbours = getNeighboursOfNodeWithout(actNode, n1Neighbours,
				n2Neighbours, clonedActPath.getGraph(), actEdge);
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
}
