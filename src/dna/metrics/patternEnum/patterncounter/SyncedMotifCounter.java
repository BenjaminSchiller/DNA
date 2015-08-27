package dna.metrics.patternEnum.patterncounter;

import java.util.concurrent.BlockingQueue;

import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.subgfinder.ITraverseCounter.EdgeAction;

public class SyncedMotifCounter implements Runnable {

	private Thread thread;
	private BlockingQueue<Path> foundSyncedPaths;
	private EdgeAction edgeAction;
	private IPatternCounter motifCounter;
	
	public Thread getThread() {
		return thread;
	}
	
	public SyncedMotifCounter(BlockingQueue<Path> foundSyncedPaths, IPatternCounter motifCounter,
			EdgeAction edgeAction) {
		this.foundSyncedPaths = foundSyncedPaths;
		this.edgeAction = edgeAction;
		this.motifCounter = motifCounter;
	}
	
	public void start () {
		thread = new Thread (this);
		thread.start ();
  }
	
	@Override
	public void run() {
		while (true) {
			try {
				Path actPath = foundSyncedPaths.take();
				
				if (actPath.getGraph().getSize() == 0) {
					break;
				}
			
				if (edgeAction.equals(EdgeAction.added)) {
					if(actPath.hasChanged()) {
						motifCounter.decrementCounterFor(actPath.getPrevGraph());
					}
					motifCounter.incrementCounterFor(actPath.getGraph());
				} else {
					if(actPath.hasChanged()) {
						motifCounter.incrementCounterFor(actPath.getPrevGraph());
					}
					motifCounter.decrementCounterFor(actPath.getGraph());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
