package dna.updates.generators.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.generators.traffic.CrossroadWeight;

public class CrossroadWeightList {
	int crossroadID;
	String crossroadName;
	double threshold;
	List<CrossroadWeight> objects;
	List<Double> weights;
	CrossroadWeight sum;
	
	public CrossroadWeightList(int index, String crossroadName, double threshold){
		this.crossroadID=index;
		this.crossroadName=crossroadName;
		this.threshold = threshold;
		this.objects = new ArrayList<>();
		this.weights = new ArrayList<>();
	}
	
	public boolean add(CrossroadWeight crw){
		if(crw.crossroadID != crossroadID || !crw.getCrossroadName().equals(crossroadName) )
			return false;
		else{
			if(sum==null){
				sum = new CrossroadWeight(crossroadID, crossroadName, threshold);
				for (Map.Entry<Integer,double[]> entry : crw.getWayWeights().entrySet()) {
					sum.addWeightWay(entry.getKey(), entry.getValue());
					sum.setMaxWeightWay(entry.getKey(), crw.getMaxWeightWay(entry.getKey()));
				}
				sum.setMaxWeight(crw.getMaxWeight());
			}
			else{
				sum.addWeights(crw.inputWayWeights);
			}
			objects.add(crw);
			weights.add(crw.getWeight()[2]);
			return true;
		}
	}
	
	public CrossroadWeight getAverage(){
		sum.divWays(objects.size());
		return sum;
	}
}
