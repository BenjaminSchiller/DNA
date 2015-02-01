package dna.updates.generators.traffic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.joda.time.DateTime;

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
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Int2dWeight;
import dna.graph.weights.IntWeight;
import dna.io.GraphWriter;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;
import dna.util.Rand;
import dna.util.parameters.IntParameter;
import dna.util.parameters.ObjectParameter;
import dna.util.parameters.Parameter;

public class TrafficSensorBatchGenerator extends BatchGenerator{
	DB db;
	DateTime initDateTime;
	int stepSize;
	int step=0;
	TrafficModi modus;
	private DateTime holidayStart;
	private boolean[] daySelection;
	private double treshold;
	private int timeRange;
	
	private HashMap<Integer, HashMap<EdgeContainer, Edge>> disabledEdges;
	private HashMap<Integer, HashMap<EdgeContainer,Edge>> newDisabledEdges;
	private TrafficUpdate trafficUpdate;
	
	public TrafficSensorBatchGenerator(String name,DB db, DateTime initDateTime, int stepSize, TrafficModi modus, DateTime holidayStart, boolean [] daySelection, double treshold,int timeRange,TrafficUpdate trafficUpdate) {
		super(name, new IntParameter("NA", 0), new IntParameter("NR",
				0), new IntParameter("NW", 0),
				new ObjectParameter("NWS", 0), new IntParameter("EA", 0),
				new IntParameter("ER", 0));
		this.db = db;
		this.initDateTime = initDateTime;
		this.stepSize = stepSize;
		this.modus=modus;
		this.holidayStart=holidayStart;
		this.daySelection = daySelection;
		this.treshold = treshold;
		this.timeRange = timeRange;
		this.trafficUpdate = trafficUpdate;
		disabledEdges=new HashMap<>();
		newDisabledEdges = new HashMap<>();
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, 0,
				0, 0);
		if(g.getTimestamp()==0){
			disabledEdges = db.getDisabledEdgesInputWay();
		}
		GraphWriter.write(g, "SensorGraph/", "batch"+step+++".txt");
		Integer newKey = null;
		int newTimestamp = (int)g.getTimestamp() + 1;
		
		DateTime time = initDateTime;
		time = Helpers.calculateNextDay(time, g.getTimestamp(),daySelection,holidayStart,true);
		DateTime fromTime = null;
		DateTime toTime = null;
		
		Set<Edge> toDisable = new HashSet<>();
		Edge edge = null;
		
		switch (modus) {
		case Continuous:
			fromTime = initDateTime.plusMinutes((int) (g.getTimestamp()+1)*stepSize);
			toTime = initDateTime.plusMinutes((int) (g.getTimestamp()+2)*stepSize);
			break;
		case DayTimeRange:
			time = Helpers.calculateNextDay(time, g.getTimestamp(),daySelection,holidayStart,true);
			fromTime = time.minusMinutes(timeRange);
			toTime = time.plusMinutes(timeRange);
			break;

		default:
			break;
		}
		// Vorabberechnung aller Gewichte der Realen Sensoren
		if(modus == TrafficModi.Continuous || modus == TrafficModi.DayTimeRange){
			db.getSensorWeights(fromTime,toTime, newTimestamp);
		}
		
		double[] update = null;
		for (IElement currentNode : g.getNodes()) {
			
			// Get Node
			DirectedWeightedNode currentWeighted = null;
			if(currentNode instanceof DirectedWeightedNode)
				currentWeighted= (DirectedWeightedNode) currentNode;
			else
				continue;
			
			// Get Weight
			switch (modus) {
			case Continuous:
				update = db.getSensorModelWeight(currentWeighted.getIndex(),fromTime,toTime,newTimestamp);
				break;
			case DayTimeRange:
				update = db.getSensorModelWeight(currentWeighted.getIndex(),fromTime,toTime,newTimestamp);
				break;	
			case Simulation:
				update = trafficUpdate.isAffected(currentWeighted.getIndex()) ? db.getSensorModelWeightStaticBatch(currentWeighted.getIndex(),trafficUpdate) : null;
				break;
			default:
				System.out.println("error - Modus nicht definiert @ TrafficSensorBatchGenerator");
				break;
			}
			Double3dWeight oldWeight = (Double3dWeight) currentWeighted.getWeight();
			if(update!=null){
				Double3dWeight newWeight = new Double3dWeight(update[0],update[1],update[2]);
			
				// Set Weight
				if(!oldWeight.equals(newWeight))
					b.add(new NodeWeight((dna.graph.weights.IWeightedNode) currentNode,newWeight));
			}
			else{
				update = new double[]{oldWeight.getX(),oldWeight.getY(),oldWeight.getZ()};
			}
			
			
			// Edge Removal
			EdgeContainer ec = null;
			if(update[2] > treshold) {
				newKey = currentWeighted.getIndex();
				int from;
				int to;
				// Verhindere das Hinzufügen bereits gelöschter Kante
				if(disabledEdges.containsKey(newKey)){
					HashMap<EdgeContainer,Edge> oldMap = disabledEdges.get(newKey);
					for (Entry<EdgeContainer, Edge> entry : oldMap.entrySet()) {
						EdgeContainer key = entry.getKey();
						Edge value = entry.getValue();
						removeEdge(key.getTo(),key,value);
						addEdge(key.getTo(), key, value);
						removeEdge(key.getFrom(),key,value);
						addEdge(key.getFrom(),key,value);
					}
					disabledEdges.remove(newKey);
				}
				
				// Lösche neue Kanten
				for (IElement elem : currentWeighted.getEdges()) {
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
		}
		
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
