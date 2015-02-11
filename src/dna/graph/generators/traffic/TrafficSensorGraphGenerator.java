package dna.graph.generators.traffic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.IRandomGenerator;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double3dWeight;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public class TrafficSensorGraphGenerator extends GraphGenerator{

	List<Node> nodeslist;
	private DB db;
	private TrafficModi modus;
	private DateTime initDateTime;
	private int stepsize;
	private int timeRange;
	private TrafficUpdate trafficUpdate;
	private double treshold;
	private HashMap<Integer, HashMap<EdgeContainer,Edge>> disabledEdges = new HashMap<>();
	
	public TrafficSensorGraphGenerator(String name, GraphDataStructure gds, DB db,long timeStamp,TrafficModi modus,DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate,double treshold) {
		this(name, null, gds,timeStamp, 0, 0,db,modus,initDateTime,stepsize,timeRange,trafficupdate,treshold);
	}
	
	public TrafficSensorGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit,DB db, TrafficModi modus,DateTime initDateTime,int stepsize,int timeRange,TrafficUpdate trafficUpdate,double treshold) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
		this.db= db;
		this.modus = modus;
		this.initDateTime=initDateTime;
		this.stepsize = stepsize;
		this.timeRange = timeRange;
		this.trafficUpdate = trafficUpdate;
		this.treshold = treshold;
	}
	
	public TrafficSensorGraphGenerator(TrafficConfig tc, GraphDataStructure gds, DB db, long timeStampInit){
		super(tc.getGraphName(), null, gds, timeStampInit,0,0);
		this.db = db;
		this.modus = tc.getModus();
		this.initDateTime=tc.getInitDateTime();
		this.stepsize = tc.getStepSize();
		this.timeRange = tc.getTimeRange();
		this.trafficUpdate = tc.getTrafficUpdate();
		this.treshold = tc.getTreshold();
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();
		Set<Integer> overloaded = new HashSet<>();
		
		List<INode> nodes = db.getSensorsForDNA();
		double[] weight = null;
		Node currentNode = null;
		DirectedWeightedNode currentWeighted = null;
		switch (modus) {
		case Continuous:
			db.getSensorWeights(initDateTime, initDateTime.plusMinutes(stepsize), 0);
			break;
		case DayTimeRange:
			db.getSensorWeights(initDateTime.minusMinutes(timeRange),initDateTime.plusMinutes(timeRange),0);
			break;

		default:
			break;
		}
		
		// Knoten	
		for (int i = 0; i < nodes.size(); i++) {
			currentNode = (Node) nodes.get(i);
			if(currentNode instanceof DirectedWeightedNode)
				currentWeighted = (DirectedWeightedNode) currentNode;
			else{
				continue;
			}
			switch (modus) {
			case Continuous:
				weight = db.getSensorModelWeight(currentWeighted.getIndex(),initDateTime,initDateTime.plusMinutes(stepsize),0);
				break;
			case DayTimeRange:
				weight = db.getSensorModelWeight(currentWeighted.getIndex(),initDateTime.minusMinutes(timeRange),initDateTime.plusMinutes(timeRange),0);
				break;	
			case Simulation:
				weight = db.getSensorModelWeightStaticInit(currentWeighted.getIndex(),trafficUpdate);
				break;
			default:
				System.out.println("error - Modus nicht definiert");
				break;
			}
			if(weight== null){
				weight = new double[]{0,0,0};
			}
			if(weight[2] > treshold){
				overloaded.add(currentWeighted.getIndex());
			}
			currentWeighted.setWeight(new Double3dWeight(weight[0],weight[1],weight[2]));
			g.addNode(currentWeighted);
		}
		
		// Kanten
		List<EdgeContainer> connection = db.getSensorConnectionForDNA();
		DirectedWeightedNode fromNode;
		DirectedWeightedNode toNode;
		Edge e = null;
		EdgeContainer ec = null;
		for (int i = 0; i < connection.size(); i++) {
			ec = connection.get(i);	
			fromNode = (DirectedWeightedNode) g.getNode(ec.getFrom());
			toNode = (DirectedWeightedNode) g.getNode(ec.getTo());
			try {
				e = gds.newEdgeInstance(fromNode,toNode);
			} catch (Exception e2) {
				System.out.println("Problem: \t" +ec.getFrom()+"\t"+ec.getTo());
			}
			
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
