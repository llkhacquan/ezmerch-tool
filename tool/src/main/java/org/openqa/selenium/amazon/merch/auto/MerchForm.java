package org.openqa.selenium.amazon.merch.auto;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
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

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	public MerchForm() {
		init();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("EZMerch Tool");
		MerchForm form = new MerchForm();
		frame.setContentPane(form.panelMain);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void init() {
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

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		panelMain = new JPanel();
		panelMain.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panelMain.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel1.setBorder(BorderFactory.createTitledBorder("Amazon's account password"));
		passwordField1 = new JPasswordField();
		panel1.add(passwordField1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panelMain.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel2.setBorder(BorderFactory.createTitledBorder("Chrome's user dir"));
		chromeDirTextField = new JTextField();
		panel2.add(chromeDirTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(350, -1), null, 0, false));
		chooseButton = new JButton();
		chooseButton.setText("Choose");
		panel2.add(chooseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		initiateButton = new JButton();
		initiateButton.setText("Initiate");
		panel2.add(initiateButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
		panelMain.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel3.setBorder(BorderFactory.createTitledBorder("Data "));
		productList = new JList();
		productList.setVisibleRowCount(12);
		panel3.add(productList, new GridConstraints(0, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(450, 500), null, 0, false));
		browseDataButton = new JButton();
		browseDataButton.setText("Browse");
		panel3.add(browseDataButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		checkButton = new JButton();
		checkButton.setText("Check");
		panel3.add(checkButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		submitButton = new JButton();
		submitButton.setText("Submit");
		panel3.add(submitButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return panelMain;
	}
}