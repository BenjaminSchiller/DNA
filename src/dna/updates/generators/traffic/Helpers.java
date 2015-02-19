package dna.updates.generators.traffic;

import org.joda.time.DateTime;

public class Helpers {
	public static boolean isWorkDay(Days d) {
		return !(d == Days.SATURDAY || d==Days.SUNDAY);
	}
	
	/**
	 * berechnet ausgehend von "start" den nächsten Werktag in der Zukunft(forward) oder Vergangenheit (!forward)
	 * @param start - Startdatum
	 * @param l - Timestep
	 * @param forward - true = in der Zukunft, false = in der Vergangenheit
	 * @return
	 */
	public static DateTime calculateNextWorkDay(DateTime start,long l, boolean forward){
		return calculateNextDay(start, l, new boolean[]{true,true,true,true,true,false,false},null,forward);
	}
	
	/**
	 *  berechnet ausgehend von "start" den nächsten Tag in der Zukunft(forward) oder Vergangenheit (!forward)
	 * @param start - Startdatum
	 * @param timestep - Zeitschritt für den der nächste Tag berechnet werden soll
	 * @param daySelection - Array mit Wochentagen, die für die Berechnung berücksichtigt werden sollen
	 * @param ignoreTo - Zeitpunkt bis zu welchem die Berechnung ignoriert wird
	 * @param forward - true = in der Zukunft, false = in der Vergangenheit
	 * @return
	 */
	public static DateTime calculateNextDay(DateTime start, long timestep, boolean[] daySelection,DateTime ignoreTo, boolean forward){
		DateTime current = start;
		int count = 0;
		while(count <= timestep) {
			current = forward ? current.plusDays(1) : current.minusDays(1);
			if(daySelection[current.getDayOfWeek()-1] && (current.isBefore(ignoreTo) || forward)){
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
