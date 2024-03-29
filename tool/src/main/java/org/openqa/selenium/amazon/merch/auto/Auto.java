package org.openqa.selenium.amazon.merch.auto;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Predicate;

import static org.openqa.selenium.amazon.merch.auto.AmazonPageByTitle.CREATE;
import static org.openqa.selenium.amazon.merch.auto.Product.Color.*;

final class Auto {
	private static final Logger LOG = LoggerFactory.getLogger(Auto.class);
	private static final String PAGE_1 = "choose_variations";
	private static final String PAGE_2 = "add_details";
	private static final String PAGE_3 = "review_details";

	private Auto() {

	}

	/**
	 * @param driver   must logged
	 * @param product  product which will be submitted
	 * @param password optional password
	 * @param publish
	 * @return true if everything is ok
	 */
	static void createNewProduct(WebDriver driver, Product product, String password, boolean publish) throws InterruptedException {
		Preconditions.checkNotNull(driver);
		Preconditions.checkNotNull(product);
		Actions actions = new Actions(driver);
		driver.get(CREATE.url);
		checkSignIn(driver, password, url -> url.endsWith("upload_art"));
		{
			WebElement productTypeElement = driver.findElement(By.id("data-draft-shirt-type-native"));
			Select dropdown = new Select(productTypeElement);
			dropdown.selectByIndex(product.getProductType().ordinal());
			LOG.info("Select product type = {}", product.getProductType());
		}

		// marketplace, only need when productType = 0 (standard T-shirt)
		// else marketplace is always amazon.com (0)
		if (product.getProductType().ordinal() == 0) {
			WebElement productTypeElement = driver.findElement(By.id("data-marketplace-native"));
			Select dropdown = new Select(productTypeElement);
			dropdown.selectByIndex(product.getMarketPlace().ordinal());
			LOG.info("Select product type = {}", product.getMarketPlace());

		}

		{
			WebElement submit = driver.findElement(By.id("save-and-continue-upload-art-announce"));
			if (product.getProductType() == Product.ProductType.POP_SOCKETS) {
				WebElement element = driver.findElements(By.xpath("//input[@type='file']")).get(4);
				element.sendKeys(product.getPathToFront());
				LOG.info("Submit file {}", product.getPathToFront());
				waitForSubmitButton(submit);
			} else if (product.getProductType() == Product.ProductType.PULLOVER_HOODIE) {
				if (StringUtils.isNotBlank(product.getPathToFront())) {
					WebElement element = driver.findElements(By.xpath("//input[@type='file']")).get(2);
					element.sendKeys(product.getPathToFront());
					LOG.info("Submit file {}", product.getPathToFront());
					waitForSubmitButton(submit);
				}
				if (StringUtils.isNotBlank(product.getPathToBack())) {
					WebElement element = driver.findElements(By.xpath("//input[@type='file']")).get(3);
					element.sendKeys(product.getPathToBack());
					LOG.info("Submit file {}", product.getPathToBack());
					waitForSubmitButton(submit);
				}
			} else {
				if (StringUtils.isNotBlank(product.getPathToFront())) {
					WebElement element = driver.findElements(By.xpath("//input[@type='file']")).get(0);
					element.sendKeys(product.getPathToFront());
					LOG.info("Submit file {}", product.getPathToFront());
					waitForSubmitButton(submit);
				}
				if (StringUtils.isNotBlank(product.getPathToBack())) {
					WebElement element = driver.findElements(By.xpath("//input[@type='file']")).get(1);
					element.sendKeys(product.getPathToBack());
					LOG.info("Submit file {}", product.getPathToBack());
					waitForSubmitButton(submit);
				}
			}
		}

		{
			WebElement submit = driver.findElement(By.id("save-and-continue-upload-art-announce"));
			Preconditions.checkArgument(submit.isEnabled());
			Thread.sleep(3000);
			submit.click();
			LOG.info("Clicked the submit button");
			checkSignIn(driver, password, url -> url.endsWith(PAGE_1));
		}

		if (product.getProductType() != Product.ProductType.POP_SOCKETS) {
			WebElement element = driver.findElement(By.id("data-shirt-configurations-fit-type-men"));
			if (product.isFitTypeMen() != element.isSelected()) {
				actions.moveToElement(element).click().perform();
				LOG.info("Clicked men type");
			}
			element = driver.findElement(By.id("data-shirt-configurations-fit-type-women"));
			if (product.isFitTypeWomen() != element.isSelected()) {
				actions.moveToElement(element).click().perform();
				LOG.info("Clicked women type");
			}

			element = driver.findElement(By.id("data-shirt-configurations-fit-type-youth"));
			if (product.isFitTypeYouth() != element.isSelected()) {
				actions.moveToElement(element).click().perform();
				LOG.info("Clicked youth type");
			}
		}
		if (product.getProductType() != Product.ProductType.POP_SOCKETS) {
			final Product.Color[] colors = product.getProductType() == Product.ProductType.PULLOVER_HOODIE || product.getProductType() == Product.ProductType.SWEATSHIRT || product.getProductType() == Product.ProductType.LONG_SLEEVE_T_SHIRT ?
					new Product.Color[]{heather_grey, dark_heather, black, navy, royal} : Product.Color.values();
			for (Product.Color color : colors) {
				// clear all check
				WebElement element = driver.findElement(By.id(color.getId()));
				while (element.getText().length() > 0) {
					actions.moveToElement(element).click().perform();
					Thread.sleep(100);
				}
			}
			for (Product.Color i : product.getColor()) {
				WebElement element = driver.findElement(By.id(i.getId()));
				while (element.getText().length() == 0) {
					actions.moveToElement(element).click().perform();
					Thread.sleep(100);
				}
				LOG.info("Check color {}", i);
			}
		}
		{
			WebElement element = driver.findElement(By.id("data-draft-list-prices-marketplace-amount"));
			element.clear();
			element.sendKeys(String.valueOf(product.getListPrice()));
			LOG.info("Set list prices: {}", product.getListPrice());
		}
		{
			WebElement submit = driver.findElement(By.id("save-and-continue-choose-variations-announce"));
			Preconditions.checkArgument(submit.isEnabled());
			LOG.info("Clicking submit button...");
			submit.click();
			checkSignIn(driver, password, url -> url.endsWith(PAGE_2));
		}

		{
			WebElement element = driver.findElement(By.id("data-draft-brand-name"));
			if (product.getBrandName() != null) {
				element.clear();
				element.sendKeys(product.getBrandName());
				LOG.info("Set brand name: {}", product.getBrandName());
			}

			element = findByIdWithCustomLang(driver, "data-draft-name");
			if (product.getTitleOfProduct() != null) {
				Objects.requireNonNull(element).clear();
				element.sendKeys(product.getTitleOfProduct());
				LOG.info("Set product title: {}", product.getTitleOfProduct());
			}

			element = findByIdWithCustomLang(driver, "data-draft-bullet-points-bullet1");
			if (product.getKeyFeature1() != null) {
				Objects.requireNonNull(element).clear();
				element.sendKeys(product.getKeyFeature1());
				LOG.info("Set key feature 1: {}", product.getKeyFeature1());
			}

			element = findByIdWithCustomLang(driver, "data-draft-bullet-points-bullet2");
			if (product.getKeyFeature2() != null) {
				Objects.requireNonNull(element).clear();
				element.sendKeys(product.getKeyFeature2());
				LOG.info("Set key feature 2: {}", product.getKeyFeature2());
			}

			element = findByIdWithCustomLang(driver, "data-draft-description");
			if (product.getProductDescription() != null) {
				Objects.requireNonNull(element).clear();
				element.sendKeys(product.getProductDescription());
				LOG.info("Set product description: {}", product.getProductDescription());
			}
		}

		if (publish) {
			//  TODO implement here
		}

		{
			WebElement submit = driver.findElement(By.id("save-and-continue-announce"));
			Preconditions.checkArgument(submit.isEnabled());
			submit.click();
			LOG.info("Submitted");
			checkSignIn(driver, password, url -> url.endsWith(PAGE_3));
		}
	}

