package dna.visualization;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JFrame;

public class Charttest {

	private Charttest() {
		super();
	}

	public static void main(String[] args) {
		// Create a chart:
		Chart2D chart = new Chart2D();
		// Create an ITrace:
		ITrace2D trace = new Trace2DSimple();
		// Add the trace to the chart. This has to be done before adding points
		// (deadlock prevention):
		chart.addTrace(trace);
		// Add all points, as it is static:
		Random random = new Random();
		for (int i = 100; i >= 0; i--) {
			trace.addPoint(i, random.nextDouble() * 10.0 + i);
		}
		// Make it visible:
		// Create a frame.
		JFrame frame = new JFrame("MinimalStaticChart");
		// add the chart to the frame:
		frame.getContentPane().add(chart);
		frame.setSize(400, 300);
		// Enable the termination button [cross on the upper right edge]:
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}