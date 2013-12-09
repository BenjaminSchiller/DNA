package dna.visualization;

import java.io.IOException;
import java.util.Random;

import dna.io.filesystem.Dir;
import dna.series.data.BatchData;
import dna.series.lists.BatchDataList;

/**
 * A batchhandler is used to read a run from the filesystem and simulate it by
 * sending the batches to the GUI in a given time intervall.
 * 
 * @author Rwilmes
 */
public class BatchHandler implements Runnable {

	// class variables
	private String dir;
	private BatchData currentBatch;
	private BatchData nextBatch;
	private BatchDataList batches;
	private int index;
	private MainDisplay mainFrame;

	private boolean isInit;
	private boolean threadSuspended;
	private boolean timeSlided;

	private Thread t;

	private int speed = 500;

	// constructors
	public BatchHandler(String dir) {
		this.dir = dir;
		this.batches = new BatchDataList();
		this.index = 0;
		this.timeSlided = false;
		this.isInit = false;
		this.threadSuspended = false;
	}

	public BatchHandler(String dir, MainDisplay mainFrame) {
		this.dir = dir;
		this.mainFrame = mainFrame;
		this.batches = new BatchDataList();
		this.index = 0;
		this.timeSlided = false;
		this.isInit = false;
		this.threadSuspended = false;
	}

	// get methods
	public String getDir() {
		return this.dir;
	}

	public BatchDataList getBatches() {
		return this.batches;
	}

	public long getIndex() {
		return this.index;
	}

	public int getSpeed() {
		return this.speed;
	}

	public int getAmountOfBatches() {
		return this.batches.size();
	}

	/** returns the maximum timestamp **/
	public long getMaxTimestamp() {
		return this.getBatches().get(this.getBatches().size() - 1)
				.getTimestamp();
	}

	/** returns minimum timestamp **/
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

