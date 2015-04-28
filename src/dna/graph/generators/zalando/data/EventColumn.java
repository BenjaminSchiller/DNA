package dna.graph.generators.zalando.data;

public enum EventColumn {

	/* TODO copy&pasted from ProductEntryColumn -> not good */

	// product identification
	PRODUCTID, PRODUCTFAMILYID,

	// brand
	BRAND,

	// visual features
	COLOR,

	// commodity groups (hierarchical)
	COMMODITYGROUP1, COMMODITYGROUP2, COMMODITYGROUP3, COMMODITYGROUP4, COMMODITYGROUP5,

	// age groups (a product may be within more than one group)
	AGEGROUPADULT, AGEGROUPBABY, AGEGROUPTEEN, AGEGROUPKID,

	// genders (a product may be for man than one gender)
	GENDERMALE, GENDERFEMALE,

	// price
	PRICELEVEL,

	/* TODO copy&pasted from ProductEntryColumn -> not good */

	// date
	DAYNUMBER,

	// about the user
	USER, SESSION,

	// about the product
	PRODUCTIDS, ACTION, QUANTITY, PRICEREDUCTION;

	enum Actions {
		VIEW, CART, SALE, WISH, DELETE_FROM_CART, DELETE_FROM_WISHLIST, RECOPDS, VIEWRECO
	}

	static EventColumn CommodityGroup(int i) {
		switch (i) {
		case 0:
			return COMMODITYGROUP1;
		case 1:
			return COMMODITYGROUP2;
		case 2:
			return COMMODITYGROUP3;
		case 3:
			return COMMODITYGROUP4;
		case 4:
			return COMMODITYGROUP5;
		default:
			return null;
		}
	}

}
