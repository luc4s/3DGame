package cs211.tangiblegame;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.MouseEvent;

@SuppressWarnings("serial")
public class TangibleGame extends PApplet {
	
	public final static String PATH = "C:\\Users\\Luc4s\\Dropbox\\Workspace\\Webcam\\";
	
	private final float MAX_ANGLE = PI / 3;
	private final int PLATE_THICKNESS = 20, 
			PLATE_SIZE = 600,
			MIN_MOTION_SPEED = 10, 
			TOPVIEW_SIZE = 200, 
			MARGIN = 10;

	private int motionSpeed = 100;

	private float dx = 0, 
			rx = 0, 
			orx = 0, 
			dy = 0, 
			ry = 0, 
			ory = 0, 
			rotate = 0,
			depth = 2000,
			cam_angle = PI/2, nrx, nry;

	private boolean plate_controlled, 
					creationMode = false, 
					rotate_en = false,
					calibrate = false;

	private PGraphics topView;
	private Mover mover;
	private ImageProcessing imgProc;

	private PShape doge, background;
	
	private HScrollBar hue, hue_delta, brightness, saturation, intensity;

	@Override
	public void setup() {
		size(1280, 1024, P3D);
		noStroke();
//		frameRate(20);
		mover = new Mover(-PLATE_THICKNESS / 2);
		topView = createGraphics(200, 200, P2D);

		doge = loadShape(PATH + "doge.obj");
		doge.scale(13);
		doge.rotateZ(PI);
		doge.rotateY(-PI / 2);

		background = loadShape(PATH + "background.obj");
		background.translate(0, 800, 100);
		background.rotateX(PI);
		background.rotateY(PI / 2 + PI / 4);
		background.scale(160);
		
		try {
			imgProc = new ImageProcessing(this);
			plate_controlled = true;
		} catch (Exception e) {
			e.printStackTrace();
			plate_controlled = false;
		}
//		
		
		hue = new HScrollBar(this, 200, height - 10, width-210, 10, imgProc.HUE / 255);
		hue_delta = new HScrollBar(this, 200, height - 30, width-210, 10, imgProc.HUE_DELTA / 255);
		brightness = new HScrollBar(this, 200, height - 50, width-210, 10, imgProc.MIN_BRIGHTNESS / 255);
		saturation = new HScrollBar(this, 200, height - 70, width-210, 10, imgProc.MIN_SATURATION / 255);
		intensity = new HScrollBar(this, 200, height - 90, width-210, 10, imgProc.INTENSITY / 255);
	}
	
	@Override
	public void stop(){
		if(imgProc != null) imgProc.stop();
	}

//	private long stamp = System.currentTimeMillis();
	@Override
	public void draw() {
		if(calibrate) {
			background(0);
			displayCalibrateMenu();
			return;
		}
		
		
		background(47, 49, 158);
		ambientLight(150, 150, 150);
		directionalLight(150, 150, 150, -1, 1, -1);
		perspective(PI/3, (float)(width)/(float)(height), (height/2.0f) / tan(PI/6), (height/2.0f) / tan(PI/6) * 20);


		pushMatrix();

		PImage img = imgProc.getImage();
		if(img != null){
			img.resize(200, 200);
			image(img, 0, height - 200);
		}
		
		translate(width / 2, height / 2, -depth);
		fill(255);
		// If creation mode is enabled, then we show a second plate to click on
		if (creationMode) {
			rotateX(-PI / 2); // We rotate it, so we can see it from top
			box(PLATE_SIZE, PLATE_THICKNESS, PLATE_SIZE);

			mover.display(this);
			popMatrix();
			return;
		}
		shape(background);
		hint(DISABLE_DEPTH_TEST);
		
		
		
		if(plate_controlled){
			PVector r = imgProc.getRotations();
			if(r != null){
				nrx = clamp(-r.z); 
				
				if(r.x > 0)
					nry = clamp(r.x - cam_angle);
				else
					nry = clamp(r.x + cam_angle);
				
				if(!(abs(nrx - rx) > MAX_ANGLE || abs(nry - ry) > MAX_ANGLE)){
					ry = nry;
					rx = nrx;
					
				}
			}
		}

		rotateZ(rx);
		rotateX(ry);
		rotateY(rotate);
		scale(2);
		fill(255, 255, 0, 100);
		box(PLATE_SIZE, PLATE_THICKNESS, PLATE_SIZE);

		mover.update();
		mover.checkPlate(ry, rotate, rx);
		mover.checkBorder(ry, rotate, rx, PLATE_SIZE);
		mover.checkDoges();
		mover.display(this);
		popMatrix();
		hint(ENABLE_DEPTH_TEST);
		
		textSize(32);
		fill(255, 150, 0);
//		text(round(1000f / (System.currentTimeMillis() - stamp)), 0, 30);
//		stamp = System.currentTimeMillis();
	}

