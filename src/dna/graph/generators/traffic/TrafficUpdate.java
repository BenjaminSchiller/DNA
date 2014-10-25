package dna.graph.generators.traffic;

import java.util.List;

public class TrafficUpdate {
	private double initCount;
	private double initLoad;
	private double initUtilization;
	private double updateCount;
	private double updateLoad;
	private double updateUtilization;
	private List<Integer> nodesToUpdate;
	
	private int modus;
	private int sleeptillUpdate;
	
	public TrafficUpdate(double initCount,double initLoad,double updateCount,double updateLoad,int sleeptillUpdate, List<Integer> affectedNodes){
		this.initCount=initCount;
		this.initLoad=initLoad;
		this.updateCount=updateCount;
		this.updateLoad=updateLoad;
		this.nodesToUpdate = affectedNodes;
		this.modus=0;
		this.sleeptillUpdate = sleeptillUpdate;
	}
	
	public TrafficUpdate(double initUtilization, double updateUtilization,int sleepTilleTimestamp,List<Integer> affectedNodes){
		this.initUtilization = initUtilization;
		this.updateUtilization = updateUtilization;
		this.nodesToUpdate = affectedNodes;
		this.modus=1;
	}
	
	public int getModus(){
		return modus;
	}
	
	public double getInitCount(){
		return initCount;
	}
	
	public double getInitLoad(){
		return initLoad;
	}
	
	public double getUpdateCount(){
		return updateCount;
	}
	
	public double getUpdateLoad(){
		return updateLoad;
	}
	
	public boolean isAffected(int nodeID){
		return nodesToUpdate.contains(nodeID);
	}
	public boolean changeToUpdate(int timeStamp){
		return sleeptillUpdate<=timeStamp;
	}
	
	public double getInitUtilization(){
		return initUtilization;
	}
	
	public double getUpdateUtilization(){
		return updateUtilization;
	}
	
	public double getSleepTillUpdate(){
		return sleeptillUpdate;
	}
}
