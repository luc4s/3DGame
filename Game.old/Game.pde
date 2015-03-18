float depth = 2000;
float rotate = 0;
float mousex = 0;
float mousey = 0;

void setup() {
  size (500, 500, P3D);
  noStroke();
}



void draw() {
  camera(width/2, height/2, depth, 250, 250, 0, 0, 1, 0);
  directionalLight(50, 100, 125, 0, -1, 0);
  ambientLight(102, 102, 102);
  background(200);
  translate(width/2, height/2, 0);
  
  rotateZ(mousex);
  rotateX(mousey);
  rotateY(rotate);
  pushMatrix();

  box(1000, 50, 1000);
  popMatrix();
    
    
  
}

void mouseDragged()
{
  mousex = -50.25*mouseX;
  mousey = -50.25*mouseY;
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      depth -= 10;
    }
    else if (keyCode == DOWN) {
      depth += 10;
    }
    else if (keyCode == LEFT) {
      rotate -= 0.1;
    }
    else if (keyCode == RIGHT) {
      rotate += 0.1;
    }
  }
}
