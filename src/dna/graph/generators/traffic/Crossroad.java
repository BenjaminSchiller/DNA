package dna.graph.generators.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Crossroad {
	HashMap<CardinalDirection, Integer> inputWays;
	HashMap<CardinalDirection, Integer> outputWays;
	HashMap<CardinalDirection,Set<CardinalDirection>> inputToOutput;
	HashMap<CardinalDirection, InputWay> outputToInput;
	int crossroadID;
	DB db;
	
	public Crossroad(int crossroadID, DB db){
		inputToOutput=new HashMap<>();
		this.crossroadID=crossroadID;
		this.db=db;
		outputToInput = new HashMap<>();
	}
	
	public boolean setInputWays(HashMap<CardinalDirection, Integer> newInputWays) {
		boolean wasNull = inputWays==null;
		inputWays=newInputWays;
		return wasNull;	
	}
	
	public boolean setOutputWays(HashMap<CardinalDirection, Integer> newOutputWays) {
		boolean wasNull = outputWays==null;
		outputWays=newOutputWays;
		return wasNull;	
	}
	
	public boolean setOutputWay(CardinalDirection inDir, CardinalDirection outDir){
		if(!inputToOutput.containsKey(inDir)){
			inputToOutput.put(inDir, new HashSet<CardinalDirection>());
		}
		return inputToOutput.get(inDir).add(outDir);
	}
	
	public Integer getWay(CardinalDirection direction, int type) {
		if(type==0)
			return (inputWays.containsKey(direction)) ? inputWays.get(direction) : -1;
		else
			return (outputWays.containsKey(direction)) ? outputWays.get(direction) : -1;
	}
	
	public void connectWays () {
		for (CardinalDirection outDir : outputWays.keySet()) {
			List<InputWay> connectedWays = db.getConnectedInputWays(getWay(outDir,1), crossroadID);
			for (InputWay integers : connectedWays) {
				if(integers!=null) {
					outputToInput.put(outDir, integers);
				}
			}
		}
	}
	
	public Set<InputWayConnection> getConnections(){
		Set<CardinalDirection> connectedOutputways;
		Set<InputWayConnection> connectedCrossroads = new HashSet<>();
		InputWay connection;
		for (Map.Entry<CardinalDirection, Set<CardinalDirection>> innerConnection : inputToOutput.entrySet()) {
			connectedOutputways = innerConnection.getValue();
			for (CardinalDirection outputDirection : connectedOutputways) {
				if(outputToInput.containsKey(outputDirection)) {
					connection = outputToInput.get(outputDirection);
					connectedCrossroads.add(new InputWayConnection(this.crossroadID, inputWays.get(innerConnection.getKey()),innerConnection.getKey(), connection.wayID , connection.crossroadID,outputDirection));
				}
			}
		}
		return connectedCrossroads;
	}
	
	public void printInputWays(){
		System.out.println("InputWays von:\t"+this.crossroadID);
		for (Map.Entry<CardinalDirection, Integer> inputWay : inputWays.entrySet()) {
			System.out.println(inputWay.getKey() +"\t"+inputWay.getValue());
		}
	}
	
	public void printInput2Output(){
		System.out.println("Input2Output von:\t"+this.crossroadID);
		for (Map.Entry<CardinalDirection, Set<CardinalDirection>> inputWay : inputToOutput.entrySet()) {
			System.out.println(inputWay.getKey());
			for (CardinalDirection outputWay : inputWay.getValue()) {
				System.out.println("\t"+outputWay);
			}
		}
	}
	
	public void printOutputWays(){
		System.out.println("OutputWays von:\t"+this.crossroadID);
		for (Map.Entry<CardinalDirection, Integer> outputWay : outputWays.entrySet()) {
			System.out.println(outputWay.getKey() +"\t"+outputWay.getValue());
		}
	}
}
