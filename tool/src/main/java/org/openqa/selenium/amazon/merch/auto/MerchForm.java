package org.openqa.selenium.amazon.merch.auto;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.amazon.merch.auto.AmazonTool.waitForDriverToClose;

public class MerchForm {
	private final static Logger LOG = LoggerFactory.getLogger(MerchForm.class);

	private JPanel panelMain;
	private JTextField chromeDirTextField;
	private JButton chooseButton;
	private JButton initiateButton;
	private JPasswordField passwordField1;
	private JList<File> productList;
	private JButton browseDataButton;
	private JButton checkButton;
	private JButton submitButton;
	private DefaultListModel<File> selectedDataFiles = new DefaultListModel<>();
	private List<Product> products = null;

	public MerchForm() {
		initiateButton.addActionListener(event -> {
			int i = JOptionPane.showConfirmDialog(panelMain, "Chrome will be opened. Please log-in to your merch account and then quit Chrome");
			if (i == JOptionPane.OK_OPTION) {
				WebDriver webDriver = AmazonTool.getWebDriver(chromeDirTextField.getText());
				webDriver.get("https://merch.amazon.com/landing");
				waitForDriverToClose(webDriver);
			}
		});
		chooseButton.addActionListener(actionEvent -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(chooseButton) == JFileChooser.APPROVE_OPTION) {
				File folder = chooser.getSelectedFile();
				chromeDirTextField.setText(folder.toString());
			}
		});
		browseDataButton.addActionListener(actionEvent -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setMultiSelectionEnabled(true);
			chooser.setAcceptAllFileFilterUsed(true);
			if (chooser.showOpenDialog(browseDataButton) == JFileChooser.APPROVE_OPTION) {
				File[] files = chooser.getSelectedFiles();
				for (File file : files) {
					if (!selectedDataFiles.contains(file)) {
						selectedDataFiles.addElement(file);
					}
				}

			}
		});
		productList.setModel(selectedDataFiles);
		productList.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
					for (File file : productList.getSelectedValuesList()) {
						selectedDataFiles.removeElement(file);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {

			}
		});
		checkButton.addActionListener(event -> {
			int n = selectedDataFiles.getSize();
			products = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				try {
					productList.setSelectedIndex(i);
					Product product = Product.parseFromJson(selectedDataFiles.get(i));
					products.add(product);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(panelMain, "Error when parsing file " + selectedDataFiles.get(i) + ". Error:\n" + e.toString());
					LOG.error("Error when parsing file {}", selectedDataFiles.get(i), e);
					products = null;
					break;
				}
			}
			if (products != null) {
				JOptionPane.showMessageDialog(panelMain, "Loaded " + products.size() + " products");
			}
		});
		submitButton.addActionListener(actionEvent -> {
			if (products == null) {
				JOptionPane.showMessageDialog(panelMain, "Please \"check\" the data files first");
			} else if (products.size() == 0) {
				JOptionPane.showMessageDialog(panelMain, "There is no product to summit");
			} else {
				int confirmResult = JOptionPane.showConfirmDialog(panelMain, "We are submitting {} " + products.size() + " product. Please confirm to process...");
				if (confirmResult == JOptionPane.OK_OPTION) {
					final WebDriver webDriver = AmazonTool.getWebDriver(chromeDirTextField.getText());
					try {
						for (Product product : products) {
							Auto.createNewProduct(webDriver, product, new String(passwordField1.getPassword()));
						}
					} finally {
						webDriver.quit();
					}
					JOptionPane.showMessageDialog(panelMain, "Done!");
				}
			}
		});
		chromeDirTextField.setText(AmazonTool.USER_HOME.toPath().resolve("chrome-amazon").toString());
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("EZMerch Tool");
		frame.setContentPane(new MerchForm().panelMain);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
