package dna.metrics.patternEnum.subgfinder.hub.storedpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.utils.GraphUtils;

/**
 * Manages the stored paths. Can add and remove paths.
 * 
 * @author Bastian Laur
 *
 */
public class RedundantHubExplorer implements IHubExplorer {
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Add Paths
	
	public void addPaths(Collection<Path> paths, StoredPathVertex spv) {
		addPaths(paths, spv, false);
	}
	
	public void addPaths(Collection<Path> paths, StoredPathVertex spv, boolean sorted) {
		List<Path> sortedPaths = new ArrayList<Path>(paths);
		
		if (!sorted) {
			Collections.sort(sortedPaths, new Comparator<Path>() {
				@Override
				public int compare(Path arg0, Path arg1) {
					return arg0.getGraph().getSize() - arg1.getGraph().getSize();
				}
			});
		}
		
		for (Path path : sortedPaths) {
			addPath(path, spv);			
		}
	}
	
	private void addPath(Path path, StoredPathVertex spv) {
		if (path.getGraph().getSize() <= 1
			|| !path.getGraph().getNodes().contains(spv.getVertex())) {
			return;
		}
		
		ArrayList<StoredPathVertex> seenSpv = new ArrayList<>();
		seenSpv.add(spv);
		addPathRec(path, spv, seenSpv);
	}
	
	private void addPathRec(Path path, StoredPathVertex spv, List<StoredPathVertex> seenSpv) {
		Collection<IEdge> expectedEdges = getEdgesBetween(seenSpv, spv.getVertex());
		if (expectedEdges.size() > spv.getEdges().size()) {
			spv.setEdges(expectedEdges);
		}
		
		if (seenSpv.size() >= path.getGraph().getSize()) {
			return;
		}
		
		boolean found = false;
		
		for (StoredPathVertex nextSpv : spv.getNextVertices()) {
			if (path.getGraph().getNodes().contains(nextSpv.getVertex())) {
				seenSpv.add(nextSpv);
				
				addPathRec(path, nextSpv, seenSpv);
				
				seenSpv.remove(nextSpv);
				
				found = true;
			}
		}
		
		if (!found) {
			addRemainingNodeToStoredPath(seenSpv, path.getGraph(), spv);
		}
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Add Path
	
	public void addPath(IEdge actEdge, Path path, StoredPathVertex spv, int maxSize) {
		if (path.getGraph().getSize() <= 1
			|| !path.getGraph().getNodes().contains(spv.getVertex())) {
			return;
		}
		
		ArrayList<StoredPathVertex> seenSpv = new ArrayList<>();
		seenSpv.add(spv);
		addPathRec(actEdge, path, spv, seenSpv, false, maxSize, 1);
	}
	
	private void addPathRec(IEdge actEdge, Path path, StoredPathVertex spv,
			List<StoredPathVertex> foundSpv, boolean foundN1, int maxSize, int actSize) {
		if (actEdge.isConnectedTo((Node) spv.getVertex())) {
			if (!foundN1) {
				foundN1 = true;
			} else {
				if (!spv.getEdges().contains(actEdge)) {
					spv.getEdges().add(actEdge);
				}
			}
		}
		
		if (actSize >= maxSize || foundSpv.size() == path.getGraph().getSize()) {
			return;
		}
		
		boolean foundNext = false;
		
		for (StoredPathVertex nextSpv : spv.getNextVertices()) {
			if (path.getGraph().getNodes().contains(nextSpv.getVertex())) {
				foundSpv.add(nextSpv);
				foundNext = true;
				addPathRec(actEdge, path, nextSpv, foundSpv, foundN1, maxSize, actSize + 1);
				foundSpv.remove(nextSpv);
			}
		}
		
		if (!foundNext) {
			addRemainingNodeToStoredPath(foundSpv, path.getGraph(), spv);
		}
	}
	
	private void addRemainingNodeToStoredPath(List<StoredPathVertex> matchingSpvs,
		SmallGraph path, StoredPathVertex lastSpv) {
		
		INode remainingNode = findRemainingNode(matchingSpvs, path);
		if (remainingNode == null) {
			return;
		}
		
		Collection<IEdge> connectingEdges = getEdgesBetween(matchingSpvs, remainingNode);
		StoredPathVertex newSpv = new StoredPathVertex(remainingNode, connectingEdges);
		lastSpv.getNextVertices().add(newSpv);
	}
	
	private static INode findRemainingNode(List<StoredPathVertex> matchingSpvs, SmallGraph path) {
		for (INode actNode : path.getNodes()) {
			boolean found = false;
			
			for (StoredPathVertex actSpv : matchingSpvs) {
				if (actSpv.getVertex().equals(actNode)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				return actNode;
			}
		}
		
		return null;
	}
	
	private Collection<IEdge> getEdgesBetween(Collection<StoredPathVertex> storedPathList,
			INode node){
		ArrayList<IEdge> returnList = new ArrayList<IEdge>();
		
		Iterable<IEdge> edges = GraphUtils.getEdgesForNode(node);
		for(StoredPathVertex storedPath : storedPathList) {
			if (storedPath.getVertex().equals(node)) {
				continue;
			}
			
			for(IEdge edge : edges) {
				if(edge.isConnectedTo((Node)storedPath.getVertex())) {
					returnList.add(edge);
				}
			}
		}
		
		return returnList;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Remove Path
	
	public void removePath(IEdge edgeToRemove, StoredPathVertex spv) {
		removePathRec(edgeToRemove, spv);
	}
	
	private void removePathRec(IEdge edgeToRemove, StoredPathVertex spv) {
		Iterator<StoredPathVertex> iter = spv.getNextVertices().iterator();
		
		while (iter.hasNext()) {
			StoredPathVertex nextSpv = iter.next();
			if (nextSpv.getEdges().contains(edgeToRemove)) {
				nextSpv.getEdges().remove(edgeToRemove);
				
				//if (!nextSpv.isConnectedTo(spv)) {
				if (nextSpv.getEdges().isEmpty()) {
					iter.remove();
					continue;
				}
			}
			
			removePathRec(edgeToRemove, nextSpv);
		}
	}
}
