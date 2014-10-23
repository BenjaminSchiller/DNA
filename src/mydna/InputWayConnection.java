package mydna;

public class InputWayConnection {
	int fromCrossroad;
	int fromWayID;
	CardinalDirection fromWayDirection;
	int toCrossroad;
	int toWayID;
	CardinalDirection toWayDirection;
	
	public InputWayConnection(int fromCrossroad, int fromWayID,CardinalDirection fromWayDirection, int toCrossroad, int toWayID,CardinalDirection toWayDirection) {
		this.fromCrossroad=fromCrossroad;
		this.fromWayID=fromWayID;
		this.toCrossroad=toCrossroad;
		this.toWayID=toWayID;
	}
	
	public String toString() {
		return "Connected " + fromWayID + " on " +fromCrossroad + " with " +toWayID + " on " + toCrossroad;
	}
}
