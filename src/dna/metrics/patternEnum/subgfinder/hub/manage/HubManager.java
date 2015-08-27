package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import dna.graph.Graph;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.metrics.patternEnum.DynamicMotifsListener;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.RedundantHubExplorer;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathRoot;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathVertex;

public class HubManager implements DynamicMotifsListener {
	private Graph graph;
	private int minHubDegree;
	private int hubRemoveSafetyDistance = 2;
	private int patternSize;
	private IHubAmountChecker amountChecker = new HubAmountCheckerImpl();
	private IHubAddCandidatePicker hubAdder;
	private IHubRemoveCandidatePicker hubRemover;
	private int hubUpdateInterval;
	private int updateCounter;
	private IHubTraverser hubTraverser = new HubInitTraverser();
	
	/** The sum of the degree of all nodes in the graph. Used to calc the average degree. */
	private int degreeSum;
	
	/** Node-Id : StoredPathInfo */
	private HashMap<Integer, StoredPathRoot> storedPathInfos;
	private double maxHubRate;
	
	public enum HubChooseAlg {
		random,
		degree
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Getter - Setter
	
	public HashMap<Integer, StoredPathRoot> getStoredPathInfos() {
		return storedPathInfos;
	}
	
	public StoredPathRoot getStoredPathInfoForNode(INode node){
		return storedPathInfos.get(node.getIndex());
	}
	
	public IHubAmountChecker getAmountChecker() {
		return amountChecker;
	}

	public void setAmountChecker(IHubAmountChecker amountChecker) {
		this.amountChecker = amountChecker;
	}

	public IHubAddCandidatePicker getHubAdder() {
		return hubAdder;
	}

	public void setHubAdder(IHubAddCandidatePicker hubAdder) {
		this.hubAdder = hubAdder;
	}

	public IHubRemoveCandidatePicker getHubRemover() {
		return hubRemover;
	}

