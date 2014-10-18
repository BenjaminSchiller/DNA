package mydna;

public enum Days {
	MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY;
	
	static Days getDay(int i){
		return values()[i-1];
	}
}