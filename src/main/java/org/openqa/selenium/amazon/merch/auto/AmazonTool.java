package org.openqa.selenium.amazon.merch.auto;

import com.google.common.base.Preconditions;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.apache.commons.codec.binary.Base32;
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
	private static final String DEV_USER_HOME = "quannk";
	private static final boolean DEV_MODE;
	private static final File USER_HOME = new File(System.getProperty("user.home"));
	private static final int[] TIMES = new int[]{30_000, 30 * 60_000, 60 * 60_000, 86400_000, 86400_000 * 7}; // 30s, 30 mins, 1 hour, 1 day, 7 day
	private static final String KEY;

	static {
		DEV_MODE = DEV_USER_HOME.equals(USER_HOME.getName());
		Base32 base32 = new Base32();
		String key = base32.encodeAsString(String.valueOf(USER_HOME.getName().hashCode()).getBytes());
		key = key.replace("=", "");
		while (key.length() < 16) {
			key = key + key;
		}
		KEY = key.substring(0, 16);
	}

	public static void main(String[] args) throws IOException {
		String defaultPath = "/home/quannk/.config/google-chrome/amazon";
		String defaultDataFile = "/home/quannk/merch-amazon/data.txt";

		if (DEV_MODE) {
			args = new String[]{defaultPath, defaultDataFile, "ngaothe78", "00000000"};
		}

		if (args.length != 4) {
			System.err.println("Usage: <user-dir> <data-file> <password_of_amazon_account> <OTP>");
			System.exit(1);
		}

		final List<Product> products = Product.parse(new File(args[1]));

		// check authentication
		Preconditions.checkState(DEV_MODE || checkAuthentication(args[3]), args[3] + " is not a valid OTP");
		final WebDriver driver = getWebDriver(args[0]);
		try {
			for (Product product : products) {
				Auto.createNewProduct(driver, product, "ngaothe78");
			}
		} finally {
			driver.quit();
		}

	}

	private static boolean checkAuthentication(String arg) {
		int code;
		try {
			code = Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			return false;
		}

		for (int time : TIMES) {
			GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setTimeStepSizeInMillis(time).build();
			GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
			int trueCode = gAuth.getTotpPassword(KEY);
			if (trueCode == code) {
				LOG.info(code + " is correct OTP");
				return true;
			}
		}
		return false;
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