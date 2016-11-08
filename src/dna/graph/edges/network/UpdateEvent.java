package dna.graph.edges.network;

/**
 * An UpdateEvent is a very basic object representing a single event occurring
 * at a given time t.
 * 
 * @author Rwilmes
 * 
 */
public class UpdateEvent {

	protected long time;

	public UpdateEvent(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long t) {
		this.time = t;
	}

}
