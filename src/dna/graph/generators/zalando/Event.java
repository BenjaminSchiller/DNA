package dna.graph.generators.zalando;

import dna.graph.generators.zalando.EventColumn.Aktionen;

/**
 * An event of Zalando online shop.
 */
public class Event {

	/** Columns / values of each line in event log are separated by this string. */
	private final static String SEPERATOR = "	";

	/** The number of {@code WARENGRUPPE_}<i>i</i> of an event. */
	private final static int WARENGRUPPENANZAHL = 5;

	/**
	 * The unique ID of the product in this {@link Event}.
	 * <p>
	 * Please note that a product can have one {@link #familySku} but e.g. a
	 * bunch of {@link #sku}s, one for each product color.
	 * </p>
	 * 
	 * @see EventColumn#SKU
	 */
	private String sku;
	/**
	 * The unique ID of the product family in this {@link Event}.
	 * <p>
	 * Please note that a product can have one {@link #familySku} but e.g. a
	 * bunch of {@link #sku}s, one for each product color.
	 * </p>
	 * 
	 * @see EventColumn#FAMILY_SKU
	 */
	private String familySku;
	/**
	 * The brand of the product in this {@link Event}.
	 * 
	 * @see EventColumn#MARKE
	 */
	private String marke;
	/**
	 * The color of the product in this {@link Event}.
	 * 
	 * @see EventColumn#FARBE
	 */
	private String farbe;
	/**
	 * The groups where the product of this {@link Event} is sorted in. The
	 * groups are hierarchically sorted from 1 to 5. A product can be grouped in
	 * less than five groups, indicated in the log file by either a hyphen ("-")
	 * for the empty groups or consecutive equal groups.
	 * 
	 * @see EventColumn#WARENGRUPPE_1
	 * @see EventColumn#WARENGRUPPE_2
	 * @see EventColumn#WARENGRUPPE_3
	 * @see EventColumn#WARENGRUPPE_4
	 * @see EventColumn#WARENGRUPPE_5
	 */
	private String[] warengruppe;
	/**
	 * The unique ID of the customer who caused this {@link Event}.
	 * <p>
	 * Please note that a customer can have one {@link #permanentCookieID} but
	 * multiple {@link #sessionID}s.
	 * </p>
	 * 
	 * @see EventColumn#PERMANENT_COOKIE_ID
	 */
	private String permanentCookieId;
	/**
	 * The unique ID of the customer in one session who caused this
	 * {@link Event}.
	 * <p>
	 * Please note that a customer can have one {@link #permanentCookieID} but
	 * multiple {@link #sessionID}s.
	 * </p>
	 * 
	 * @see EventColumn#SESSION_ID
	 */
	private String sessionId;
	/**
	 * The action of this {@link Event}.
	 * 
	 * @see EventColumn#AKTION
	 * @see EventColumn.Aktionen All possible actions
	 */
	private Aktionen aktion;
	/**
	 * Is -1 for every {@link #aktion} but {@code SALE} where it is the number
	 * of purchases products in this {@link Event}.
	 * 
	 * @see EventColumn#ANZAHL
	 */
	private int anzahl;
	/**
	 * Is -1 for every {@link #aktion} but {@code SALE} where it is the price
	 * rating from 1 (i.e. cheap for a product in these {@link #warengruppe}) to
	 * 15 (expensive for a product in these {@link #warengruppe}).<br>
	 * In fact it can be -1 for {@code SALE}s too, if the product was not rated.
	 * 
	 * @see EventColumn#PREISLAGE
	 */
	private int preislage;

	/**
	 * Creates an {@link Event} with the values of given line.
	 * 
	 * @param line
	 *            A line of Zalando log file.
	 */
	Event(String line) {
		final String[] temp = line.split(Event.SEPERATOR);
		int i = 0;

		this.sku = temp[i++];

		this.familySku = temp[i++];

		this.marke = temp[i++];

		this.farbe = temp[i++];

		this.warengruppe = new String[Event.WARENGRUPPENANZAHL];
		this.warengruppe[0] = temp[i++];
		this.warengruppe[1] = temp[i++];
		this.warengruppe[2] = temp[i++];
		this.warengruppe[3] = temp[i++];
		this.warengruppe[4] = temp[i++];
		// replace the second of two similar Warengruppen in consecutive rows by
		// "-"
		for (int column = warengruppe.length - 1; column > 0; column--)
			if (warengruppe[column].equals(warengruppe[column - 1]))
				warengruppe[column] = "-";

		this.permanentCookieId = temp[i++];

		this.sessionId = temp[i++];

		this.aktion = Aktionen.valueOf(temp[i++]);

		this.anzahl = temp[i++].equals("-") ? -1 : Integer
				.parseInt(temp[i - 1]);

		this.preislage = temp[i].equals("-") ? -1 : Integer.parseInt(temp[i]);
	}

	/**
	 * @return The value of given {@link EventColumn} for this {@link Event} as
	 *         {@link String}.
	 */
	public String get(EventColumn column) {
		switch (column) {
		case SKU:
			return this.sku;
		case FAMILY_SKU:
			return this.familySku;
		case MARKE:
			return this.marke;
		case FARBE:
			return this.farbe;
		case WARENGRUPPE_1:
			return this.warengruppe[0];
		case WARENGRUPPE_2:
			return this.warengruppe[1];
		case WARENGRUPPE_3:
			return this.warengruppe[2];
		case WARENGRUPPE_4:
			return this.warengruppe[3];
		case WARENGRUPPE_5:
			return this.warengruppe[4];
		case PERMANENT_COOKIE_ID:
			return this.permanentCookieId;
		case SESSION_ID:
			return this.sessionId;
		case AKTION:
			return this.aktion.name();
		case ANZAHL:
			return String.valueOf(this.anzahl);
		case PREISLAGE:
			return String.valueOf(this.preislage);
		default:
			return null;
		}
	}

	/**
	 * Returns a string representation of this {@link Event}.
	 * 
	 * @return This {@link Event} formatted like the line originally read in.
	 */
	@Override
	public String toString() {
		return this.sku + Event.SEPERATOR + this.familySku + Event.SEPERATOR
				+ this.marke + Event.SEPERATOR + this.farbe + Event.SEPERATOR
				+ this.warengruppe[0] + Event.SEPERATOR + this.warengruppe[1]
				+ Event.SEPERATOR + this.warengruppe[2] + Event.SEPERATOR
				+ this.warengruppe[3] + Event.SEPERATOR + this.warengruppe[4]
				+ Event.SEPERATOR + this.permanentCookieId + Event.SEPERATOR
				+ this.sessionId + Event.SEPERATOR + this.aktion
				+ Event.SEPERATOR + this.anzahl + Event.SEPERATOR
				+ this.preislage;
	}

}
