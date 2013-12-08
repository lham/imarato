package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class FileReader {
	static final String[] IMG_EXT = new String[]{ "gif", "png", "bmp", "jpg", "jpeg" };

	public String[] loadLabelsFile(File file) {
		String[] split = file.getName().split("\\.");
		String ext = split[split.length - 1];
		
		if (!ext.equals("txt"))
			return null;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			ArrayList<String> list = new ArrayList<String>();

			for (String line = br.readLine(); line != null; line = br.readLine()) {
				list.add(line);
			}
			
			br.close();
			
			return list.toArray(new String[list.size()]);
			
		} catch (IOException e) {
			System.out.println("Couldn't read file: " + file);
			return null;
		}
	}

	public ArrayList<String> getImagePaths(String imageFolderPath) {
		ArrayList<String> images = new ArrayList<String>();
		
		// Get the image paths
		File[] files = new File(imageFolderPath).listFiles(new FilenameFilter() {
	    	@Override
	    	public boolean accept(File dir, String name) {
	    		for (String ext : IMG_EXT) {
	    			if (name.endsWith("." + ext)) {
	    				return true;
	    			}
	    		}
	    		return false;
	    	}
	    });
		
		// If path name was not a directory, return empty list
		if (files == null)
			return images;
		
		// Make the strings
		for (File f : files) {
			images.add(f.getAbsolutePath());
		}
		
		return images;
	}

}
