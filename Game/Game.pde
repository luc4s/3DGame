float depth = 4000;
float rotate = 0;
float mousex = 0;
float mousey = 0;
boolean rotate_en = false;
float dx = 0;
float dy = 0;
float rx = 0, orx = 0;
float ry = 0, ory = 0;
int sphereRadius = 50;
boolean creationMode = false;


Mover mover;

void setup() {
  size (500, 500, P3D);
  noStroke();
  mover = new Mover();
}



void draw() {
  camera(width/2, height/2, depth, 250, 250, 0, 0, 1, 0);
  directionalLight(50, 100, 125, 0, -1, 0);
  ambientLight(102, 102, 102);
  background(200);
  translate(width/2, height/2, 0);
  
  if(creationMode){
  return;
  }
  
  rotateZ(rx);
  rotateX(ry);
 
  rotateY(rotate);
  pushMatrix();

  box(1000, 50, 1000);
  translate(0, -25-sphereRadius, 0); // 25 = moitié de la hauteur de la box
  sphere(sphereRadius);
  popMatrix();
  
  mover.update();
  mover.checkPlate(ry, rotate, rx);
  mover.checkBorder(ry, rotate, rx);
  mover.display();
    
  
}

void mouseDragged()
{
  mousex = -50.25*mouseX;
  mousey = -50.25*mouseY;
  
  if(!creationMode && rotate_en){
    rx = clamp(orx + (mouseX - dx)/100); //-50.25*
    ry = clamp(ory + (mouseY - dy)/100);
  }
}

final float MAX_ANGLE = PI/3;
float clamp(float angle){
    if(angle < -MAX_ANGLE)  return -MAX_ANGLE;
    if(angle > MAX_ANGLE)   return MAX_ANGLE;
    return angle;
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
    else if(keyCode == SHIFT){
        creationMode = true;
        
    }
  }
}

void keyReleased(){
if (key==CODED){
  if(keyCode == SHIFT){
   creationMode = false;
  }
}
}
