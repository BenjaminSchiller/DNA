package dna.graph.generators.traffic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossroadWeight {
	public int crossroadID;
	private String crossroadName;
	public HashMap<Integer,double[]> inputWayWeights;
	private HashMap<Integer,double[]> inputWayMaxWeights;
	private double maxCount;
	private double maxLoad;
	private double threshold;
	private int timestamp;
	
	public CrossroadWeight(int crossroadID,String crossroadName, double treshold){
		this.crossroadID = crossroadID;
		this.crossroadName = crossroadName;
		this.inputWayWeights = new HashMap<>();
		this.inputWayMaxWeights = new HashMap<>();
		this.threshold = treshold;
		this.timestamp=0;
	}
	/**
	 * liefert den Schwellwert für die Überlastung
	 * @return
	 */
	public double getThreshold(){
		return threshold;
	}
	/**
	 * liefert den Namen der Kreuzung im Format "A ..."
	 * @return
	 */
	public String getCrossroadName(){
		return crossroadName;
	}
	/**
	 * fügt Gewichte zu einem Einfahrtsweg hinzu
	 * @param osmWayID, OSM-ID des Einfahrtsweges
	 * @param weights, Gewichte des Einfahrtsweges
	 */
	public void addWeightWay(int osmWayID, double[] weights) {
		inputWayWeights.put(osmWayID, weights);
	}
	/**
	 * fügt die maximalen Gewichte des Einfahrtsweges hinzu
	 * @param osmWayID - OSM-ID des Einfahrtsweges
	 * @param maxWeights
	 */
	public void setMaxWeightWay(int osmWayID, double[] maxWeights) {
		inputWayMaxWeights.put(osmWayID, maxWeights);
	}
	
	/**
	 * liefert die maximalen Gewichte des Einfahrtsweges
	 * @param osmWayID, OSM-ID des Einfahrtsweges
	 * @return
	 */
	public double[] getMaxWeightWay(int osmWayID) {
		return inputWayMaxWeights.get(osmWayID);
	}
	
	/**
	 * setzt den Zeitstempel, für den das letzte Gewicht vorliegt
	 * @param timestamp
	 */
	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}
	
	/**
	 * liefert den Zeitstempel, für den das letzte Gewicht vorliegt
	 * @return
	 */
	public int getTimestamp(){
		return timestamp;
	}
	
	/**
	 * setzt die maximalen Gewichte für die gesamte Kreuzung
	 * @param maxWeights
	 */
	public void setMaxWeight(double[] maxWeights) {
		this.maxCount=maxWeights[0];
		this.maxLoad=maxWeights[1];
	}
	
	/**
	 * liefert die maximalen Werte für die gesamte Kreuzung
	 * @return
	 */
	public double[] getMaxWeight() {
		return new double[] {maxCount,maxLoad};
	}
	
	/**
	 * verteilt das Knotengewicht gleichmäßig auf die Einfahrtswege, wird bei der Simulation verwendet
	 * @param count
	 * @param load
	 */
	public void resetInputWayWeight(double count, double load){
		double numOfInputWays = inputWayWeights.keySet().size();
		double count_value = count/numOfInputWays;
		double load_value = load/numOfInputWays;
		for (Map.Entry<Integer, double[]> entry : inputWayWeights.entrySet()) {
			entry.setValue(new double[]{count_value,load_value,(count_value/inputWayMaxWeights.get(entry.getKey())[0])*100});
		}
	}
	
	/**
	 * berechnet das Gewicht des Kreuzungsknotens
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
		
		// Normierte Werte
		if(maxCount>0)
			sum[2]=(sum[0]/maxCount)*100;
		else
			sum[2]=0;
		
		return sum;
	}
	
	/**
	 * liefert alle Einfahrtswege, deren Gewicht den Schwellwert überschritten hat
	 * @return
	 */
	public HashMap<Integer,double[]> getOverladedEdges() {
		HashMap<Integer, double[]> result = new HashMap<>();
		for (Map.Entry<Integer, double[]> inputWay : inputWayWeights.entrySet()) {
			if(inputWay.getValue()[2] >threshold){
				result.put(inputWay.getKey(), inputWay.getValue());
			}
		}
		return result;
	}
	
	/**
	 * liefert alle Einfahrtswege mit ihren Gewichten
	 * @return
	 */
	public HashMap<Integer, double[]> getWayWeights(){
		return inputWayWeights;
	}
	
	/**
	 * addiert die übergebenen Gewichte auf die Gewichte der Einfahrtswege
	 * @param wayWeights
	 * @return
	 */
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
	
	/**
	 * bildet den Durchschnitt der aufsummierten Gewichte
	 * @param divisor, Anzahl der Batches/Tage über die aggregiert wurde
	 */
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
