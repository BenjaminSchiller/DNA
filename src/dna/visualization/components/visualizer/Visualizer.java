package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyManualTicks;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import dna.util.Config;
import dna.visualization.MainDisplay;

public class Visualizer extends JPanel {
	// components
	protected Dimension defaultVisualizerSize = new Dimension(670, 410);

	// menu bar
	protected MenuBar menuBar;
	protected Dimension defaultMenuBarSize = new Dimension(655, 50);

	// legend
	protected Legend legend;
	protected Dimension defaultLegendSize = new Dimension(190, 330);

	// chart and axis
	protected Chart2D chart;
	protected Dimension defaultChartSize = new Dimension(450, 320);
	protected IAxis xAxis;
	protected IAxis yRight;
	protected IAxis yLeft;

	// fonts
	protected Font defaultFont = MainDisplay.defaultFont;
	protected Font defaultFontBorders = MainDisplay.defaultFontBorders;

	// timestamps
	protected long minTimestamp;
	protected long maxTimestamp;
	protected long minShownTimestamp;
	protected long maxShownTimestamp;

	protected int TRACE_LENGTH;
	protected Boolean FIXED_VIEWPORT;

	private GridBagConstraints mainConstraints;

	// constructor
	public Visualizer() {
		// initialization
		this.setPreferredSize(this.defaultVisualizerSize);

		this.TRACE_LENGTH = Config.getInt("DEFAULT_TRACE_LENGTH");
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
		this.chart.setPreferredSize(this.defaultChartSize);

		// axis configuration
		this.xAxis = this.chart.getAxisX();
		this.yLeft = this.chart.getAxisY();
		this.xAxis.setAxisTitle(new AxisTitle("Timestamp"));
		this.yLeft.setAxisTitle(new AxisTitle(""));
		this.yRight = new AxisLinear();
		this.chart.addAxisYRight((AAxis) yRight);
		this.yRight.setVisible(false);

		// add chart to visualizer
		this.mainConstraints.gridx = 0;
		this.mainConstraints.gridy = 0;
		this.chart.setPaintLabels(false);
		this.add(this.chart, this.mainConstraints);

		// init and add legend
		this.legend = new Legend(this);
		this.legend.setPreferredSize(this.defaultLegendSize);
		this.mainConstraints.gridx = 1;
		this.mainConstraints.gridy = 0;
		this.add(this.legend, this.mainConstraints);

		// general settings
		this.xAxis.setMajorTickSpacing(1.0);
		this.xAxis.setStartMajorTick(true);
		AxisScalePolicyManualTicks manualTickScalePolicy = new AxisScalePolicyManualTicks();
		this.xAxis.setAxisScalePolicy(manualTickScalePolicy);
	}

	protected void addMenuBar(Dimension size, boolean addCoordsPanel,
			boolean addIntervalPanel, boolean addXOptionsPanel,
			boolean addYLeftOptionsPanel, boolean addYRightOptionsPanel,
			boolean addSortOptionsPanel) {
		this.mainConstraints.gridx = 0;
		this.mainConstraints.gridy = 1;
		this.mainConstraints.gridwidth = 2;
		this.menuBar = new MenuBar(this, size, addCoordsPanel,
				addIntervalPanel, addXOptionsPanel, addYLeftOptionsPanel,
				addYRightOptionsPanel, addSortOptionsPanel);
		this.add(this.menuBar, this.mainConstraints);
	}

	/** handles the ticks that are shown on the x axis **/
	protected void updateXTicks() {
		double minTemp = 0;
		double maxTemp = 10;
		if (this.xAxis.getRangePolicy() instanceof RangePolicyUnbounded) {
			minTemp = this.minTimestamp * 1.0;
			maxTemp = this.maxTimestamp * 1.0;
		} else {
			if (this.xAxis.getRangePolicy() instanceof RangePolicyFixedViewport) {
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
				this.xAxis.setMajorTickSpacing(tickSpacingNew);
				this.xAxis.setMinorTickSpacing(tickSpacingNew);
			}
		}
	}

	/** handles the ticks that are shown on the y axis **/
	// TODO: FINISH
	protected void updateYTicks() {
		double minTemp = 0;
		double maxTemp = 10;

		if (this.xAxis.getRangePolicy() instanceof RangePolicyUnbounded) {
			this.yRight.setRangePolicy(new RangePolicyUnbounded());
			this.yLeft.setRangePolicy(new RangePolicyUnbounded());
		} else {
			if (this.xAxis.getRangePolicy() instanceof RangePolicyFixedViewport) {
				// System.out.println("MAX: " + this.yLeft.getMax() + " "
				// + this.yLeft.getMaxValue());
				// System.out.println("MIN: " + this.yLeft.getMin() + " "
				// + this.yLeft.getMinValue());
			}

			this.yRight.getTraces();
		}
	}

	/** toggles grid on left y axis **/
	public void toggleYLeftGrid() {
		if (this.yLeft.isPaintGrid())
			this.yLeft.setPaintGrid(false);
		else
			this.yLeft.setPaintGrid(true);
	}

	/** toggles grid on right y axis **/
	public void toggleYRightGrid() {
		if (this.yRight.isPaintGrid())
			this.yRight.setPaintGrid(false);
		else
			this.yRight.setPaintGrid(true);
	}

	/** toggles grid on x axis **/
	public void toggleXGrid() {
		if (this.xAxis.isPaintGrid())
			this.xAxis.setPaintGrid(false);
		else
			this.xAxis.setPaintGrid(true);
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

	public IAxis getXAxis() {
		return this.xAxis;
	}

	public IAxis getYRightAxis() {
		return this.yRight;
	}

	public IAxis getYLeftAxis() {
		return this.yLeft;
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

	/** toggle right y axis visibility **/
	public void toggleYAxisVisibility() {
		for (IAxis rightAxe : this.chart.getAxesYRight()) {
			if (rightAxe.getTraces().size() < 1)
				rightAxe.setVisible(false);
			else
				rightAxe.setVisible(true);
		}
	}

	/** clears all list items in the legend **/
	public void clearList() {
		this.legend.reset();
	}
}
