package org.openqa.selenium.amazon.merch.auto;

import com.google.common.base.Preconditions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class Product {
	private static final Logger LOG = LoggerFactory.getLogger(Product.class);
	// first
	private final String pathToFront;
	private final String pathToBack;
	private final ProductType productType;
	private final MarketPlace marketPlace;
	// second page
	private final boolean fitTypeMen;
	private final boolean fitTypeWomen;
	private final boolean fitTypeYouth;
	private final Color[] color;
	private final double listPrice;
	// third page
	private final String brandName;
	private final String titleOfProduct;
	private final String keyFeature1;
	private final String keyFeature2;
	private final String productDescription;

	public Product(String pathToFront, String pathToBack, ProductType productType, MarketPlace marketPlace, boolean fitTypeMen, boolean fitTypeWomen, boolean fitTypeYouth, Color[] color, double listPrice,
	               String brandName, String titleOfProduct, String keyFeature1, String keyFeature2, String productDescription) {
		Preconditions.checkNotNull(brandName);
		Preconditions.checkNotNull(titleOfProduct);
		Preconditions.checkNotNull(keyFeature1);
		Preconditions.checkNotNull(keyFeature2);
		Preconditions.checkNotNull(productDescription);
		Preconditions.checkArgument(pathToFront != null || pathToBack != null, "You must provide front or back");
		this.pathToFront = pathToFront;
		this.pathToBack = pathToBack;
		Preconditions.checkArgument(marketPlace == MarketPlace.AMAZON_COM || productType == ProductType.STANDARD_T_SHIRT);
		this.productType = productType;
		this.marketPlace = marketPlace;
		Preconditions.checkArgument(productType == ProductType.POP_SOCKETS || fitTypeMen || fitTypeWomen || fitTypeYouth);
		this.fitTypeMen = fitTypeMen;
		this.fitTypeWomen = fitTypeWomen;
		this.fitTypeYouth = fitTypeYouth;
		Preconditions.checkNotNull(color);
		Preconditions.checkArgument(productType == ProductType.POP_SOCKETS || color.length > 0 && color.length <= 5);
		this.color = color;
		Preconditions.checkArgument(listPrice > 0);
		this.listPrice = listPrice;
		Preconditions.checkNotNull(brandName);
		Preconditions.checkArgument(brandName.length() <= 50);
		this.brandName = brandName;
		Preconditions.checkNotNull(titleOfProduct);
		Preconditions.checkArgument(titleOfProduct.length() <= 60);
		this.titleOfProduct = titleOfProduct;
		Preconditions.checkArgument(keyFeature1.length() == 0 || keyFeature1.length() <= 256);
		this.keyFeature1 = keyFeature1;
		Preconditions.checkArgument(keyFeature2.length() == 0 || keyFeature2.length() <= 256);
		this.keyFeature2 = keyFeature2;
		Preconditions.checkArgument(productDescription.length() == 0 || productDescription.length() >= 75 && productDescription.length() <= 2000);
		this.productDescription = productDescription;
	}

	public static Product parseFromJson(File file) throws IOException {
		String json = new String(Files.readAllBytes(file.toPath()));
		JSONObject obj = new JSONObject(json);
		System.out.println(obj);
		obj.keys().forEachRemaining(s -> {
			System.out.println(s + " " + obj.get(s));
		});
		ProductType productType = ProductType.values()[obj.getInt("productType") - 1];
		MarketPlace marketPlace = MarketPlace.valueOf(obj.getString("marketplace").replaceAll("\\.", "_").toUpperCase());
		double listPrice = obj.getDouble("listPrice");
		String front = file.getParentFile().toPath().resolve(obj.getString("front")).toString();
		Preconditions.checkState(new File(front).exists(), front + " must exist");
		String back = file.getParentFile().toPath().resolve(obj.getString("back")).toString();
		Preconditions.checkState(new File(back).exists(), back + " must exist");
		boolean[] fitTypeResult = new boolean[3];
		{
			final JSONArray fitType = obj.getJSONArray("fitType");
			fitType.forEach(o -> {
				if ("men".equalsIgnoreCase(o.toString())) {
					fitTypeResult[0] = true;
				} else if ("women".equalsIgnoreCase(o.toString())) {
					fitTypeResult[1] = true;
				} else if ("youth".equalsIgnoreCase(o.toString())) {
					fitTypeResult[2] = true;
				} else {
					throw new RuntimeException(o.toString() + " is not a valid fit type (men, women, youth)");
				}
			});
		}
		List<Color> colorsList = new ArrayList<>();
		{
			final JSONArray checkedColors = obj.getJSONArray("checkedColors");
			checkedColors.forEach(o -> {
				colorsList.add(Color.valueOf(o.toString().toLowerCase().replaceAll(" ", "_")));
			});
		}
		String[] description = obj.getString("description").split("\n");
		String brand = "";
		String title = "";
		String key1 = "";
		String key2 = "";
		String des = "";
		if (description.length >= 1) {
			brand = description[0];
		}
		if (description.length >= 2) {
			title = description[1];
		}
		if (description.length >= 3) {
			key1 = description[2].trim();
		}
		if (description.length >= 4) {
			key2 = description[3].trim();
		}
		if (description.length >= 5) {
			des = description[4].trim();
		}
		return new Product(front, back, productType, marketPlace, fitTypeResult[0], fitTypeResult[1], fitTypeResult[2], colorsList.toArray(new Color[0]), listPrice, brand, title, key1, key2, des);
	}

	private static void checkImageDimension(BufferedImage image, ProductType productType, int w, int h) {
		if (image != null) {
			Preconditions.checkNotNull(image, "Image must be provided for " + productType);
			Preconditions.checkArgument(image.getWidth() == w, "width must be " + w + " for " + productType);
			Preconditions.checkArgument(image.getHeight() == h, "height must be" + h + " for " + productType);
		}
	}

	public static void main(String[] args) throws IOException {
		Product.parseFromJson(new File("form.json"));
	}

	public String getPathToFront() {
		return pathToFront;
	}

	public String getPathToBack() {
		return pathToBack;
	}

	public ProductType getProductType() {
		return productType;
	}

	public MarketPlace getMarketPlace() {
		return marketPlace;
	}

	public boolean isFitTypeMen() {
		return fitTypeMen;
	}

	public boolean isFitTypeWomen() {
		return fitTypeWomen;
	}

	public boolean isFitTypeYouth() {
		return fitTypeYouth;
	}

	public Color[] getColor() {
		return color;
	}

	public double getListPrice() {
		return listPrice;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getTitleOfProduct() {
		return titleOfProduct;
	}

	public String getKeyFeature1() {
		return keyFeature1;
	}

	public String getKeyFeature2() {
		return keyFeature2;
	}

	public String getProductDescription() {
		return productDescription;
	}

	enum Color {
		dark_heather,
		heather_grey,
		heather_blue,
		black,
		navy,
		silver,
		royal,
		brown,
		slate,
		red,
		asphalt,
		grass,
		olive,
		kelly_green,
		baby_blue,
		white,
		lemon,
		cranberry,
		pink,
		orange,
		purple;

		String getId() {
			return "gear-checkbox-" + this.toString();
		}
	}

	enum ProductType {
		STANDARD_T_SHIRT,
		PREMIUM_T_SHIRT,
		LONG_SLEEVE_T_SHIRT,
		SWEATSHIRT,
		PULLOVER_HOODIE,
		POP_SOCKETS
	}

	enum MarketPlace {
		AMAZON_COM,
		AMAZON_CO_UK,
		AMAZON_DE
	}

	@Override
	public String toString() {
		return "Product{" +
				"pathToFront='" + pathToFront + '\'' +
				", productType=" + productType +
				", marketPlace=" + marketPlace +
				", brandName='" + brandName + '\'' +
				", titleOfProduct='" + titleOfProduct + '\'' +
				'}';
	}
}
