class Mover {
  public final static float  MU           = 0.01,
                             GRAVITY      = -2.0,
                             AMORTISE     = 0.75;
  public final static int    SPHERE_RADIUS  = 30;
  
  private PVector location,
                  velocity,
                  gravityForce;
        
  
  private ArrayList<Cylinder> cylinders, queue;
  
  public Mover(int plateOffset) {
    location = new PVector (0, plateOffset - SPHERE_RADIUS, 0);
    velocity = new PVector (0, 0, 0);
    gravityForce = new PVector (0, 0, 0);
    cylinders = new ArrayList<Cylinder>();
    queue = new ArrayList<Cylinder>();
  }
  
  void update() {
    velocity.add(gravityForce);
    location.add(velocity);
    PVector friction = velocity.get();
      friction.mult(-1);
      friction.normalize();
      friction.mult(MU);
    location.add(friction);
    
    //Attempt to place queued cylinders
    for(Cylinder c : (ArrayList<Cylinder>)queue.clone()){
       if (c.dist(location) > Cylinder.BASE_SIZE + SPHERE_RADIUS){
         cylinders.add(c);
         queue.remove(c);
       }
    }
  }
  
  void display(){
    noStroke();
    
    for(Cylinder c : cylinders)
      c.display();
    
    //We display queued cylinders in the ground
    pushMatrix();
      translate(0, Cylinder.HEIGHT, 0);
      for(Cylinder c : queue)
        c.display();
    popMatrix();
    
    pushMatrix();
      translate(location.x, location.y, location.z);
      fill(150, 150, 255);
      sphere(SPHERE_RADIUS);
    popMatrix();
  }
  
  void checkPlate(float rx, float ry, float rz) {
    PVector temp = new PVector(-sin(rz) * GRAVITY, sin(rx) * GRAVITY);
    temp.rotate(ry);  //if the plate rotated on itself, we have to adjuste the gravity
    gravityForce.set(temp.x, 0, temp.y);
  }
  
  void checkBorder(float rx, float ry, float rz) {
    if (location.x > 500) {
      velocity.x = -velocity.x*AMORTISE;
      location.x = 500;
    } 
    if (location.x < -500) {
      velocity.x = -velocity.x*AMORTISE;
      location.x = -500;
    } 
    if (location.z > 500) {
      velocity.z = -velocity.z*AMORTISE;
      location.z = 500;
    } 
    if (location.z < -500) {
      velocity.z = -velocity.z*AMORTISE;
      location.z = -500;
    } 
  }
  
  void add(Cylinder c){
      //Attempt to place cylinder, if not possible, queue cylinder
       if (c.dist(location) < Cylinder.BASE_SIZE + SPHERE_RADIUS)
         queue.add(c);
       else
         cylinders.add(c);
  }
  
  void checkCylinder(){
      for(Cylinder c : cylinders){
         if (c.dist(location) < Cylinder.BASE_SIZE + SPHERE_RADIUS){
             //ball is partially inside the cylinder, so we push it out to avoid the ball "stick" to it
             location.sub(velocity); 
             reflect(c.getPos(), location, velocity);
         }
      }
  }
  
  
  void reflect(PVector cylinder, PVector ball, PVector v) {
      PVector normal = new PVector(ball.x - cylinder.x, ball.z - cylinder.z);
      normal.normalize();
      
      PVector copy = new PVector(v.x, v.z);
      PVector temp = new PVector(normal.x, normal.y);
      temp.mult(2 * copy.dot(normal));
      copy.sub(temp);
      velocity.set(copy.x, v.y, copy.y);
      velocity.mult(AMORTISE);
  }
  
  ArrayList<Cylinder> getCylinders(){
     return cylinders; 
  }
  
  PVector getBallPos(){
    return location.get();
  }
}