	public void setHubRemover(IHubRemoveCandidatePicker hubRemover) {
		this.hubRemover = hubRemover;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructor

	public HubManager(Graph graph, int subgraphSize){
		this(graph , subgraphSize, HubChooseAlg.degree, 0, 1, 1);
	}
	
	public HubManager(Graph graph, int subgraphSize, HubChooseAlg hca, int minHubDegree,
			double maxHubRate, int hubUpdateInterval){
		this.graph = graph;
		this.patternSize = subgraphSize;
		this.minHubDegree = minHubDegree;
		this.maxHubRate = maxHubRate;
		this.hubUpdateInterval = hubUpdateInterval;
		storedPathInfos = new HashMap<>();
		updateCounter = 0;
		
		selectHubChooseAlg(hca);
	}
	
	private void selectHubChooseAlg(HubChooseAlg hca) {
		switch (hca) {
			case degree:
				hubAdder = new HubAddCandidatePickerDegree();
				hubRemover = new HubRemoveCandidatePickerDegree();
				break;
			case random:
				hubAdder = new HubAddCandidatePickerRan();
				hubRemover = new HubRemoveCandidatePickerRan();
				break;
			default:
				hubAdder = new HubAddCandidatePickerDegree();
				hubRemover = new HubRemoveCandidatePickerDegree();
				break;
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Init
	
	public void initializeHubs(){
		Collection<INode> storedPathInfoCandidates = getHubCandidates();
		
		initializeHubs(storedPathInfoCandidates);
	}

	public void initializeHubs(Collection<INode> hubCandidates) {
		for(INode storedPathInfoCandidate : hubCandidates){
			addHub(storedPathInfoCandidate);
		}
	}
	
	private List<INode> getHubCandidates() {
		@SuppressWarnings("unchecked")
		Iterable<INode> nodes = (Iterable<INode>)(Object)graph.getNodes();
		List<INode> nodeList = Lists.newArrayList(nodes);
		
		Collections.sort(nodeList, new Comparator<INode>()
			{
				public int compare(INode n1, INode n2){
					return n2.getDegree() - n1.getDegree();
				}
			});
		
		return nodeList;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Events

	@Override
	public void edgeAddedEvent(IEdge e) {
		updateAverageDegree(2);
		manageHubAmount(false);
	}

	@Override
	public void edgeRemovedEvent(IEdge e) {
		updateAverageDegree(-2);
		manageHubAmount(false);
	}

	@Override
	public void nodeAddedEvent(Node n) {
		manageHubAmount(false);
	}

	@Override
	public void nodeRemovedEvent(Node n) {
		storedPathInfos.remove(n.getIndex());
		manageHubAmount(false);
	}

	@Override
	public void initialized() {
		initDegreeSum();
		manageHubAmount(true);
	}
	
	/**
	 * Tests if new hubs can be added or old hubs should be removed and adds / removes them.
	 */
	private void manageHubAmount(boolean forceUpdate) {
		if (forceUpdate || updateCounter % hubUpdateInterval == 0 && graph.getEdgeCount() > 0) {
			cleanHubs();
			
			int newHubAmount = amountChecker.checkOptimalHubAmount(graph, storedPathInfos.size(),
					patternSize, getAverageDegree(), maxHubRate);
			
			if (newHubAmount > 0) {
				addHubs(newHubAmount);
			} else if (newHubAmount < 0) {
				removeHubs(newHubAmount);
			} else {
				updateHubs();
			}
		}
		updateCounter++;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Remove Hubs
	
	private void removeHubs(int amount) {
		Collection<INode> hubCandidates = hubRemover.getNextRemoveHubCandidate(amount, graph, this,
				minHubDegree);
		
		for(INode hubCandidate : hubCandidates){
			removeHub(hubCandidate.getIndex());
		}
	}
	
	private void removeHub(int index) {
		storedPathInfos.remove(index);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Add Hubs

	public void addHubs(int amount) {
		Collection<INode> hubCandidates = hubAdder.getNextHubAddCandidate(amount, graph, this,
				minHubDegree);
		
		for (INode hubCandidate : hubCandidates) {
			addHub(hubCandidate);
		}
	}

	public void addHub(INode hub) {
		Collection<Path> foundPaths = hubTraverser.getSubgraphsForHub(hub, patternSize-1);
		
		StoredPathVertex storedPathRoot = new StoredPathVertex();
		storedPathRoot.setVertex(hub);
		new RedundantHubExplorer().addPaths(foundPaths, storedPathRoot, true);
		
		storedPathInfos.put( hub.getIndex(),
				new StoredPathRoot(hub, storedPathRoot));
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Clean Hubs
	
	/**
	 * Removes all Hubs with an degree <= ({@link #minHubDegree} - {@link #hubRemoveSafetyDistance}).
	 */
	private void cleanHubs() {
		List<Entry<Integer, StoredPathRoot>> tmpEntrSet =
				new ArrayList<Entry<Integer, StoredPathRoot>>(storedPathInfos.entrySet());
		for (Entry<Integer, StoredPathRoot> spi : tmpEntrSet) {
			if (spi.getValue().getStoredPathRoot().getVertex().getDegree()
					<= (minHubDegree - hubRemoveSafetyDistance)) {
				removeHub(spi.getKey());
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Hubs
	
	private void updateHubs() {
		if (storedPathInfos.isEmpty()) {
			return;
		}
		
		StoredPathRoot lowestSpi = getSpiWithLowestDegree();
		
		@SuppressWarnings("unchecked")
		Iterable<INode> nodes = (Iterable<INode>)(Object)graph.getNodes();
		for (INode node : nodes) {
			if (node.getDegree() > (lowestSpi.getAssociatedNode().getDegree() + hubRemoveSafetyDistance)
					&& !storedPathInfos.containsKey(node.getIndex())) {
				removeHub(lowestSpi.getAssociatedNode().getIndex());
				addHub(node);
				lowestSpi = getSpiWithLowestDegree();
			}
		}
	}
	
	/**
	 * Returns the {@link StoredPathRoot} with the lowest degree.
	 * 
	 * @return
	 */
	private StoredPathRoot getSpiWithLowestDegree() {
		StoredPathRoot lowestSpi = null;
		for (StoredPathRoot spi : storedPathInfos.values()) {
			if (lowestSpi == null
					|| spi.getAssociatedNode().getDegree() < lowestSpi.getAssociatedNode().getDegree()) {
				lowestSpi = spi;
			}
		}
		
		return lowestSpi;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Average Degree
	
	private float getAverageDegree() {
		int nodeCount = graph.getNodeCount();
		if (nodeCount == 0) {
			return 1;
		}
		
		return (float)degreeSum / graph.getNodeCount();
	}
	
	/**
	 * Call this method every time the degree of a node changes.
	 * @param deltaDegree The difference to the new degree. E.g if a new edge is added to the graph
	 * {@code deltaDegree} is {@code +2} because the degree of two nodes has increased by one.
	 */
	private void updateAverageDegree(int deltaDegree) {
		degreeSum += deltaDegree;
	}
	
	/**
	 * Initializes {@link #degreeSum} with the degrees of the actual grpah.
	 */
	private void initDegreeSum() {
		degreeSum = 0;
		
		@SuppressWarnings("unchecked")
		Iterable<INode> nodes = (Iterable<INode>)(Object)graph.getNodes();
		for (INode node : nodes) {
			degreeSum += node.getDegree();
		}
	}
}
