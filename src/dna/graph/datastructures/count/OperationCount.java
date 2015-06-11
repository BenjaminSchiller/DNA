package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.datastructures.DataStructure.ListType;
import dna.io.Reader;
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
		INIT, ADD_SUCCESS, ADD_FAILURE, RANDOM_ELEMENT, SIZE, ITERATE, CONTAINS_SUCCESS, CONTAINS_FAILURE, GET_SUCCESS, GET_FAILURE, REMOVE_SUCCESS, REMOVE_FAILURE
	}

	public String name = null;

	public int listSize;
	public int listCount;

	public ListType lt;

	public OperationCount(ListType lt, int listSize, int listCount, String name) {
		this(lt, listSize, listCount);
		this.name = name;
	}

	public OperationCount(ListType lt, int listSize, int listCount) {
		this(lt);
		this.listSize = listSize;
		this.listCount = listCount;
	}

	public OperationCount(ListType lt) {
		this.lt = lt;
	}

	// write
	public int INIT = 0;
	public int ADD_SUCCESS = 0;
	public int ADD_FAILURE = 0;
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
		buff.append("  ADD_SUCCESS: " + ADD_SUCCESS + "\n");
		buff.append("  ADD_FAILURE: " + ADD_FAILURE + "\n");
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
		buff.append("NaN	SIZE	" + listSize + "\n");
		buff.append("NaN	COUNT	" + listCount + "\n");
		buff.append("1	INIT	" + INIT + "\n");
		buff.append("2	ADD_SUCCESS	" + ADD_SUCCESS + "\n");
		buff.append("3	ADD_FAILURE	" + ADD_FAILURE + "\n");
		buff.append("4	RANDOM_ELEMENT	" + RANDOM_ELEMENT + "\n");
		buff.append("5	SIZE	" + SIZE + "\n");
		buff.append("6	ITERATE	" + ITERATE + "\n");
		buff.append("7	CONTAINS_SUCCESS	" + CONTAINS_SUCCESS + "\n");
		buff.append("8	CONTAINS_FAILURE	" + CONTAINS_FAILURE + "\n");
		buff.append("9	GET_SUCCESS	" + GET_SUCCESS + "\n");
		buff.append("10	GET_FAILURE	" + GET_FAILURE + "\n");
		buff.append("11	REMOVE_SUCCESS	" + REMOVE_SUCCESS + "\n");
		buff.append("12	REMOVE_FAILURE	" + REMOVE_FAILURE);
		return buff.toString();
	}

	public void writeValues(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		w.write(this.getValues());
		w.close();
	}

	public static OperationCount read(String dir, String filename, ListType lt)
			throws IOException {
		OperationCount oc = new OperationCount(lt);
		Reader r = new Reader(dir, filename);
		String sep = "	";
		oc.listSize = Integer.parseInt(r.readString().split(sep)[2]);
		oc.listCount = Integer.parseInt(r.readString().split(sep)[2]);
		oc.INIT = Integer.parseInt(r.readString().split(sep)[2]);
		oc.ADD_SUCCESS = Integer.parseInt(r.readString().split(sep)[2]);
		oc.ADD_FAILURE = Integer.parseInt(r.readString().split(sep)[2]);
		oc.RANDOM_ELEMENT = Integer.parseInt(r.readString().split(sep)[2]);
		oc.SIZE = Integer.parseInt(r.readString().split(sep)[2]);
		oc.ITERATE = Integer.parseInt(r.readString().split(sep)[2]);
		oc.CONTAINS_SUCCESS = Integer.parseInt(r.readString().split(sep)[2]);
		oc.CONTAINS_FAILURE = Integer.parseInt(r.readString().split(sep)[2]);
		oc.GET_SUCCESS = Integer.parseInt(r.readString().split(sep)[2]);
		oc.GET_FAILURE = Integer.parseInt(r.readString().split(sep)[2]);
		oc.REMOVE_SUCCESS = Integer.parseInt(r.readString().split(sep)[2]);
		oc.REMOVE_FAILURE = Integer.parseInt(r.readString().split(sep)[2]);
		r.close();
		return oc;
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
