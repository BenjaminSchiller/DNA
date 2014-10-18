package mydna;

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
	DB db;
	DateTime initDateTime;
	int stepSize;
	int run=0;
	private int modus;
	DateTime holidayStart;
	boolean[] daySelection;
	HashMap<EdgeContainer,Edge> disabledEdges = new HashMap<>();
	private TrafficUpdate trafficUpdate;

	public TrafficCrossroadBatchGenerator(String name,DB db, DateTime initDateTime, int stepSize, int modus, DateTime holidayStart, boolean [] daySelection,TrafficUpdate trafficUpdate) {
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
	}

	@Override
	public Batch generate(Graph g) {
		GraphWriter.write(g, "CrossroadGraph/", "batch"+run+++".txt");
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, 0,
				0, 0);
		if(g.getTimestamp()==0){
			disabledEdges = db.getDisabledEdges();
		}
		HashMap<EdgeContainer, Edge> newDisabled = new HashMap<>();
		Iterable<IElement> nodes = g.getNodes();
		CrossroadWeight crossroadWeight = null;
		DateTime time = null;
		if(modus == 1 || modus == 2){
			time = initDateTime;
			time = Helpers.calculateNextDay(time, g.getTimestamp(),daySelection,holidayStart);
		}
		int newTimeStamp = (int) g.getTimestamp() + 1;
		System.out.println("Neuer Batch am : \t" +time);
		for (IElement currentNode : nodes) {
			DirectedWeightedNode n = (DirectedWeightedNode) currentNode;
			double[] update = null;
			
			// Gewichts-Update
			
			if(modus==0){ // Vergleich in Schritten der Größe Stepsize
				crossroadWeight =db.getCrossroadWeight(n.getIndex(), initDateTime.plusMinutes((int) (g.getTimestamp()*stepSize)),initDateTime.plusMinutes((int) (g.getTimestamp()+stepSize)*stepSize),newTimeStamp); 
			}
			else if (modus == 1 ){ // Vergleich für Zeiträume der Länge timeRange an Tagen aus Dayselection
				crossroadWeight = db.getCrossroadWeight(n.getIndex(),time.minusMinutes(db.timeRange),time.plusMinutes(db.timeRange),newTimeStamp);
			}
			else if (modus == 2){ // Statische Verkehrsanalyse
				crossroadWeight = db.getCrossroadWeightStaticBatch(n.getIndex(),trafficUpdate);
			}
			update = crossroadWeight.getWeight();
			Double3dWeight oldWeight = (Double3dWeight) n.getWeight();
			Double3dWeight newWeight = new Double3dWeight(update[0],update[1],update[2]);
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