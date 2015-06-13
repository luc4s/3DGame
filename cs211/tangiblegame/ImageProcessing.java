package cs211.tangiblegame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import processing.video.*;

public class ImageProcessing {

	public float MIN_BRIGHTNESS = 70;
	public float MIN_SATURATION = 100;
	public float HUE_DELTA = 26;
	public float INTENSITY = 20;
	public float HUE = 109f;
	
	public float SOBEL = 0.7f;
	
	public int LINES = 5;
	
	public  int MIN_VOTES = 5;
	
	private float max_area = 200000;
	private float min_area = 10000;

	private PImage img, threshold, blurred, sobel;
	private TwoDThreeD twothree;
	private List<PVector> lines, quad;
	private Update updater;
	
	
	private PApplet applet;

//	private Capture cam;
	private Movie cam;
	
	public ImageProcessing(PApplet applet) throws Exception{
		this.applet  = applet;
		
		/*
		
		String[] cameras = Capture.list();
		if(cameras.length == 0){
			throw new Exception("No webcam available.");
		}
		else{
			cam = new Capture(applet, cameras[1]);
			cam.start();
			
			for(;!cam.available();)
				Thread.sleep(100);
		}
		 */
		cam	= new Movie(applet,TangibleGame.PATH + "testvideo.mp4");	//Put the video in the same directory
		cam.loop();
		
		cam.read();
		twothree = new TwoDThreeD(cam.width, cam.height);
		
		updater = new Update();
		updater.start();
	}
	
	public void stop(){
//		if(cam != null) cam.stop();
	}
	
	public PVector getRotations(){
		return twothree.get3DRotations(quad);
	}
	
	private class Update extends Thread{
		@Override
		public void run(){
			while(true){
				if(cam.available()) {
					cam.read();
					img = cam.get();
					threshold = color_threshold(img);
					
					blurred = intensity_threshold(blur(threshold));
					
					sobel = sobel(blurred);
					
					lines = hough(sobel, LINES);
					
					quad = xtractQuads(lines);
				}
			}
		}
	}
	
	public List<PVector> getQuad(){
		return quad;
	}
	public PImage getImage() {
		return img;
	}
	
	public PImage getBlurredImage(){
		return blurred;
	}
	public PImage getThresholdImage(){
		return threshold;
	}
	
	public PImage getSobelImage(){
		return sobel;
	}
	
	public List<PVector> xtractQuads(List<PVector> lines){
		QuadGraph qgraph = new QuadGraph();
			qgraph.build(lines, cam.width, cam.height);
		
		ArrayList<PVector> foundQuad = new ArrayList<>();
		List<int[]> quads =  qgraph.findCycles();
		for (int[] quad : quads) {
			
			PVector l1 = lines.get(quad[0]);
			PVector l2 = lines.get(quad[1]);
			PVector l3 = lines.get(quad[2]);
			PVector l4 = lines.get(quad[3]);
			// (intersection() is a simplified version of the
			// intersections() method you wrote last week, that simply
			// return the coordinates of the intersection between 2 lines)
			PVector c12 = getIntersections(l1, l2);
			PVector c23 = getIntersections(l2, l3);
			PVector c34 = getIntersections(l3, l4);
			PVector c41 = getIntersections(l4, l1);
			
			if(
					QuadGraph.isConvex(c12,  c23, c34,  c41) &&
					QuadGraph.nonFlatQuad(c12,  c23, c34,  c41) &&
					  QuadGraph.validArea(c12,  c23,  c34,  c41, max_area, min_area)
			){
				foundQuad.add(c12);
				foundQuad.add(c23);
				foundQuad.add(c34);
				foundQuad.add(c41);
			}
			//*/
		}
		return foundQuad;
	}

	private PVector getIntersections(PVector l1, PVector l2) {
		return getIntersections(Arrays.asList(new PVector[]{l1, l2})).get(0);
	}