	private static void waitForSubmitButton(WebElement submit) throws InterruptedException {
		do {
			Thread.sleep(4500);
			LOG.info("Wait for submit button enabled...");
			Thread.sleep(500);
		} while (!submit.isEnabled());
	}

	private static WebElement findByIdWithCustomLang(WebDriver driver, String id) {
		//data-draft-name-de-de
		//data-draft-name-en-gb
		//data-draft-name-en-us
		try {
			return driver.findElement(By.id(id + "-de-de"));
		} catch (NoSuchElementException e) {
			try {
				return driver.findElement(By.id(id + "-en-gb"));
			} catch (NoSuchElementException e1) {
				try {
					return driver.findElement(By.id(id + "-en-us"));
				} catch (NoSuchElementException e2) {
					return null;
				}
			}
		}
	}

	/**
	 * wait for 0.5s and check the signIn as well
	 */
	private static void checkSignIn(WebDriver driver, String password, Predicate<String> urlVerifier) throws InterruptedException {
		do {
			Thread.sleep(500);
			if (driver.getCurrentUrl().startsWith("https://www.amazon.com/ap/signin")) {
				LOG.info("Meet sign in page!");
				final WebElement apPassword = driver.findElement(By.id("ap_password"));
				apPassword.clear();
				apPassword.sendKeys((CharSequence) password);
				LOG.info("Filled the password");
				apPassword.submit();
				LOG.info("Sign in..");
			}
		} while (!urlVerifier.test(driver.getCurrentUrl()));
	}
}
