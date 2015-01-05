package dna.updates.generators.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.mockito.internal.util.collections.ListUtil;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.Edge;
import dna.graph.generators.traffic.DB;
import dna.graph.generators.traffic.EdgeContainer;
import dna.graph.generators.traffic.TrafficModi;
import dna.graph.generators.traffic.TrafficUpdate;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double3dWeight;
import dna.io.GraphWriter;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;
import dna.util.parameters.ObjectParameter;
import dna.util.parameters.Parameter;

public class TrafficInputWayBatchGenerator extends BatchGenerator{
	DB db;
	DateTime initDateTime;
	int stepSize;
	int step=0;
	private TrafficModi modus;
	private DateTime holidayStart;
	private boolean[] daySelection;
	private HashMap<Integer, HashMap<EdgeContainer, Edge>> disabledEdges = new HashMap<>();
	private HashMap<Integer, HashMap<EdgeContainer,Edge>> newDisabledEdges = new HashMap<>();
	private double treshold;
	private TrafficUpdate trafficUpdate;
	private HashMap<Integer, List<Double>> nodeHistory;
	
	
	public TrafficInputWayBatchGenerator(String name,DB db, DateTime initDateTime, int stepSize, TrafficModi modus, DateTime holidayStart, boolean [] daySelection, double treshold,TrafficUpdate trafficUpdate) {
		super(name, new IntParameter("NA", 0), new IntParameter("NR",
				0), new IntParameter("NW", 10),
				new ObjectParameter("NWS", 0), new IntParameter("EA", 0),
				new IntParameter("ER", 0));
		this.db = db;
		this.initDateTime = initDateTime;
		this.stepSize = stepSize;
		this.modus = modus;
		this.holidayStart = holidayStart;
		this.daySelection = daySelection;
		this.treshold = treshold;
		this.trafficUpdate = trafficUpdate;
		this.nodeHistory = new HashMap<>();
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, 0,
				0, 0);
		if(g.getTimestamp()==0){
			disabledEdges=db.getDisabledEdgesInputWay();
		}
		Set<Edge> toDisable = new HashSet<>();
		GraphWriter.write(g, "InputWayGraph/", "batch"+step+++".txt");
		Iterable<IElement> nodes = g.getNodes();
		DateTime time = null;
		Edge edge = null;
		Integer newKey = null;
		if(modus == TrafficModi.DayTimeRange){
			time = initDateTime;
			time = Helpers.calculateNextDay(time, g.getTimestamp(),daySelection,holidayStart,true);
		}
		newDisabledEdges = new HashMap<>();
		for (IElement currentNode : nodes) {
			DirectedWeightedNode n = (DirectedWeightedNode) currentNode;
			double[] update =null;
			
			//WeightUpdate
			switch (modus) {
			case Continuous:
				update = db.getInputWayWeight(n.getIndex(),initDateTime.plusMinutes((int) (g.getTimestamp()+1)*stepSize),initDateTime.plusMinutes((int) (g.getTimestamp()+2)*stepSize));
				break;
			case DayTimeRange:
				update = db.getInputWayWeight(n.getIndex(),time.minusMinutes(db.timeRange),time.plusMinutes(db.timeRange));
				break;
			case Simulation:
				update = (trafficUpdate.isAffected(n.getIndex()))?  db.getInputWayWeightStaticBatch(n.getIndex(),trafficUpdate) : null;
				break;
			case Aggregation:
				time = initDateTime;
				int index = n.getIndex();
				long start = g.getTimestamp();
				double max = 0;
				for (int i = 0; i < 5; i++) {
					time = Helpers.calculateNextDay(initDateTime, start++, daySelection, holidayStart, false);
					double[] weightOfDay = db.getInputWayWeight(n.getIndex(),time.minusMinutes(db.timeRange),time.plusMinutes(db.timeRange));
					if(nodeHistory.containsKey(index)){
						nodeHistory.get(index).add(weightOfDay[2]);
					}
					else {
						List<Double> weightList = new ArrayList<>();
						weightList.add(weightOfDay[2]);
						nodeHistory.put(index, weightList);
					}	
				}
				double sum = 0;
				List<Double> values = nodeHistory.get(index);
				for (int i = 0; i < values.size(); i++) {
					sum+=values.get(i);
				}
				double value = (values.isEmpty())? 0 : sum/values.size();
				update = new double[]{0,0,value};

				break;
			default:
				System.out.println("error - Modus nicht definiert");
				break;
			}
			
			Double3dWeight oldWeight = (Double3dWeight) n.getWeight();
			if(update!=null){
				Double3dWeight newWeight = new Double3dWeight(update[0],update[1],update[2]);
				if(!oldWeight.equals(newWeight))
					b.add(new NodeWeight((dna.graph.weights.IWeightedNode) currentNode,new Double3dWeight(update[0],update[1],update[2])));
			}
			else{
				update = new double[]{oldWeight.getX(),oldWeight.getY(),oldWeight.getZ()};
			}
			
			EdgeContainer ec = null;
			
			if(update[2] > treshold) {
				newKey = n.getIndex();
				int from;
				int to;
				// Verhindere das Hinzufügen bereits gelöschter Kante
				if(disabledEdges.containsKey(newKey)){
					HashMap<EdgeContainer,Edge> oldMap = disabledEdges.get(newKey);
					for (Entry<EdgeContainer, Edge> entry : oldMap.entrySet()) {
						EdgeContainer key = entry.getKey();
						Edge value = entry.getValue();
						if(key.getFrom()==newKey){
							removeEdge(key.getTo(),key,value);
							addEdge(key.getTo(), key, value);
						}
						else{
							removeEdge(key.getFrom(),key,value);
							addEdge(key.getFrom(),key,value);
						}
					}
					disabledEdges.remove(newKey);
				}
				
				// Lösche neue Kanten
				for (IElement elem : n.getEdges()) {
					edge = (Edge) elem;
					from = edge.getN1Index();
					to = edge.getN2Index();
					ec = new EdgeContainer(from, to);
					removeEdge(from,ec,edge);
					removeEdge(to, ec, edge);
					addEdge(from,ec,edge);
					addEdge(to, ec, edge);
					toDisable.add(edge);
				}
			}
			
			
			
			/*
			//EdgeRemoval
			if(update[2] > treshold){ //Knoten ist überlastet
				key = n.getIndex();
				if(!disabledEdges.containsKey(key)){
					List<Edge> edges= new ArrayList<>();
					for (IElement e : n.getEdges()) {
						if(e instanceof Edge){
							edge = (Edge) e;
							b.add(new EdgeRemoval(edge));
						}	
					}
					newDisabledEdges.put(key, edges);
				}
				else{
					newDisabledEdges.put(key, disabledEdges.get(key));
				}
				disabledEdges.remove(key);
			}*/
		}
		//TODO: War in der Schleife, prüfen
		for (Edge e : toDisable) {
			b.add(new EdgeRemoval(e));
		}
		
		for (Entry<Integer, HashMap<EdgeContainer, Edge>> freeEdges : disabledEdges.entrySet()) {
			int node = freeEdges.getKey();
			for (Entry<EdgeContainer, Edge> elem : freeEdges.getValue().entrySet()) {
				if(node == elem.getKey().getFrom())
					b.add(new EdgeAddition(elem.getValue()));
			}
		}
		
		disabledEdges=newDisabledEdges;
		
		return b;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub	
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void removeEdge(int index, EdgeContainer ec, Edge e){
		if(disabledEdges.containsKey(index)){
			HashMap<EdgeContainer,Edge> oldMap = disabledEdges.get(index);
			oldMap.remove(ec);
			if(oldMap.isEmpty()){
				disabledEdges.remove(index);
			}
		}
	}
	public void addEdge(int index, EdgeContainer ec, Edge e){
		if(newDisabledEdges.containsKey(index)){
			HashMap<EdgeContainer,Edge> oldMap = newDisabledEdges.get(index);
			oldMap.put(ec, e);
		}
		else {
			HashMap<EdgeContainer,Edge> newMap = new HashMap<>();
			newMap.put(ec, e);
			newDisabledEdges.put(index,newMap);
		}
	}
	

}
