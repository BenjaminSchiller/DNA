package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyManualTicks;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterNumber;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JPanel;

import dna.util.Config;
import dna.visualization.GuiOptions;

@SuppressWarnings("serial")
public class Visualizer extends JPanel {
	// components
	protected MenuBar menuBar;
	protected Legend legend;

	// chart and axis
	protected Chart2D chart;
	@SuppressWarnings("rawtypes")
	protected IAxis xAxis1;
	@SuppressWarnings("rawtypes")
	protected IAxis xAxis2;
	@SuppressWarnings("rawtypes")
	protected IAxis yAxis2;
	@SuppressWarnings("rawtypes")
	protected IAxis yAxis1;

	// timestamps
	protected long minTimestamp;
	protected long maxTimestamp;
	protected long minShownTimestamp;
	protected long maxShownTimestamp;

	protected int TRACE_LENGTH;
	protected Boolean FIXED_VIEWPORT;

	// constraints
	private GridBagConstraints mainConstraints;

	// enumerations for axis selection
	public enum xAxisSelection {
		x1, x2
	};

	public enum yAxisSelection {
		y1, y2
	};

	// shows if the UI is paused or not
	protected boolean paused;

	// constructor
	@SuppressWarnings("rawtypes")
	public Visualizer() {
		// initialization
		this.setPreferredSize(GuiOptions.visualizerDefaultSize);
		this.paused = true;

		this.TRACE_LENGTH = Config.getInt("GUI_TRACE_LENGTH");
		this.FIXED_VIEWPORT = false;
		this.minTimestamp = 0;
		this.maxTimestamp = 0;
		this.minShownTimestamp = 0;
		this.maxShownTimestamp = 10;

		this.mainConstraints = new GridBagConstraints();
		this.mainConstraints.fill = GridBagConstraints.HORIZONTAL;

		// set layout
		this.setLayout(new GridBagLayout());

		// init chart
		this.chart = new Chart2D();
		this.chart.setPreferredSize(GuiOptions.visualizerDefaultChartSize);

		/*
		 * axis configuration
		 */
		// x1
		this.xAxis1 = this.chart.getAxisX();
		this.xAxis1.setAxisTitle(new AxisTitle("Timestamp"));

		// y1
		this.yAxis1 = this.chart.getAxisY();
		this.yAxis1.setAxisTitle(new AxisTitle("y1"));
		this.yAxis1.setFormatter(new LabelFormatterNumber(new DecimalFormat(
				"0.0")));

		// x2
		this.xAxis2 = new AxisLinear();
		this.xAxis2.setVisible(false);
		this.chart.addAxisXBottom((AAxis) this.xAxis2);

		// y2
		this.yAxis2 = new AxisLinear(new LabelFormatterNumber(
				new DecimalFormat("0.0")));
		this.yAxis2 = new AxisLinear();
		this.yAxis2.setVisible(false);
		this.yAxis2.setAxisTitle(new AxisTitle("y2"));
		this.chart.addAxisYRight((AAxis) yAxis2);

		// add chart to visualizer
		this.mainConstraints.gridx = 0;
		this.mainConstraints.gridy = 0;
		this.chart.setPaintLabels(false);
		this.add(this.chart, this.mainConstraints);

		// init and add legend
		this.legend = new Legend(this);
		this.legend.setPreferredSize(GuiOptions.visualizerDefaultLegendSize);
		this.mainConstraints.gridx = 1;
		this.mainConstraints.gridy = 0;
		this.add(this.legend, this.mainConstraints);

		// general settings for x1
		this.xAxis1.setMajorTickSpacing(1.0);
		this.xAxis1.setStartMajorTick(true);
		AxisScalePolicyManualTicks manualTickScalePolicy = new AxisScalePolicyManualTicks();
		this.xAxis1.setAxisScalePolicy(manualTickScalePolicy);

		// general settings for x2
		this.xAxis2.setMajorTickSpacing(1.0);
		this.xAxis2.setStartMajorTick(true);
		this.xAxis2.setAxisScalePolicy(manualTickScalePolicy);
	}

	protected void addMenuBar(Dimension size, boolean addCoordsPanel,
			boolean addIntervalPanel, boolean addXOptionsPanel,
			boolean addYLeftOptionsPanel, boolean addYRightOptionsPanel) {
		this.mainConstraints.gridx = 0;
		this.mainConstraints.gridy = 1;
		this.mainConstraints.gridwidth = 2;
		this.menuBar = new MenuBar(this, size, addCoordsPanel,
				addIntervalPanel, addXOptionsPanel, addYLeftOptionsPanel,
				addYRightOptionsPanel);
		this.add(this.menuBar, this.mainConstraints);
	}