	private ArrayList<PVector> getIntersections(List<PVector> lines) {
		ArrayList<PVector> intersections = new ArrayList<PVector>();
		for (int i = 0; i < lines.size() - 1; i++) {
			PVector l1 = lines.get(i);
			for (int j = i + 1; j < lines.size(); j++) {
				PVector l2 = lines.get(j);
				double d = Math.cos(l2.y) * Math.sin(l1.y) - Math.cos(l1.y) * Math.sin(l2.y);
				// compute the intersection and add it to 'intersections'
				int x = (int) ((l2.x * Math.sin(l1.y) - l1.x * Math.sin(l2.y)) / d), 
						y = (int) ((-l2.x * Math.cos(l1.y) + l1.x * Math.cos(l2.y)) / d);
				
				intersections.add(new PVector(x, y, 1));
			}
		}
		return intersections;
	}

	public PImage blur(PImage img) {
		int[][] kernel = { 
				{ 9, 12, 9 }, 
				{ 12, 15, 12 }, 
				{ 9, 12, 9 } };
		
		PImage result = applet.createImage(img.width, img.height, PConstants.RGB);
		// clear the image
		for (int i = 0; i < img.width * img.height; i++) 
			result.pixels[i] = 0;

		for (int i = 1; i < img.width - 1; i++) {
			for (int j = 1; j < img.height - 1; j++) {

				float c = 0;
				for (int x = -1; x < 2; x++) {
					for (int y = -1; y < 2; y++) {
						int index = (j + y) * img.width + (i + x);
						c += brightness(img.pixels[index]) * kernel[y + 1][1 + x];
					}
				}
				c /= 99f;
				result.set(i, j, (int)c);
			}
		}
		return result;
	}
	
	
	private float saturation(int c){
		float min = min(c), max = brightness(c);
		if(max == 0) return 0;
		else
			return (1 - (min/max)) * 255;
	}
	
	private float hue(int c){
		float min = min(c), max = brightness(c);
		int r = red(c), g = green(c), b = blue(c);
		
		float hue = 0;
		if(max == min) 
			return 0;
		else if(max == r){
			hue = (60 * (g - b) / (max-min) + 360) % 360;
		}
		else if(max == g){
			hue = 60 * (b - r) / (max-min) + 120;
		}
		else{
			hue = (60 * (r - g) / (max-min) + 240);
		}
		return hue * 255f/360f;
	}
	
	private float min(int c){
		int r = red(c), g = green(c), b = blue(c);

		return
		(r < g) ?(
				(r < b) ?
						r :
						b):
				((g < b) ?
						g :
						b);
	}
	
	private float brightness(int c){
		int r = red(c), g = green(c), b = blue(c);
		return
				(r > g) ?(
						(r > b) ?
								r :
								b):
						((g > b) ?
								g :
								b);
						
	}
	
	private int red(int c){
		return c >> 16 & 0xFF;
	}
	
	private int green(int c){
		return c >> 8 & 0xFF;
	}
	
	private int blue(int c){
		return c & 0xFF;
	}
	public PImage color_threshold(PImage image) {
		PImage result = applet.createImage(image.width, image.height, PConstants.RGB);
		int c;
		for (int i = 0; i < image.width * image.height; i++) {
				c =  saturation(image.pixels[i]) > MIN_SATURATION &&
						hue(image.pixels[i]) < HUE + HUE_DELTA &&
						hue(image.pixels[i]) > HUE - HUE_DELTA &&
							brightness(image.pixels[i]) > MIN_BRIGHTNESS
							
						? image.pixels[i] : 0;
				result.pixels[i] = c;
		}
		return result;
	}
	
	public PImage intensity_threshold(PImage image) {
		PImage result = applet.createImage(image.width, image.height, PConstants.RGB);
		int c;
		for (int i = 0; i < image.width * image.height; i++) {
			c =  brightness(image.pixels[i]) > INTENSITY ? 0xFFFFFF : 0;

			result.pixels[i] = c;
		}
		return result;
	}
	

