package cs211.tangiblegame;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

class Doge {
    public final static float COLLISION_BOUNDS = 30;
    public final static float HEIGHT = 50;
    
    private final PShape shape;
    private final PVector position;
     
    public Doge( int xx, int yy, int zz, PShape shape){
      position = new PVector(xx, yy, zz);
      this.shape = shape;
//      shape.translate(xx, yy, zz);
    }
    
    public PVector getPos(){
      return position;
    }
    
    public float dist(PVector p){
       return position.dist(p); 
    }
     
    public void display(PApplet applet) {
    	applet.pushMatrix();
    	applet.translate(position.x, 0, position.z);
    	if(shape != null)
    		applet.shape(shape);
    	else
    		applet.box(HEIGHT);
    		
    	applet.popMatrix();
    }
}