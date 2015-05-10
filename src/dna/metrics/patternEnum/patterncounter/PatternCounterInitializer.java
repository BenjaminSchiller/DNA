package dna.metrics.patternEnum.patterncounter;

import java.util.Collection;
import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.subgfinder.ITraverser;
import dna.metrics.patternEnum.subgfinder.ITraverser.EdgeAction;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;

/**
 * Initializes the given {@link IPatternCounter} with the initial pattern counts.
 * 
 * @author Bastian Laur
 *
 */
public class PatternCounterInitializer {
	private static HashMap<Integer, INode> addedNodes;
		
	public static void initializePatternCounter(Graph g, IPatternCounter patternCounter,
			HubManager hubManager, int patternSize, ITraverser traverser) {
		addedNodes = new HashMap<>();
		
		SmallGraph growingGraph = new SmallGraph();
		
		@SuppressWarnings("unchecked")
		Iterable<IEdge> edges = (Iterable<IEdge>)(Object)g.getEdges();
		for(IEdge edge : edges) {
			int n1Index = edge.getN1().getIndex();
			INode newNode1 = addedNodes.get(n1Index);
			if(newNode1 == null) {
				newNode1 = g.getGraphDatastructures().newNodeInstance(n1Index);
				addedNodes.put(n1Index, newNode1);
				growingGraph.getNodes().add(newNode1);
			}
			
			int n2Index = edge.getN2().getIndex();
			INode newNode2 = addedNodes.get(n2Index);
			if(newNode2 == null) {
				newNode2 = g.getGraphDatastructures().newNodeInstance(n2Index);
				addedNodes.put(n2Index, newNode2);
				growingGraph.getNodes().add(newNode2);
			}
			
			IEdge newEdge = g.getGraphDatastructures().newEdgeInstance((Node)newNode1, (Node)newNode2);
			newEdge.connectToNodes();
			growingGraph.getEdges().add(newEdge);
			
			Collection<Path> foundSubgraphs = traverser.getSubgraphsForEdge(newEdge, patternSize,
					hubManager, EdgeAction.added);
				
			for(Path fsg : foundSubgraphs) {
				if(fsg.hasChanged() && fsg.getPrevGraph() != null) {
					patternCounter.decrementCounterFor(fsg.getPrevGraph());
				}
				patternCounter.incrementCounterFor(fsg.getGraph());
			}
		}
	}
}
