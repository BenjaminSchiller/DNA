package dna.visualization;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Random;

import name.pachler.nio.file.ClosedWatchServiceException;
import name.pachler.nio.file.StandardWatchEventKind;
import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.BatchData;
import dna.series.lists.BatchDataList;
import dna.util.Log;

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
	private boolean liveDisplay;
	private boolean batchesZipped;

	private Thread t;

	private int speed = 500;
	private int dirTimeout = 120;

	// constructors
	public BatchHandler(String dir, MainDisplay mainFrame, boolean liveDisplay,
			boolean batchesZipped) {
		this.dir = dir;
		this.mainFrame = mainFrame;
		this.batches = new BatchDataList();
		this.index = 0;
		this.timeSlided = false;
		this.isInit = false;
		this.threadSuspended = false;
		this.liveDisplay = liveDisplay;
		this.batchesZipped = batchesZipped;
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
			long timestamp = this.getBatches().get(0).getTimestamp();
			BatchData tempBatch;
			if (this.batchesZipped)
				tempBatch = BatchData.readFromSingleFile(this.dir, timestamp,
						Dir.delimiter, BatchReadMode.readAllValues);
			else
				tempBatch = BatchData.read(
						Dir.getBatchDataDir(this.dir, timestamp), timestamp,
						BatchReadMode.readAllValues);
			return tempBatch;
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

	/** adds new batches from the filesystem to the batches. **/
	public void updateBatches() throws IOException {
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
			BatchDataList tempBatches = BatchDataList.read(this.getDir(),
					BatchReadMode.readNoValues);
			if (this.getBatches().size() <= tempBatches.size()) {
				for (BatchData b : tempBatches.list) {
					this.getBatches().add(b);
				}
			}
			this.sortBatches();

		} catch (IOException e) {
			Log.error("Error in BatchHandler while attempting to read timestamps.");
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
		for (BatchData b : this.getBatches().list) {
			tempBatches.add(b);
		}
		this.batches = tempBatches;
	}

	/**
	 * Run() of the batch handler thread
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();

		// live display
		if (liveDisplay) {
			try {
				// check if directory is present, if not wait until it is
				File f = new File(this.dir);
				if (!f.exists() && !f.isDirectory()) {
					Log.info("Directory '" + this.dir
							+ "' missing, checking it for " + this.dirTimeout
							+ " seconds.");
					int checkCounter = 0;
					this.mainFrame.setStatusMessage("Dir missing.. "
							+ checkCounter);
					while (!f.exists() && !f.isDirectory()
							&& checkCounter < this.dirTimeout) {
						checkCounter++;
						Thread.sleep(1000);
						this.mainFrame.setStatusMessage("Dir missing.. "
								+ checkCounter);
					}
				}
				// check again if dir is now present, if not end thread
				if (f.exists() && f.isDirectory()) {
					this.mainFrame.setStatusMessage("Waiting for Batches..");
					if (System.getProperty("os.name").startsWith("Windows")) {
						// windows machine -> use java nio watchservice with
						// native
						// filesystem implementation

						// setting up watch-service
						FileSystem fs = FileSystems.getDefault();
						WatchService watcher = fs.newWatchService();
						Path p = fs.getPath(this.dir);
						WatchKey key = p.register(watcher,
								StandardWatchEventKinds.ENTRY_CREATE);

						Log.infoSep();
						Log.info("Windows operating system discovered.. using java.nio.watchservice");
						Log.info("Watching directory: " + p.toString());
						while (t == thisThread) {

							for (WatchEvent<?> event : key.pollEvents()) {
								WatchEvent<Path> ev = (WatchEvent<Path>) event;

								Path filename = ev.context();
								Path child = p.resolve(filename);

								String filenameString = filename.toString();

								String[] parts = filenameString.split("\\.");

								if (parts.length > 1
										&& parts[0].equals("batch")) {
									String suffix = parts[parts.length - 1];
									if (suffix.charAt(suffix.length() - 1) != '_') {
										// Log.info("new batch ready: " +
										// child.toString());

										// read batch
										BatchData batch;
										if (this.batchesZipped)
											batch = BatchData
													.readFromSingleFile(
															this.dir,
															Long.parseLong(parts[1]),
															Dir.delimiter,
															BatchReadMode.readAllValues);
										else
											batch = BatchData
													.read(Dir
															.getBatchDataDir(
																	this.dir,
																	Long.parseLong(suffix)),
															Long.parseLong(suffix),
															BatchReadMode.readAllValues);

										// hand over batch
										if (!this.isInit) {
											this.mainFrame.initData(batch);
											this.isInit = true;
										} else {
											this.mainFrame.updateData(batch);
										}
									} else {
										/*
										 * Log.info("new batch generation started: "
										 * + child.toString());
										 */
									}
								}
							}
							key.reset();

							synchronized (this) {
								while (this.threadSuspended)
									wait();
							}
						}

					} else {
						// not windows os, probably mac -> use jpathwatch
						// library
						name.pachler.nio.file.WatchService watchService = name.pachler.nio.file.FileSystems
								.getDefault().newWatchService();

						name.pachler.nio.file.Path p = name.pachler.nio.file.Paths
								.get(this.dir);

						name.pachler.nio.file.WatchKey key = p.register(
								watchService,
								StandardWatchEventKind.ENTRY_CREATE);

						Log.infoSep();
						Log.info("No windows operating system.. using jpathwatch-0-95 library");
						Log.info("Watching directory: '" + this.dir + "'");
						while (t == thisThread) {
							// take event key
							name.pachler.nio.file.WatchKey eventKey;
							try {
								eventKey = watchService.take();
							} catch (InterruptedException e) {
								continue;
							} catch (ClosedWatchServiceException e) {
								Log.info("Watch service closed, terminating.");
								break;
							}

							List<name.pachler.nio.file.WatchEvent<?>> list = eventKey
									.pollEvents();

							eventKey.reset();

							for (name.pachler.nio.file.WatchEvent e : list) {
								if (e.kind() == StandardWatchEventKind.ENTRY_CREATE) {
									name.pachler.nio.file.Path filename = (name.pachler.nio.file.Path) e
											.context();
									name.pachler.nio.file.Path child = p
											.resolve(filename);

									String filenameString = filename.toString();

									String[] parts = filenameString
											.split("\\.");

									if (parts.length > 1
											&& parts[0].equals("batch")) {
										String suffix = parts[parts.length - 1];
										if (suffix.charAt(suffix.length() - 1) != '_') {
											// Log.info("new batch ready: " +
											// child.toString());

											// read batch
											BatchData batch = BatchData
													.read(Dir
															.getBatchDataDir(
																	this.dir,
																	Long.parseLong(suffix)),
															Long.parseLong(suffix),
															BatchReadMode.readAllValues);

											// hand over batch
											if (!this.isInit) {
												this.mainFrame.initData(batch);
												this.isInit = true;
											} else {
												this.mainFrame
														.updateData(batch);
											}
										} else {
											/*
											 * Log.info(
											 * "new batch generation started: "
											 * + child.toString());
											 */
										}
									}
								}
							}
						}
						synchronized (this) {
							while (this.threadSuspended)
								wait();
						}
					}
				} else {
					Log.info("Directory '" + this.dir
							+ "' still missing, ending batchHandler thread.");
					this.mainFrame.setStatusMessage("Idle");
					this.stop();
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			// playback mode
			if (!isInit)
				this.init();

			while (t == thisThread && !this.isLastBatchSend()) {
				try {
					long startProcessing = System.currentTimeMillis();

					if (this.index == 0 && !this.timeSlided)
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
	}

	/** resets the batchhandler **/
	public void reset() throws InterruptedException {
		Log.info("Resetting BatchHandler");
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
			Log.info("Starting BatchHandler in new thread: " + t);
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

	/** pause the batchhandler **/
	public synchronized void setPaused(boolean paused) {
		this.threadSuspended = paused;
		if (!this.threadSuspended)
			notify();
	}

	/** returns if the batchhandler is paused **/
	public boolean isPaused() {
		return this.threadSuspended;
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
		if (!this.liveDisplay) {
			this.mainFrame.initData(this.getInitBatch());
			this.index = 0;
			this.isInit = true;
		}
	}

	/** returns if the batchHandler is initialized **/
	public boolean isInit() {
		return this.isInit;
	}

	/** clears the batches list **/
	public void resetBatches() {
		this.batches = new BatchDataList();
		this.index = 0;
	}

	/** reads and returns a batch from the filesystem **/
	public BatchData readBatch(int index) {
		try {
			long timestamp = this.getBatches().get(index).getTimestamp();
			BatchData tempBatch;
			if (this.batchesZipped)
				tempBatch = BatchData.readFromSingleFile(this.dir, timestamp,
						Dir.delimiter, BatchReadMode.readAllValues);
			else
				tempBatch = BatchData.read(
						Dir.getBatchDataDir(this.dir, timestamp), timestamp,
						BatchReadMode.readAllValues);
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
			BatchData tempBatch;
			if (this.batchesZipped)
				tempBatch = BatchData.readFromSingleFile(this.getDir(),
						bestMatchingTimestamp, Dir.delimiter,
						BatchReadMode.readAllValues);
			else
				tempBatch = BatchData.read(
						Dir.getBatchDataDir(this.dir, bestMatchingTimestamp),
						bestMatchingTimestamp, BatchReadMode.readAllValues);
			mainFrame.updateData(tempBatch);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set time slided flag
		this.timeSlided = true;

		// return the timestamp
		return bestMatchingTimestamp;
	}

	/** returns timestamp of the next smaller batch **/
	public long getNextTimestamp(long timestamp) {
		if (timestamp >= this.getMaxTimestamp())
			return this.getMaxTimestamp();
		for (int i = 0; i < this.getBatches().size(); i++) {
			BatchData b = this.getBatches().get(i);
			if (b.getTimestamp() > timestamp)
				return b.getTimestamp();
		}
		Log.error("Error calculating next timestamp, returning 0");
		return 0;
	}

	/** returns timestamp of the next bigger batch **/
	public long getPreviousTimestamp(long timestamp) {
		if (this.getMinTimestamp() >= timestamp)
			return this.getMinTimestamp();
		for (int i = 0; i < this.getBatches().size() - 1; i++) {
			if (this.getBatches().get(i + 1).getTimestamp() >= timestamp)
				return this.getBatches().get(i).getTimestamp();
		}
		Log.error("Error calculating next timestamp, returning 0");
		return 0;
	}

	/** returns the amount of timestamps before the timestamp batch **/
	public int getAmountOfPreviousTimestamps(long timestamp) {
		int counter = 0;
		for (BatchData b : this.getBatches().list) {
			if (b.getTimestamp() < timestamp)
				counter++;
			else
				return counter;
		}
		return counter;
	}
}
