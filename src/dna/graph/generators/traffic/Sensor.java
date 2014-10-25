package dna.graph.generators.traffic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Sensor {
	int sensorID;
	String sensorName;
	int crossroadID;
	String crossroadName;
	int wayID;
	Set<CardinalDirection> outputDiretions;
	HashMap<CardinalDirection,InputWay> connections;
	
	public Sensor(int sensorID, String sensorName, int crossroadID, String crossroadName, int wayID,Set<CardinalDirection> outputDirections){
		this.sensorID=sensorID;
		this.sensorName=sensorName;
		this.crossroadID=crossroadID;
		this.crossroadName=crossroadName;
		this.wayID=wayID;
		this.outputDiretions=outputDirections;
	}
	public Sensor(int sensorID, String sensorName, int crossroadID, String crossroadName, int wayID){
		this.sensorID=sensorID;
		this.sensorName=sensorName;
		this.crossroadID=crossroadID;
		this.crossroadName=crossroadName;
		this.wayID=wayID;
	}
	public HashMap<CardinalDirection, InputWay> getConnection(HashMap<CardinalDirection, InputWay> outputConnection) {
		HashMap<CardinalDirection,InputWay> connections = new HashMap<>();
		for (CardinalDirection output : outputDiretions) {
			if(outputConnection.containsKey(output))
				connections.put(output, outputConnection.get(output));
		}
		this.connections=connections;
		return connections;
	}
	public void printConnection() {
		System.out.println("Verbindungen von:\t"+sensorID+"("+sensorName+") on " +crossroadID+"("+crossroadName+")");
		for (Map.Entry<CardinalDirection, InputWay> connection : connections.entrySet()) {
			System.out.println("\t\t"+connection.getKey()+"\t"+connection.getValue());
		}
		
	}
	
	
}
