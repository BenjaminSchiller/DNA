package dna.metrics.patternEnum.subgfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import dna.graph.Graph;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager.HubChooseAlg;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.IHubExplorer;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.RedundantHubExplorer;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathRoot;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathVertex;
import dna.metrics.patternEnum.utils.GraphUtils;

/**
 * A trivial (and slow) traverser to enumerate all subgraphs. 
 * 
 * @author Bastian Laur
 * 
 */
public class HubTraverser extends TraverseBase implements ITraverser {
	
	private int patternSize;
	private HubManager hubManager;
	private INode otherNode;
	private INode startNode;
	private List<Collection<Path>> foundSubgraphs;
	private IEdge actEdge;
	private Path startPath;
	public HashSet<StoredPathVertex> hubsToUpdate;
	private IHubExplorer hubExplorer;
	public static boolean usedHub = false;
	
	public HubManager getHubManager() {
		return hubManager;
	}
	
	public HubTraverser(Graph g, HubChooseAlg hca, int minHubDegree, double maxHubRate,
			int hubUpdateInterval) {
		hubManager = new HubManager(g, patternSize, hca, minHubDegree, maxHubRate, hubUpdateInterval);
	}
	
	@Override
	public Collection<Path> getSubgraphsForEdge(IEdge actEdge, int maxSize,
			HubManager hubManager, EdgeAction edgeAction){
		
		if(maxSize <= 0) {
			return new ArrayList<Path>();
		}
		
		initVariables(actEdge, maxSize, hubManager);
		
		startTraversing();
		
		updateHubs(edgeAction);
		
		return foundSubgraphs.get(patternSize - 1);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Hubs

	private void updateHubs(EdgeAction edgeAction) {
		if (edgeAction.equals(EdgeAction.added) ) {
			updateHubsWithAddedEdge();
		} else if (edgeAction.equals(EdgeAction.removed)) {
			updateHubsWithRemovedEdge();
		}
	}

	private void updateHubsWithRemovedEdge() {
		for(StoredPathVertex hubToUpdate : hubsToUpdate) {
			hubExplorer.removePath(actEdge, hubToUpdate);
		}
	}

	private void updateHubsWithAddedEdge() {
		for(StoredPathVertex hubToUpdate : hubsToUpdate) {
			for(Collection<Path> fs : foundSubgraphs) {
				for(Path foundSubgraph : fs) {
					if(foundSubgraph.getGraph().getNodes().contains(hubToUpdate.getVertex())) {
						hubExplorer.addPath(actEdge, foundSubgraph, hubToUpdate, patternSize - 1);
					}
				}
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Init

	private void initVariables(IEdge actEdge_, int maxSize_,
			HubManager hubManager_) {
		patternSize = maxSize_;
		hubManager = hubManager_;
		actEdge = actEdge_;
		startNode = actEdge_.getN1();
		otherNode = actEdge_.getN2();
		startPath = createStartPath();
		hubsToUpdate = new HashSet<>();
		hubExplorer = new RedundantHubExplorer();
		
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
		traverseDecision(startNode, startPath, n1Neighbours, n2Neighbours, true, new ArrayList<Integer>());
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
	// Traverse Decision

	private void traverseDecision(INode actNode, Path actPath,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours, boolean n1PathActive,
			Collection<Integer> prevNeighbours){
		Path clonedActPath = actPath.shallowClone();
		
		Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
				n1PathActive);
		activeNeighbours.remove(actNode);
		
		StoredPathRoot spi = hubManager.getStoredPathInfoForNode(actNode);
		if(spi != null){
			usedHub = true;
			hubTraverse(spi, clonedActPath, n1Neighbours, n2Neighbours, n1PathActive, prevNeighbours);
		} else {
			noHubTraverse(actNode, clonedActPath, n1Neighbours, n2Neighbours, n1PathActive,
					prevNeighbours);
		}
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////
// Normal Traverse

	private void noHubTraverse(INode actNode, Path clonedActPath,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive, Collection<Integer> prevNeighbours){
		
		Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
				n1PathActive);
		
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
			traverseDecision(n1Neighbour, clonedActPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					true, new ArrayList<Integer>(prevNeighbours));
		}
		
		for (Iterator<INode> iter = n2Neighbours.iterator(); iter.hasNext();) {
			INode n2Neighbour = iter.next();
			traverseDecision(n2Neighbour, clonedActPath,
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
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Traverse with Hub
	
	private void hubTraverse(StoredPathRoot spi, Path clonedActPath,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive, Collection<Integer> alreadySeen){
		
		HashMap<INode, Collection<IEdge>> edgesToActPath =
				createNeighbourHashMapForActPath(clonedActPath, spi);
		HashSet<INode> connectedToOtherNode =
				createConnectionsToOtherNode(clonedActPath, getOtherNode(n1PathActive));
		
		hubTraverseRec(spi.getStoredPathRoot(), clonedActPath, clonedActPath,
				n1Neighbours, n2Neighbours, n1PathActive, true, edgesToActPath, connectedToOtherNode,
				alreadySeen, new ArrayList<StoredPathVertex>());
	}
	
	private void hubTraverseRec(StoredPathVertex spv, Path actPath, Path prevPath,
			Collection<INode> n1Neighbours, Collection<INode> n2Neighbours,
			boolean n1PathActive, boolean firstRun,
			HashMap<INode,Collection<IEdge>> edgesToPrevPath, HashSet<INode> connectedToOtherNode,
			Collection<Integer> alreadySeen, List<StoredPathVertex> hubTraversedPath){
		
		hubTraversedPath.add(spv);
		
		StoredPathRoot spi = hubManager.getStoredPathInfoForNode(spv.getVertex());
		if (spi != null) {
			hubsToUpdate.add(spi.getStoredPathRoot());
		}
		
		Path clonedPath = actPath.shallowClone();
		
		if (!(firstRun
				&& (spv.getVertex().equals(startNode) || spv.getVertex().equals(otherNode)))) {
			Collection<INode> activeNeighbours = getActiveNeighbours(n1Neighbours, n2Neighbours,
					n1PathActive);
			activeNeighbours.remove(spv.getVertex());
			
			clonedPath.getGraph().getNodes().add(spv.getVertex());
			clonedPath.getGraph().getEdges().addAll(spv.getEdges());
			
			Collection<IEdge> prevPathEdges = edgesToPrevPath.get(spv.getVertex());
			if (prevPathEdges != null) {
				clonedPath.getGraph().getEdges().addAll(prevPathEdges);
				if(pathAlreadyFound(clonedPath, foundSubgraphs)) {
					return;
				}
			}
			
			addToFoundSubgraphs(clonedPath);
			
			if (clonedPath.hasChanged() || connectedToOtherNode.contains(spv.getVertex())) {
				clonedPath.setChanged(true);
				setPrevGraph(clonedPath, actEdge);
			}
			
			if(clonedPath.getGraph().getNodes().size() >= patternSize) {
				return;
			}
		}
		
		for(StoredPathVertex nextSpv : spv.getNextVertices()) {
			if (alreadySeen.contains(nextSpv.getVertex().getIndex())
					|| prevPath.getGraph().getNodes().contains(nextSpv.getVertex())) {
				continue;
			}
			
			alreadySeen.add(nextSpv.getVertex().getIndex());
			hubTraverseRec(nextSpv, clonedPath, prevPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					n1PathActive, false, edgesToPrevPath, connectedToOtherNode,
					new ArrayList<Integer>(alreadySeen), new ArrayList<StoredPathVertex>(hubTraversedPath));
		}
		
		for (Iterator<INode> iter = n1Neighbours.iterator(); iter.hasNext();) {
			INode n1Neighbour = iter.next();
			
			if (alreadySeen.contains(n1Neighbour.getIndex())
					|| prevPath.getGraph().getNodes().contains(n1Neighbour)) {
				continue;
			}
			alreadySeen.add(n1Neighbour.getIndex());
			
			iter.remove();
			traverseDecision(n1Neighbour , clonedPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					true, alreadySeen);
		}
		
		for (Iterator<INode> iter = n2Neighbours.iterator(); iter.hasNext();) {
			INode n2Neighbour = iter.next();
			
			if (alreadySeen.contains(n2Neighbour.getIndex())
					|| prevPath.getGraph().getNodes().contains(n2Neighbour)) {
				continue;
			}
			alreadySeen.add(n2Neighbour.getIndex());
			
			iter.remove();
			traverseDecision(n2Neighbour, clonedPath,
					new ArrayList<INode>(n1Neighbours), new ArrayList<INode>(n2Neighbours),
					false, alreadySeen);
		}
	}
	
	private HashMap<INode, Collection<IEdge>> createNeighbourHashMapForActPath(
			Path clonedActPath, StoredPathRoot spi){
		HashMap<INode, Collection<IEdge>> neighbours = new HashMap<>();
		
		for(INode node : clonedActPath.getGraph().getNodes()){
			if (node.equals(spi.getAssociatedNode())) {
				continue;
			}
			
			Iterable<IEdge> nodeEdges = GraphUtils.getEdgesForNode(node);
			for(IEdge edge : nodeEdges){
				INode diffNode = edge.getDifferingNode((Node)node);
				
				Collection<IEdge> tmpNeighbours = neighbours.get(diffNode);
				if(tmpNeighbours == null){
					tmpNeighbours = new ArrayList<>();
					tmpNeighbours.add(edge);
					neighbours.put(diffNode, tmpNeighbours);
				} else {
					tmpNeighbours.add(edge);
				}
			}
		}
		
		return neighbours;
	}
	
	private HashSet<INode> createConnectionsToOtherNode(Path actPath, INode otherNode){
		HashSet<INode> connectedToOtherNode = new HashSet<>();
		connectedToOtherNode.addAll(GraphUtils.getNeighboursOfNode(otherNode));
		
		return connectedToOtherNode;
	}
	
	private INode getOtherNode(boolean n1PathActive) {
		if(n1PathActive) {
			return otherNode;
		}
		return startNode;
	}
	
	private void addToFoundSubgraphs(Path p) {
		foundSubgraphs.get(p.getGraph().getSize() - 1).add(p);
	}
}
