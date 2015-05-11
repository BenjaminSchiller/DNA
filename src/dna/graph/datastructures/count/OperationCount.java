package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.datastructures.DataStructure.ListType;
import dna.io.Writer;

/**
 * 
 * stores how many times each operation on a specific list type was executed. in
 * addition, the total number of lists of this type is recorded as well as the
 * average size of each list.
 * 
 * @author benni
 *
 */
public class OperationCount {

	public static enum Operation {
		INIT, ADD, RANDOM_ELEMENT, SIZE, ITERATE, CONTAINS_SUCCESS, CONTAINS_FAILURE, GET_SUCCESS, GET_FAILURE, REMOVE_SUCCESS, REMOVE_FAILURE
	}

	public int listSize;
	public int listCount;

	public ListType lt;

	public OperationCount(ListType lt) {
		this.lt = lt;
	}

	// write
	public int INIT = 0;
	public int ADD = 0;
	public int REMOVE_SUCCESS = 0;
	public int REMOVE_FAILURE = 0;

	// read
	public int RANDOM_ELEMENT = 0;
	public int SIZE = 0;
	public int ITERATE = 0;
	public int CONTAINS_SUCCESS = 0;
	public int CONTAINS_FAILURE = 0;
	public int GET_SUCCESS = 0;
	public int GET_FAILURE = 0;

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("count for " + listCount + " x " + listSize + " lists ("
				+ this.isWriteOnly() + ")\n");
		buff.append("  INIT: " + INIT + "\n");
		buff.append("  ADD: " + ADD + "\n");
		buff.append("  RANDOM_ELEMENT: " + RANDOM_ELEMENT + "\n");
		buff.append("  SIZE: " + SIZE + "\n");
		buff.append("  ITERATE: " + ITERATE + "\n");
		buff.append("  CONTAINS_SUCCESS: " + CONTAINS_SUCCESS + "\n");
		buff.append("  CONTAINS_FAILURE: " + CONTAINS_FAILURE + "\n");
		buff.append("  GET_SUCCESS: " + GET_SUCCESS + "\n");
		buff.append("  GET_FAILURE: " + GET_FAILURE + "\n");
		buff.append("  REMOVE_SUCCESS: " + REMOVE_SUCCESS + "\n");
		buff.append("  REMOVE_FAILURE: " + REMOVE_FAILURE + "\n");
		return buff.toString();
	}

	public String getValues() {
		StringBuffer buff = new StringBuffer();
		buff.append("1	INIT	" + INIT + "\n");
		buff.append("2	ADD	" + ADD + "\n");
		buff.append("3	RANDOM_ELEMENT	" + RANDOM_ELEMENT + "\n");
		buff.append("4	SIZE	" + SIZE + "\n");
		buff.append("5	ITERATE	" + ITERATE + "\n");
		buff.append("6	CONTAINS_SUCCESS	" + CONTAINS_SUCCESS + "\n");
		buff.append("7	CONTAINS_FAILURE	" + CONTAINS_FAILURE + "\n");
		buff.append("8	GET_SUCCESS	" + GET_SUCCESS + "\n");
		buff.append("9	GET_FAILURE	" + GET_FAILURE + "\n");
		buff.append("10	REMOVE_SUCCESS	" + REMOVE_SUCCESS + "\n");
		buff.append("11	REMOVE_FAILURE	" + REMOVE_FAILURE);
		return buff.toString();
	}

	public void writeValues(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		w.write(this.getValues());
		w.close();
	}

	/**
	 * 
	 * @return true, if only write operations are executed, i.e., no read
	 *         operations are executed which implied that the list could be
	 *         removed!
	 */
	public boolean isWriteOnly() {
		return this.RANDOM_ELEMENT == 0 && this.SIZE == 0 && this.ITERATE == 0
				&& this.CONTAINS_SUCCESS == 0 && this.CONTAINS_FAILURE == 0
				&& this.GET_SUCCESS == 0 && this.GET_FAILURE == 0;
	}

}
