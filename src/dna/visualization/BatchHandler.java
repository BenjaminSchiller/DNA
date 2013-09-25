package dna.visualization;

import java.io.IOException;

import dna.series.data.BatchData;
import dna.series.lists.BatchDataList;

public class BatchHandler {

	// class variables
	private String dir;
	private BatchDataList batches;
	private int index;

	// constructors
	public BatchHandler() {
		this.dir = "/data/test/";
		this.batches = new BatchDataList();
		this.index = 0;
	}

	public BatchHandler(String dir) {
		this.dir = dir;
		this.batches = new BatchDataList();
		this.index = 0;
	}

	public BatchHandler(BatchDataList batches, int index) {
		this.dir = null;
		this.batches = batches;
		this.index = index;
	}

	public BatchHandler(String dir, int index) {
		this.dir = dir;
		this.batches = new BatchDataList();
		this.index = index;
	}

	// get methods
	public String getDir() {
		return this.dir;
	}

	public BatchDataList getBatches() {
		return this.batches;
	}

	public int getIndex() {
		return this.index;
	}

	// set methods
	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setBatches(BatchDataList batches) {
		this.batches = batches;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	// methods

	// returns the next batch
	public BatchData getNextBatch() {
		if (this.getIndex() < this.getBatches().size()) {
			return this.batches.get(this.index++);
		} else {
			return null;
		}
	}

	// adds new batches from the filesystem to the batches
	public void updateBatches() throws IOException {
		BatchDataList tempBatches = BatchDataList.read(this.getDir(), true);
		if (this.getBatches().size() <= tempBatches.size()) {
			int offset = tempBatches.size() - this.getBatches().size();
			for (BatchData b : tempBatches.list) {
				this.getBatches().add(b);
			}
		} else {
			System.out.println("No update");
		}
		this.sortBatches();
	}

	// checks if new batch is available
	public boolean isNewBatchAvailable() {
		if (this.getIndex() < this.getBatches().size())
			return true;
		else
			return false;
	}

	// prints out all batches
	public void printBatches() {
		for (BatchData b : this.getBatches().list) {
			System.out.println("Batch " + b.getTimestamp());
		}
	}

	// sorts the holded BatchDataList by timestamp
	public void sortBatches() {
		BatchDataList tempBatches = new BatchDataList();
		long min = this.getMinTimestamp();
		for (int i = 0; i < this.getBatches().size(); i++) {
			for (BatchData b : this.getBatches().list) {
				if (b.getTimestamp() == min)
					tempBatches.add(b);
			}
			min++;
		}
		this.batches = tempBatches;
	}

	// returns lowest timestamp
	public long getMinTimestamp() {
		long min = 0;
		if (this.getBatches().size() != 0) {
			min = this.getBatches().get(0).getTimestamp();
			for (BatchData b : this.getBatches().list) {
				if (b.getTimestamp() < min)
					min = b.getTimestamp();
			}
		} else {
			return 0;
		}

		return min;
	}
}
