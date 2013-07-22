package dna.series.aggdata;

import java.io.IOException;

import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.series.lists.List;

/**
 * An AggregatedRunTimeList object contains aggregated values of a RunTimeList.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedRunTimeList extends List<AggregatedValue> {

	// member variables
	private String name;
	
	// constructors
	public AggregatedRunTimeList() {
		super();
	}

	public AggregatedRunTimeList(int size) {
		super(size);
	}
	
	public AggregatedRunTimeList(String name) {
		super();
		this.name = name;
	}
	
	public AggregatedRunTimeList(String name, int size) {
		super(size);
		this.name = name;
	}
	
	// methods
	public String getName() {
		return this.name;
	}
	
	// IO methods
	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		
		for(AggregatedValue aggData : this.getList()) {			
			String temp = "" + aggData.getName() + Keywords.aggregatedDataDelimiter;
			for (int i = 0; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + Keywords.aggregatedDataDelimiter;
			}
			w.writeln(temp);
		}
		w.close();
	}

}
