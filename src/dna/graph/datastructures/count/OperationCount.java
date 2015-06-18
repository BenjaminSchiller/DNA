package dna.graph.datastructures.count;

import java.io.IOException;
import java.util.Random;

import dna.graph.datastructures.DataStructure.ListType;
import dna.io.Reader;
import dna.io.Writer;
import dna.util.ArrayUtils;

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
		if (INIT != 0)
			buff.append("  INIT: " + INIT + "\n");
		if (ADD_SUCCESS != 0)
			buff.append("  ADD_SUCCESS: " + ADD_SUCCESS + "\n");
		if (ADD_FAILURE != 0)
			buff.append("  ADD_FAILURE: " + ADD_FAILURE + "\n");
		if (RANDOM_ELEMENT != 0)
			buff.append("  RANDOM_ELEMENT: " + RANDOM_ELEMENT + "\n");
		if (SIZE != 0)
			buff.append("  SIZE: " + SIZE + "\n");
		if (ITERATE != 0)
			buff.append("  ITERATE: " + ITERATE + "\n");
		if (CONTAINS_SUCCESS != 0)
			buff.append("  CONTAINS_SUCCESS: " + CONTAINS_SUCCESS + "\n");
		if (CONTAINS_FAILURE != 0)
			buff.append("  CONTAINS_FAILURE: " + CONTAINS_FAILURE + "\n");
		if (GET_SUCCESS != 0)
			buff.append("  GET_SUCCESS: " + GET_SUCCESS + "\n");
		if (GET_FAILURE != 0)
			buff.append("  GET_FAILURE: " + GET_FAILURE + "\n");
		if (REMOVE_SUCCESS != 0)
			buff.append("  REMOVE_SUCCESS: " + REMOVE_SUCCESS + "\n");
		if (REMOVE_FAILURE != 0)
			buff.append("  REMOVE_FAILURE: " + REMOVE_FAILURE + "\n");
		return buff.toString();
	}

	public void writeValues(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		w.write("NaN	SIZE	" + listSize + "\n");
		w.write("NaN	COUNT	" + listCount + "\n");
		w.write("1	INIT	" + INIT + "\n");
		w.write("2	ADD_SUCCESS	" + ADD_SUCCESS + "\n");
		w.write("3	ADD_FAILURE	" + ADD_FAILURE + "\n");
		w.write("4	RANDOM_ELEMENT	" + RANDOM_ELEMENT + "\n");
		w.write("5	SIZE	" + SIZE + "\n");
		w.write("6	ITERATE	" + ITERATE + "\n");
		w.write("7	CONTAINS_SUCCESS	" + CONTAINS_SUCCESS + "\n");
		w.write("8	CONTAINS_FAILURE	" + CONTAINS_FAILURE + "\n");
		w.write("9	GET_SUCCESS	" + GET_SUCCESS + "\n");
		w.write("10	GET_FAILURE	" + GET_FAILURE + "\n");
		w.write("11	REMOVE_SUCCESS	" + REMOVE_SUCCESS + "\n");
		w.write("12	REMOVE_FAILURE	" + REMOVE_FAILURE);
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

	public void writeValues(Writer w, String prefix) throws IOException {
		String sep = "=";
		w.writeln(prefix + "SIZE" + sep + listSize);
		w.writeln(prefix + "COUNT" + sep + listCount);
		w.writeln(prefix + "INIT" + sep + INIT);
		w.writeln(prefix + "ADD_SUCCESS" + sep + ADD_SUCCESS);
		w.writeln(prefix + "ADD_FAILURE" + sep + ADD_FAILURE);
		w.writeln(prefix + "RANDOM_ELEMENT" + sep + RANDOM_ELEMENT);
		w.writeln(prefix + "SIZE" + sep + SIZE);
		w.writeln(prefix + "ITERATE" + sep + ITERATE);
		w.writeln(prefix + "CONTAINS_SUCCESS" + sep + CONTAINS_SUCCESS);
		w.writeln(prefix + "CONTAINS_FAILURE" + sep + CONTAINS_FAILURE);
		w.writeln(prefix + "GET_SUCCESS" + sep + GET_SUCCESS);
		w.writeln(prefix + "GET_FAILURE" + sep + GET_FAILURE);
		w.writeln(prefix + "REMOVE_SUCCESS" + sep + REMOVE_SUCCESS);
		w.writeln(prefix + "REMOVE_FAILURE" + sep + REMOVE_FAILURE);
	}

	public static OperationCount read(Reader r, String prefix, ListType lt)
			throws IOException {
		OperationCount oc = new OperationCount(lt);
		String sep = "=";
		oc.listSize = Integer.parseInt(r.readString().split(sep)[1]);
		oc.listCount = Integer.parseInt(r.readString().split(sep)[1]);
		oc.INIT = Integer.parseInt(r.readString().split(sep)[1]);
		oc.ADD_SUCCESS = Integer.parseInt(r.readString().split(sep)[1]);
		oc.ADD_FAILURE = Integer.parseInt(r.readString().split(sep)[1]);
		oc.RANDOM_ELEMENT = Integer.parseInt(r.readString().split(sep)[1]);
		oc.SIZE = Integer.parseInt(r.readString().split(sep)[1]);
		oc.ITERATE = Integer.parseInt(r.readString().split(sep)[1]);
		oc.CONTAINS_SUCCESS = Integer.parseInt(r.readString().split(sep)[1]);
		oc.CONTAINS_FAILURE = Integer.parseInt(r.readString().split(sep)[1]);
		oc.GET_SUCCESS = Integer.parseInt(r.readString().split(sep)[1]);
		oc.GET_FAILURE = Integer.parseInt(r.readString().split(sep)[1]);
		oc.REMOVE_SUCCESS = Integer.parseInt(r.readString().split(sep)[1]);
		oc.REMOVE_FAILURE = Integer.parseInt(r.readString().split(sep)[1]);
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

	public static enum AggregationType {
		MIN, MAX, AVG, FIRST, LAST
	}

	public static OperationCount add(AggregationType at, OperationCount... ocs) {
		OperationCount oc = new OperationCount(ocs[0].lt);
		for (int i = 1; i < ocs.length; i++) {
			if (!ocs[i].lt.equals(oc.lt)) {
				throw new IllegalArgumentException(
						"cannot add lists of different types: " + oc.lt
								+ " and " + ocs[i].lt);
			}
		}
		int[] size = new int[ocs.length];
		int[] count = new int[ocs.length];
		for (int i = 0; i < ocs.length; i++) {
			size[i] = ocs[i].listSize;
			count[i] = ocs[i].listCount;
			oc.INIT += ocs[i].INIT;
			oc.ADD_SUCCESS += ocs[i].ADD_SUCCESS;
			oc.ADD_FAILURE += ocs[i].ADD_FAILURE;
			oc.RANDOM_ELEMENT += ocs[i].RANDOM_ELEMENT;
			oc.SIZE += ocs[i].SIZE;
			oc.ITERATE += ocs[i].ITERATE;
			oc.CONTAINS_SUCCESS += ocs[i].CONTAINS_SUCCESS;
			oc.CONTAINS_FAILURE += ocs[i].CONTAINS_FAILURE;
			oc.GET_SUCCESS += ocs[i].GET_SUCCESS;
			oc.GET_FAILURE += ocs[i].GET_FAILURE;
			oc.REMOVE_SUCCESS += ocs[i].REMOVE_SUCCESS;
			oc.REMOVE_FAILURE += ocs[i].REMOVE_FAILURE;
		}
		switch (at) {
		case AVG:
			oc.listSize = (int) ArrayUtils.avg(size);
			oc.listCount = (int) ArrayUtils.avg(count);
			break;
		case MAX:
			oc.listSize = ArrayUtils.max(size);
			oc.listCount = ArrayUtils.max(count);
			break;
		case MIN:
			oc.listSize = ArrayUtils.min(size);
			oc.listCount = ArrayUtils.min(count);
			break;
		case FIRST:
			oc.listSize = ocs[0].listSize;
			oc.listCount = ocs[0].listCount;
			break;
		case LAST:
			oc.listSize = ocs[ocs.length - 1].listSize;
			oc.listCount = ocs[ocs.length - 1].listCount;
			break;
		default:
			break;
		}
		return oc;
	}

	public static OperationCount getRandom(ListType lt) {
		OperationCount oc = new OperationCount(lt);
		Random rand = new Random();
		oc.listSize = rand.nextInt(1000);
		oc.listCount = rand.nextInt(1000);
		oc.INIT = rand.nextInt(1000);
		oc.ADD_SUCCESS = rand.nextInt(1000);
		oc.ADD_FAILURE = rand.nextInt(1000);
		oc.RANDOM_ELEMENT = rand.nextInt(1000);
		oc.SIZE = rand.nextInt(1000);
		oc.ITERATE = rand.nextInt(1000);
		oc.CONTAINS_SUCCESS = rand.nextInt(1000);
		oc.CONTAINS_FAILURE = rand.nextInt(1000);
		oc.GET_SUCCESS = rand.nextInt(1000);
		oc.GET_FAILURE = rand.nextInt(1000);
		oc.REMOVE_SUCCESS = rand.nextInt(1000);
		oc.REMOVE_FAILURE = rand.nextInt(1000);
		return oc;
	}

	public boolean isNodes() {
		return this.lt.equals(ListType.GlobalNodeList)
				|| this.lt.equals(ListType.LocalNodeList);
	}

}
