package org.openqa.selenium.amazon.merch.auto;

public enum AmazonPageByTitle {
	TITLE("Merch by Amazon", "https://merch.amazon.com/dashboard"),
	SIGN_IN("Amazon Sign In", ""),
	CREATE("https://merch.amazon.com/merch-tshirt/title-setup/new/upload_art", "https://merch.amazon.com/merch-tshirt/title-setup/new/upload_art");

	public final String title;
	public final String url;

	AmazonPageByTitle(String title, String url) {
		this.title = title;
		this.url = url;
	}
}
