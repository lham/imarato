package core;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Image {
	private ArrayList<Region> regions;
	private String imagePath;
	private String filename;
	private int activeRegion;
	
	public Image(String path, String[] labels) {
		imagePath = path;
		String[] split = path.split("\\" + File.separator);
		filename = split[split.length - 1];
		
		if (labels != null) {
			loadRegions(labels);
		}
	}

	public Region getActiveRegion() {
		return regions.get(activeRegion);
	}

	public void setActiveRegion(int activeRegion) {
		this.activeRegion = activeRegion;
	}

	public String getImagePath() {
		return imagePath;
	}
	
	public Region getRegion(int i) {
		if (i < regions.size())
			return regions.get(i);
		else
			return null;
	}
	
	public void saveRegions() {
		//TODO: Write masks to matlab files
	}
	
	public void loadRegions(String[] labels) {
		//TODO: Read masks from matlab files using filename pattern.
		//TODO: Create regions for missing files
		
		regions = new ArrayList<Region>();
		for (int i = 0; i < labels.length; i++) {
			regions.add(new Region(new Color(255, 0, 0), labels[i], null));
		}
		
		
		
	}

	public String getName() {
		return filename;
	}
	
	public int getNumberOfRegions() {
		return regions.size();
	}
	
}
