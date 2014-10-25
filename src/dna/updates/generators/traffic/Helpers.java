package dna.updates.generators.traffic;


import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.JodaTimePermission;

public class Helpers {
	public static boolean isWorkDay(Days d) {
		return !(d == Days.SATURDAY || d==Days.SUNDAY);
	}
	
	public static DateTime calculateNextWorkDay(DateTime start,long l){
		return calculateNextDay(start, l, new boolean[]{true,true,true,true,true,false,false},null);
	}
	
	public static DateTime calculateNextDay(DateTime end, long l, boolean[] daySelection,DateTime ignoreTo){
		DateTime current = end;
		int count = 0;
		while(count <= l) {
			current = current.minusDays(1);
			if(daySelection[current.getDayOfWeek()-1] && current.isBefore(ignoreTo)){
				count++;
			}
		}
		return current;
	}
	/**
	 * Wandelt die Beobachtungswochen in die Beobachtungstage um (=#Batches)
	 * @param weeks, Wochen die beobachtet werden sollen
	 * @param daySelection , Tage die in einer Woche beobachtet werden sollen
	 * @return
	 */
	public static int weekToDay(int weeks, boolean[] daySelection){
		int i =0;
		for (int j = 0; j < daySelection.length; j++) {
			if(daySelection[j])
				i++;
		}
		return i*weeks;
	}
	
}
