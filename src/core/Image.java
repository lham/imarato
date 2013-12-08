package core;

import java.io.File;
import java.util.ArrayList;

public class Image {
	private ArrayList<Region> regions;
	private String imagePath;
	
	public Image(String path, String[] labels) {
		imagePath = path;
		
		if (labels != null) {
			loadRegions(labels);
		}
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
	}
	
	
}
