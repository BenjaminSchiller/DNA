package dna.graph.generators.traffic;

import java.util.Arrays;

public class InputWay {
	int wayID;
	int crossroadID;
	
	public InputWay(int wayID, int crossroadID) {
		this.wayID=wayID;
		this.crossroadID=crossroadID;
	}
	
	public String toString(){
		return String.valueOf(wayID) + "\t"+crossroadID;
	}

	public int hashCode(){
		return Arrays.hashCode(new int[]{wayID,crossroadID});
	}
	
	public boolean equals(Object obj) {
		InputWay inputWay = (InputWay) obj;
		return Arrays.equals(new int[]{wayID,crossroadID}, new int[]{inputWay.wayID,inputWay.crossroadID});
	}
	
}
