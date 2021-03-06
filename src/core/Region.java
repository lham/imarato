package core;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Region {
	public static final byte MASK_INSIDE = 1;
	public static final byte MASK_OUTSIDE = 0;
	
	private Color color;
	private String label;
	private byte[][] mask;
	private BufferedImage layer;
	
	public Region (Color color, String label, int width, int height) {
		this.color = color;
		this.label = label;
		this.mask = new byte[width][height];
	}
	
	public Region (Color color, String label, byte[][] mask) {
		this.color = color;
		this.label = label;
		this.mask = mask;
	}
	
	public Color getColor() {
		return color;
	}

	public byte[][] getMask() {
		return mask;
	}

	public String getLabel() {
		return label;
	}
	
	public boolean hasEmptyMask() {
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[0].length; j++) {
				if (mask[i][j] == MASK_INSIDE) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean insideRegion(int x, int y) {
		return mask[x][y] == MASK_INSIDE;
	}

	public BufferedImage getLayer() {
		return layer;
	}

	public void setLayer(BufferedImage layer) {
		this.layer = layer;
	}
	
	public boolean hasLayer() {
		return layer != null;
	}
	
}
