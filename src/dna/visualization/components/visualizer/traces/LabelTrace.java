package dna.visualization.components.visualizer.traces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import dna.labels.Label;
import dna.visualization.components.visualizer.LabelVisualizer;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;

/**
 * The LabelTrace represents one entire trace of a single labeler-type pair in a
 * label-visualizer. It encapsulates and handles all logical traces that are
 * actually being used.
 * 
 * @author Rwilmes
 *
 */
public class LabelTrace {

	protected LabelVisualizer parent;
	protected Chart2D chart;
	protected String key;

	protected int yMapping;

	protected int size;
	protected Color color;

	protected double initTimestamp;
	protected double lastTimestamp;

	protected boolean active;
	protected HashMap<Double, ITrace2D> currentTraces;
	protected ArrayList<ITrace2D> removedTraces;

	public LabelTrace(LabelVisualizer parent, Chart2D chart, String key, int yMapping, int size, Color color,
			double initTimestamp) {
		this.parent = parent;
		this.chart = chart;
		this.key = key;
		this.yMapping = yMapping;
		this.size = size;
		this.color = color;

		this.initTimestamp = initTimestamp;
		this.lastTimestamp = initTimestamp;

		this.active = false;
		this.currentTraces = new HashMap<Double, ITrace2D>();
		this.removedTraces = new ArrayList<ITrace2D>();
	}

	/**
	 * Called for each batch to update the trace. If label was not present in
	 * the batch the handed over Label-object should be null.
	 **/
	public void update(double timestamp, Label label) {
		// System.out.println("adding from : " + this.lastTimestamp + " --> " +
		// timestamp);
		if (label != null) {
			if (active) {
				// add points to all traces
				for (double y : this.currentTraces.keySet()) {
					this.currentTraces.get(y).addPoint(timestamp, y);
				}
			} else {
				// init new traces and add points from last-timestamp to next
				// double start = this.yMapping + this.padding;
				// double middle = this.yMapping;
				// double steps = this.steps; // number of traces per
				// label-trace
				// int halfSteps = (int) Math.floor(steps / 2);
				//
				// double range = start - middle;
				// double stepSize = range / halfSteps;
				//
				// // for each step init own trace
				// for (int i = 0; i < steps; i++) {
				// double y = start - (stepSize * i);
				//
				// Trace2DLtd newTrace = new
				// Trace2DLtd(this.parent.getTraceLength());
				// newTrace.setColor(this.color);
				//
				// BasicStroke stroke = new BasicStroke(this.size);
				// stroke = new BasicStroke(50, BasicStroke.CAP_BUTT,
				// BasicStroke.JOIN_BEVEL);
				//
				// newTrace.setStroke(stroke);
				// this.chart.addTrace(newTrace);
				// newTrace.addTracePainter(new TracePainterLine());
				// newTrace.addPoint(this.lastTimestamp, y);
				// newTrace.addPoint(timestamp, y);
				//
				// this.currentTraces.put(y, newTrace);
				// }

				double y = this.yMapping;

				Trace2DLtd newTrace = new Trace2DLtd(this.parent.getTraceLength());
				newTrace.setColor(this.color);
				newTrace.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
				this.chart.addTrace(newTrace);
				newTrace.addTracePainter(new TracePainterLine());

				// add point t-1
				if (this.lastTimestamp == timestamp)
					newTrace.addPoint(timestamp - 1, y);
				else
					newTrace.addPoint(lastTimestamp, y);

				// add point t
				newTrace.addPoint(timestamp, y);

				// add trace to current traces
				this.currentTraces.put(y, newTrace);

				// mark as active
				this.active = true;
			}
		} else {
			// label is null
			if (active) {
				// move all current traces to removed traces
				this.removeCurrentTraces();

				// mark as inactive
				this.active = false;
			}
		}

		// update last timestamp to this one
		this.lastTimestamp = timestamp;
	}

	/** Clears all traces associated with this object. **/
	public void clear() {
		this.removeCurrentTraces();
		for (ITrace2D trace : this.removedTraces) {
			trace.removeAllPoints();
			trace.removeAllPointHighlighters();
			this.chart.removeTrace(trace);
		}
		this.removedTraces.clear();
	}

	/** Moves all current traces to the removed traces. **/
	protected void removeCurrentTraces() {
		for (Double y : this.currentTraces.keySet())
			this.removedTraces.add(this.currentTraces.get(y));

		this.currentTraces.clear();
	}

	/** Sets the last-timestamp. **/
	public void setLastTimestamp(double timestamp) {
		this.lastTimestamp = timestamp;
	}

	/** Returns the traces y-mapping. **/
	public int getYMapping() {
		return this.yMapping;
	}

	/** Sets a new size and updates all traces. **/
	public void setSize(int size) {
		this.size = size;
		updateTraceSizes();
	}

	/** Updates the size of all traces. **/
	protected void updateTraceSizes() {
		for (Double y : this.currentTraces.keySet()) {
			ITrace2D trace = this.currentTraces.get(y);
			trace.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		}
		for (ITrace2D trace : this.removedTraces) {
			trace.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		}
	}

}
