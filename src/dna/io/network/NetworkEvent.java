package dna.io.network;

import org.joda.time.DateTime;

public class NetworkEvent {

	protected DateTime time;

	public NetworkEvent(DateTime time) {
		this.time = time;
	}

	public DateTime getTime() {
		return this.time;
	}
}
