package org.openqa.selenium.amazon.merch.auto;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.apache.commons.codec.binary.Base32;

import java.io.File;

public class GoogleAuthTest {
	public static void main(String[] args) throws InterruptedException {
		GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setCodeDigits(8).setTimeStepSizeInMillis(1_000).build();
		GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
		Base32 base32 = new Base32();
		String key = base32.encodeAsString(String.valueOf(new File(System.getProperty("user.home")).getName().hashCode()).getBytes());
		key = key.replace("=", "");
		while (key.length() < 16) {
			key = key + key;
		}
		key = key.substring(0, 16);
		System.out.println("key=" + key);
		int lastOtp = 0;
		while (true) {
			int otp = gAuth.getTotpPassword(key);
			if (lastOtp != otp) {
				System.out.println(otp);
				lastOtp = otp;
			}
			Thread.sleep(500);
		}
	}
}
