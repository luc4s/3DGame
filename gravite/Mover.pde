class Mover {
  
  PVector location;
  PVector velocity;
  PVector gravity;
  int ballSize = 48;
  
  Mover() {
    location = new PVector (width/2, height/2);
    velocity = new PVector (1, 1);
    gravity = new PVector (0, 2);
  }
  
  void update() {
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
      location = clamp(location);
      velocity.x *= -1;
    }
    if (location.y + ballSize/2 > height || location.y - ballSize/2 < 0) {
      location = clamp(location);
      velocity.y *= -1;
    }
  }
  
  PVector clamp(PVector v){
     
     float x = v.x, y = v.y;
     if(v.x < 0) x = 0 + ballSize/2;
     if(v.x > width) x = width - ballSize/2;
     if(v.y < 0) y = 0 + ballSize/2;
     if(v.y > height) y = height - ballSize/2; 
     return new PVector(x, y);
  }
}
