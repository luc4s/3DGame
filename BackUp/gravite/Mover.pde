class Mover {
  
  PVector location;
  PVector velocity;
  PVector gravity;
  int ballSize = 48;
  
  Mover() {
    location = new PVector (width/2, height/2);
    velocity = new PVector (1, 1);
	
    gravity = new PVector (0, 1);
  }
  
  void update() {
  // if (location.y < height - ballSize/2) {
   //   System.out.println(height);
    velocity.add(gravity);
    
    location.add(velocity);

  }
  void display(){
    stroke(0);
    strokeWeight(2);
    fill(127);
    ellipse(location.x, location.y, ballSize, ballSize);
  }
  
  void checkEdges() {
    if (location.x + ballSize/2 > width || location.x - ballSize/2 < 0) {
      velocity.x = velocity.x * -1;
      location.x = width - ballSize/2;
    }
    if (location.y + ballSize/2 > height || location.y - ballSize/2 < 0) {
            velocity.y = -abs(velocity.y);// * -1;
      location.y = height - ballSize/2;
    }
  }
  
  float R = ballSize/2;
  PVector clamp(PVector v){
     
     float x = v.x, y = v.y;
     if(v.x - R < 0) x = R;
     if(v.x + R> width) x = width - R;
     if(v.y - R < 0) y = R;
     if(v.y + R > height) y = height - R; 
     return new PVector(x, y);
  }
  
  float R = ballSize/2;
  PVector clamp(PVector v){
     
     float x = v.x, y = v.y;
     if(v.x - R < 0) x = R;
     if(v.x + R> width) x = width - R;
     if(v.y - R < 0) y = R;
     if(v.y + R > height) y = height - R; 
     return new PVector(x, y);
  }
}
