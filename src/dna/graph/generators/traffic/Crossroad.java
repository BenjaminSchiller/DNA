package dna.graph.generators.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Crossroad {
	private HashMap<CardinalDirection, Integer> inputWays;
	private HashMap<CardinalDirection, Integer> outputWays;
	private HashMap<CardinalDirection,Set<CardinalDirection>> inputToOutput;
	private HashMap<CardinalDirection, InputWay> outputToInput;
	private int crossroadID;
	private DB db;
	
	public Crossroad(int crossroadID, DB db){
		this.inputToOutput=new HashMap<>();
		this.crossroadID=crossroadID;
		this.db=db;
		this.outputToInput = new HashMap<>();
	}
	/**
	 * speichert die übergebene HashMap von Einfahrtswegen
	 * @param newInputWays
	 * @return Setzen = true, Ersetzen = False
	 */
	public boolean setInputWays(HashMap<CardinalDirection, Integer> newInputWays) {
		boolean wasNull = inputWays==null;
		inputWays=newInputWays;
		return wasNull;	
	}
	/**
	 * speichert die übergebene HashMap von Ausfahrtswegen
	 * @param newOutputWays
	 * @return Setzen = true, Ersetzen = False
	 */
	public boolean setOutputWays(HashMap<CardinalDirection, Integer> newOutputWays) {
		boolean wasNull = outputWays==null;
		outputWays=newOutputWays;
		return wasNull;	
	}
	/**
	 * Verbindet einen Einfahrtsweg mit einem Ausfahrtsweg
	 * @param inDir CardinalDirection des Einfahrtsweges
	 * @param outDir CardinalDirection des Ausfahrtsweges
	 * @return Wert neu gesetzt = true, Wert bereits vorhanden = false
	 */
	public boolean setOutputWay(CardinalDirection inDir, CardinalDirection outDir){
		if(!inputToOutput.containsKey(inDir)){
			inputToOutput.put(inDir, new HashSet<CardinalDirection>());
		}
		return inputToOutput.get(inDir).add(outDir);
	}
	/**
	 * liefert den Identifier eines Weges
	 * @param direction, Himmelsrichtung des Weges
	 * @param isInputWay, Einfahrtsweg = true, Ausfahrtsweg = false
	 * @return
	 */
	public Integer getWay(CardinalDirection direction, boolean isInputWay) {
		if(isInputWay)
			return (inputWays.containsKey(direction)) ? inputWays.get(direction) : -1;
		else
			return (outputWays.containsKey(direction)) ? outputWays.get(direction) : -1;
	}
	
	/**
	 * verbindet die Ausfahrtswege mit den Einfahrtswegen benachbarter Kreuzungen
	 */
	public void connectWays () {
		for (CardinalDirection outDir : outputWays.keySet()) {
			List<InputWay> connectedWays = db.getConnectedInputWays(getWay(outDir,false), crossroadID);
			for (InputWay integers : connectedWays) {
				if(integers!=null) {
					outputToInput.put(outDir, integers);
				}
			}
		}
	}
	/**
	 * liefert eine Menge von Verbindungen von Einfahrtswegen zu benachbarten Einfahrtswegen
	 * @return
	 */
	public Set<InputWayConnection> getConnections(){
		Set<CardinalDirection> connectedOutputways;
		Set<InputWayConnection> connectedCrossroads = new HashSet<>();
		InputWay connection;
		for (Map.Entry<CardinalDirection, Set<CardinalDirection>> innerConnection : inputToOutput.entrySet()) {
			connectedOutputways = innerConnection.getValue();
			for (CardinalDirection outputDirection : connectedOutputways) {
				if(outputToInput.containsKey(outputDirection)) {
					connection = outputToInput.get(outputDirection);
					connectedCrossroads.add(new InputWayConnection(this.crossroadID, inputWays.get(innerConnection.getKey()),innerConnection.getKey(), connection.getWayID() , connection.getCrossroadID() ,outputDirection));
				}
			}
		}
		return connectedCrossroads;
	}
	
	/**
	 * Kontrollausgabe für die Einfahrtswege
	 */
	public void printInputWays(){
		System.out.println("InputWays von:\t"+this.crossroadID);
		for (Map.Entry<CardinalDirection, Integer> inputWay : inputWays.entrySet()) {
			System.out.println(inputWay.getKey() +"\t"+inputWay.getValue());
		}
	}
	/**
	 * Kontrollausgabe für die innere Verbindung von Wegen
	 */
	public void printInput2Output(){
		System.out.println("Input2Output von:\t"+this.crossroadID);
		for (Map.Entry<CardinalDirection, Set<CardinalDirection>> inputWay : inputToOutput.entrySet()) {
			System.out.println(inputWay.getKey());
			for (CardinalDirection outputWay : inputWay.getValue()) {
				System.out.println("\t"+outputWay);
			}
		}
	}
	/**
	 * Kontrollausgabe für die Ausfahrtswege
	 */
	public void printOutputWays(){
		System.out.println("OutputWays von:\t"+this.crossroadID);
		for (Map.Entry<CardinalDirection, Integer> outputWay : outputWays.entrySet()) {
			System.out.println(outputWay.getKey() +"\t"+outputWay.getValue());
		}
	}
}
