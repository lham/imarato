package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
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
	private static final long serialVersionUID = 1L;

	public static final int 	DEFAULT_BRUSH_SIZE	= 20;
	public static final boolean	DEFAULT_PAINT_ALL	= false;
	public static final float	DEFAULT_ALPHA		= 0.7f;
	
	private Image img;
	private BufferedImage image;
	private Graphics2D g2d;
	private int currentX, currentY, oldX, oldY;
	private boolean paintAll;
	private float alpha;
	private int brushSize;
	
	public ImagePanel() {
		setDoubleBuffered(false);
		setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		brushSize = DEFAULT_BRUSH_SIZE;
		paintAll = DEFAULT_PAINT_ALL;
		alpha = DEFAULT_ALPHA;
		
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
		this.brushSize = size;
		
		if (g2d != null) {
			g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL));
		}
	}
	
	public void switchCanvas() {
		if (!img.getActiveRegion().hasLayer()) 
			createLayer();
		
		g2d = (Graphics2D) img.getActiveRegion().getLayer().getGraphics();
		g2d.setPaint(img.getActiveRegion().getColor());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, alpha));
		setBrushSize(brushSize);
		
		repaint();
	}
	
	public void setImage(Image img) {
		this.img = img;
		
		try {                
    		image = ImageIO.read(new File(img.getImagePath()));
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
			
			if (!paintAll) {
				g.drawImage(img.getActiveRegion().getLayer(), getX0(), getY0(), null);
			} else {
				for (int i = 0; i < img.getNumberOfRegions(); i++) {
					if (img.getRegion(i).hasLayer()) {
						g.drawImage(img.getRegion(i).getLayer(), getX0(), getY0(), null);
					}
				}
			}
		}
	}
	
	private void createLayer() {
		BufferedImage layer = new BufferedImage(image.getWidth(), image.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
		img.getActiveRegion().setLayer(layer);
	}

	public void setPaintAll(boolean b) {
		paintAll = b;
		repaint();
	}
	
}
