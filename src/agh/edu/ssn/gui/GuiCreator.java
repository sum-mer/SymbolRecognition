package agh.edu.ssn.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import agh.edu.ssn.binarization.Binarization;

public class GuiCreator {

	private JFrame mainFrame = new JFrame("Symbol Recognition"); // create Frame
	private JPanel panel = new JPanel();
	private JPanel panelForImg = new JPanel();

	// Menu
	private JMenuBar mb = new JMenuBar(); // Menubar
	private JMenu mnuFile = new JMenu("File"); // File Entry on Menu bar
	private JMenuItem mnuItemQuit = new JMenuItem("Quit"); // Quit sub item
	private JMenu mnuHelp = new JMenu("Help"); // Help Menu entry
	private JMenuItem mnuItemAbout = new JMenuItem("About"); // About Entry
	private static JFileChooser fileChooser = new JFileChooser();
	private JButton openFileButton = new JButton("Open file");
	private JButton binarizationButton = new JButton("Binarization");

	public static BufferedImage img;
	private BufferedImage binImg;
	private ImageIcon iconOrg;
	private ImageIcon iconBin;
	private JLabel labelForImgOrg;
	private JLabel labelForImgBin;
	private File selectedfile;

	public GuiCreator() throws IOException {

		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().add(panel, BorderLayout.CENTER);
		mainFrame.getContentPane().add(panelForImg, BorderLayout.LINE_START);
		// Set menubar
		mainFrame.setJMenuBar(mb);

		FileFilter filter1 = new ExtensionFileFilter("JPG,JPEG,BMP,PNG",
				new String[] { "JPG", "JPEG", "BMP", "PNG" });
		fileChooser.setFileFilter(filter1);

		panel.add(openFileButton);
		panel.add(binarizationButton);

		// Build Menus
		mnuFile.add(mnuItemQuit); // Create Quit line
		mnuHelp.add(mnuItemAbout); // Create About line
		mb.add(mnuFile); // Add Menu items to form
		mb.add(mnuHelp);

		// Label with original image
		labelForImgOrg = new JLabel();
		labelForImgOrg.setHorizontalAlignment(JLabel.CENTER);
		labelForImgOrg
				.setBorder(BorderFactory.createLineBorder(Color.black, 3));

		// Label with binarized image
		labelForImgBin = new JLabel();
		labelForImgBin.setHorizontalAlignment(JLabel.CENTER);
		labelForImgBin
				.setBorder(BorderFactory.createLineBorder(Color.black, 3));

		setCanvas();

		// add labels to panel
		panelForImg.add(labelForImgOrg);
		panelForImg.add(labelForImgBin);

		// Allows the Swing App to be closed
		mainFrame.addWindowListener(new ListenCloseWdw());

		// Add Menu listener
		mnuItemQuit.addActionListener(new ListenMenuQuit());

		// Add Open file listener
		openFileButton.addActionListener(new ListenOpenFile());

		// Add binarization listener
		binarizationButton.setEnabled(false);
		binarizationButton.addActionListener(new ListenBinarizationImg());

		mnuItemAbout.addActionListener(new ListenAboutInMenu());

	}

	public static void main(String args[]) throws IOException {

		GuiCreator gui = new GuiCreator();
		gui.launchFrame();

	}

	public class ListenMenuQuit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	public class ListenCloseWdw extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public class ListenAboutInMenu implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String aboutText = "Authors:\n	Olga Zachariasz\n	Aleksander Sobol";
			JOptionPane.showMessageDialog(mainFrame, aboutText, "About",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// open selected original image and add it to label
	public class ListenOpenFile implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			int retVal = fileChooser.showOpenDialog(mainFrame);
			if (retVal == JFileChooser.APPROVE_OPTION) {

				try {
					selectedfile = fileChooser.getSelectedFile();

					img = ImageIO.read(selectedfile);

					iconOrg = new ImageIcon(getScaledImage(img, 256, 256));

					labelForImgOrg.setIcon(iconOrg);

					binarizationButton.setEnabled(true);

				} catch (IOException e1) {
					e1.printStackTrace();

				}

			}
		}
	}

	// after clicking Binarization button binarize original image and display
	// result
	public class ListenBinarizationImg implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			BufferedImage selectedImage;
			try {
				selectedImage = ImageIO.read(fileChooser.getSelectedFile());
				System.out.println(selectedImage.getHeight());

				Binarization bin = new Binarization();

				binImg = bin.makeBinarization(img);

				iconBin = new ImageIcon(getScaledImage(binImg, 256, 256));

				labelForImgBin.setIcon(iconBin);

			} catch (IOException e1) {
				e1.printStackTrace();

			}

		}

	}

	public void launchFrame() {
		// Display Frame
		// get the screen size as a java dimension
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// get 2/3 of the height, and 2/3 of the width
		int height = screenSize.height * 2 / 3;
		int width = screenSize.width * 2 / 3;

		// set the jframe height and width
		mainFrame.setPreferredSize(new Dimension(width, height));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.pack(); // Adjusts panel to components for display
		mainFrame.setVisible(true);
		mainFrame.setLocation(175, 175);
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	private void setCanvas() {
		BufferedImage image;
		try {
			image = ImageIO.read(new File("images/canvas.jpg"));
			ImageIcon canvas = new ImageIcon(getScaledImage(image, 256, 256));
			labelForImgBin.setIcon(canvas);
			labelForImgOrg.setIcon(canvas);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
