class Cylinder {
  PShape tree;
  
    public final static float BASE_SIZE = 50;
    public final static float HEIGHT = 50;
    public final static int RESOLUTION = 40;
    
    private final PShape openCylinder;
    private final PShape couvercle;
    private final PShape couvercle2;
    private final PVector position;
     
    public Cylinder( int xx, int yy, int zz){
      tree = loadShape("ARBR.obj");
      tree.translate(xx,yy,zz);
      tree.rotate(PI);
      tree.scale(10);
      position = new PVector(xx, yy, zz);
      
      
      fill(255, 255,0);
      float angle;
      float[] x = new float[RESOLUTION + 1];
      float[] z = new float[RESOLUTION + 1];
      //get the x and z position on a circle for all the sides
      for(int i = 0; i < x.length; i++) {
        angle = (TWO_PI / RESOLUTION) * i;
        x[i] = xx + sin(angle) * BASE_SIZE;
        z[i] = zz + cos(angle) * BASE_SIZE;
      }
      openCylinder = createShape();
      openCylinder.beginShape(QUAD_STRIP);
      //draw the border of the cylinder
      for(int i = 0; i < x.length; i++) {
        openCylinder.vertex(x[i], yy, z[i]);
        openCylinder.vertex(x[i], yy + HEIGHT, z[i]);
      }
      openCylinder.endShape();
      
      couvercle = createShape();
      
      couvercle.beginShape(TRIANGLE_FAN);
      couvercle.vertex(xx,yy,zz);
      for(int i = 0; i < x.length; i++) 
        couvercle.vertex(x[i],yy,z[i]);
      
      couvercle.endShape();
       
      couvercle2 = createShape();
      couvercle2.beginShape(TRIANGLE_FAN);
      couvercle2.vertex(xx,yy + HEIGHT,zz);
      for(int i = 0; i < x.length; i++) 
        couvercle2.vertex(x[i], yy + HEIGHT,z[i]);
      
      couvercle2.endShape();
    }
    
    PVector getPos(){
      return position;
    }
    
    float dist(PVector p){
       return position.dist(p); 
    }
     
    void display() {
      shape(tree);
    //  shape(openCylinder);
    //  shape(couvercle);
    //  shape(couvercle2);
    }
}


