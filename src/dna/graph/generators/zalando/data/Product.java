package dna.graph.generators.zalando.data;

import dna.graph.generators.zalando.parser.Line;

public class Product {

	private int productID;

	private int productFamilyID;

	private int brand;

	private String color;

	private String commodityGroup1;
	private String commodityGroup2;
	private String commodityGroup3;
	private String commodityGroup4;
	private String commodityGroup5;

	private boolean ageGroupAdult;
	private boolean ageGroupBaby;
	private boolean ageGroupTeen;
	private boolean ageGroupKid;

	private boolean genderMale;
	private boolean genderFemale;

	private int priceLevel;

	public Product(int productID, int productFamilyID, int brand, String color,
			String commodityGroup1, String commodityGroup2,
			String commodityGroup3, String commodityGroup4,
			String commodityGroup5, boolean ageGroupAdult,
			boolean ageGroupBaby, boolean ageGroupTeen, boolean ageGroupKid,
			boolean genderMale, boolean genderFemale, int priveLevel) {
		this.productID = productID;

		this.productFamilyID = productFamilyID;

		this.brand = brand;

		this.color = color;

		this.commodityGroup1 = commodityGroup1;
		this.commodityGroup2 = commodityGroup2;
		this.commodityGroup3 = commodityGroup3;
		this.commodityGroup4 = commodityGroup4;
		this.commodityGroup5 = commodityGroup5;

		this.ageGroupAdult = ageGroupAdult;
		this.ageGroupBaby = ageGroupBaby;
		this.ageGroupTeen = ageGroupTeen;
		this.ageGroupKid = ageGroupKid;

		this.genderMale = genderMale;
		this.genderFemale = genderFemale;

		this.priceLevel = priveLevel;
	}

	public Product(String productsDescriptionsLine) {
		final String[] columns = productsDescriptionsLine.split(";");

		this.productID = Line.getNumber1(columns[0]);

		this.productFamilyID = Line.getNumber1(columns[1]);

		this.brand = Line.getNumber1(columns[2]);

		this.color = columns[3];

		this.commodityGroup1 = columns[4];
		this.commodityGroup2 = columns[5];
		this.commodityGroup3 = columns[6];
		this.commodityGroup4 = columns[7];
		this.commodityGroup5 = columns[8];

		this.ageGroupAdult = Line.stringToBool(columns[9]);
		this.ageGroupBaby = Line.stringToBool(columns[10]);
		this.ageGroupTeen = Line.stringToBool(columns[11]);
		this.ageGroupKid = Line.stringToBool(columns[12]);

		this.genderMale = Line.stringToBool(columns[13]);
		this.genderFemale = Line.stringToBool(columns[14]);

		this.priceLevel = columns.length == 16 ? Integer.valueOf(columns[15])
				: -1;
	}

	public boolean getAgeGroupAdult() {
		return ageGroupAdult;
	}

	public boolean getAgeGroupBaby() {
		return ageGroupBaby;
	}

	public boolean getAgeGroupKid() {
		return ageGroupKid;
	}

	public boolean getAgeGroupTeen() {
		return ageGroupTeen;
	}

	public int getBrand() {
		return brand;
	}

	public String getColor() {
		return color;
	}

	public String getCommodityGroup1() {
		return commodityGroup1;
	}

	public String getCommodityGroup2() {
		return commodityGroup2;
	}

	public String getCommodityGroup3() {
		return commodityGroup3;
	}

	public String getCommodityGroup4() {
		return commodityGroup4;
	}

	public String getCommodityGroup5() {
		return commodityGroup5;
	}

	public boolean getGenderFemale() {
		return genderFemale;
	}

	public boolean getGenderMale() {
		return genderMale;
	}

	public int getPriceLevel() {
		return priceLevel;
	}

	public int getProductFamilyID() {
		return productFamilyID;
	}

	public int getProductID() {
		return productID;
	}

	@Override
	public String toString() {
		return ("p-" + productID + ";" + "f-" + productFamilyID + ";" + "b-"
				+ brand + ";" + color + ";" + commodityGroup1 + ";"
				+ commodityGroup2 + ";" + commodityGroup3 + ";"
				+ commodityGroup4 + ";" + commodityGroup5 + ";"
				+ Line.boolToString(ageGroupAdult) + ";"
				+ Line.boolToString(ageGroupBaby) + ";"
				+ Line.boolToString(ageGroupTeen) + ";"
				+ Line.boolToString(ageGroupKid) + ";"
				+ Line.boolToString(genderMale) + ";"
				+ Line.boolToString(genderFemale) + ";"
				+ (priceLevel == -1 ? "" : priceLevel) + System.lineSeparator());
	}

}
