package dna.graph.generators.zalando.data;

public class Event {

	private Product product;

	private LogEntry logEntry;

	public Event(Product product, LogEntry logEntry) {
		
		if(product == null)
			System.err.println("null product at day " + logEntry.getDay_number() + " (in logs)");
		
		this.product = product;
		this.logEntry = logEntry;
	}

	public String get(ProductEntryColumn column) {
		switch (column) {
		case PRODUCTID:
			return String.valueOf(this.product.getProductID());
		case PRODUCTFAMILYID:
			return String.valueOf(this.product.getProductFamilyID());
		case BRAND:
			return String.valueOf(this.product.getBrand());
		case COLOR:
			return this.product.getColor();
		case COMMODITYGROUP1:
			return this.product.getCommodityGroup1();
		case COMMODITYGROUP2:
			return this.product.getCommodityGroup2();
		case COMMODITYGROUP3:
			return this.product.getCommodityGroup3();
		case COMMODITYGROUP4:
			return this.product.getCommodityGroup4();
		case COMMODITYGROUP5:
			return this.product.getCommodityGroup5();
		case AGEGROUPADULT:
			return String.valueOf(this.product.getAgeGroupAdult());
		case AGEGROUPBABY:
			return String.valueOf(this.product.getAgeGroupBaby());
		case AGEGROUPTEEN:
			return String.valueOf(this.product.getAgeGroupTeen());
		case AGEGROUPKID:
			return String.valueOf(this.product.getAgeGroupKid());
		case GENDERMALE:
			return String.valueOf(this.product.getGenderMale());
		case GENDERFEMALE:
			return String.valueOf(this.product.getGenderFemale());
		case PRICELEVEL:
			return String.valueOf(this.product.getPriceLevel());
		default:
			return null;
		}
	}

	public String get(LogEntryColumn column) {
		switch (column) {
		case DAYNUMBER:
			return String.valueOf(this.logEntry.getDay_number());
		case USER:
			return String.valueOf(this.logEntry.getUser());
		case SESSION:
			return this.logEntry.getSession();
		case PRODUCTIDS:
			/*
			 * FIXME the following statements assumes that event covers ONLY ONE
			 * product (which is not true for RECOPDS)
			 */
			return String.valueOf(this.logEntry.getProduct_ids()[0]);
		case ACTION:
			return this.logEntry.getAction().name();
		case QUANTITY:
			return String.valueOf(this.logEntry.getQuantity());
		case PRICEREDUCTION:
			return String.valueOf(this.logEntry.getPrice_reduction());
		default:
			return null;
		}
	}

	public String get(EventColumn column) {
		switch (column) {
		case PRODUCTID:
			return String.valueOf(this.product.getProductID());
		case PRODUCTFAMILYID:
			return String.valueOf(this.product.getProductFamilyID());
		case BRAND:
			return String.valueOf(this.product.getBrand());
		case COLOR:
			return this.product.getColor();
		case COMMODITYGROUP1:
			return this.product.getCommodityGroup1();
		case COMMODITYGROUP2:
			return this.product.getCommodityGroup2();
		case COMMODITYGROUP3:
			return this.product.getCommodityGroup3();
		case COMMODITYGROUP4:
			return this.product.getCommodityGroup4();
		case COMMODITYGROUP5:
			return this.product.getCommodityGroup5();
		case AGEGROUPADULT:
			return String.valueOf(this.product.getAgeGroupAdult());
		case AGEGROUPBABY:
			return String.valueOf(this.product.getAgeGroupBaby());
		case AGEGROUPTEEN:
			return String.valueOf(this.product.getAgeGroupTeen());
		case AGEGROUPKID:
			return String.valueOf(this.product.getAgeGroupKid());
		case GENDERMALE:
			return String.valueOf(this.product.getGenderMale());
		case GENDERFEMALE:
			return String.valueOf(this.product.getGenderFemale());
		case PRICELEVEL:
			return String.valueOf(this.product.getPriceLevel());
		case DAYNUMBER:
			return String.valueOf(this.logEntry.getDay_number());
		case USER:
			return String.valueOf(this.logEntry.getUser());
		case SESSION:
			return this.logEntry.getSession();
		case PRODUCTIDS:
			/*
			 * FIXME the following statements assumes that event covers ONLY ONE
			 * product (which is not true for RECOPDS)
			 */
			return String.valueOf(this.logEntry.getProduct_ids()[0]);
		case ACTION:
			return this.logEntry.getAction().name();
		case QUANTITY:
			return String.valueOf(this.logEntry.getQuantity());
		case PRICEREDUCTION:
			return String.valueOf(this.logEntry.getPrice_reduction());
		default:
			return null;
		}
	}

}
