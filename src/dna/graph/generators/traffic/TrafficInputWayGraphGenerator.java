package dna.graph.generators.traffic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double3dWeight;
import dna.io.GraphWriter;
import dna.util.parameters.Parameter;

public class TrafficInputWayGraphGenerator extends GraphGenerator{

	List<Node> nodeslist;
	private DB db;
	private DateTime initTime;
	private int modus;
	private DateTime initDateTime;
	private int stepsize;
	private int timeRange;
	private TrafficUpdate trafficUpdate;
	private double treshold;
	private HashMap<Integer, HashMap<EdgeContainer,Edge>> disabledEdges = new HashMap<>();
	
	public TrafficInputWayGraphGenerator(String name, GraphDataStructure gds, DB db, long timeStamp, int modus, DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate,double treshold) {
		this(name, null, gds,timeStamp, 0, 0,db, modus,initDateTime,stepsize,timeRange,trafficupdate,treshold);
	}
	
	public TrafficInputWayGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit,DB db,int modus, DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate,double treshold) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
		this.db= db;
		this.modus = modus;
		this.initDateTime = initDateTime;
		this.stepsize = stepsize;
		this.timeRange = timeRange;
		this.trafficUpdate = trafficupdate;
		this.treshold = treshold;
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();
		
		
		List<INode> nodes = null;
		Set<Integer> overloaded = new HashSet<>();
		
		// Nodes
		nodes = db.getInputWaysForDNA(modus);

		Node currentNode = null;
		DirectedWeightedNode currentWeighted = null;
		double[] weight = null;
		for (int i = 0; i < nodes.size(); i++) {
			currentNode = (Node) nodes.get(i);
			if(currentNode instanceof DirectedWeightedNode)
				currentWeighted = (DirectedWeightedNode) currentNode;
			else{
				continue;
			}
			
			switch (modus) {
			case 0:
				weight = db.getInputWayWeight(currentWeighted.getIndex(), initDateTime, initDateTime.plusMinutes(stepsize));
				break;
			case 1: case 3:
				weight = db.getInputWayWeight(currentWeighted.getIndex(),initDateTime.minusMinutes(timeRange),initDateTime.plusMinutes(timeRange));
				break;	
			case 2:
				weight = db.getInputWayWeightStaticInit(currentWeighted.getIndex(),trafficUpdate);
				break;
			default:
				System.out.println("error - Modus nicht definiert");
				break;
			}
			if(weight[2] > treshold){
				overloaded.add(currentWeighted.getIndex());
			}
			
			currentWeighted.setWeight(new Double3dWeight(weight[0],weight[1],weight[2]));
			g.addNode(currentWeighted);
			if(weight[2]>96)
			System.out.println("ID " +currentWeighted.getIndex() + "\tCount:" + weight[0] +"\tLoad:"+weight[1]+"\t"+"Norm:"+weight[2]);

		}
		
		List<EdgeContainer> connection = db.getInputWaysConnectionForDNA();
		DirectedWeightedNode fromNode = null;
		DirectedWeightedNode toNode = null;
		Edge e = null;
		EdgeContainer ec = null;
		for (int i = 0; i < connection.size(); i++) {
			ec = connection.get(i);
			fromNode = (DirectedWeightedNode) g.getNode(ec.getFrom());
			toNode = (DirectedWeightedNode) g.getNode(ec.getTo());
			e = gds.newEdgeInstance(fromNode,toNode);
			if(overloaded.contains(fromNode.getIndex()) || overloaded.contains((toNode.getIndex()))){
				addEdge(fromNode.getIndex(),ec,e);
				addEdge(toNode.getIndex(),ec,e);
			}
			else{
				g.addEdge(e);
				e.connectToNodes();
			}
		}
		db.setDisabledEdgesInputWay(disabledEdges);
		return g;
	}
	
	public void addEdge(int index,EdgeContainer ec, Edge e){
		if(disabledEdges.containsKey(index))
			disabledEdges.get(index).put(ec,e);
		else{
			HashMap<EdgeContainer,Edge> newEdgeList = new HashMap<>();
			newEdgeList.put(ec,e);
			disabledEdges.put(index,newEdgeList);
		}
	}

}
