package gui;

import io.FileReader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.Image;

public class GUI extends JFrame {
	private ImagePanel imagePanel;
	private JTextArea msg;
	private String imageFolderPath;
	private String[] labels;
	private ButtonGroup labelGroup;
	private JComponent labelArea;
	private ArrayList<String> imagePaths;
	private Image currentImage;
	private int currentImagePathIndex;
	private JButton prevImageButton, nextImageButton;
	
	public GUI(String imageFolderPath, String[] labels) {
		this.imageFolderPath = imageFolderPath;
		this.labels = labels;
		
		init();
		
		if (imageFolderPath != null) {
			loadImages();
		}
		
		if (currentImage != null && labels != null) {
			((JRadioButton) labelArea.getComponent(0)).setSelected(true);
			currentImage.setActiveRegion(0);
			imagePanel.switchCanvas();
		}

		pack();
	}
	
	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("IMARATO - Image Region Annotation Tool");
		//setResizable(false);
		setMinimumSize(new Dimension(730,610));
		
		// Set up layout
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = c.PAGE_START;
	
		// Add components
		c.gridx = 0;
		pane.add(initImagePane(), c);
		c.gridx = 1;
		pane.add(initRightPane(), c);
		
		// Add menu
		setJMenuBar(initMenu());
		
		//TODO: Remove
		msg.setText("There are regions that are not marked!");
		