	public void displayCalibrateMenu(){
		if(imgProc == null)
			return;

//		imgProc.update();
		

		image(imgProc.getImage(), 0, 0);
		image(imgProc.getThresholdImage(), imgProc.getCamWidth(), 0);
		image(imgProc.getBlurredImage(), 0, imgProc.getCamHeight());
		image(imgProc.getSobelImage(), imgProc.getCamWidth(), imgProc.getCamHeight());
		imgProc.drawQuad();
		imgProc.drawLines();

		hue.update();
		hue_delta.update();
		brightness.update();
		saturation.update();
		intensity.update();
		
		
		
		fill(255);
		
		textSize(16);
		fill(255);
		text("Hue", 0, height);
		text("Hue delta", 0, height - 20);
		text("Brightness", 0, height - 40);
		text("Saturation", 0, height - 60);
		text("Intensity", 0, height - 80);
		
		imgProc.HUE = hue.getPos() * 255;
		imgProc.HUE_DELTA	= hue_delta.getPos() * 255;
		imgProc.MIN_BRIGHTNESS = brightness.getPos() * 255;
		imgProc.MIN_SATURATION = saturation.getPos() * 255;
		imgProc.INTENSITY = intensity.getPos() * 255;
		
		hue.display();
		hue_delta.display();
		brightness.display();
		saturation.display();
		intensity.display();
		
//		image(imgProc.getSobelImage(), imgProc.getCamWidth(), 0);
	}
	public void drawTopView() {
		fill(0);
		image(topView, 0, height - TOPVIEW_SIZE);
		topView.beginDraw();
		topView.background(255);
		topView.noStroke();
		topView.fill(100, 100, 200);
		topView.rect(MARGIN, MARGIN, TOPVIEW_SIZE - 2 * MARGIN, TOPVIEW_SIZE
				- 2 * MARGIN);
		float ratio = (TOPVIEW_SIZE - 2 * MARGIN) / (float) PLATE_SIZE;
		topView.fill(255);
		topView.translate(MARGIN, MARGIN);
		PVector pos;
		for (Doge c : mover.getCylinders()) {
			pos = c.getPos();
			topView.ellipse((pos.x + PLATE_SIZE / 2) * ratio, (pos.z + PLATE_SIZE / 2) * ratio, Doge.COLLISION_BOUNDS, Doge.COLLISION_BOUNDS);
		}
		topView.fill(200, 20, 20);
		pos = mover.getBallPos();
		topView.ellipse((pos.x + PLATE_SIZE / 2) * ratio, (pos.z + PLATE_SIZE / 2)
				* ratio, Mover.SPHERE_RADIUS * ratio, Mover.SPHERE_RADIUS * ratio);

		topView.endDraw();
	}

	@Override
	public void mousePressed() {
		dx = mouseX; // -50.25*
		dy = mouseY;
		rotate_en = true;

		if (creationMode) {

			// We project the cursor position on the plate
			/*
			 * float ratio = PLATE_SIZE / (screenX(width/2 + PLATE_SIZE/2,
			 * height/2 + PLATE_SIZE/2, 0) - screenX(width/2 - PLATE_SIZE/2,
			 * height/2 - PLATE_SIZE/2, 0)); int cylX = (int)((mouseX - width/2)
			 * * ratio), cylY = (int)((mouseY - height/2) * ratio);
			 */
			float ratio = PLATE_SIZE
					/ (screenX(PLATE_SIZE / 2, PLATE_SIZE / 2, -depth) - screenX(
							-PLATE_SIZE / 2, PLATE_SIZE / 2, -depth));
			int cylX = (int) ((mouseX - width / 2) * ratio), cylY = (int) ((mouseY - height / 2) * ratio);

			// Check if cylinder is on the plate
			if (!(cylX < -500 - Doge.COLLISION_BOUNDS || cylX > 500 + Doge.COLLISION_BOUNDS
					|| cylY < -500 - Doge.COLLISION_BOUNDS || cylY > 500 + Doge.COLLISION_BOUNDS))
				mover.add(new Doge(cylX, -(int) Doge.HEIGHT - PLATE_THICKNESS
						/ 2, cylY, doge));
		}

	}

	@Override
	public void mouseReleased() {
		orx = rx;
		ory = ry;
		rotate_en = false;
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		motionSpeed += event.getCount() * 5;
		if (motionSpeed < MIN_MOTION_SPEED) // limits the motion speed
			motionSpeed = MIN_MOTION_SPEED;
	}

	@Override
	public void mouseDragged() {
		if (!creationMode && rotate_en && !plate_controlled) { // we don't want
																// rotation
																// while placing
																// cylinders
			rx = clamp(orx + (mouseX - dx) / motionSpeed);
			ry = clamp(ory + (mouseY - dy) / motionSpeed);
		}
	}

	public float clamp(float angle) {
		if (angle < -MAX_ANGLE)
			return -MAX_ANGLE;
		if (angle > MAX_ANGLE)
			return MAX_ANGLE;
		return angle;
	}

	@Override
	public void keyPressed() {
		if (key == CODED) {
			switch (keyCode) {
				case UP:
					depth -= 10;
					break;
				case DOWN:
					depth += 10;
					break;
				case LEFT:
					rotate -= 0.07f;
					break;
				case RIGHT:
					rotate += 0.07f;
					break;
				case SHIFT:
					rotate_en = false; // avoid unwanted behavior (shift while
										// mouse button pressed)
					creationMode = true;
					break;
				case CONTROL:
					cam_angle -= nry;
					break;
			}
		}
		else{
			switch(key){
				case ENTER:
					rx = 0;
					ry = 0;
					break;
				case ' ':
					calibrate = !calibrate;
					break;
				case '-':
					imgProc.togglePause();
			}
		}
	}

	@Override
	public void keyReleased() {
		if (key == CODED) {
			if (keyCode == SHIFT)
				creationMode = false;
		}
	}
}