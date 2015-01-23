package dna.graph.generators.traffic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Container-Klasse für einen realen Sensor
 * @author Maurice
 *
 */
public class Sensor {
	private int sensorID;
	private String sensorName;
	private int crossroadID;
	private String crossroadName;
	private int wayID;
	private Set<CardinalDirection> outputDiretions;
	private HashMap<CardinalDirection,InputWay> connections;
	
	/**
	 * Konstruktur für die nachträgliche Berechnung
	 * @param sensorID
	 * @param sensorName
	 * @param crossroadID
	 * @param crossroadName
	 * @param wayID
	 * @param outputDirections
	 */
	public Sensor(int sensorID, String sensorName, int crossroadID, String crossroadName, int wayID,Set<CardinalDirection> outputDirections){
		this.sensorID=sensorID;
		this.sensorName=sensorName;
		this.crossroadID=crossroadID;
		this.crossroadName=crossroadName;
		this.wayID=wayID;
		this.outputDiretions=outputDirections;
	}
	
	/**
	 * Konstruktur für die Nutzung als Container
	 * @param sensorID
	 * @param sensorName
	 * @param crossroadID
	 * @param crossroadName
	 * @param wayID
	 */
	public Sensor(int sensorID, String sensorName, int crossroadID, String crossroadName, int wayID){
		this.sensorID=sensorID;
		this.sensorName=sensorName;
		this.crossroadID=crossroadID;
		this.crossroadName=crossroadName;
		this.wayID=wayID;
	}
	/**
	 * liefert die globale ID des Sensors
	 * @return
	 */
	public int getSensorID(){
		return sensorID;
	}
	
	/**
	 * liefert den Namen des Sensors
	 * @return
	 */
	public String getSensorName(){
		return sensorName;
	}
	
	/**
	 * liefert die globale Kreuzungs-ID
	 * @return
	 */
	public int getCrossroadID(){
		return crossroadID;
	}
	
	/**
	 * liefert den Namen der Kreuzung
	 * @return
	 */
	public String getCrossroadName(){
		return crossroadName;
	}
	
	public int getWayID(){
		return wayID;
	}
	
	/**
	 * berechnet die Verbindungen zwischen den Ausfahrtswegen des Sensors und den Einfahrtswegen benachbarter Kreuzungen
	 * @param outputConnection Abbiegemöglichkeiten des Sensors
	 * @return Verbindungen mit Einfahrtswegen
	 */
	public HashMap<CardinalDirection, InputWay> calculateConnections(HashMap<CardinalDirection, InputWay> outputConnection) {
		HashMap<CardinalDirection,InputWay> connections = new HashMap<>();
		// Über alle Abbiegemöglichkeiten
		for (CardinalDirection output : outputDiretions) {
			// Ausfahrtsweg ist mit Einfahrtsweg benachbarter Kreuzung verbunden
			if(outputConnection.containsKey(output))
				connections.put(output, outputConnection.get(output));
		}
		this.connections=connections;
		return connections;
	}
	
	/**
	 * liefert die berechneten Verbindungen aus {@link #calculateConnections(HashMap)}
	 * @return
	 */
	public HashMap<CardinalDirection, InputWay> getConnections() {
		return connections;
	}
	
	
	/**
	 * Kontrollausgabe
	 */
	public void printConnection() {
		System.out.println("Verbindungen von:\t"+sensorID+"("+sensorName+") on " +crossroadID+"("+crossroadName+")");
		for (Map.Entry<CardinalDirection, InputWay> connection : connections.entrySet()) {
			System.out.println("\t\t"+connection.getKey()+"\t"+connection.getValue());
		}
		
	}
	
	
}
