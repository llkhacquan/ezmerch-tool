package org.openqa.selenium.amazon.merch.auto;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Product {
	public static final String COLORS = "gear-checkbox-dark_heather\n" +
			"gear-checkbox-heather_grey\n" +
			"gear-checkbox-heather_blue\n" +
			"gear-checkbox-black\n" +
			"gear-checkbox-navy\n" +
			"gear-checkbox-silver\n" +
			"gear-checkbox-royal\n" +
			"gear-checkbox-brown\n" +
			"gear-checkbox-slate\n" +
			"gear-checkbox-red\n" +
			"gear-checkbox-asphalt\n" +
			"gear-checkbox-grass\n" +
			"gear-checkbox-olive\n" +
			"gear-checkbox-kelly_green\n" +
			"gear-checkbox-baby_blue\n" +
			"gear-checkbox-white\n" +
			"gear-checkbox-lemon\n" +
			"gear-checkbox-cranberry\n" +
			"gear-checkbox-pink\n" +
			"gear-checkbox-orange\n" +
			"gear-checkbox-purple";
	public static final String COLORS2 = "gear-checkbox-heather_grey\n" +
			"gear-checkbox-dark_heather\n" +
			"gear-checkbox-black\n" +
			"gear-checkbox-navy\n" +
			"gear-checkbox-royal";
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
	private final int[] color;
	private final double listPrice;
	// third page
	private final String brandName;
	private final String titleOfProduct;
	private final String keyFeature1;
	private final String keyFeature2;
	private final String productDescription;

	public Product(String pathToFront, String pathToBack, ProductType productType, MarketPlace marketPlace, boolean fitTypeMen, boolean fitTypeWomen, boolean fitTypeYouth, int[] color, double listPrice, String brandName, String titleOfProduct, String keyFeature1, String keyFeature2, String productDescription) {
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
		Preconditions.checkArgument(keyFeature1 == null || keyFeature1.length() <= 256);
		this.keyFeature1 = keyFeature1;
		Preconditions.checkArgument(keyFeature2 == null || keyFeature2.length() <= 256);
		this.keyFeature2 = keyFeature2;
		Preconditions.checkArgument(productDescription == null || productDescription.length() >= 75 && productDescription.length() <= 2000);
		this.productDescription = productDescription;
	}

	public static List<Product> parse(File myfile) throws IOException {
		List<Product> result = new ArrayList<>();
		AtomicInteger i = new AtomicInteger();
		try (BufferedReader reader = new BufferedReader(new FileReader(myfile))) {
			String line;
			while ((line = nextLine(reader, i)) != null) {
				Preconditions.checkState(line.startsWith("["), "Line " + i + " must start with '[PRODUCT]'");
				// front image
				String front = nextLine(reader, i);
				Preconditions.checkNotNull(front, "Line " + i + " must exist");
				BufferedImage frontImage = null;
				if (front.length() > 0) {
					File file = new File(front);
					Preconditions.checkState(front.endsWith(".png"), "Line " + i + ":" + file + " must be PNG file");
					Preconditions.checkState(file.exists(), "Line " + i + ":" + file + " must exist");
					frontImage = ImageIO.read(file);
				}
				String back = nextLine(reader, i);
				Preconditions.checkNotNull(back, "Line " + i + " must exist");
				BufferedImage backImage = null;
				if (back.length() > 0) {
					File file = new File(back);
					Preconditions.checkState(back.endsWith(".png"), file + " must be PNG file");
					Preconditions.checkState(file.exists(), "Line " + i + ":" + file + " must exist");
					backImage = ImageIO.read(file);
				}
				Preconditions.checkArgument(frontImage != null || backImage != null, "An image must be provided");
				line = nextLine(reader, i);
				Preconditions.checkNotNull(line, "Line " + i + " must exist");
				final ProductType productType;
				try {
					productType = ProductType.valueOf(line.toUpperCase());
				} catch (IllegalArgumentException e) {
					StringBuilder sb = new StringBuilder("[");
					for (ProductType value : ProductType.values()) {
						sb.append(value).append(',');
					}
					sb.setCharAt(sb.length() - 1, ']');
					throw new RuntimeException("Line " + i + ":" + line + " must be in " + sb.toString(), e);
				}
				switch (productType) {
					case POP_SOCKETS:
						Preconditions.checkNotNull(frontImage, "Front image must be provided for " + productType);
						checkImageDimension(frontImage, productType, 485, 485);
						break;
					case LONG_SLEEVE_T_SHIRT:
					case SWEATSHIRT:
					case PREMIUM_T_SHIRT:
					case STANDARD_T_SHIRT:
						checkImageDimension(frontImage, productType, 4500, 5400);
						checkImageDimension(backImage, productType, 4500, 5400);
						break;
					case PULLOVER_HOODIE:
						checkImageDimension(frontImage, productType, 4500, 4050);
						checkImageDimension(backImage, productType, 4500, 5400);
						break;

				}
				line = nextLine(reader, i);
				Preconditions.checkNotNull(line, "Line " + i + " must exist");
				final MarketPlace marketPlace;
				try {
					marketPlace = MarketPlace.valueOf(line.toUpperCase());
				} catch (IllegalArgumentException e) {
					StringBuilder sb = new StringBuilder("[");
					for (MarketPlace value : MarketPlace.values()) {
						sb.append(value).append(',');
					}
					sb.setCharAt(sb.length() - 1, ']');
					throw new RuntimeException("Line " + i + ":" + line + " must be in " + sb.toString());
				}
				line = nextLine(reader, i);
				Preconditions.checkNotNull(line, "Line " + i + " must exist");
				line = line.toLowerCase();
				boolean men = false, women = false, youth = false;
				for (String s : line.split(",")) {
					if (s.equals("men")) {
						men = true;
					} else if (s.equals("women")) {
						women = true;
					} else if (s.equals("youth")) {
						youth = true;
					}
				}
				line = nextLine(reader, i);
				Preconditions.checkNotNull(line, "Line " + i + " must exist");
				line = line.replaceAll("\\s", "");
				int[] colors;
				if (productType != ProductType.POP_SOCKETS) {
					colors = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
					for (int color : colors) {
						if (productType == ProductType.STANDARD_T_SHIRT || productType == ProductType.PREMIUM_T_SHIRT) {
							Preconditions.checkArgument(color > 0 && color <= 21, "Color of " + productType + " must be between 1 and 21. Actual:" + color);
						} else {
							Preconditions.checkArgument(color > 0 && color <= 5, "Color of " + productType + " must be between 1 and 5. Actual:" + color);
						}
					}
				} else {
					colors = new int[0];
				}
				line = nextLine(reader, i);
				Preconditions.checkNotNull(line, "Line " + i + " must exist");
				double price = Double.parseDouble(line);
				String brand = nextLine(reader, i);
				String title = nextLine(reader, i);
				String feature1 = nextLine(reader, i);
				String feature2 = nextLine(reader, i);
				String description = nextLine(reader, i);
				nextLine(reader, i); // this line is ignored

				Product product = new Product(front, back, productType, marketPlace, men, women, youth, colors, price, brand, title, feature1, feature2, description);
				result.add(product);
			}
		} catch (Exception e) {
			LOG.error("Error at line {}", i, e);
			throw e;
		}
		return result;
	}

	private static void checkImageDimension(BufferedImage image, ProductType productType, int w, int h) {
		if (image != null) {
			Preconditions.checkNotNull(image, "Image must be provided for " + productType);
			Preconditions.checkArgument(image.getWidth() == w, "width must be " + w + " for " + productType);
			Preconditions.checkArgument(image.getHeight() == h, "height must be" + h + " for " + productType);
		}
	}

	public static void main(String[] args) throws IOException {
		Product.parse(new File("data.txt"));
	}

	private static String nextLine(BufferedReader reader, AtomicInteger i) throws IOException {
		i.incrementAndGet();
		String line = reader.readLine();
		return line == null ? null : line.trim();
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

	public int[] getColor() {
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

	enum ProductType {
		STANDARD_T_SHIRT,
		PREMIUM_T_SHIRT,
		LONG_SLEEVE_T_SHIRT,
		SWEATSHIRT,
		PULLOVER_HOODIE,
		POP_SOCKETS;
	}

	enum MarketPlace {
		AMAZON_COM,
		AMAZON_CO_UK,
		AMAZON_DE;
	}
}
