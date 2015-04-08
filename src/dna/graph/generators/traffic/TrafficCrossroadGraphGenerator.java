package dna.graph.generators.traffic;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double3dWeight;
import dna.util.parameters.Parameter;

public class TrafficCrossroadGraphGenerator extends GraphGenerator{

	private DB db;
	
	// Allgemeine Parameter
	private TrafficModi modus;
	private DateTime initDateTime;
	private int[] nodesFilter;
	
	// Continuous
	private int stepsize;
	
	// Daytime-Range Aggregation
	private int timeRange;
	
	// Simulation
	private TrafficUpdate trafficUpdate;
	
	
	public TrafficCrossroadGraphGenerator(String name, GraphDataStructure gds, DB db,long timeStampInit,TrafficModi modus, DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate, int[] nodesFilter) {
		this(name, null, gds,timeStampInit, 0, 0,db,modus,initDateTime,stepsize,timeRange,trafficupdate,nodesFilter);
	}
	
	public TrafficCrossroadGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit,DB db,TrafficModi modus,DateTime initDateTime,int stepsize,int timeRange,TrafficUpdate trafficUpdate, int[] nodesFilter) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
		this.db= db;
		this.modus=modus;
		this.initDateTime=initDateTime;
		this.stepsize = stepsize;
		this.timeRange = timeRange;
		this.trafficUpdate = trafficUpdate;
		this.nodesFilter = nodesFilter;
	}
	
	public TrafficCrossroadGraphGenerator(TrafficConfig tc, GraphDataStructure gds, DB db, long  timeStampInit){
		super(tc.getGraphName(), null, gds, timeStampInit,0,0);
		this.db = db;
		this.modus = tc.getModus();
		this.initDateTime = tc.getInitDateTime();
		this.stepsize = tc.getStepSize();
		this.timeRange = tc.getTimeRange();
		this.trafficUpdate = tc.getTrafficUpdate();
		this.nodesFilter = tc.getNodesFilter();
	}

	@Override
	public Graph generate() {
		
		Graph g = this.newGraphInstance();
		List<INode> nodes = null;
		HashMap<EdgeContainer,Edge> disabledEdges = new HashMap<>();
		
		// Lade abstrakte Knoten gemäß dem NodesFilter
		nodes = db.getCrossroadsForDNA(nodesFilter);
		
		CrossroadWeight crossroadWeight = null;
		Node currentNode = null;
		DirectedWeightedNode currentWeighted = null;
		
		// Berechne das Gewicht des abstrakten Knoten gemäß definiertem Modus
		for (int i = 0; i < nodes.size(); i++) {
			
			currentNode = (Node) nodes.get(i);
			if(currentNode instanceof DirectedWeightedNode)
				currentWeighted = (DirectedWeightedNode) currentNode;
			else{
				continue;
			}
			
			switch (modus) {
			
			case Continuous:
				crossroadWeight = db.getCrossroadWeight(currentWeighted.getIndex(),initDateTime,initDateTime.plusMinutes(stepsize),timestampInit);
				break;
			
			case DayTimeRange: case Aggregation:
				crossroadWeight = db.getCrossroadWeight(currentWeighted.getIndex(),initDateTime.minusMinutes(timeRange),initDateTime.plusMinutes(timeRange),timestampInit);
				break;	
			
			case Simulation:
				crossroadWeight = db.getCrossroadWeightStaticInit(currentWeighted.getIndex(),initDateTime,initDateTime.plusMinutes(1),timestampInit,trafficUpdate);
				break;
			
			default:
				System.out.println("error - Modus nicht definiert");
				break;
			
			}
			
			double[] weight = crossroadWeight.getWeight();

			if(modus == TrafficModi.DayTimeRange)
				db.setMaximalWeightsCrossroadImproved(currentWeighted.getIndex(), weight[0], weight[1], initDateTime, timeRange);
			
			currentWeighted.setWeight(new Double3dWeight(weight[0],weight[1],weight[2]));
			
			g.addNode(currentWeighted);
			
			// Entferne die ueberlasteten Kanten
			EdgeContainer ec = null;
			for (Integer wayId : crossroadWeight.getOverladedEdges().keySet()) {
				List<int[]> edgesToRemove = db.getFromWays(currentWeighted.getIndex(), wayId);
				if(edgesToRemove != null){
					for (int[] way : edgesToRemove) {
						ec = new EdgeContainer(way[0], currentNode.getIndex());
						disabledEdges.put(ec, null);
					}
				}
			}
		}
		
		// Kanten

		List<EdgeContainer> connection = db.getCrossroadConnectionForDNA(nodesFilter);
		EdgeContainer current = null;
		
		Node fromNode;
		Node toNode;
		Edge e;
		for (int i = 0; i < connection.size(); i++) {
			
			current = connection.get(i);
			fromNode = g.getNode(current.getFrom());
			toNode = g.getNode(current.getTo());
			
			if(fromNode!= null && toNode!=null)
				e = gds.newEdgeInstance(fromNode, toNode);
			else
				continue;
			
			if(disabledEdges.containsKey(current)){
				disabledEdges.put(current, e);
			}
			else{
				e.connectToNodes();
				g.addEdge(e);
			}
			
		}
		
		// Speichere die deaktiverten Kanten für den Batch in die DB-Klasse
		db.setDisabledEdges(disabledEdges);
        return g;
	}

}
