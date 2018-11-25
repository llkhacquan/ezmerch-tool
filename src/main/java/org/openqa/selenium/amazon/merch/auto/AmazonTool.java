package org.openqa.selenium.amazon.merch.auto;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AmazonTool {
	private static final Logger LOG = LoggerFactory.getLogger(AmazonTool.class);

	public static void main(String[] args) throws IOException {
		String defaultPath = "/home/quannk/.config/google-chrome/amazon";
		String defaultDataFile = "/home/quannk/merch-amazon/data.txt";
		if (args.length == 0 && new File(defaultPath).exists() && new File(defaultDataFile).exists()) {
			args = new String[]{defaultPath, defaultDataFile};
		}

		if (args.length == 0 || args.length > 2) {
			System.err.println("Usage: <user-dir> <data-file>");
			System.exit(1);
		}

		final List<Product> products = Product.parse(new File(args[1]));
		final WebDriver driver = getWebDriver(args[0]);
		try {
			for (Product product : products) {
				Auto.createNewProduct(driver, product, "ngaothe78");
			}
		} finally {
			driver.quit();
		}

	}


	private static WebDriver getWebDriver(String userDataDir) {
		try {
			if (new File(userDataDir).exists()) {
				LOG.info("Use an existing user-directory:{}", userDataDir);
			} else {
				LOG.info("Use a new user-directory:{}", userDataDir);
			}
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("user-data-dir=" + userDataDir);
			return new ChromeDriver(chromeOptions);
		} catch (Exception e) {
			LOG.error("Cannot get chromeDriver", e);
			if (e.getMessage().contains("(unknown error: DevToolsActivePort file doesn't exist)")) {
				System.out.println("Encounter \"DevToolsActivePort file doesn't exist\", please close all amazon-chrome instance(s)");
			}
			System.exit(1);
			return null;
		}
	}

}