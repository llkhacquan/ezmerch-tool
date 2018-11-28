package org.openqa.selenium.amazon.merch.auto;

public class Temp {
	public static void main(String[] args) {
		for (String id : Product.COLORS.split("\n")) {
			String[] ss = id.split("-");
			String color = ss[ss.length - 1];
			System.out.println("<input type=\"checkbox\" id=\"" + id + "\" name=\"" +
					"name-" + color + "\"><label for=\"" + id + "\">" + color + "</label>");
		}
	}
}
