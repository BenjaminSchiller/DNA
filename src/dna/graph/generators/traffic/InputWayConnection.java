package dna.graph.generators.traffic;

/**
 * Container-Klasse für Verbindungen zwischen Einfahrtswegen
 * @author Maurice
 *
 */
public class InputWayConnection {
	private int fromCrossroad;
	private int fromWayID;
	private CardinalDirection fromWayDirection;
	private int toCrossroad;
	private int toWayID;
	private CardinalDirection toWayDirection;
	

	public InputWayConnection(int fromCrossroad, int fromWayID,CardinalDirection fromWayDirection, int toCrossroad, int toWayID,CardinalDirection toWayDirection) {
		this.fromCrossroad=fromCrossroad;
		this.fromWayID=fromWayID;
		this.toCrossroad=toCrossroad;
		this.toWayID=toWayID;
	}
	
	/**
	 * liefert die Startkreuzung
	 * @return
	 */
	public int getFromCrossroad(){
		return fromCrossroad;
	}
	
	/**
	 * liefert den Startweg
	 * @return
	 */
	public int getFromWayID(){
		return fromWayID;
	}
	
	/**
	 * liefert die Startrichtung
	 * @return
	 */
	public CardinalDirection getFromWayDirection(){
		return fromWayDirection;
	}
	
	/**
	 * liefert die Zielkreuzung
	 * @return
	 */
	public int getToCrossroad(){
		return toCrossroad;
	}
	
	/**
	 * liefert den Zielweg
	 * @return
	 */
	public int getToWayID(){
		return toWayID;
	}
	
	/**
	 * liefert die Richtung des Zielweges
	 * @return
	 */
	public CardinalDirection getToWayDirection(){
		return toWayDirection;
	}
	
	/**
	 * Kontrollausgabe für die Verbindung von Wegen
	 */
	public String toString() {
		return "Connected " + fromWayID + " on " +fromCrossroad + " with " +toWayID + " on " + toCrossroad;
	}
}
