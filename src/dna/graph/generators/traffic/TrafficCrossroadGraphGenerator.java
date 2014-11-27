package dna.graph.generators.traffic;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.IRandomGenerator;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double3dWeight;
import dna.io.GraphReader;
import dna.io.GraphWriter;
import dna.updates.update.EdgeRemoval;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public class TrafficCrossroadGraphGenerator extends GraphGenerator{

	List<Node> nodeslist;
	private DB db;
	private int modus;
	private DateTime initDateTime;
	private int stepsize;
	private int timeRange;
	private TrafficUpdate trafficUpdate;
	
	public TrafficCrossroadGraphGenerator(String name, GraphDataStructure gds, DB db,long timeStampInit,int modus, DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate) {
		this(name, null, gds,timeStampInit, 0, 0,db,modus,initDateTime,stepsize,timeRange,trafficupdate);
	}
	
	public TrafficCrossroadGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit,DB db,int modus,DateTime initDateTime,int stepsize,int timeRange,TrafficUpdate trafficUpdate) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
		this.db= db;
		this.modus=modus;
		this.initDateTime=initDateTime;
		this.stepsize = stepsize;
		this.timeRange = timeRange;
		this.trafficUpdate = trafficUpdate;
	}

	@Override
	public Graph generate() {
		System.out.println("Generiere den Graph");
		db.getMaximalWeightInputWay();
		db.getMaximalWeightCrossroad();
		Graph g = this.newGraphInstance();
		List<INode> nodes = null;
		HashMap<EdgeContainer,Edge> disabledEdges = new HashMap<>();
		
		// Nodes
		nodes = db.getCrossroadsForDNA(modus);
		CrossroadWeight crossroadWeight = null;
		Node currentNode = null;
		DirectedWeightedNode currentWeighted = null;
		for (int i = 0; i < nodes.size(); i++) {
			
			currentNode = (Node) nodes.get(i);
			if(currentNode instanceof DirectedWeightedNode)
				currentWeighted = (DirectedWeightedNode) currentNode;
			else{
				continue;
			}
			
			switch (modus) {
			case 0:
				crossroadWeight = db.getCrossroadWeight(currentWeighted.getIndex(),initDateTime,initDateTime.plusMinutes(stepsize),0);
				break;
			case 1:case 3:
				crossroadWeight = db.getCrossroadWeight(currentWeighted.getIndex(),initDateTime.minusMinutes(timeRange),initDateTime.plusMinutes(timeRange),0);
				break;	
			case 2:
				crossroadWeight = db.getCrossroadWeightStaticInit(currentWeighted.getIndex(),initDateTime,initDateTime.plusMinutes(1),0,trafficUpdate);
				break;
			default:
				System.out.println("error - Modus nicht definiert");
				break;
			}
			
			double[] weight = crossroadWeight.getWeight();
			System.out.println("ID " +currentWeighted.getIndex() + "\tCount:" + weight[0] +"\tLoad:"+weight[1]+"\t"+"Norm:"+weight[2]);
			db.setMaximalWeightsCrossroadImproved(currentWeighted.getIndex(), weight[0], weight[1], initDateTime,db.timeRange);
			currentWeighted.setWeight(new Double3dWeight(weight[0],weight[1],weight[2]));
			g.addNode(currentWeighted);
			
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
		
		
		
		//Edges
		List<EdgeContainer> connection = db.getCrossroadConnection();
		EdgeContainer current = null;
		for (int i = 0; i < connection.size(); i++) {
			current = connection.get(i);
			Edge e = gds.newEdgeInstance(g.getNode(current.getFrom()), g.getNode(current.getTo()));
			
			if(disabledEdges.containsKey(current)){
				disabledEdges.put(current, e);
			}
			else{
				e.connectToNodes();
				g.addEdge(e);
			}
			
		}
		
		db.setDisabledEdges(disabledEdges);
        return g;
	}

}
