package dna.visualization;

import java.io.IOException;
import java.util.Random;

import dna.series.data.BatchData;
import dna.series.lists.BatchDataList;
import dna.visualization.components.StatsDisplay;

/**
 * A batchhandler is used to read a run from the filesystem and simulate it by
 * sending the batches to the GUI in a given time intervall.
 * 
 * @author Rwilmes
 */
public class BatchHandler implements Runnable {

	// class variables
	private String dir;
	private BatchDataList batches;
	private int index;
	private StatsDisplay statsFrame;
	private MainDisplay mainFrame;

	private boolean isInit;
	private boolean threadSuspended;

	private Thread t;

	final private int INITIAL_WAIT_TIME = 1000;
	private int speed = 500;

	// constructors
	public BatchHandler(String dir) {
		this.dir = dir;
		this.batches = new BatchDataList();
		this.index = 0;
		this.isInit = false;
		this.threadSuspended = false;
	}

	public BatchHandler(String dir, StatsDisplay statsFrame,
			MainDisplay mainFrame) {
		this.dir = dir;
		this.statsFrame = statsFrame;
		this.mainFrame = mainFrame;
		this.batches = new BatchDataList();
		this.index = 0;
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

	public int getIndex() {
		return this.index;
	}

	public int getSpeed() {
		return this.speed;
	}

	public int getAmountOfBatches() {
		return this.batches.size();
	}

	public long getMaxTimestamp() {
		return this.getMinTimestamp() + this.getAmountOfBatches() - 1;
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

	/** returns the next batch **/
	public BatchData getNextBatch() {
		if (this.getIndex() < this.getBatches().size()) {
			return this.batches.get(this.index++);
		} else {
			return null;
		}
	}

	/** adds new batches from the filesystem to the batches **/
	public void updateBatches() {
		try {
			BatchDataList tempBatches = BatchDataList.read(this.getDir(), true);
			if (this.getBatches().size() <= tempBatches.size()) {
				int offset = tempBatches.size() - this.getBatches().size();
				for (BatchData b : tempBatches.list) {
					this.getBatches().add(b);
				}
			}
			this.sortBatches();

		} catch (IOException e) {
			System.out
					.println("Error in BatchHandler while attempting to read batches.");
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

	/** returns lowest timestamp **/
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

	/**
	 * Run() of the batch handler thread
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();
		if (!isInit)
			this.init();

		while (t == thisThread && this.isNewBatchAvailable()) {
			try {
				long startProcessing = System.currentTimeMillis();
				BatchData tempBatch = this.getNextBatch();
				this.mainFrame.updateData(tempBatch);
				long waitTime = this.getSpeed()
						- (System.currentTimeMillis() - startProcessing);
				if (waitTime > 0)
					this.t.sleep(waitTime);

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
		this.mainFrame.initData(this.getNextBatch());
		this.isInit = true;
	}

	/** clears the batches list **/
	public void resetBatches() {
		this.batches = new BatchDataList();
		this.index = 0;
	}
}
