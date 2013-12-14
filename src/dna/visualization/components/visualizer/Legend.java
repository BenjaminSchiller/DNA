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

import dna.util.Config;
import dna.visualization.GuiOptions;
import dna.visualization.components.BoundsPopupMenuListener;
import dna.visualization.components.ColorHandler;
import dna.visualization.components.visualizer.MultiScalarVisualizer.SortModeDist;
import dna.visualization.components.visualizer.MultiScalarVisualizer.SortModeNVL;

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
	private Visualizer parent;
	private Legend thisLegend;
	private LegendList list;
	private JScrollPane scrollBar;

	private JPanel addButtonPanel;

	private JComboBox<String> addBox;
	private String[] addBoxMenu;

	private ColorHandler colorHandler;

	public Legend(Visualizer parent) {
		super();
		this.parent = parent;
		thisLegend = this;
		this.colorHandler = new ColorHandler();
		this.setPreferredSize(new Dimension(190, 320));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// init list and add it
		this.list = new LegendList(this);

		c.gridx = 0;
		c.gridy = 0;
		this.add(list, c);
		this.scrollBar = new JScrollPane(this.list);
		this.scrollBar.setPreferredSize(new Dimension(187, 295));
		this.scrollBar
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollBar);

		// init addbutton panel and add it
		this.addButtonPanel = new JPanel();
		this.addButtonPanel.setPreferredSize(new Dimension(187, 20));
		this.addButtonPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.addButtonPanel.setLayout(new BoxLayout(this.addButtonPanel,
				BoxLayout.X_AXIS));

		c.gridx = 0;
		c.gridy = 1;
		this.add(addButtonPanel, c);

		this.initAddBox(null);
	}

	/** adds an item to the list, if its already added nothing will happen **/
	public void addItemToList(String name) {
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
			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addTrace(name, color);
			this.parent.updateTicks();
		}
		this.parent.toggleXAxisVisibility();
		this.parent.toggleYAxisVisibility();
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
			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addTrace(name, color);

			// usually added to x1/y1, check if config says otherways and toggle
			// accordingly
			switch (Config.getXAxisSelection("GUI_DIST_X_AXIS")) {
			case x1:
				break;
			case x2:
				this.list.toggleXAxis(i);
				break;
			}
			switch (Config.getYAxisSelection("GUI_DIST_Y_AXIS")) {
			case y1:
				break;
			case y2:
				this.list.toggleYAxis(i);
				break;
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
			if (this.parent instanceof MultiScalarVisualizer)
				((MultiScalarVisualizer) this.parent).addTrace(name, color);

			// usually added to x1/y1, check if config says otherways and toggle
			// accordingly
			switch (Config.getXAxisSelection("GUI_NVL_X_AXIS")) {
			case x1:
				break;
			case x2:
				this.list.toggleXAxis(i);
				break;
			}
			switch (Config.getYAxisSelection("GUI_NVL_Y_AXIS")) {
			case y1:
				break;
			case y2:
				this.list.toggleYAxis(i);
				break;
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
		this.addBox.setFont(GuiOptions.defaultFont);
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
		this.addBox.setFont(GuiOptions.defaultFont);
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
					// means it is a metric value without sub-elements
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