	/** handles the ticks that are shown on the x axis **/
	protected void updateX1Ticks() {
		double minTemp = 0;
		double maxTemp = 10;
		if (this.xAxis1.getRangePolicy() instanceof RangePolicyUnbounded) {
			minTemp = this.minTimestamp * 1.0;
			maxTemp = this.maxTimestamp * 1.0;
		} else {
			if (this.xAxis1.getRangePolicy() instanceof RangePolicyFixedViewport) {
				minTemp = this.minShownTimestamp;
				maxTemp = this.maxShownTimestamp;
			}
		}
		if (maxTemp > minTemp) {
			double range = maxTemp - minTemp;
			if (range > 0) {
				double tickSpacingNew = Math.floor(range / 10);
				if (tickSpacingNew < 1)
					tickSpacingNew = 1.0;
				this.xAxis1.setMajorTickSpacing(tickSpacingNew);
				this.xAxis1.setMinorTickSpacing(tickSpacingNew);
			}
		}
	}

	/** handles the ticks that are shown on the y axis **/
	protected void updateY1Ticks() {
		double min = 0;
		double max = 0;
		boolean init = false;
		// calculate the visible min and max values
		for (Object t : this.yAxis1.getTraces()) {
			if (t instanceof Trace2DLtd) {
				if (!init) {
					min = ((Trace2DLtd) t).getMinY();
					max = ((Trace2DLtd) t).getMaxY();
					init = true;
				}
				if (((Trace2DLtd) t).getMinY() < min)
					min = ((Trace2DLtd) t).getMinY();
				if (((Trace2DLtd) t).getMaxY() > max)
					max = ((Trace2DLtd) t).getMaxY();
			}
			if (t instanceof Trace2DSimple) {
				if (!init) {
					min = ((Trace2DSimple) t).getMinY();
					max = ((Trace2DSimple) t).getMaxY();
					init = true;
				}
				if (((Trace2DSimple) t).getMinY() < min)
					min = ((Trace2DSimple) t).getMinY();
				if (((Trace2DSimple) t).getMaxY() > max)
					max = ((Trace2DSimple) t).getMaxY();
			}
		}
		// select format
		DecimalFormat decimalFormatNew = selectFormat(min, max);

		decimalFormatNew.setGroupingSize(3);
		decimalFormatNew.setGroupingUsed(true);

		this.yAxis1.setFormatter(new LabelFormatterNumber(decimalFormatNew));
	}

	/** handles the ticks that are shown on the y axis **/
	protected void updateY2Ticks() {
		double min = 0;
		double max = 0;
		boolean init = false;
		// calculate the visible min and max values
		for (Object t : this.yAxis2.getTraces()) {
			if (t instanceof Trace2DLtd) {
				if (!init) {
					min = ((Trace2DLtd) t).getMinY();
					max = ((Trace2DLtd) t).getMaxY();
					init = true;
				}
				if (((Trace2DLtd) t).getMinY() < min)
					min = ((Trace2DLtd) t).getMinY();
				if (((Trace2DLtd) t).getMaxY() > max)
					max = ((Trace2DLtd) t).getMaxY();
			}
			if (t instanceof Trace2DSimple) {
				if (!init) {
					min = ((Trace2DSimple) t).getMinY();
					max = ((Trace2DSimple) t).getMaxY();
					init = true;
				}
				if (((Trace2DSimple) t).getMinY() < min)
					min = ((Trace2DSimple) t).getMinY();
				if (((Trace2DSimple) t).getMaxY() > max)
					max = ((Trace2DSimple) t).getMaxY();
			}
		}
		// select format
		DecimalFormat format = selectFormat(min, max);

		// set format
		this.yAxis2.setFormatter(new LabelFormatterNumber(format));
	}

	/** selects the decimalformat for y-axis based on min and max values **/
	private DecimalFormat selectFormat(double min, double max) {
		NumberFormat f = NumberFormat.getInstance();
		if (f instanceof DecimalFormat) {
			if (min == 0 && max == 0) {
				((DecimalFormat) f).applyPattern("0.0");
				((DecimalFormat) f).setMaximumFractionDigits(1);
				((DecimalFormat) f).setMaximumIntegerDigits(1);
				return (DecimalFormat) f;
			} else {
				String patternTemp = "";
				double delta = max - min;
				if (delta != 0) {
					if (delta < 10000) {
						patternTemp = "0";
					}
					if (delta < 10) {
						patternTemp = "0.0";
					}
					if (delta < 1) {
						patternTemp = "0.00";
					}
					if (delta < 0.1) {
						patternTemp = "0.000";
					}
					if (delta < 0.01) {
						patternTemp = "0.0000";
					}
					if (delta < 0.001) {
						patternTemp = "0.0000";
					}
					if (delta < 0.0001) {
						patternTemp = "0.###E0";
					}
					((DecimalFormat) f).applyPattern(patternTemp);

					if (delta > 10000) {
						((DecimalFormat) f).applyPattern("0.0E0");
					}
				}
				return (DecimalFormat) f;
			}
		}
		return new DecimalFormat("0.0");
	}

