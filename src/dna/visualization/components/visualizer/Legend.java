package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import dna.visualization.MainDisplay;
import dna.visualization.components.BoundsPopupMenuListener;
import dna.visualization.components.ColorHandler;
import dna.visualization.config.MetricVisualizerItem;
import dna.visualization.config.MultiScalarDistributionItem;
import dna.visualization.config.MultiScalarNodeValueListItem;
import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;
import dna.visualization.config.components.MultiScalarVisualizerConfig;

/**
 * Used within a metric visualizer component to give the user control over which
 * data is shown in the visualizer and how.
 * 
 * @author RWilmes
 * 
 */
@SuppressWarnings("serial")
public class Legend extends JPanel {
	// components
	protected Visualizer parent;
	private Legend thisLegend;
	private LegendList list;
	private JScrollPane scrollPane;

	private JPanel addButtonPanel;

	private JComboBox<String> addBox;
	private String[] addBoxMenu;

	private ColorHandler colorHandler;

	public Legend(Visualizer parent, Dimension size) {
		this.parent = parent;
		thisLegend = this;
		this.colorHandler = new ColorHandler();
		this.setPreferredSize(size);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// init list and add it
		this.list = new LegendList(this);

		c.gridx = 0;
		c.gridy = 0;
		this.add(list, c);
		this.scrollPane = new JScrollPane(this.list);
		this.scrollPane.setPreferredSize(new Dimension(size.width - 3,
				size.height - 25));
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane);

		// init addbutton panel and add it
		this.addButtonPanel = new JPanel();
		this.addButtonPanel.setPreferredSize(new Dimension(size.width - 3, 20));
		this.addButtonPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.addButtonPanel.setLayout(new BoxLayout(this.addButtonPanel,
				BoxLayout.X_AXIS));

		c.gridx = 0;
		c.gridy = 1;
		this.add(addButtonPanel, c);

