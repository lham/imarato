package gui;

import java.awt.Color;
import java.util.Random;

public class ColorFactory {
	private float h;
	private float goldenRatioConjugate = 0.618033988749895f;


	public ColorFactory() {
		this(new Random().nextFloat());
	}
	
	public ColorFactory(float seed) {
		h = seed;
		System.out.println("Color seed: " + seed);
	}
	
	public Color nextColor() {
		h += goldenRatioConjugate;
		h %= 1;
		
		return new Color(Color.HSBtoRGB(h, 0.7f, 1.0f));
	}
}
