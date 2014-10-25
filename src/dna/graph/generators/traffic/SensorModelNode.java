package dna.graph.generators.traffic;


public class SensorModelNode {
	private double[] weight;
	private boolean isReal;
	private int nodeID;
	private int timestep;
	private int inputWayID;
	private CardinalDirection direction;
	
	public SensorModelNode(int nodeID, boolean isReal){
		this.nodeID=nodeID;
		this.isReal=isReal;
		this.timestep=-1;
	}
	
	public SensorModelNode(int nodeID, boolean isReal,double[] weight,int timestep){
		this.nodeID=nodeID;
		this.isReal=isReal;
		this.weight=weight;
		this.timestep=timestep;
	}
	
	public SensorModelNode(int nodeID, boolean isReal,double[] weight,int timestep,int inputWayID){
		this.nodeID=nodeID;
		this.isReal=isReal;
		this.weight=weight;
		this.timestep=timestep;
		this.inputWayID = inputWayID;
	}
	
	public SensorModelNode(int nodeID, boolean isReal,int inputWayID, CardinalDirection direction){
		this.nodeID=nodeID;
		this.isReal=isReal;
		this.inputWayID = inputWayID;
		this.direction = direction;
		this.timestep=-1;
	}
	
	public double[] getWeight(){
		return weight;
	}
	
	public void setWeight(double[] weight, int timestep){
		this.weight=weight;
		this.timestep=timestep;
	}
	
	public boolean isReal(){
		return isReal;
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public int getTimestep(){
		return timestep;
	}
	
	public int getInputWayID(){
		return inputWayID;
	}
	
	public CardinalDirection getDirection(){
		return direction;
	}
}
