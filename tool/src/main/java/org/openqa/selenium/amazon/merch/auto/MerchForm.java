package org.openqa.selenium.amazon.merch.auto;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

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

	public MerchForm() {
		initiateButton.addActionListener(event -> {
			int i = JOptionPane.showConfirmDialog(panelMain, "Chrome will be opened. Please log-in to your merch account and then quit Chrome");
			if (i == JOptionPane.OK_OPTION) {
				WebDriver webDriver = AmazonTool.getWebDriver(chromeDirTextField.getText());
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
			for (int i = 0; i < n; i++) {
				try {
					Product product = Product.parseFromJson(selectedDataFiles.get(i));
				} catch (IOException e) {
					// TODO:
					e.printStackTrace();
				}
			}
		});
		submitButton.addActionListener(actionEvent -> {

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
