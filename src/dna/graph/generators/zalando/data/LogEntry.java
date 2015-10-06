package dna.graph.generators.zalando.data;

import dna.graph.generators.zalando.parser.Line;

public class LogEntry {

	private int day_number;

	private int user;

	private String session;

	private int[] product_ids;

	private LogEntryColumn.Actions action;

	private int quantity;

	private int price_reduction;

	public LogEntry(int day_number, int user, String session,
			int[] product_ids, LogEntryColumn.Actions action, int quantity,
			int price_reduction) {
		this.day_number = day_number;

		this.user = user;

		this.session = session;

		this.product_ids = product_ids;

		this.action = action;

		this.quantity = quantity;

		this.price_reduction = price_reduction;
	}

	/*
	 * Examples for logDataLines: "d-31 u-1 u-1_5 p-1971499 VIEW - -" and
	 * "d-31 u-1 u-1_5 p-1444438,p-2060654,p-1865573 RECOPDS - -"
	 */
	public LogEntry(String logDataLine) {

		final String[] columns = logDataLine.split("\\t");

		this.day_number = Line.getNumber1(columns[0]);

		this.user = Line.getNumber1(columns[1]);

		if (!columns[2].startsWith(columns[1] + "_")) {
			throw new IllegalStateException(
					"user id of current session matches not current user");
		}

		/*
		 * Previously session was set to this.session =
		 * Line.getNumber2(columns[2]); But session ids are not globally but
		 * only locally (per user) unique. So, for globally unique session ids,
		 * we use the string without "u-", e.g. 174532_12 identifies the 12th
		 * session of user 174532.
		 */
		this.session = Line.getString2(columns[2]);

		final String[] product_ids = columns[3].split(",");
		this.product_ids = new int[product_ids.length];
		for (int i = 0; i < product_ids.length; i++)
			this.product_ids[i] = Line.getNumber1(product_ids[i]);

		this.action = LogEntryColumn.Actions.valueOf(columns[4]);

		this.quantity = columns[5].equals("-") ? -1 : Integer
				.valueOf(columns[5]);

		this.price_reduction = columns[6].equals("-") ? -1 : Integer
				.valueOf(columns[6]);
	}

	public LogEntryColumn.Actions getAction() {
		return action;
	}

	public int getDay_number() {
		return day_number;
	}

	public int getPrice_reduction() {
		return price_reduction;
	}

	public int[] getProduct_ids() {
		return product_ids;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getSession() {
		return session;
	}

	public int getUser() {
		return user;
	}

	@Override
	public String toString() {
		return ("d-" + day_number + "\t" + "u-" + user + "\t" + "u-" + session
				+ "\t" + "p-" + product_ids[0] + "\t" + action + "\t"
				+ (quantity == -1 ? "-" : quantity) + "\t"
				+ (price_reduction == -1 ? "-" : price_reduction) + System
					.lineSeparator());
	}

}
