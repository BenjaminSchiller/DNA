package dna.metrics.patternEnum.patterncounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import dna.graph.Graph;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.utils.GraphUtils;

public class KavoshInitializer implements IPatternCounterInitializer {
	private HashSet<INode> visited = new HashSet<>();
	private List<INode> actPath = new ArrayList<INode>();
	private INode actNode;
	private Collection<SmallGraph> foundGraphs = new ArrayList<SmallGraph>();
	private int patternSize;
	
	@Override
	public void initialize(Graph graph, IPatternCounter motifCounter, int patternSize) {
		this.patternSize = patternSize;
		
		@SuppressWarnings("unchecked")
		Iterable<INode> nodes = (Iterable<INode>)(Object)graph.getNodes();
		for (INode node : nodes) {
			getSubgraphsForNode(node);
			
			for (SmallGraph g : foundGraphs) {
				motifCounter.incrementCounterFor(g);
			}
		}
	}

	public void getSubgraphsForNode(INode actNode) {
		this.actNode = actNode;
		visited.clear();
		actPath.clear();
		foundGraphs.clear();
		
		visited.add(actNode);
		actPath.add(actNode);
		
		List<List<INode>> selection = new ArrayList<>(patternSize);
		for (int i = 0; i < patternSize; i++) {
			selection.add(new ArrayList<INode>());
		}
		selection.set(0, Arrays.asList(actNode));
		enumerateVertex(selection, patternSize - 1, 1);
	}
	
	private void enumerateVertex(List<List<INode>> selection, int remainder, int depth) {
		
		if (remainder == 0) {
			return;
		}
		
		List<INode> valList = validate(selection.get(depth-1));
		int n = Math.min(valList.size(), remainder);
		
		List<INode> actComp = new ArrayList<>();
		for (int k = 1; k <= n; k++) {
			Iterator<List<INode>> iter = comp(valList, k).iterator();
			if (iter.hasNext()) {
				actComp = iter.next();
			}
			
			addToActPath(actComp);
			
			boolean  hasNext = false;
			do {
				selection.set(depth, actComp);
				enumerateVertex(selection, remainder - k, depth + 1);
				
				actPath.removeAll(actComp);
				
				hasNext = iter.hasNext();
				if (hasNext) {
					actComp = iter.next();
					addToActPath(actComp);
				}
			} while (hasNext);
		}
		
		for (INode v : valList) {
			visited.remove(v);
		}
	}
	
	private List<INode> validate(Collection<INode> parents) {
		List<INode> valList = new ArrayList<>();
		
		for (INode v : parents) {
			Collection<INode> neighbors = GraphUtils.getNeighboursOfNode(v);
			for (INode w : neighbors) {
				if (actNode.getIndex() < w.getIndex()
						&& !visited.contains(w)) {
					visited.add(w);
					valList.add(w);
				}
			}
		}
		
		return valList;
	}
	
	private List<List<INode>> comp(List<INode> nodes, int k) {
		List<List<INode>> returnList = new ArrayList<>();
		
		if (k == 1) {
			for (INode node : nodes) {
				returnList.add(Arrays.asList(node));
			}
			return returnList;
		}
		
		for (int i = 0; i < nodes.size() - k + 1; i++) {
			List<List<INode>> otherElements = comp(nodes.subList(i + 1, nodes.size()), k - 1);
			for (Collection<INode> otherElem : otherElements) {
				List<INode> tmp = new ArrayList<>();
				tmp.add(nodes.get(i));
				tmp.addAll(otherElem);
				returnList.add(tmp);
			}
		}
		
		return returnList;
	}
	
	private void addToActPath(Collection<INode> nodes) {
		actPath.addAll(nodes);
		
		if (actPath.size() == patternSize) {
			SmallGraph newGraph = genInducedSubgraph(actPath);
			foundGraphs.add(newGraph);
		}
	}
	
	private SmallGraph genInducedSubgraph(List<INode> nodes) {
		SmallGraph graph = new SmallGraph(nodes, new ArrayList<IEdge>());
		
		for (INode node : nodes) {
			Iterable<IEdge> edges = GraphUtils.getEdgesForNode(node);
			for (IEdge edge : edges) {
				if (edge.getN1().equals(node)
						&& nodes.contains(edge.getN2())) {
					graph.getEdges().add(edge);
				}
			}
		}
		return graph;
	}
}
