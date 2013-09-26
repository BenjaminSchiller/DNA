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
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// constructors
	public AggregatedRunTimeList() {
		super();
	}

	public AggregatedRunTimeList(int size) {
		super(size);
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	public AggregatedRunTimeList(String name) {
		super();
		this.name = name;
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	public AggregatedRunTimeList(String name, int size) {
		super(size);
		this.name = name;
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// methods
	public String getName() {
		return this.name;
	}
<<<<<<< HEAD
	
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
=======

	// IO methods
	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);

		for (AggregatedValue aggData : this.getList()) {
			String temp = "" + aggData.getName()
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 0; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
>>>>>>> remotes/beniMaster/master
			}
			w.writeln(temp);
		}
		w.close();
	}

}
