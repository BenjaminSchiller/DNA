package dna.graph.generators.traffic;

import java.util.Arrays;

public class EdgeContainer {
	int from;
	int to;
	
	public EdgeContainer(int from, int to){
		this.from = from;
		this.to = to;
	}
	
	public int hashCode(){
		return Arrays.hashCode(new int[]{from,to});
	}

	public boolean equals(Object obj) {
		EdgeContainer ec = (EdgeContainer) obj;
		return Arrays.equals(new int[]{from, to},new int[]{ec.from,ec.to});
	}
	public String toString(){
		return "FROM:"+from+"\tTO:"+to;
	}
	
	public int getFrom(){
		return from;
	}
	
	public int getTo(){
		return to;
	}
}
