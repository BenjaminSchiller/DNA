package dna.graph.generators.traffic;

import java.util.Arrays;

/**
 * Containerklasse für einen Einfahrtsweg
 * @author Maurice
 *
 */
public class InputWay {
	
	private int wayID; // OSM-WayID
	private int crossroadID; // globale Kreuzungs-ID
	
	public InputWay(int wayID, int crossroadID) {
		this.wayID=wayID;
		this.crossroadID=crossroadID;
	}
	
	/**
	 * Kontrollausgbae
	 */
	public String toString(){
		return "Einfahrtsweg: wayID:"+String.valueOf(wayID) + "\t crossroadID:"+crossroadID;
	}

	/**
	 * liefert den HashCode des Einfahrtsweges basierend auf der Array-Darstellung
	 */
	public int hashCode(){
		return Arrays.hashCode(new int[]{wayID,crossroadID});
	}
	
	/**
	 * liefert die OSM-ID für den Weg
	 * @return
	 */
	public int getWayID(){
		return wayID;
	}
	
	/**
	 * liefert die KreuzungsID für den Weg
	 * @return
	 */
	public int getCrossroadID(){
		return crossroadID;
	}
	
	/**
	 * vergleicht das Objekt mit dem übergebenen Objekt auf Gleichheit
	 * @param obj, zu vergleichendes Objekt
	 * @return true, wenn das übergebene Objekt den gleichen Einfahrtsweg repräsentiert
	 */
	public boolean equals(Object obj) {
		if(obj instanceof InputWay){
			InputWay inputWay = (InputWay) obj;
			return Arrays.equals(new int[]{wayID,crossroadID}, new int[]{inputWay.wayID,inputWay.crossroadID});
		}
		else
			return false;
	}
	
}
