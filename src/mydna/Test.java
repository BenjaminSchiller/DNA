package mydna;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import dna.util.Config;

public class Test {

	public static void main(String[] args) {
		DB db = new DB(0, null, null, 5, 0, null,false);
		/*for (int i = 0; i < 213; i++) {
			db.writeMaximalWeightsInputWays_5Minutes(i);
		}*/
		db.writeMaximalWeightSensorRandom();
	}
	
	/*
	public static void fillMaxInputWays(){
		DB db = new DB(5,new DateTime(),null,0);
		db.getInputWays();
		HashMap<Integer, int[]> inputways = db.inputWays;
		for (Integer string : inputways.keySet()) {
			db.writeMaximalWeightsInputWays(string);
		}
	}
	
	public static void getSensorConnection(){
		DB db = new DB(5,new DateTime(),null,0);
		//Setting Up Sensor-Connection
		Sensor s;
		for (int i = 0; i < 2600; i++) {
			s=db.getSensor(i);
			if(s!=null) {
				Crossroad xRoad = db.innerconnectionForCrossroad(s.crossroadID);
				if(xRoad == null) {
					System.out.println(s.crossroadID);
				}
				xRoad.connectWays();
				HashMap<CardinalDirection, InputWay> outputConnection = xRoad.outputToInput;
				s.getConnection(outputConnection);
				s.printConnection();
				db.writeSensorConnection(s);
			}
			else {
				System.out.println(s + "mit ID " +i);
			}
		}
	}
	*/
}
