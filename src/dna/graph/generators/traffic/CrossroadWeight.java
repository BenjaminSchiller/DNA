package dna.graph.generators.traffic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossroadWeight {
	public int crossroadID;
	public String crossroadName;
	public HashMap<Integer,double[]> inputWayWeights;
	public HashMap<Integer,double[]> inputWayMaxWeights;
	public double maxCount;
	public double maxLoad;
	private double threshold;
	private int timestamp;
	
	public CrossroadWeight(int crossroadID,String crossroadName, double treshold){
		this.crossroadID = crossroadID;
		this.crossroadName = crossroadName;
		inputWayWeights = new HashMap<>();
		inputWayMaxWeights = new HashMap<>();
		this.threshold = treshold;
		this.timestamp=0;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public void addWeightWay(int osmWayID, double[] weights) {
		inputWayWeights.put(osmWayID, weights);
	}
	
	public void setMaxWeightWay(int osmWayID, double[] maxWeights) {
		inputWayMaxWeights.put(osmWayID, maxWeights);
	}
	
	public double[] getMaxWeightWay(int osmWayID) {
		return inputWayMaxWeights.get(osmWayID);
	}
	
	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}
	
	public int getTimestamp(){
		return timestamp;
	}
	
	public void setMaxWeight(double[] maxWeights) {
		this.maxCount=maxWeights[0];
		this.maxLoad=maxWeights[1];
	}
	public double[] getMaxWeight() {
		return new double[] {maxCount,maxLoad};
	}
	
	public void resetInputWayWeight(double count, double load){
		double numOfInputWays = inputWayWeights.keySet().size();
		double count_value = count/numOfInputWays;
		double load_value = load/numOfInputWays;
		for (Map.Entry<Integer, double[]> entry : inputWayWeights.entrySet()) {
			entry.setValue(new double[]{count_value,load_value,(count_value/inputWayMaxWeights.get(entry.getKey())[0])*100});
		}
	}
	
	/**
	 * calculates and returns the weight of a crossroad. 
	 * Index 0 - count
	 * Index 1 - load
	 * Index 2 - count/maxcount
	 * @return 3D-double
	 */
	public double[] getWeight(){
		double[] sum = new double[3];
		for (double[] entry : inputWayWeights.values()) {
			sum[0]+=entry[0];
			sum[1]+=entry[1];
		}
		int numOfinputWays = inputWayWeights.size();
		if(numOfinputWays>0){
			sum[1]/=numOfinputWays;
		}
		/*if(maxCount>0)
			sum[2]=(sum[0]/maxCount)*100;
		else
			sum[2]=0;*/
		if(sum[1]>0)
		sum[2] = sum[1];
		return sum;
	}
	
	public HashMap<Integer,double[]> getOverladedEdges() {
		HashMap<Integer, double[]> result = new HashMap<>();
		for (Map.Entry<Integer, double[]> inputWay : inputWayWeights.entrySet()) {
			if(inputWay.getValue()[1] >threshold){
				result.put(inputWay.getKey(), inputWay.getValue());
			}
		}
		return result;
	}
	
	public HashMap<Integer, double[]> getWayWeights(){
		return inputWayWeights;
		
	}
	
	public boolean addWeights(HashMap<Integer,double[]> wayWeights){
		for (Integer keys : wayWeights.keySet()) {
			if(!inputWayWeights.containsKey(keys))
				return false;
		}
		for (Map.Entry<Integer, double[]>  entry : wayWeights.entrySet()) {
			double[] value = inputWayWeights.get(entry.getKey());
			double[] newValue = entry.getValue();
			for (int i = 0; i < value.length; i++) {
				value[i]+=newValue[i];
			}
			inputWayWeights.put(entry.getKey(), value);
		}
		return true;
	}
	
	public void divWays(int divisor){
		if(divisor>0){
			for (Map.Entry<Integer, double[]> entry : inputWayWeights.entrySet()) {
				double[] value = entry.getValue();
				for (int i = 0; i < value.length; i++) {
					value[i] = value[i]/divisor;
				}
				inputWayWeights.put(entry.getKey(), value);
			}
		}
	}
	
}
