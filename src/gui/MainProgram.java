package gui;

import java.io.File;

import io.FileReader;

import javax.swing.SwingUtilities;

public class MainProgram {

	public static void main(String[] args){
		final String[] defaultLabels = new FileReader().loadLabelsFile(
				new File("labels.txt"));
		
		final String defaultPath= "C:\\Users\\Linus\\git\\ImageMarkupTool\\images";
		
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final GUI window = new GUI(defaultPath, defaultLabels);
        		window.setLocationRelativeTo(null);
        		window.setVisible(true);
            }
        });
		
	}

}
