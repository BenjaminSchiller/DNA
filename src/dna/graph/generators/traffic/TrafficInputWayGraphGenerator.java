package dna.graph.generators.traffic;

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
import dna.util.parameters.Parameter;

public class TrafficInputWayGraphGenerator extends GraphGenerator{

	private DB db;
	
	// Allgemeine Parameter
	private TrafficModi modus;
	private DateTime initDateTime;
	private int[] nodesFilter;
	
	// Continuous-Modus
	private int stepSize;
	
	// DayTimeRange / Aggregation
	private int timeRange;
	private double treshold;
	
	// Simulation
	private TrafficUpdate trafficUpdate;
	
	
	private HashMap<Integer, HashMap<EdgeContainer,Edge>> disabledEdges = new HashMap<>();
	
	
	public TrafficInputWayGraphGenerator(String name, GraphDataStructure gds, DB db, long timeStamp, TrafficModi modus, DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate,double treshold, int[] nodesFilter) {
		this(name, null, gds,timeStamp, 0, 0,db, modus,initDateTime,stepsize,timeRange,trafficupdate,treshold, nodesFilter);
	}
	
	public TrafficInputWayGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit,DB db,TrafficModi modus, DateTime initDateTime, int stepsize,int timeRange,TrafficUpdate trafficupdate,double treshold, int[] nodesFilter) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
		this.db= db;
		this.modus = modus;
		this.initDateTime = initDateTime;
		this.stepSize = stepsize;
		this.timeRange = timeRange;
		this.trafficUpdate = trafficupdate;
		this.treshold = treshold;
		this.nodesFilter = nodesFilter;
	}
	
	/**
	 * Erstellt einen GraphGenerator für das WayModel für die übergebene TrafficConfig
	 * @param tc
	 * @param gds
	 * @param db
	 * @param timeStampInit
	 */
	public TrafficInputWayGraphGenerator(TrafficConfig tc, GraphDataStructure gds, DB db, long timeStampInit){
		super(tc.getGraphName(), null, gds, timeStampInit,0,0);
		this.db = db;
		this.modus = tc.getModus();
		this.initDateTime = tc.getInitDateTime();
		this.stepSize = tc.getStepSize();
		this.timeRange = tc.getTimeRange();
		this.trafficUpdate = tc.getTrafficUpdate();
		this.treshold = tc.getTreshold();
		this.nodesFilter = tc.getNodesFilter();
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();
		
		
		List<INode> nodes = null;
		Set<Integer> overloaded = new HashSet<>();
		
		
		// Knoten
		
		// Lade abstrakte Knoten gemäß NodesFilter
		nodes = db.getInputWaysForDNA(nodesFilter);

		Node currentNode = null;
		DirectedWeightedNode currentWeighted = null;
		double[] weight = null;
		
		// Berechne für jeden abstrakten Knoten das Gewicht gemäß des ausgewählten Modus
		for (int i = 0; i < nodes.size(); i++) {
			currentNode = (Node) nodes.get(i);
			
			if(currentNode instanceof DirectedWeightedNode)
				currentWeighted = (DirectedWeightedNode) currentNode;
			else{
				continue;
			}
			
			switch (modus) {
			case Continuous:
				weight = db.getInputWayWeight(currentWeighted.getIndex(), initDateTime, initDateTime.plusMinutes(stepSize));
				break;

			case DayTimeRange: case Aggregation:
				weight = db.getInputWayWeight(currentWeighted.getIndex(),initDateTime.minusMinutes(timeRange),initDateTime.plusMinutes(timeRange));
				break;	
				
			case Simulation:
				weight = db.getInputWayWeightStaticInit(currentWeighted.getIndex(),trafficUpdate);
				break;
				
			default:
				System.out.println("error - Modus nicht definiert @ TrafficInputwayGraphGenerator");
				break;
				
			}
			
			// Knoten ist überlastet
			if(weight[2] > treshold){
				overloaded.add(currentWeighted.getIndex());
			}
			
			currentWeighted.setWeight(new Double3dWeight(weight[0],weight[1],weight[2]));
			g.addNode(currentWeighted);

		}
		
		// Kanten
		
		// Lade die Kanten für die durch NodesFilter definierten Knoten
		List<EdgeContainer> connection = db.getInputWaysConnectionForDNA(nodesFilter);
		
		DirectedWeightedNode fromNode = null;
		DirectedWeightedNode toNode = null;
		Edge e = null;
		EdgeContainer ec = null;
		
		// Prüfe für jede Kante, ob sie gültig oder überlastet ist, sortiere dementsprechend ein
		for (int i = 0; i < connection.size(); i++) {
			ec = connection.get(i);
			fromNode = (DirectedWeightedNode) g.getNode(ec.getFrom());
			toNode = (DirectedWeightedNode) g.getNode(ec.getTo());
			
			if(fromNode != null && toNode != null)
				e = gds.newEdgeInstance(fromNode,toNode);
			else
				continue;
			
			// einer der Enknoten ist ueberlastet
			if(overloaded.contains(fromNode.getIndex()) || overloaded.contains((toNode.getIndex()))){
				addEdge(fromNode.getIndex(),ec,e);
				addEdge(toNode.getIndex(),ec,e);
			}
			// Gültige Kante
			else{
				g.addEdge(e);
				e.connectToNodes();
			}
		}
		// Zwischenspeichern der überlasteten Kanten in der DB
		db.setDisabledEdgesInputWay(disabledEdges);
		return g;
	}
	
	/**
	 * fügt eine Kante in den Zwischenspeicher ein, der über die DB-Klasse an den Batch übergeben wird.
	 * @param index Knotenindex des Endknoten
	 * @param ec ContainerKlasse der Kante
	 * @param e Kante
	 */
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
