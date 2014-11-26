package dna.graph.generators.zalando;

/**
 * All columns of an {@link Event}.
 */
public enum EventColumn {

	SKU, FAMILYSKU, MARKE, FARBE, WARENGRUPPE1, WARENGRUPPE2, WARENGRUPPE3, WARENGRUPPE4, WARENGRUPPE5, PERMANENTCOOKIEID, SESSIONID, AKTION, ANZAHL, PREISLAGE;

	/**
	 * All possible values for column {@link #AKTION}.
	 */
	enum Aktionen {
		VIEW, CART, SALE, WISH, VIEWRECO, DELETE_FROM_WISHLIST, DELETE_FROM_CART
	}

	/**
	 * This method allows to easily access {@link #WARENGRUPPE1} to
	 * {@link #WARENGRUPPE5} in iterations like for-loops.
	 * 
	 * @param i
	 *            The index of the Warengruppe to return.
	 * @return {@code WARENGRUPPE_}<i>i+1</i> or {@code null} if given index is
	 *         < 0 or > 4
	 */
	static EventColumn Warengruppe(int i) {
		switch (i) {
		case 0:
			return EventColumn.WARENGRUPPE1;
		case 1:
			return EventColumn.WARENGRUPPE2;
		case 2:
			return EventColumn.WARENGRUPPE3;
		case 3:
			return EventColumn.WARENGRUPPE4;
		case 4:
			return EventColumn.WARENGRUPPE5;
		default:
			return null;
		}
	}

}
