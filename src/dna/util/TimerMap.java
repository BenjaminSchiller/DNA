package dna.util;

import java.util.HashMap;

public class TimerMap {
	private HashMap<String, Timer> timerList = new HashMap<>();
	
	public void put(Timer value) {
		put(value.getName(), value);
	}
	
	public void put(String key, Timer value) {
		timerList.put(key, value);
	}
	
	public Timer get(String key) {
		Timer res = timerList.get(key);
		if ( res == null ) {
			res = new Timer(key);
		}
		return res;
	}

	public void remove(String resetTimerName) {
		timerList.remove(resetTimerName);
	}
}
