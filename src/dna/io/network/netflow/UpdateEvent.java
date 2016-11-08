package dna.io.network.netflow;

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
