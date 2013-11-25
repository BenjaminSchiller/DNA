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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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

	// constructor
	@SuppressWarnings("rawtypes")
	public Visualizer() {
		// initialization
		this.setPreferredSize(GuiOptions.visualizerDefaultSize);

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
		this.yAxis1.setFormatter(new LabelFormatterNumber(
				GuiOptions.visualizerYAxisDecimalFormat));
		// x2
		this.xAxis2 = new AxisLinear();
		this.xAxis2.setVisible(false);
		this.chart.addAxisXBottom((AAxis) this.xAxis2);
		// y2
		this.yAxis2 = new AxisLinear(new LabelFormatterNumber(
				GuiOptions.visualizerYAxisDecimalFormat));
		this.chart.addAxisYRight((AAxis) yAxis2);
		this.yAxis2.setVisible(false);
		this.yAxis2.setAxisTitle(new AxisTitle("y2"));

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
		/*
		 * double min = this.yAxis1.getMin(); double max = this.yAxis1.getMax();
		 * System.out.println("y1 min:" + min + " max:" + max);
		 * 
		 * double range = max - min; if (range > 1) { double tickSpacingNew =
		 * range / 10; this.yAxis1.setMajorTickSpacing(tickSpacingNew);
		 * this.yAxis1.setMinorTickSpacing(tickSpacingNew);
		 * 
		 * System.out.println(range + " > " + tickSpacingNew); } else {
		 * this.yAxis1.setMajorTickSpacing(1.0);
		 * this.yAxis1.setMinorTickSpacing(1.0); }
		 */

	}

	/** handles the ticks that are shown on the y axis **/
	protected void updateY2Ticks() {
		/*
		 * double min = this.yAxis2.getMin(); double max = this.yAxis2.getMax();
		 * System.out.println("y2 min:" + min + " max:" + max);
		 * 
		 * double range = max - min; if (range > 0) { double tickSpacingNew =
		 * range / 10; this.yAxis2.setMajorTickSpacing(tickSpacingNew);
		 * this.yAxis2.setMinorTickSpacing(tickSpacingNew);
		 * 
		 * System.out.println(range + " > " + tickSpacingNew); } else {
		 * this.yAxis2.setMajorTickSpacing(1.0);
		 * this.yAxis2.setMinorTickSpacing(1.0); }
		 */

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
}
