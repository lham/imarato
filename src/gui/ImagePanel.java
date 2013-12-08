package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import core.Image;

public class ImagePanel extends JPanel {
	public static final int DEFAULT_BRUSH_SIZE = 20;
	
	Image img;
	BufferedImage image;
	BufferedImage layer;
	Graphics2D g2d;
	int currentX, currentY, oldX, oldY;
	
	//TODO multilayer
	
	public ImagePanel() {
		setDoubleBuffered(false);
		setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				oldX = e.getX() - getX0();
				oldY = e.getY() - getY0();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				currentX = e.getX() - getX0();
				currentY = e.getY() - getY0();
				
				if(g2d != null) {
					g2d.drawLine(oldX, oldY, currentX, currentY);
				}
				
				repaint();
				oldX = currentX;
				oldY = currentY;
			}

		});
		
		
	}
	
	public void setBrushSize(int size) {
		if (g2d != null) {
			g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL));
		}
	}
	
	public void setImage(Image img) {
		this.img = img;
		
		try {                
    		image = ImageIO.read(new File(img.getImagePath()));
    		layer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    		
    		g2d = (Graphics2D) layer.getGraphics();
    		g2d.setPaint(new Color(1.0f, 0.0f, 0.0f));
    		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 0.5f));
    		
    		setBrushSize(DEFAULT_BRUSH_SIZE);
    		
    	} catch (IOException ex) {
            System.out.println("Didn't find image: " + img.getImagePath());
    	}
		
		repaint();
	}
	
	protected int getX0() {
		return (int) this.getSize().getWidth()/2 - image.getWidth()/2;
	}
	
	protected int getY0() {
		return (int) this.getSize().getHeight()/2 - image.getHeight()/2;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (image != null) {
			g.drawImage(image, getX0(), getY0(), null); 
			g.drawImage(layer, getX0(), getY0(), null);
		}
	}
	
}
