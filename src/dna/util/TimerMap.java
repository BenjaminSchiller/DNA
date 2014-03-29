package dna.util;

import java.util.HashMap;

public class TimerMap {
	private static HashMap<String, Timer> timerList = new HashMap<>();
	
	public static void put(Timer value) {
		put(value.getName(), value);
	}
	
	public static void put(String key, Timer value) {
		timerList.put(key, value);
	}
	
	public static Timer get(String key) {
		Timer res = timerList.get(key);
		if ( res == null ) {
			res = new Timer(key);
		}
		return res;
	}

	public static void remove(String resetTimerName) {
		timerList.remove(resetTimerName);
	}
}
