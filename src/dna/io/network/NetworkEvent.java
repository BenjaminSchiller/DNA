package dna.io.network;

import org.joda.time.DateTime;

/**
 * A NetworkEvent provides a very basic event object, representing a single
 * event occurring at a certain time in the network.
 * 
 * @author Rwilmes
 * 
 */
public class NetworkEvent {

	protected DateTime time;

	public NetworkEvent(DateTime time) {
		this.time = time;
	}

	public DateTime getTime() {
		return this.time;
	}
}