	/** returns the next batch **/
	public BatchData getNextBatch() {
		try {
			return this.readBatch(this.index + 1);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** return the initialization batch **/
	public BatchData getInitBatch() {
		try {
			return BatchData.read(Dir.getBatchDataDir(this.dir, this.batches
					.get(0).getTimestamp()),
					this.batches.get(0).getTimestamp(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BatchData(0);
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

	public void setSpeed(int speed) {
		if (speed < 0)
			this.speed = 0;
		else
			this.speed = speed;
	}

	/** adds new batches from the filesystem to the batches **/
	public void updateBatches() {
		BatchDataList tempBatches = BatchDataList.readTimestamps(this.getDir());
		if (this.getBatches().size() <= tempBatches.size()) {
			for (BatchData b : tempBatches.list) {
				this.getBatches().add(b);
			}
		}
		this.sortBatches();
	}

	/** checks available batches and updates timestamps, does not read values **/
	public void getTimestamps() {
		try {
			BatchDataList tempBatches = BatchDataList
					.read(this.getDir(), false);
			if (this.getBatches().size() <= tempBatches.size()) {
				for (BatchData b : tempBatches.list) {
					this.getBatches().add(b);
				}
			}
			this.sortBatches();

		} catch (IOException e) {
			System.out
					.println("Error in BatchHandler while attempting to read timestamps.");
			e.printStackTrace();
		}
	}

	/** checks if a new batch is available **/
	public boolean isNewBatchAvailable() {
		if (this.getIndex() < this.getBatches().size())
			return true;
		else
			return false;
	}

	/** checks if all available batches have been send **/
	public boolean isLastBatchSend() {
		if (this.getIndex() == this.getBatches().size())
			return true;
		else
			return false;
	}

	/** prints out all batches **/
	public void printBatches() {
		for (BatchData b : this.getBatches().list) {
			System.out.println("Batch " + b.getTimestamp());
		}
	}

	/** sorts the holded BatchDataList by timestamp **/
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

	/**
	 * Run() of the batch handler thread
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();
		if (!isInit)
			this.init();

		while (t == thisThread && !this.isLastBatchSend()) {
			try {
				long startProcessing = System.currentTimeMillis();

				if (this.index == 0)
					this.currentBatch = this.getInitBatch();
				else {
					if (this.nextBatch == null) {
						this.currentBatch = this.getNextBatch();
					} else {
						this.currentBatch = this.nextBatch;
					}
				}
				// when time was slided adjust index here
				if (this.timeSlided) {
					timeSlided = false;
					index++;
				}

				// handover new batch
				this.mainFrame.updateData(this.currentBatch);
				// read next batch
				if (this.index < this.getBatches().size() - 1)
					this.nextBatch = this.getNextBatch();

				this.index++;

				long waitTime = this.getSpeed()
						- (System.currentTimeMillis() - startProcessing);
				if (waitTime > 0)
					Thread.sleep(waitTime);

				synchronized (this) {
					while (this.threadSuspended)
						wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** resets the batchhandler **/
	public void reset() throws InterruptedException {
		if (this.threadSuspended)
			this.togglePause();
		this.batches = new BatchDataList();
		this.currentBatch = null;
		this.nextBatch = null;
		this.stop();
		this.mainFrame.reset();
		this.setIndex(0);
	}

	/** starts the batchhandler **/
	public void start() {
		if (this.t == null) {
			Random random = new Random();
			this.t = new Thread(this, "BatchHandler-Thread"
					+ random.nextFloat());
			this.threadSuspended = false;
			System.out.println("New Thread: " + t);
			this.t.start();
		}
	}

	/** stops the batchhandler **/
	public void stop() {
		this.isInit = false;
		t = null;
	}

	/** pause / unpause the batchhandler **/
	public synchronized void togglePause() {
		this.threadSuspended = !this.threadSuspended;
		if (!this.threadSuspended)
			notify();
	}

	/** pause the batchahndler **/
	public synchronized void setPaused(boolean paused) {
		this.threadSuspended = paused;
		if (!this.threadSuspended)
			notify();
	}

	/** register new mainFrame to the batchhandler **/
	public void registerMainFrame(MainDisplay mainFrame) {
		this.mainFrame = mainFrame;
	}

	/** returns the mainframe the batch handler belongs to **/
	public MainDisplay getMainFrame() {
		return this.mainFrame;
	}

	/** initializes by sending the first batch to the mainwindow **/
	public void init() {
		this.mainFrame.initData(this.getInitBatch());
		this.index = 0;
		this.isInit = true;
	}

	/** clears the batches list **/
	public void resetBatches() {
		this.batches = new BatchDataList();
		this.index = 0;
	}

	/** reads and returns the next batch **/
	public BatchData readNextBatch() {
		try {
			return BatchData.read(
					Dir.getBatchDataDir(this.dir, this.index + 1),
					this.index + 1, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** reads and returns a batch from the filesystem **/
	public BatchData readBatch(int index) {
		try {
			long timestamp = this.getBatches().get(index).getTimestamp();
			BatchData tempBatch = BatchData.read(
					Dir.getBatchDataDir(this.dir, timestamp), timestamp, true);
			return tempBatch;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** sets the time to a specific timestamp and pauses **/
	public long setTime(int timeValue) {
		long bestMatchingTimestamp = 0;
		// find the best matching batch
		for (int i = 0; i < this.getBatches().size(); i++) {
			BatchData b = this.getBatches().get(i);
			if (b.getTimestamp() >= timeValue) {
				bestMatchingTimestamp = b.getTimestamp();
				this.nextBatch = null;
				this.index = i;
				break;
			}
			if (b.getTimestamp() > timeValue) {
				try {
					bestMatchingTimestamp = this.getBatches().get(i - 1)
							.getTimestamp();
					this.nextBatch = null;
					this.index = i - 1;
					break;
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		// send best matching batch to mainFrame
		try {
			mainFrame.updateData(BatchData.read(
					Dir.getBatchDataDir(this.getDir(), bestMatchingTimestamp),
					bestMatchingTimestamp, true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set time slided flag
		this.timeSlided = true;

		// return the timestamp
		return bestMatchingTimestamp;
	}

}