	/** toggles grid on left y axis **/
	public void toggleY1Grid() {
		if (this.yAxis1.isPaintGrid())
			this.yAxis1.setPaintGrid(false);
		else
			this.yAxis1.setPaintGrid(true);
	}

	/** toggles grid on right y axis **/
	public void toggleY2Grid() {
		if (this.yAxis2.isPaintGrid())
			this.yAxis2.setPaintGrid(false);
		else
			this.yAxis2.setPaintGrid(true);
	}

	/** toggles grid on x axis **/
	public void toggleX1Grid() {
		if (this.xAxis1.isPaintGrid())
			this.xAxis1.setPaintGrid(false);
		else
			this.xAxis1.setPaintGrid(true);
	}

	public long getMinTimestamp() {
		return this.minTimestamp;
	}

	public void setMinTimestamp(long timestamp) {
		this.minTimestamp = timestamp;
	}

	public long getMaxTimestamp() {
		return this.maxTimestamp;
	}

	public void setMaxTimestamp(long timestamp) {
		this.maxTimestamp = timestamp;
	}

	public long getMinShownTimestamp() {
		return this.minShownTimestamp;
	}

	public void setMinShownTimestamp(long timestamp) {
		this.minShownTimestamp = timestamp;
	}

	public long getMaxShownTimestamp() {
		return this.maxShownTimestamp;
	}

	public void setMaxShownTimestamp(long timestamp) {
		this.maxShownTimestamp = timestamp;
	}

	@SuppressWarnings("rawtypes")
	public IAxis getX2Axis() {
		return this.xAxis2;
	}

	@SuppressWarnings("rawtypes")
	public IAxis getX1Axis() {
		return this.xAxis1;
	}

	@SuppressWarnings("rawtypes")
	public IAxis getY2Axis() {
		return this.yAxis2;
	}

	@SuppressWarnings("rawtypes")
	public IAxis getY1Axis() {
		return this.yAxis1;
	}

	public int getTraceLength() {
		return this.TRACE_LENGTH;
	}

	public void setTraceLength(int length) {
		this.TRACE_LENGTH = length;
	}

	public void setFixedViewport(boolean isViewportFixed) {
		this.FIXED_VIEWPORT = isViewportFixed;
	}

	public boolean isViewPortFixed() {
		return this.FIXED_VIEWPORT;
	}

	/**
	 * Toggles the visibility of y1 and y2-axis. When both axis are used, both
	 * are shown. When only one is used, only that one is shown. When none is
	 * used, only y1 is shown.
	 */
	@SuppressWarnings("rawtypes")
	public void toggleYAxisVisibility() {
		boolean rightAxisVisible = true;

		for (IAxis rightAxe : this.chart.getAxesYRight()) {
			if (rightAxe.getTraces().size() < 1) {
				rightAxe.setVisible(false);
				rightAxisVisible = false;
			} else {
				rightAxe.setVisible(true);
				rightAxisVisible = true;
			}
		}
		for (IAxis leftAxe : this.chart.getAxesYLeft()) {
			if (leftAxe.getTraces().size() > 0) {
				leftAxe.setVisible(true);
			} else {
				if (rightAxisVisible)
					leftAxe.setVisible(false);
				else
					leftAxe.setVisible(true);
			}
		}
	}

	/**
	 * Toggles the visibility of x1 and x2-axis. When both axis are used, both
	 * are shown. When only one is used, only that one is shown. When none is
	 * used, only x1 is shown.
	 */
	@SuppressWarnings("rawtypes")
	public void toggleXAxisVisibility() {
		for (IAxis axis : this.chart.getAxesXBottom()) {
			if (axis.getTraces().size() < 1)
				axis.setVisible(false);
			else
				axis.setVisible(true);
		}
		if (!this.xAxis2.isVisible() && !this.xAxis1.isVisible())
			this.xAxis1.setVisible(true);
	}

	/** clears all list items in the legend **/
	public void clearList() {
		this.legend.reset();
	}

	/** updates the ticks on all axis **/
	public void updateTicks() {
		this.updateX1Ticks();
		if (this instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this).updateX2Ticks();
		this.updateY1Ticks();
		this.updateY2Ticks();
	}

	/** Called when the UI gets pause/unpaused **/
	public void togglePause() {
		this.paused = !this.paused;
	}

}