		this.initAddBox(null);
	}

	public void setLegendSize(Dimension size) {
		this.scrollPane.setPreferredSize(new Dimension(size.width - 3,
				size.height - 25));
		this.addButtonPanel.setPreferredSize(new Dimension(size.width - 3, 20));
		this.validate();
	}

	/** adds a value item to the list, if its already added nothing will happen **/
	public void addValueItemToList(String name) {
		boolean alreadyAdded = false;
		for (Component c : this.list.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name)) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded) {
			Color color = this.colorHandler.getNextColor();

			LegendItem i = new LegendItemValue(this.list, name, color);
			i.setToolTipText(name);
			this.list.add(i);
			if (this.parent instanceof MetricVisualizer)
				((MetricVisualizer) this.parent).addTrace(name, color);

			this.parent.updateTicks();
		}
		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
		this.validate();
	}

	/**
	 * adds a value item to the list, if its already added nothing will happen.
	 * Item will be set up using the given config
	 **/
	public void addValueItemToList(MetricVisualizerItem item) {
		boolean alreadyAdded = false;
		for (Component c : this.list.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(item.getName())) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded) {
			String name = item.getName();
			Color color;

			if ((item.getColor() != null)
					&& this.colorHandler.containsColor(item.getColor())) {
				color = item.getColor();
				this.colorHandler.addColor(color);
			} else {
				color = this.colorHandler.getNextColor();
			}

			LegendItem i = new LegendItemValue(this.list, name, color);
			i.setToolTipText(name);
			this.list.add(i);
			if (this.parent instanceof MetricVisualizer)
				((MetricVisualizer) this.parent).addTrace(name, color);

			// usually added to y1, check if config says otherways and toggle
			// accordingly
			if (item.getYAxis().equals(yAxisSelection.y2)) {
				i.setYAxisButton(false);
				this.list.toggleYAxis(i);
			}

			// check if it should be added visible
			if (item.getVisibility().equals(GraphVisibility.hidden)) {
				i.setShowHideButton(false);
				this.list.toggleVisiblity(i);
			}

			// check display mode
			if (item.getDisplayMode().equals(DisplayMode.bars)) {
				i.setDisplayModeButton(false);
				this.list.toggleDisplayMode(i);
			}

			this.parent.updateTicks();
		}
		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
		this.validate();
	}

	/**
	 * adds a distribution item to the list, if its already added nothing will
	 * happen
	 **/
	public void addDistributionItemToList(String name) {
		boolean alreadyAdded = false;
		for (Component c : this.list.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name)) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded) {
			Color color = this.colorHandler.getNextColor();

			LegendItem i = new LegendItemDistribution(this.list, name, color);
			i.setToolTipText(name);

			this.list.add(i);

			xAxisSelection xAxis = xAxisSelection.x1;
			yAxisSelection yAxis = yAxisSelection.y1;

			// get default axis orientation
			if (this.parent instanceof MultiScalarVisualizer) {
				MultiScalarVisualizerConfig config = ((MultiScalarVisualizer) this.parent).config;
				if (config.getListConfig() != null) {
					xAxis = config.getListConfig().getAllDistributionsConfig()
							.getXAxis();
					yAxis = config.getListConfig().getAllDistributionsConfig()
							.getYAxis();
				}
			}

			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addDistributionTrace(
						name, color, xAxis, yAxis);
			this.parent.updateTicks();
		}
		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
		this.validate();
	}

	/**
	 * adds a distribution item to the list, if its already added nothing will
	 * happen. Item will be set up using the given config
	 **/
	public void addDistributionItemToList(MultiScalarDistributionItem item) {
		boolean alreadyAdded = false;
		for (Component c : this.list.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(item.getName())) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded) {
			String name = item.getName();
			Color color;

			if ((item.getColor() != null)
					&& this.colorHandler.containsColor(item.getColor())) {
				color = item.getColor();
				this.colorHandler.addColor(color);
			} else {
				color = this.colorHandler.getNextColor();
			}

			LegendItem i = new LegendItemDistribution(this.list, name, color);
			i.setToolTipText(name);

			this.list.add(i);
			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addDistributionTrace(
						name, color, item.getXAxis(), item.getYAxis());

			// usually added to x1/y1, check if config says otherways and toggle
			// buttons accordingly
			if (item.getXAxis().equals(xAxisSelection.x2))
				((LegendItemDistribution) i).setXAxisButton(false);
			if (item.getYAxis().equals(yAxisSelection.y2))
				i.setYAxisButton(false);

			// check if it should be added visible
			if (item.getVisibility().equals(GraphVisibility.hidden)) {
				i.setShowHideButton(false);
				this.list.toggleVisiblity(i);
			}

			// check display mode
			if (item.getDisplayMode().equals(DisplayMode.bars)) {
				i.setDisplayModeButton(false);
				this.list.toggleDisplayMode(i);
			}

			this.parent.updateTicks();
		}
		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
		this.validate();
	}

	/**
	 * adds a nodevaluelist item to the list, if its already added nothing will
	 * happen
	 **/
	public void addNodeValueListItemToList(String name) {
		boolean alreadyAdded = false;
		for (Component c : this.list.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name)) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded) {
			Color color = this.colorHandler.getNextColor();

			LegendItem i = new LegendItemNodeValueList(this.list, name, color);
			i.setToolTipText(name);
			this.list.add(i);

			// get default axis orientation
			xAxisSelection xAxis = xAxisSelection.x2;
			yAxisSelection yAxis = yAxisSelection.y2;

			if (this.parent instanceof MultiScalarVisualizer) {
				MultiScalarVisualizerConfig config = ((MultiScalarVisualizer) this.parent).config;
				if (config.getListConfig() != null) {
					xAxis = config.getListConfig().getAllNodeValueListsConfig()
							.getXAxis();
					yAxis = config.getListConfig().getAllNodeValueListsConfig()
							.getYAxis();
				}
			}

			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addNodeValueListTrace(
						name, color, xAxis, yAxis);

			this.parent.updateTicks();
		}
		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
		this.validate();
	}

	/**
	 * adds a nodevaluelist item to the list, if its already added nothing will
	 * happen. Item will be set up using the given config
	 **/
	public void addNodeValueListItemToList(MultiScalarNodeValueListItem item) {
		boolean alreadyAdded = false;
		for (Component c : this.list.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(item.getName())) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded) {
			String name = item.getName();
			Color color;

			if ((item.getColor() != null)
					&& this.colorHandler.containsColor(item.getColor())) {
				color = item.getColor();
				this.colorHandler.addColor(color);
			} else {
				color = this.colorHandler.getNextColor();
			}

			LegendItem i = new LegendItemNodeValueList(this.list, name, color);
			i.setToolTipText(name);
			this.list.add(i);
			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addNodeValueListTrace(
						name, color, item.getXAxis(), item.getYAxis());

			// usually added to x1/y1, check if config says otherways and toggle
			// buttons accordingly
			if (item.getXAxis().equals(xAxisSelection.x2))
				((LegendItemNodeValueList) i).setXAxisButton(false);
			if (item.getYAxis().equals(yAxisSelection.y2))
				i.setYAxisButton(false);

			// check if it should be added visible
			if (item.getVisibility().equals(GraphVisibility.hidden)) {
				i.setShowHideButton(false);
				this.list.toggleVisiblity(i);
			}

			// check display mode
			if (item.getDisplayMode().equals(DisplayMode.bars)) {
				i.setDisplayModeButton(false);
				this.list.toggleDisplayMode(i);
			}

			this.parent.updateTicks();
		}

		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
		this.validate();
	}

	/** returns the legendlist **/
	public LegendList getLegendList() {
		return this.list;
	}

	/** removes an item from the legend **/
	public void removeItem(String name, Color color) {
		if (this.parent instanceof MetricVisualizer)
			((MetricVisualizer) this.parent).removeTrace(name);
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).removeTrace(name);
		this.colorHandler.removeColor(color);
	}

	/** updated addbox with new selectable values **/
	public void updateAddBox(String[] addBoxChoices) {
		this.addBoxMenu = addBoxChoices;
		this.addButtonPanel.remove(this.addBox);
		this.addBox = new JComboBox<String>(addBoxChoices);
		this.addBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (thisLegend.addBox.getSelectedItem() instanceof String) {
					thisLegend.addSelection(thisLegend.addBox
							.getSelectedIndex());
				}
			}
		});
		this.addBox.setToolTipText("Add selected value to the list");
		this.addBox.setFont(MainDisplay.config.getDefaultFont());
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true,
				false);
		this.addBox.addPopupMenuListener(listener);
		this.addBox.setPrototypeDisplayValue("Test");
		this.addButtonPanel.add(addBox);
		this.validate();
	}

	/** initializes the addbox **/
	private void initAddBox(String[] addBoxChoices) {
		this.addBoxMenu = addBoxChoices;
		if (addBoxChoices != null)
			this.addBox = new JComboBox<String>(addBoxChoices);
		else
			this.addBox = new JComboBox<String>();
		this.addBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (thisLegend.addBox.getSelectedItem() instanceof String)
					thisLegend.addSelection(thisLegend.addBox
							.getSelectedIndex());
			}
		});
		this.addBox.setToolTipText("Add selected value to the list");
		this.addBox.setFont(MainDisplay.config.getDefaultFont());
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true,
				false);
		this.addBox.addPopupMenuListener(listener);
		this.addBox.setPrototypeDisplayValue("Test");
		this.addButtonPanel.add(addBox);
		this.validate();
	}

	/**
	 * Called when an item from the checkbox is selected. Determines which
	 * elements will be added to the legend list.
	 * 
	 * @param selectionIndex
	 *            index of the selected element
	 */
	public void addSelection(int selectionIndex) {
		// if its a metric visualizer -> add value-legenditems
		if (this.parent instanceof MetricVisualizer) {
			// if selected element is a category
			if (this.addBoxMenu[selectionIndex].charAt(0) != '-') {
				// if selected element is "metrics" category
				// means it probably has different metrics with different values
				// each
				if (this.addBoxMenu[selectionIndex].equals("metrics")) {
					int x = selectionIndex + 1;
					while (x != this.addBoxMenu.length
							&& this.addBoxMenu[x].charAt(0) == '-') {
						if (this.addBoxMenu[x].substring(0, 6).equals("----- ")) {
							this.addValueItemToList(this.addBoxMenu[x]
									.substring(6));
						}
						x++;
					}
				} else {
					// if selected element is a category other than "metrics"
					int x = selectionIndex + 1;
					while (x != this.addBoxMenu.length
							&& this.addBoxMenu[x].charAt(0) == '-') {
						this.addValueItemToList(this.addBoxMenu[selectionIndex]
								+ "." + this.addBoxMenu[x].substring(3));
						x++;
					}
				}
			}
			// if selected element starts with "---"
			if (this.addBoxMenu[selectionIndex].substring(0, 3).equals("---")) {
				// if selected element starts with "--- "
				// means element is metric with different values
				if (this.addBoxMenu[selectionIndex].substring(0, 4).equals(
						"--- ")) {
					int x = selectionIndex + 1;
					while (x != this.addBoxMenu.length
							&& this.addBoxMenu[x].substring(0, 6).equals(
									"----- ")) {
						this.addValueItemToList(this.addBoxMenu[x].substring(6));
						x++;
					}
				} else {
					// if selected element is single element starting with
					// "----- "
					// means it is a metric value without sub-elements
					if (this.addBoxMenu[selectionIndex].substring(0, 6).equals(
							"----- ")) {
						this.addValueItemToList(this.addBoxMenu[selectionIndex]
								.substring(6));
					} else {
						// if selected element starts with "---"
						// means it is a value of runtimes or batch statistics
						int x = selectionIndex - 1;
						while (x > 0
								&& (this.addBoxMenu[x].substring(0, 3)
										.equals("---"))) {
							x--;
						}
						this.addValueItemToList(this.addBoxMenu[x] + "."
								+ this.addBoxMenu[selectionIndex].substring(3));
					}
				}
			}
		}
		// if its a multiscalarvisualizer -> add either distribution or
		// nodevaluelist legenditem
		if (this.parent instanceof MultiScalarVisualizer) {
			// if selected element starts with "---"
			if (this.addBoxMenu[selectionIndex].substring(0, 3).equals("---")) {
				// if selected element starts with "--- "
				// means element is metric with different values
				if (this.addBoxMenu[selectionIndex].substring(0, 4).equals(
						"--- ")) {

					// check if distributions or nodevaluelists should be added
					boolean addDistribution;
					if (this.addBoxMenu[selectionIndex].substring(0, 5).equals(
							"--- D"))
						addDistribution = true;
					else
						addDistribution = false;

					int x = selectionIndex + 1;
					while (x != this.addBoxMenu.length
							&& this.addBoxMenu[x].substring(0, 6).equals(
									"----- ")) {
						if (addDistribution)
							this.addDistributionItemToList(this.addBoxMenu[x]
									.substring(6));
						else
							this.addNodeValueListItemToList(this.addBoxMenu[x]
									.substring(6));
						x++;
					}
				} else {
					// if selected element is single element starting with
					// "----- "
					// means it is a single value without sub-elements
					if (this.addBoxMenu[selectionIndex].substring(0, 6).equals(
							"----- ")) {

						// check if a distribution or a nodevaluelist should be
						// added
						boolean addDistribution = true;
						for (int i = 1; selectionIndex - i >= 0; i++) {
							if (this.addBoxMenu[selectionIndex - i].substring(
									0, 5).equals("--- N")) {
								addDistribution = false;
								break;
							}
							if (this.addBoxMenu[selectionIndex - i].substring(
									0, 5).equals("--- D")) {
								addDistribution = true;
								break;
							}
						}

						if (addDistribution)
							this.addDistributionItemToList(this.addBoxMenu[selectionIndex]
									.substring(6));
						else
							this.addNodeValueListItemToList(this.addBoxMenu[selectionIndex]
									.substring(6));
					}
				}
			}
		}
		this.parent.toggleXAxisVisibility();
	}

	/** updates the value of an item **/
	public void updateItem(String name, double value) {
		this.list.updateItem(name, value);
	}

	/** updates the denominator of an item **/
	public void updateItem(String name, long denominator) {
		this.list.updateItem(name, denominator);
	}

	/** resets the legend list **/
	public void reset() {
		this.list.reset();
	}

	/** toggles y axis of a trace **/
	public void toggleYAxis(LegendItem item) {
		if (this.parent instanceof MetricVisualizer)
			((MetricVisualizer) this.parent).toggleYAxis(item.getName());
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).toggleYAxis(item.getName());
		this.parent.updateTicks();
	}

	/** toggles x axis of a trace **/
	public void toggleXAxis(LegendItem item) {
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).toggleXAxis(item.getName());
	}

	/** toggles visiblity of a trace **/
	public void toggleVisiblity(LegendItem item) {
		if (this.parent instanceof MetricVisualizer)
			((MetricVisualizer) this.parent).toggleTraceVisiblity(item
					.getName());
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).toggleTraceVisiblity(item
					.getName());
	}

	/** toggles the display mode of a trace **/
	public void toggleDisplayMode(LegendItem item) {
		if (this.parent instanceof MetricVisualizer)
			((MetricVisualizer) this.parent).toggleDisplayMode(item.getName());
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).toggleDisplayMode(item
					.getName());
	}

	/** called from an item to get resorted while paused **/
	public void sortItem(LegendItem i, SortModeNVL s) {
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).sortItem(i.getName(), s);
	}

	/** called from an item to get resorted while paused **/
	public void sortItem(LegendItem i, SortModeDist s) {
		if (this.parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) this.parent).sortItem(i.getName(), s);
	}
}
