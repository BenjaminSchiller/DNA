package dna.metrics.patternEnum;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterNR;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeNR;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.datastructures.SmallGraph;
import dna.metrics.patternEnum.patterncounter.DirectedMotifCounter;
import dna.metrics.patternEnum.patterncounter.IPatternCounter;
import dna.metrics.patternEnum.patterncounter.KavoshInitializer;
import dna.metrics.patternEnum.patterncounter.MotifType;
import dna.metrics.patternEnum.patterncounter.UndirectedMotifCounter;
import dna.metrics.patternEnum.subgfinder.ITraverser;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;
import dna.metrics.patternEnum.utils.CanonicalLabelGenerator;
import dna.metrics.patternEnum.utils.GraphTransformer;
import dna.metrics.patternEnum.utils.GraphUtils;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.parameters.Parameter;

public class PatternEnumU extends Metric implements IAfterEA, IBeforeER, IBeforeNR, IAfterNR,
		IAfterNA, IDynamicAlgorithm {
	
	private boolean countPatterns = true;
	private boolean initPatternCounter = true;
	private final boolean writeFoundSubgCount = true;
	private final String foundSubgCountFile = "foundSubgCount.txt";
	
	private enum ListenerEvent {
		initialized,
		nodeAdded,
		nodeRemoved,
		edgeAdded,
		edgeRemoved
	}
	
	private Collection<DynamicMotifsListener> listeners = new ArrayList<>();
	protected ITraverser traverser;
	private HubManager hubManager;
	private IPatternCounter motifCounter;
	private int motifSize;
	
	public HubManager getHubManager() {
		return hubManager;
	}
	
	public int getMotifSize() {
		return motifSize;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructor
	
	public PatternEnumU(int motifSize, boolean directed, ITraverser traverser,
			boolean countPatterns, boolean initPatternCounter) {
		this (PatternEnumU.class.getName());
		
		this.motifSize = motifSize;
		this.countPatterns = countPatterns;
		this.initPatternCounter = initPatternCounter;
		this.traverser = traverser;
		
		if (directed) {
			motifCounter = new DirectedMotifCounter();
		} else {
			motifCounter = new UndirectedMotifCounter();
		}
	}
	
	public PatternEnumU(String name, Parameter... p) {
		super(name, p);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	
	/**
	 * Adds a listener.
	 * 
	 * @param newListener
	 */
	public void addListener(DynamicMotifsListener newListener) {
		listeners.add(newListener);
	}
	
	private void callListenerFunction(ListenerEvent event, IEdge edge, Node node) {
		for (DynamicMotifsListener listener : listeners) {
			switch (event) {
			case initialized : listener.initialized();
			break;
			case nodeAdded : listener.nodeAddedEvent(node);
			break;
			case nodeRemoved : listener.nodeRemovedEvent(node);
			break;
			case edgeAdded : listener.edgeAddedEvent(edge);
			break;
			case edgeRemoved : listener.edgeRemovedEvent(edge);
			break;
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Init
	
	@Override
	public boolean init() {
		return initWithHubs(null);
	}
	
	public boolean initWithHubs(Collection<INode> hubCandidates) {

		if (countPatterns && initPatternCounter) {
			new KavoshInitializer().initialize(getGraph(), motifCounter, motifSize);
		}
		
		hubManager.setGraph(getGraph());
		
		callListenerFunction(ListenerEvent.initialized, null, null);
		
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Add Node
	
	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		callListenerFunction(ListenerEvent.nodeAdded, null, (Node) na.getNode());
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Remove Node 
	
	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		Collection<IEdge> removedEdges = new ArrayList<>();
		
		Iterable<IEdge> iterableEdges = GraphUtils.getEdgesForNode(nr.getNode());
		ArrayList<IEdge> edges = Lists.newArrayList(iterableEdges);
		for (IEdge edge : edges) {
			removedEdges.add(edge);
			GraphUtils.removeEdge(getGraph(), (Edge) edge);
			
			applyBeforeUpdate(new EdgeRemoval(edge));
		}
		
		for(IEdge removedEdge : removedEdges) {
			GraphUtils.addEdge(getGraph(), (Edge) removedEdge);
		}
		
		hubManager.getStoredPathInfos().remove(nr.getNode().getIndex());
		
		return true;
	}
	
	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		callListenerFunction(ListenerEvent.nodeRemoved, null, (Node) nr.getNode());
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Add Edge
	
	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		IEdge addedEdge = ea.getEdge();
		
		Collection<Path> foundSubgraphs = traverser.getSubgraphsForEdge(addedEdge,
				motifSize, hubManager, ITraverser.EdgeAction.added);
		
		int totalPatternCountBefore = 0;
		if (writeFoundSubgCount) {
			totalPatternCountBefore = motifCounter.getTotalMotifCount();
		}
		
		if (countPatterns) {
			for(Path g: foundSubgraphs) {
				if(g.hasChanged()) {
					motifCounter.decrementCounterFor(g.getPrevGraph());
				}
				motifCounter.incrementCounterFor(g.getGraph());
			}
		}
		
		if (writeFoundSubgCount) {
			int totalPatternCountAfter = motifCounter.getTotalMotifCount();
			String text = totalPatternCountBefore + "\t" + totalPatternCountAfter;
			writeFile(text, foundSubgCountFile);
		}
		
		callListenerFunction(ListenerEvent.edgeAdded, addedEdge, null);
		
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Remove Edge
	
	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		IEdge removedEdge = er.getEdge();
		
		Collection<Path> foundSubgraphs = traverser.getSubgraphsForEdge(removedEdge,
				motifSize, hubManager, ITraverser.EdgeAction.removed);
		
		int totalPatternCountBefore = 0;
		if (writeFoundSubgCount) {
			totalPatternCountBefore = motifCounter.getTotalMotifCount();
		}
		
		if (countPatterns) {
			for(Path g: foundSubgraphs) {
				if(g.hasChanged()) {
					motifCounter.incrementCounterFor(g.getPrevGraph());
				}
				motifCounter.decrementCounterFor(g.getGraph());
			}
		}
		
		if (writeFoundSubgCount) {
			int totalPatternCountAfter = motifCounter.getTotalMotifCount();
			String text = totalPatternCountBefore + "\t" + totalPatternCountAfter;
			writeFile(text, foundSubgCountFile);
		}
		
		callListenerFunction(ListenerEvent.edgeRemoved, removedEdge, null);
		
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Values

	@Override
	public Value[] getValues() {
		//return new Value[0];
		
		List<Value> returnList = new ArrayList<>();
		
		try {
			StringBuilder motifIndexOutput = new StringBuilder();
			StringBuilder motifCounterOutput = new StringBuilder();
			
			if(getGraph() == null) {
				return new Value[0];
			}
			
			String graphString = "Graph: "
					+ Iterables.toString(getGraph().nodes)
					+ Iterables.toString(getGraph().edges) + "\n";
			motifIndexOutput.append(graphString);
			motifCounterOutput.append(graphString);
			
			for(MotifType motif : motifCounter.getOrderedMotifs()) {
				SmallGraph sg = GraphTransformer.transformToSmallGraph(motif.getGraph());
				long canLabel = new CanonicalLabelGenerator().genCanonicalLabelFor(sg);
				Integer motifCount = motifCounter.getMotifCounter().get(motif);
				
				motifIndexOutput.append(canLabel).append("\t").append(motif.toString())
					.append(Arrays.toString(motif.getDegreeHash()))
					.append("\n");
				motifCounterOutput
					.append(canLabel).append("\t")
					.append(motif.getGraph()).append("\t")
					.append(motifCount)
					.append("\n");
				
				returnList.add(new Value(Long.toString(canLabel), motifCount));
			}
			
			PrintWriter writer = new PrintWriter(getName() + "_motif_indexes.txt");
			writer.append(motifIndexOutput.toString());
			writer.close();
			
			writer = new PrintWriter(getName() + "_motif_counts.txt");
			writer.append(motifCounterOutput.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return returnList.toArray(new Value[0]);
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[0];
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Compare to other Metrics

	@Override
	public boolean isComparableTo(IMetric m) {
		return false;
	}

	@Override
	public boolean equals(IMetric m) {
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Applicable

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Tools
	
	private void writeFile(String text, String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(text + "\n");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
