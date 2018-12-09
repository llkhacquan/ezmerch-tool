package org.openqa.selenium.amazon.merch.auto;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.openqa.selenium.amazon.merch.auto.AmazonTool.USER_HOME;
import static org.openqa.selenium.amazon.merch.auto.AmazonTool.hasQuit;

public class MerchForm {
	public static final String INI_CHROME_DIR = "chrome-dir";
	public static final String INI_DATA_DIR = "data-dir";
	public static final String INI_PW = "pw";
	private static final Logger LOG = LoggerFactory.getLogger(MerchForm.class);
	private static final File iniFile = new File(USER_HOME, ".ezmerch.ini");
	private final DefaultListModel<File> selectedDataFiles = new DefaultListModel<>();
	private final JFileChooser dataChooser = new JFileChooser();
	private JPanel panelMain;
	private JTextField chromeDirTextField;
	private JButton chooseButton;
	private JButton openChromeButton;
	private JPasswordField passwordField;
	private JList<File> productList;
	private JButton browseDataButton;
	private JButton checkButton;
	private JButton submitButton;
	private JButton deleteButton;
	private JButton openToSubmitButton;
	private volatile List<Product> products = null;
	private volatile WebDriver webDriver = null;

	private MerchForm() {
		openChromeButton.addActionListener(event -> {
			if (hasQuit(webDriver)) {
				webDriver = AmazonTool.getWebDriver(chromeDirTextField.getText());
			}
			webDriver.get("https://merch.amazon.com/manage/products");
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
		dataChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dataChooser.setMultiSelectionEnabled(true);
		dataChooser.setAcceptAllFileFilterUsed(true);
		browseDataButton.addActionListener(actionEvent -> {
			if (dataChooser.showOpenDialog(browseDataButton) == JFileChooser.APPROVE_OPTION) {
				File[] files = dataChooser.getSelectedFiles();
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
				} catch (Exception e) {
					JOptionPane.showMessageDialog(panelMain, "Error when parsing file " + selectedDataFiles.get(i) + ". Error:\n" + e.toString());
					LOG.error("Error when parsing file {}", selectedDataFiles.get(i), e);
					products = null;
					break;
				}
			}
			if (products != null) {
				JOptionPane.showMessageDialog(panelMain, "Loaded " + products.size() + " product(s)");
			}
		});
		submitButton.addActionListener(actionEvent -> {
			if (products == null) {
				JOptionPane.showMessageDialog(panelMain, "Please \"check\" the data files first");
			} else if (products.size() == 0) {
				JOptionPane.showMessageDialog(panelMain, "There is no product to summit");
			} else {
				int confirmResult = JOptionPane.showConfirmDialog(panelMain, "We are submitting " + products.size() + " product. Please confirm to process...");
				if (confirmResult == JOptionPane.OK_OPTION) {
					if (hasQuit(webDriver)) {
						webDriver = AmazonTool.getWebDriver(chromeDirTextField.getText());
					}
					try {
						for (Product product : products) {
							LOG.info("START {}", product);
							Auto.createNewProduct(webDriver, product, new String(passwordField.getPassword()));
							LOG.info("DONE {}", product);
						}
						JOptionPane.showMessageDialog(panelMain, "Done!");
					} catch (Exception e) {
						LOG.error("Error when submitting", e);
						JOptionPane.showMessageDialog(panelMain, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
					} finally {
						products = null;
					}
				}
			}
		});
		deleteButton.addActionListener(e -> {
			for (File file : productList.getSelectedValuesList()) {
				selectedDataFiles.removeElement(file);
			}
		});
		chromeDirTextField.setText(USER_HOME.toPath().resolve("chrome-amazon").toString());
		openToSubmitButton.addActionListener(event -> {
			if (hasQuit(webDriver)) {
				JOptionPane.showMessageDialog(panelMain, "Please open Chrome and move to \"Manage\" page", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			List<WebElement> elements = webDriver.findElements(By.className("gear-manage-products-row"));
			List<String> urls = new ArrayList<>();
			for (WebElement element : elements) {
				if (element.getTagName().equals("tr")) {
					List<WebElement> img = element.findElements(By.className("a-thumbnail-left"));
					if (img.size() == 0) {
						continue;
					}
					String src = img.get(0).getAttribute("src");
					if (src == null) {
						continue;
					}
					String[] split = src.split("/");
					String productId = null;
					for (int i = 0; i < split.length; i++) {
						if (split[i].equals("gear")) {
							productId = split[i + 1];
						}
					}
					List<WebElement> l = element.findElements(By.className("a-text-left"));
					boolean isPop = l.get(1).getText().equalsIgnoreCase("PopSockets");
					String newUrl = (isPop ? "https://merch.amazon.com/merch-popsocket/title-setup/" : "https://merch.amazon.com/merch-tshirt/title-setup/") + productId + "/review_details";
					urls.add(newUrl);
				}
			}
			for (String url : urls) {
				((JavascriptExecutor) webDriver).executeScript("window.open('" + url + "', '_blank');");
				LOG.info("Openning in new tab: " + url);
			}
		});
	}

	public static void main(String[] args) throws IOException {
		File logDir = new File(USER_HOME, ".ezmerch.logs");
		logDir.mkdirs();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		File file = new File(logDir, "app_" + sdf.format(new Date()) + ".log");
		updateLog4jConfiguration(file.getAbsolutePath());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.error("Error when set look and feel");
		}
		MerchForm form = new MerchForm();
		if (iniFile.exists() && iniFile.isFile()) {
			List<String> strings = Files.readAllLines(iniFile.toPath());
			for (String string : strings) {
				int index = string.indexOf('=');
				if (index < 0) {
					continue;
				}
				String attribute = string.substring(0, index);
				String data = string.substring(index + 1);
				if (INI_CHROME_DIR.equals(attribute)) {
					form.chromeDirTextField.setText(data);
				} else if (INI_DATA_DIR.equals(attribute)) {
					if (new File(data).isDirectory()) {
						form.dataChooser.setCurrentDirectory(new File(data));
					}
				} else if (INI_PW.equals(attribute)) {
					form.passwordField.setText(data);
				}
			}
		}
		JFrame frame = new JFrame("EZMerch Tool");
		frame.setContentPane(form.panelMain);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(iniFile))) {
					writer.write(INI_CHROME_DIR + "=" + form.chromeDirTextField.getText() + "\n");
					writer.write(INI_DATA_DIR + "=" + form.dataChooser.getCurrentDirectory() + "\n");
					writer.write(INI_PW + "=" + new String(form.passwordField.getPassword()) + "\n");
				} catch (IOException ex) {
					LOG.error("Exception when saving ini file", ex);
				}
				try {
					Process p = Runtime.getRuntime().exec("attrib +h " + iniFile.getAbsolutePath());
					p.waitFor();
				} catch (Exception ex) {
					LOG.error("Exception when hide the ini file", ex);
				}
				super.windowClosing(e);
			}
		});
		frame.pack();
		frame.setVisible(true);
	}

	private static void updateLog4jConfiguration(String logFile) {
		Properties props = new Properties();
		try {
			InputStream configStream = MerchForm.class.getResourceAsStream("/log4j.properties");
			props.load(configStream);
			configStream.close();
		} catch (IOException e) {
			System.out.println("Error: Cannot laod configuration file ");
		}
		props.setProperty("log4j.appender.MainLogAppender.File", logFile);
		PropertyConfigurator.configure(props);
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
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
		panelMain.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panel1.setBorder(BorderFactory.createTitledBorder("Amazon's account password"));
		passwordField = new JPasswordField();
		panel1.add(passwordField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panelMain.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panel2.setBorder(BorderFactory.createTitledBorder("Chrome's user dir"));
		chromeDirTextField = new JTextField();
		chromeDirTextField.setEditable(false);
		panel2.add(chromeDirTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(350, -1), null, 0, false));
		chooseButton = new JButton();
		chooseButton.setText("Choose");
		panel2.add(chooseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		openChromeButton = new JButton();
		openChromeButton.setText("Open Chrome");
		panel2.add(openChromeButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
		panelMain.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel3.setBorder(BorderFactory.createTitledBorder("Data "));
		browseDataButton = new JButton();
		browseDataButton.setText("Browse");
		panel3.add(browseDataButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel3.add(scrollPane1, new GridConstraints(0, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 500), new Dimension(750, 500), new Dimension(-1, 500), 1, false));
		productList = new JList();
		productList.setVisibleRowCount(12);
		scrollPane1.setViewportView(productList);
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		panel3.add(deleteButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		checkButton = new JButton();
		checkButton.setText("Check");
		panel3.add(checkButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		submitButton = new JButton();
		submitButton.setText("Submit");
		panel3.add(submitButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		openToSubmitButton = new JButton();
		openToSubmitButton.setText("Open to submit");
		panel3.add(openToSubmitButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return panelMain;
	}

}
