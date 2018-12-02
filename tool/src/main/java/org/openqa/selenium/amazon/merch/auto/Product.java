package org.openqa.selenium.amazon.merch.auto;

import com.google.common.base.Preconditions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

	public static List<Product> parseFromTxt(File myfile) throws IOException {
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
				Color[] colors;
				if (productType != ProductType.POP_SOCKETS) {
					colors = Arrays.stream(line.split(",")).map(s -> {
						try {
							return Color.valueOf(s.replaceAll(" ", "_"));
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(s + " is not a valid color. List of valid colors: " + Arrays.toString(Color.values()));
						}
					}).toArray(Color[]::new);
				} else {
					colors = new Color[0];
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
				LOG.info("Load a " + product.getProductType() + " with title=" + title);
				result.add(product);
			}
		} catch (Exception e) {
			LOG.error("Error at line {}", i, e);
			throw e;
		}
		LOG.info("Load " + result.size() + " products");
		return result;
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
		String back = file.getParentFile().toPath().resolve(obj.getString("back")).toString();
		boolean[] fitTypeResult = new boolean[3];
		{
			final JSONArray fitType = obj.getJSONArray("fitType");
			fitType.forEach(o -> {
				if ("men".equalsIgnoreCase(o.toString())) {
					fitTypeResult[0] = true;
				} else if ("women".equalsIgnoreCase(o.toString())) {
					fitTypeResult[1] = true;
				} else if ("young".equalsIgnoreCase(o.toString())) {
					fitTypeResult[2] = true;
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
}
