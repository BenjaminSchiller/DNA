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
		return timerList.get(key);
	}

	public static void remove(String resetTimerName) {
		timerList.remove(resetTimerName);
	}
}
