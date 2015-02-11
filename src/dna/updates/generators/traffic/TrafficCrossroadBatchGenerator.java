package dna.updates.generators.traffic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.Edge;
import dna.graph.generators.traffic.CrossroadWeight;
import dna.graph.generators.traffic.DB;
import dna.graph.generators.traffic.EdgeContainer;
import dna.graph.generators.traffic.TrafficConfig;
import dna.graph.generators.traffic.TrafficModi;
import dna.graph.generators.traffic.TrafficUpdate;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double2dWeight;
import dna.graph.weights.Double3dWeight;
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

public class TrafficCrossroadBatchGenerator extends BatchGenerator{
	
	private DB db;
	private DateTime initDateTime;
	private int stepSize;
	private int run=0;
	private int observationDays;
	private TrafficModi modus;
	private DateTime holidayStart;
	private boolean[] daySelection;
	private HashMap<EdgeContainer,Edge> disabledEdges = new HashMap<>();
	private TrafficUpdate trafficUpdate;
	private HashMap<Integer,CrossroadWeightList> nodeHistory;
	private int timeRange;

	public TrafficCrossroadBatchGenerator(String name,DB db, DateTime initDateTime, int stepSize, TrafficModi modus, DateTime holidayStart, boolean [] daySelection,TrafficUpdate trafficUpdate,int timeRange, int observationDays) {
		super(name, new IntParameter("NA", 0), new IntParameter("NR",
				0), new IntParameter("NW", 0),
				new ObjectParameter("NWS", 0), new IntParameter("EA", 0),
				new IntParameter("ER", 0));
		this.db = db;
		this.initDateTime = initDateTime;
		this.stepSize = stepSize;
		this.modus = modus;
		this.holidayStart = holidayStart;
		this.daySelection = daySelection;
		this.trafficUpdate = trafficUpdate;
		this.timeRange = timeRange;
		this.observationDays = observationDays;
	}
	
	public TrafficCrossroadBatchGenerator(DB db, TrafficConfig tc){
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
		this.trafficUpdate = tc.getTrafficUpdate();
		this.timeRange = tc.getTimeRange();
		this.observationDays = tc.getOberservationDays();
	}

	@Override
	public Batch generate(Graph g) {
		GraphWriter.write(g, "CrossroadGraph/", "batch"+run+++".txt");
		
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, 0,
				0, 0);
		
		// Infos aus Initialisierungsschritt holen
		if(g.getTimestamp()==0){
			disabledEdges = db.getDisabledEdges();
		}
		
		HashMap<EdgeContainer, Edge> newDisabled = new HashMap<>();
		Iterable<IElement> nodes = g.getNodes();
		CrossroadWeight crossroadWeight = null;
		DateTime time = null;
		
		
		// Spezialkonfigurationen für bestimmte Modi
		switch (modus) {
		
		case DayTimeRange:
			time = initDateTime;
			time = Helpers.calculateNextDay(time, g.getTimestamp(),daySelection,holidayStart,true);
			break;

		case Aggregation:
			nodeHistory = new HashMap<>();
			break;
			
		default:
			break;
		}
		
		// neuer Timestamp durch Batch-Generierung
		long newTimeStamp = (int) g.getTimestamp() + 1;
		
		// Berechne neues Gewicht für jeden Knoten
		for (IElement currentNode : nodes) {
			DirectedWeightedNode n = (DirectedWeightedNode) currentNode;
			double[] update = null;
			
			// Gewichts-Update
			switch (modus) {
			
			case Continuous:
				crossroadWeight =db.getCrossroadWeight(n.getIndex(), initDateTime.plusMinutes((int) (g.getTimestamp()*stepSize)),initDateTime.plusMinutes((int) (g.getTimestamp()+stepSize)*stepSize),newTimeStamp); 
				break;
				
			case DayTimeRange:
				crossroadWeight = db.getCrossroadWeight(n.getIndex(),time.minusMinutes(timeRange),time.plusMinutes(timeRange),newTimeStamp);
				break;
				
			case Simulation:
				crossroadWeight = db.getCrossroadWeightStaticBatch(n.getIndex(),trafficUpdate);
				break;
				
			case Aggregation:
				//Initialisierung auf Starttag
				time = initDateTime;
				int index = n.getIndex();
				long start = g.getTimestamp();
				
				// Aggregiere für observationDays-viele Tage
				for (int i = 0; i < observationDays; i++) {
					
					// Gehe einen Tag zurück (gemäß daySelection) und berechne den entsprechenden Wert
					time = Helpers.calculateNextDay(initDateTime, start++,daySelection,holidayStart,false);
					CrossroadWeight weightOfDay = db.getCrossroadWeight(n.getIndex(),time.minusMinutes(timeRange*2),time.plusMinutes(timeRange*2),newTimeStamp);
					
					// Sammler für jeden Knoten die Gewichte der einzelnen Tage
					if(nodeHistory.containsKey(index)){
						nodeHistory.get(index).add(weightOfDay);
					}
					else{
						CrossroadWeightList weightList = new CrossroadWeightList(weightOfDay.crossroadID,weightOfDay.getCrossroadName(),weightOfDay.getThreshold());
						weightList.add(weightOfDay);
						nodeHistory.put(index, weightList);
					}
				
				}
				
				// Nach der Aggregation, berechne den Average
				crossroadWeight = nodeHistory.get(index).getAverage();
				break;
				
			default:
				System.out.println("error - Modus nicht definiert");
				break;
			}
			
			update = crossroadWeight.getWeight();
			Double3dWeight oldWeight = (Double3dWeight) n.getWeight();
			Double3dWeight newWeight = new Double3dWeight(update[0],update[1],update[2]);

			db.setMaximalWeightsCrossroadImproved(n.getIndex(), update[0], update[1], time, timeRange);
			if(!oldWeight.equals(newWeight))
				b.add(new NodeWeight((dna.graph.weights.IWeightedNode) currentNode,newWeight));
			
			//Edge-Removal
			Edge disabledEdge = null;
			for (Integer wayId : crossroadWeight.getOverladedEdges().keySet()) {
				List<int[]> edgesToRemove = db.getFromWays(n.getIndex(), wayId);
				if(edgesToRemove != null){
					for (int[] edge : edgesToRemove) {
						disabledEdge = g.getEdge(g.getNode(edge[0]), n);
						EdgeContainer ec = new EdgeContainer(edge[0], n.getIndex());
						if(disabledEdge==null){
							disabledEdge = disabledEdges.remove(ec);
							if(disabledEdge==null){
								for (EdgeContainer e : disabledEdges.keySet()) {
									System.out.println(e);
								}
							}
						}
						else
							b.add(new EdgeRemoval(disabledEdge));
						newDisabled.put(ec, disabledEdge);
					}
				}
			}
		}
		
		//Edge-Addition, falls diese wieder frei sind
		for (Map.Entry<EdgeContainer,Edge> oldDeletedEdge : disabledEdges.entrySet()) {
			b.add(new EdgeAddition(oldDeletedEdge.getValue()));
		}
		
		disabledEdges=newDisabled;
		
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

}
