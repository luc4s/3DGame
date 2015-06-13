package cs211.tangiblegame;

import java.util.ArrayList;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

class Mover {
	  public final static float  MU           = 0.05f,
	                             GRAVITY      = -2.0f,
	                             AMORTISE     = 0.75f;
	  public final static int    SPHERE_RADIUS  = 15;
	  
	  private PVector location,
	                  velocity,
	                  gravityForce;
	        
	  
	  private ArrayList<Doge> doges, queue;
	  
	  public Mover(int plateOffset) {
	    location = new PVector (0, plateOffset - SPHERE_RADIUS, 0);
	    velocity = new PVector (0, 0, 0);
	    gravityForce = new PVector (0, 0, 0);
	    doges = new ArrayList<Doge>();
	    queue = new ArrayList<Doge>();
	  }
	  
	  public void update() {
	    velocity.add(gravityForce);
	    location.add(velocity);
	    PVector friction = velocity.get();
	      friction.mult(-1);
	      friction.normalize();
	      friction.mult(MU);
	    location.add(friction);
	    
	    //Attempt to place queued cylinders
	    for(Doge c : (ArrayList<Doge>)queue.clone()){
	       if (c.dist(location) > Doge.COLLISION_BOUNDS + SPHERE_RADIUS){
	         doges.add(c);
	         queue.remove(c);
	       }
	    }
	  }
	  
	  public void display(PApplet applet){
	    applet.noStroke();
	    
	    applet.hint(PConstants.ENABLE_DEPTH_TEST);
	    for(Doge c : doges)
	      c.display(applet);
	    
	    applet.pushMatrix();
	    applet.translate(location.x, location.y, location.z);
	    applet.fill(255, 100, 0);
	    applet.sphere(SPHERE_RADIUS);
	    applet.popMatrix();
	  }
	  
	  public void checkPlate(float rx, float ry, float rz) {
	    PVector temp = new PVector((float)(-Math.sin(rz) * GRAVITY), (float)(Math.sin(rx) * GRAVITY));
	    temp.rotate(ry);  //if the plate rotated on itself, we have to adjuste the gravity
	    gravityForce.set(temp.x, 0, temp.y);
	  }
	  
	  public void checkBorder(float rx, float ry, float rz, float size) {
	    if (location.x > size / 2) {
	      velocity.x = -velocity.x*AMORTISE;
	      location.x = size/2;
	    } 
	    if (location.x < - size / 2) {
	      velocity.x = -velocity.x*AMORTISE;
	      location.x = -size/2;
	    } 
	    if (location.z > size / 2) {
	      velocity.z = -velocity.z*AMORTISE;
	      location.z = size/2;
	    } 
	    if (location.z < - size / 2) {
	      velocity.z = -velocity.z*AMORTISE;
	      location.z = -size/2;
	    } 
	  }
	  
	  public void add(Doge c){
	      //Attempt to place cylinder, if not possible, queue cylinder
	       if (c.dist(location) < Doge.COLLISION_BOUNDS + SPHERE_RADIUS)
	         queue.add(c);
	       else
	         doges.add(c);
	  }
	  
	  private LinkedList<Doge> hit = new LinkedList<>();
	  public void checkDoges(){
	      for(Doge c : doges){
	         if (c.dist(location) < Doge.COLLISION_BOUNDS + SPHERE_RADIUS){
	             //ball is partially inside the cylinder, so we push it out to avoid the ball "stick" to it
	             location.sub(velocity); 
	             hit.add(c);
	             reflect(c.getPos(), location, velocity);
	         }
	      }
	      for(Doge d : hit)
	    	  doges.remove(d);
	      
	      hit.clear();
	  }
	  
	  
	  public void reflect(PVector cylinder, PVector ball, PVector v) {
	      PVector normal = new PVector(ball.x - cylinder.x, ball.z - cylinder.z);
	      normal.normalize();
	      
	      PVector copy = new PVector(v.x, v.z);
	      PVector temp = new PVector(normal.x, normal.y);
	      temp.mult(2 * copy.dot(normal));
	      copy.sub(temp);
	      velocity.set(copy.x, v.y, copy.y);
	      velocity.mult(AMORTISE);
	  }
	  
	  public ArrayList<Doge> getCylinders(){
	     return doges; 
	  }
	  
	  public PVector getBallPos(){
	    return location.get();
	  }
	}