		add(pane);
	}
	
	private void setLabels() {
		labelGroup = new ButtonGroup();
		labelArea.removeAll();
		
		ActionListener al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentImage == null) {
					return;
				}
				
				currentImage.setActiveRegion(Integer.parseInt(e.getActionCommand()));
				imagePanel.switchCanvas();
			}
		};
		
		ColorFactory cf = new ColorFactory(0.12000102f);
		
		for (int i = 0; i < labels.length; i++) {
			JRadioButton but = new JRadioButton(labels[i]);
			but.setActionCommand(Integer.toString(i));
			but.addActionListener(al);
			but.setBackground(cf.nextColor());
			labelGroup.add(but);
			labelArea.add(but);
		}
		
		pack();
	}
	
	private void loadImages() {
		imagePaths = new FileReader().getImagePaths(imageFolderPath);
		
		if (imagePaths.isEmpty()) {
			JOptionPane.showMessageDialog(this, 
        			"No images found in the folder.");
			return;
		}
		
		if (imagePaths.size() > 1) {
			nextImageButton.setEnabled(true);
		}
		
		currentImagePathIndex = 0;
		if (!loadImage(0)) {
			JOptionPane.showMessageDialog(this, 
					"Could not open image " + imagePaths.get(0));
		} 
	}
	
	private boolean loadImage(int index) {
		if (imagePaths == null || index < 0 || index > imagePaths.size())
			return false;
		
		currentImage = new Image(imagePaths.get(index), labels);
		
		if (currentImage != null) {
			imagePanel.setImage(currentImage);
			setTitle("IMARATO - Image Region Annotation Tool - " + currentImage.getName());
			return true;
		} else {
			return false;
		}
	}
	
	private void openLabelChooser() {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            String[] labels = new FileReader().loadLabelsFile(fc.getSelectedFile());
            
            if (labels != null) {
            	this.labels = labels;
            	setLabels();
            } else {
            	JOptionPane.showMessageDialog(this, 
            			"Could not read the selected file.");
            }
            //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            System.out.println("Open command cancelled by user.");
        }
	}
	
	private void openImagePathChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().isDirectory() ? 
            		fc.getSelectedFile().getAbsolutePath() : null;
            
            if (path != null) {
            	this.imageFolderPath = path;
            	loadImages();
            } else {
            	JOptionPane.showMessageDialog(this, 
            			"Could not read the selected folder.");
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
	}
	
	private JComponent initRightPane() {
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.anchor = GridBagConstraints.PAGE_START;
	    c.insets = new Insets(0, 0, 0, 0);
	    c.gridx = 0;

		c.gridy = 1;
		pane.add(initLabelsArea(), c);
		c.gridy = 2;
		pane.add(initMessageArea(), c);
	    c.gridy = 3;
		pane.add(initPaintAllLabelsArea(), c);
		c.gridy = 4;
		pane.add(initBrushSizeSelection(), c);
		
		return pane;
	}
	
	private JComponent initImagePane() {
		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
	    c.anchor = GridBagConstraints.FIRST_LINE_START;
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(initPrevButton());
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(initNextButton());
		buttonPane.add(Box.createHorizontalGlue());
		
		pane.add(initImageArea(), c);

	    c.insets = new Insets(5, 0, 5, 0);
		pane.add(buttonPane, c);
		
		return pane;
	}
	
	private JComponent initMessageArea() {
		msg = new JTextArea();
		msg.setEditable(false);  
		msg.setCursor(null);  
		msg.setOpaque(false); 
		msg.setLineWrap(true);
		msg.setWrapStyleWord(true);
		msg.setFont(msg.getFont());
		
		JScrollPane scroller = new JScrollPane(msg);
		msg.setPreferredSize(new Dimension(150, 80));
		
		scroller.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("System message"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				));
		
		return scroller; 
	}
	
	private JComponent initLabelsArea() {
		labelArea = new JPanel();
		labelArea.setLayout(new BoxLayout(labelArea, BoxLayout.PAGE_AXIS));
		labelArea.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Label to annotate"),
				BorderFactory.createEmptyBorder(5,5,5,5)
			));
		
		if (labels != null) {
			setLabels();
		} else {
			JButton but = new JButton("Load labels");
			but.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					openLabelChooser();
				}
			});
			
			labelArea.add(but);
		}
		
		return labelArea;
	}
	
	private JComponent initPaintAllLabelsArea() {
		JCheckBox but = new JCheckBox("Show all regions in image");
		but.setSelected(ImagePanel.DEFAULT_PAINT_ALL);
		but.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				//TODO: Add functionality
				if(ev.getStateChange() == ItemEvent.SELECTED){
					imagePanel.setPaintAll(true);
				} else if(ev.getStateChange() == ItemEvent.DESELECTED){
					imagePanel.setPaintAll(false);
				}
			}
		});
		
		return but;
	}
	
	private JComponent initPrevButton() {
		prevImageButton = new JButton("\u2190 Previous image");
		prevImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentImage.saveRegions();
				
				if (loadImage(currentImagePathIndex-1)) {
					currentImagePathIndex--;
				}
				
				toogleImageSwitchButtons();
			}
		});
		
		prevImageButton.setEnabled(false);
		return prevImageButton;
	}
	
	private JComponent initNextButton() {
		nextImageButton = new JButton("Next image \u2192");
		nextImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentImage.saveRegions();
				
				if (loadImage(currentImagePathIndex+1)) {
					currentImagePathIndex++;
				}
				
				toogleImageSwitchButtons();
			}
		});
		
		nextImageButton.setEnabled(false);
		return nextImageButton;
	}
	
	private JComponent initImageArea() {
		imagePanel = new ImagePanel();
		imagePanel.setPreferredSize(new Dimension(500, 500));
		
		imagePanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Image"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				));
		
		return imagePanel; 
	}

	private JComponent initBrushSizeSelection() {
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridy = 1;
		c.insets = new Insets(5, 5, 2, 0);
		pane.add(new JLabel("Brush size: "), c);
		
		JSlider size = new JSlider(JSlider.HORIZONTAL, 0, 100, ImagePanel.DEFAULT_BRUSH_SIZE);
		size.setMajorTickSpacing(20);
		size.setMinorTickSpacing(1);
		size.setPaintTicks(true);
		size.setPaintLabels(true);

		size.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        int size = (int) source.getValue();
			        imagePanel.setBrushSize(size);
			    }
			}
		});
		
		c.insets = new Insets(0, 0, 0, 0);
		c.gridy = 2;
		pane.add(size, c);
		return pane;
	}

	private JMenuBar initMenu() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		
		// File menu
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Set label file", KeyEvent.VK_L);
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "Loads the file containing the labels you want to annotate in the image.");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLabelChooser();
			}
		});
		
		menuItem = new JMenuItem("Set image folder", KeyEvent.VK_I);
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "Set the path to the folder containing the images you want to annotate.");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openImagePathChooser();
			}
		});
		
		// Image menu
		menu = new JMenu("Image");
		menu.setMnemonic(KeyEvent.VK_I);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Save annotations", KeyEvent.VK_S);
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "Saves the annotations for the current image.");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Go to image...", KeyEvent.VK_G);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO: Change this to image name instead of index number
				String word = JOptionPane.showInputDialog(GUI.this, "Enter the image you want to go to:");
				
				if (word != null) {
					String[] vals = word.split("\\s+");
					
					try {
						int num = Integer.parseInt(vals[0]);
						if(!loadImage(num)) {
							JOptionPane.showMessageDialog(GUI.this, 
									"Could not open image " + num);
						} else {
							currentImagePathIndex = num;
							toogleImageSwitchButtons();
						}
						
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(GUI.this, 
								"Invalid number");
					}
				}
			}
		});
		
		// Help menu
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		
		return menuBar;
	}
	
	private void toogleImageSwitchButtons() {
		if (currentImagePathIndex == 0) {
			prevImageButton.setEnabled(false);
		} else {
			prevImageButton.setEnabled(true);
		}
		
		if (currentImagePathIndex == imagePaths.size() - 1) {
			nextImageButton.setEnabled(false);
		} else {
			nextImageButton.setEnabled(true);
		}
	}
}
