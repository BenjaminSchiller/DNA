package dna.updates.generators.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.generators.traffic.DB;
import dna.graph.generators.traffic.EdgeContainer;
import dna.graph.generators.traffic.TrafficConfig;
import dna.graph.generators.traffic.TrafficModi;
import dna.graph.generators.traffic.TrafficUpdate;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.weights.Double3dWeight;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeWeight;
import dna.util.parameters.IntParameter;
import dna.util.parameters.ObjectParameter;

public class TrafficInputWayBatchGenerator extends BatchGenerator{
	
	private DB db;
	
	// Allgemeine Parameter
	private DateTime initDateTime;
	private TrafficModi modus;
	private double treshold;
	
	// Continous
	private int stepSize;
	
	// DayTimeRange / Aggregation
	private DateTime holidayStart;
	private boolean[] daySelection;
	private int timeRange;
	
	// Aggregation
	private int observationDays;
	
	// Simulation
	private TrafficUpdate trafficUpdate;
	
	private HashMap<Integer, List<Double>> nodeHistory = new HashMap<>();
	private HashMap<Integer, HashMap<EdgeContainer, Edge>> disabledEdges = new HashMap<>();
	private HashMap<Integer, HashMap<EdgeContainer,Edge>> newDisabledEdges = new HashMap<>();
	
	
	public TrafficInputWayBatchGenerator(String name,DB db, DateTime initDateTime, int stepSize, TrafficModi modus, DateTime holidayStart, boolean [] daySelection, double treshold, TrafficUpdate trafficUpdate, int timeRange, int observationDays) {
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
		this.timeRange = timeRange;
		this.observationDays = observationDays;
	}
	
	public TrafficInputWayBatchGenerator(DB db, TrafficConfig tc){
		super(tc.getBatchName(), new IntParameter("NA", 0), new IntParameter("NR",
				0), new IntParameter("NW", 0),
				new ObjectParameter("NWS", 0), new IntParameter("EA", 0),
				new IntParameter("ER", 0));
		this.db = db;
		this.initDateTime = tc.getInitDateTime();
		this.stepSize = tc.getStepSize();
		this.modus = tc.getModus();
		this.holidayStart = tc.getHolidayStart();
		this.daySelection = tc.getDaySelection();
		this.treshold = tc.getTreshold();
		this.trafficUpdate = tc.getTrafficUpdate();
		this.timeRange = tc.getTimeRange();
		this.observationDays = tc.getOberservationDays();
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, 0,
				0, 0);
		
		Set<Edge> toDisable = new HashSet<>();
		
		// Knoten
		
		Iterable<IElement> nodes = g.getNodes();
		DateTime time = null;
		
		if(modus == TrafficModi.DayTimeRange){
			time = initDateTime;
			time = Helpers.calculateNextDay(time, g.getTimestamp(),daySelection,holidayStart,true);
		}
		
		// Aktualisiere das Gewicht für jeden Knoten, gemäß dem aktuellen Modus
		for (IElement currentNode : nodes) {
			
			DirectedWeightedNode n = (DirectedWeightedNode) currentNode;
			double[] update =null;
			
			//WeightUpdate
			switch (modus) {
			
			case Continuous:
				update = db.getInputWayWeight(n.getIndex(),initDateTime.plusMinutes((int) (g.getTimestamp()+1)*stepSize),initDateTime.plusMinutes((int) (g.getTimestamp()+2)*stepSize));
				break;
				
			case DayTimeRange:
				update = db.getInputWayWeight(n.getIndex(),time.minusMinutes(timeRange),time.plusMinutes(timeRange));
				break;
				
			case Simulation:
				update = (trafficUpdate.isAffected(n.getIndex()))?  db.getInputWayWeightStaticBatch(n.getIndex(),trafficUpdate) : null;
				break;
				
			case Aggregation:
				time = initDateTime;
				int index = n.getIndex();
				long start = g.getTimestamp();

				// Summiere für alle Beobachtungstage auf
				for (int i = 0; i < observationDays; i++) {
					time = Helpers.calculateNextDay(initDateTime, start++, daySelection, holidayStart, false);
					double[] weightOfDay = db.getInputWayWeight(n.getIndex(),time.minusMinutes(timeRange),time.plusMinutes(timeRange));
					if(nodeHistory.containsKey(index)){
						nodeHistory.get(index).add(weightOfDay[2]);
					}
					else {
						List<Double> weightList = new ArrayList<>();
						weightList.add(weightOfDay[2]);
						nodeHistory.put(index, weightList);
					}	
				}
				
				//Berechne das Knotengewicht aus der Aggregation
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
			
			// Wende das Update des Gewichts auf den Knoten an
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
			Integer newKey = null;
			Edge edge = null;
			
			// Infos aus Initialisierungsschritt holen
			if(g.getTimestamp()==0){
				disabledEdges=db.getDisabledEdgesInputWay();
			}
			newDisabledEdges = new HashMap<>();
			
			// Knoten ist ueberlastet
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
			
		}

		// Füge die Löschoperation in den Batch hinzu
		for (Edge e : toDisable) {
			b.add(new EdgeRemoval(e));
		}
		
		// Füge nicht mehr ueberlastet Knoten wieder dem Graph hinzu
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
	
	/**
	 * Entfernt eine Kante aus dem Zwischenspeicher
	 * @param index
	 * @param ec
	 * @param e
	 */
	private void removeEdge(int index, EdgeContainer ec, Edge e){
		if(disabledEdges.containsKey(index)){
			HashMap<EdgeContainer,Edge> oldMap = disabledEdges.get(index);
			oldMap.remove(ec);
			if(oldMap.isEmpty()){
				disabledEdges.remove(index);
			}
		}
	}
	
	/**
	 * fügte eine Kante zum Zwischenspeicher hinzu
	 * @param index
	 * @param ec
	 * @param e
	 */
	private void addEdge(int index, EdgeContainer ec, Edge e){
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
