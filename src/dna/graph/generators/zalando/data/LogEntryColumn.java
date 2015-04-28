package dna.graph.generators.zalando.data;

/**
 * All columns of an entry in <i>logs.gz</i>.
 */
public enum LogEntryColumn {

	// date
	DAYNUMBER,

	// about the user
	USER, SESSION,

	// about the product
	PRODUCTIDS, ACTION, QUANTITY, PRICEREDUCTION;

	/**
	 * All possible values for column {@link LogEntryColumn#ACTION}.
	 */
	enum Actions {
		VIEW, CART, SALE, WISH, DELETE_FROM_CART, DELETE_FROM_WISHLIST, RECOPDS, VIEWRECO
	}

}
