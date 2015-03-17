class Mover {
  
  PVector location;
  PVector velocity;
  PVector gravityForce;
  float gravityConstant = -2;
  float rebound = 0.75;
  
  
  Mover() {
    location = new PVector (0, -125, 0);
    velocity = new PVector (0, 0, 0);
    gravityForce = new PVector (0, 0, 0);
  }
  
  void update() {
    velocity.add(gravityForce);
    location.add(velocity);
  }
  
  void display(){
    stroke(255);
    pushMatrix();
    translate(location.x, location.y, location.z);
    sphere(100);
    popMatrix();
  }
  
  void checkPlate(float rx, float ry, float rz) {
    gravityForce.x = -sin(rz) * gravityConstant ;
    gravityForce.z = sin(rx) * gravityConstant ;   
  }
  
  void checkBorder(float rx, float ry, float rz) {
    if (location.x > 1000) {
     velocity.x = -velocity.x*rebound;
    location.x = 1000;
    } 
     if (location.x < -1000) {
     velocity.x = -velocity.x*rebound;
    location.x = -1000;
    } 
    if (location.z > 1000) {
     velocity.z = -velocity.z*rebound;
    location.z = 1000;
    } 
    if (location.z < -1000) {
     velocity.z = -velocity.z*rebound;
    location.z = -1000;
    } 
  }
}