	public List<PVector> hough(PImage edgeImg, int nLines) {
		float discretizationStepsPhi = 0.0125f;
		float discretizationStepsR = 1.25f;
		// dimensions of the accumulator
		int phiDim = (int) Math.round(Math.PI / discretizationStepsPhi);
		int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);

		// pre-compute the sin and cos values
		float[] tabSin = new float[phiDim];
		float[] tabCos = new float[phiDim];
		float ang = 0;
		float inverseR = 1.f / discretizationStepsR;
		
		for (int accPhi = 0; accPhi < phiDim; ang += discretizationStepsPhi, accPhi++) {
			// we can also pre-multiply by (1/discretizationStepsR) since we
			// need it in the Hough loop
			tabSin[accPhi] = (float) (Math.sin(ang) * inverseR);
			tabCos[accPhi] = (float) (Math.cos(ang) * inverseR);
		}

		// our accumulator (with a 1 pix margin around)
		int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];
		for (int y = 0; y < edgeImg.height; y++) {
			for (int x = 0; x < edgeImg.width; x++) {
				if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {

//					for (float phi = 0; phi < PConstants.PI; phi += discretizationStepsPhi) {
					for (int accPhi = 0; accPhi < phiDim; accPhi++) {

//						int accPhi = Math.round(phi / discretizationStepsPhi);
//						float r = (x * PApplet.cos(phi) + y * PApplet.sin(phi)) / discretizationStepsR;
						float r = x * tabCos[accPhi] + y * tabSin[accPhi];
						
						int accR = (int) (r + ((rDim - 1) * 0.5f));
						accumulator[(accPhi + 1) * (rDim + 2) + accR + 1]++;
					}
				}
			}
		}

		List<Integer> bestCandidates = new ArrayList<>();

		// size of the region we search for a local maximum
		int neighbourhood = 100;

		for (int accR = 0; accR < rDim; accR++) {
			for (int accPhi = 0; accPhi < phiDim; accPhi++) {
				// compute current index in the accumulator
				int idx = (accPhi + 1) * (rDim + 2) + accR + 1;
				if (accumulator[idx] > MIN_VOTES) {
					boolean bestCandidate = true;
					// iterate over the neighbourhood
					for (int dPhi = -neighbourhood / 2; dPhi < neighbourhood / 2 + 1; dPhi++) {
						// check we are not outside the image
						if (accPhi + dPhi < 0 || accPhi + dPhi >= phiDim)
							continue;
						for (int dR = -neighbourhood / 2; dR < neighbourhood / 2 + 1; dR++) {
							// check we are not outside the image
							if (accR + dR < 0 || accR + dR >= rDim)
								continue;
							int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2)
									+ accR + dR + 1;
							if (accumulator[idx] < accumulator[neighbourIdx]) {
								// the current idx is not a local maximum!
								bestCandidate = false;
								break;
							}
						}
						if (!bestCandidate)
							break;
					}
					if (bestCandidate) {
						// the current idx *is* a local maximum
						bestCandidates.add(idx);
					}
				}
			}
		}

		final int[] buffer = accumulator;
		Collections.sort(bestCandidates, new Comparator<Integer>() {
			@Override
			public int compare(Integer i1, Integer i2) {
				if (buffer[i1] > buffer[i2]
						|| (accumulator[i1] == accumulator[i2] && i1 < i2)) {
					return -1;
				} else
					return 1;
			}

		});
		if (nLines <= bestCandidates.size())
			bestCandidates = bestCandidates.subList(0, nLines);

		List<PVector> lines = new ArrayList<PVector>();
		int idx;
		for (Integer i : bestCandidates) {
			idx = i;
			int accPhi = idx / (rDim + 2) - 1;
			int accR = idx - (accPhi + 1) * (rDim + 2) - 1;
			float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
			float phi = accPhi * discretizationStepsPhi;
			lines.add(new PVector(r, phi));
		}

		return lines;
	}

	public PImage sobel(PImage img) {
		float[][] hKernel = { { 0, 1, 0 }, { 0, 0, 0 }, { 0, -1, 0 } };
		float[][] vKernel = { { 0, 0, 0 }, { 1, 0, -1 }, { 0, 0, 0 } };
		PImage result = applet.createImage(img.width, img.height, PConstants.ALPHA);
		// clear the image
		for (int i = 0; i < img.width * img.height; i++) {
			result.pixels[i] = 0;
		}
		float max = 0;
		float[] buffer = new float[img.width * img.height];
		// *************************************
		// Implement here the double convolution
		// *************************************
		for (int y = 2; y < img.height - 2; y++) { // Skip top and bottom edges
			for (int x = 2; x < img.width - 2; x++) { // Skip left and right

				float c1 = 0, c2 = 0;
				for (int xx = 0; xx < 3; xx++) {
					for (int yy = 0; yy < 3; yy++) {
						c1 += brightness(img.get(xx + x - 1, yy + y - 1))
								* hKernel[xx][yy];
					}
				}

				for (int xx = 0; xx < 3; xx++) {
					for (int yy = 0; yy < 3; yy++) {
						c2 += brightness(img.get(xx + x - 1, yy + y - 1))
								* vKernel[xx][yy];
					}
				}
				float sum = PApplet.sqrt(PApplet.pow(c1, 2) + PApplet.pow(c2, 2));
				if (sum > max)
					max = sum;

				buffer[x + y * result.width] = sum;
			}
		}

		for (int y = 2; y < img.height - 2; y++) { // Skip top and bottom edges
			for (int x = 2; x < img.width - 2; x++) { // Skip left and right
				if (buffer[y * img.width + x] > (int) (max * SOBEL)) { // 30% of
																		// the
																		// max
					result.pixels[y * img.width + x] = 0xFFFFFF;
				} else {
					result.pixels[y * img.width + x] = 0;
				}
			}
		}
		return result;
	}
	
	public void drawQuad(){
		if(quad.size() < 4) return;
		
		applet.fill(255, 0, 0, 150);
		applet.quad(
				quad.get(0).x,
				quad.get(0).y,
				quad.get(1).x,
				quad.get(1).y, 
				quad.get(2).x,
				quad.get(2).y,
				quad.get(3).x,
				quad.get(3).y);
	}
	public void drawLines(){
		for(PVector line : lines){
			applet.stroke(255, 0, 0);
			drawLine(line.y, line.x);
		}
	}

	private void drawLine(float phi, float r) {
		int x0 = 0;
		int y0 = (int) (r / PApplet.sin(phi));
		
		int x1 = (int) (r / PApplet.cos(phi));
		int y1 = 0;
		
		int x2 = cam.width;
		int y2 = (int) (-PApplet.cos(phi) / PApplet.sin(phi) * x2 + r /PApplet. sin(phi));
		
		int y3 = cam.height;
		int x3 = (int) (-(y3 - r / PApplet.sin(phi)) * (PApplet.sin(phi) / PApplet.cos(phi)));
		
		// Finally, plot the lines
		if (y0 > 0) {
			if (x1 > 0)
				applet.line(x0, y0, x1, y1);
			else if (y2 > 0)
				applet.line(x0, y0, x2, y2);
			else
				applet.line(x0, y0, x3, y3);
		} else {
			if (x1 > 0) {
				if (y2 > 0)
					applet.line(x1, y1, x2, y2);
				else
					applet.line(x1, y1, x3, y3);
			} else
				applet.line(x2, y2, x3, y3);
		}
	}

	public float getCamWidth() {
		return cam.width;
	}
	
	public float getCamHeight(){
		return cam.height;
	}

	private boolean paused = false;
	public void togglePause() {
		if(!paused)
			cam.pause();
		else
			cam.play();
		
		paused = !paused;
	}

}
