final float MAX_ANGLE        = PI/3;
final int   WINDOW_SIZE      = 1000,
            PLATE_THICKNESS  = 50,
            PLATE_SIZE       = 1000,
            MIN_MOTION_SPEED = 10,
            TOPVIEW_SIZE     = 200,
            MARGIN           = 10;
            
int   motionSpeed = 100;

float dx = 0, rx = 0, orx = 0,
      dy = 0, ry = 0, ory = 0,
      rotate = 0,
      depth  = 2000,
      pplate_ratio = 1;
      
boolean creationMode = false,
        rotate_en    = false;
 
PGraphics topView;
Mover mover;
 
void setup() {
  size (WINDOW_SIZE, WINDOW_SIZE, P3D);
  noStroke();
  mover = new Mover(-PLATE_THICKNESS/2);
  topView = createGraphics(200, 200, P2D);
}
 
 
 
void draw() {
  background(200);
  ambientLight(102, 102, 102);
  directionalLight(150, 150, 150, 1, 1, -1);
  drawTopView();
  //camera(width/2, height/2, depth, width/2, height/2, 0, 0, 1, 0);
  
  
  pushMatrix();
  translate(width/2, height/2, -depth);
  
  fill(255);
  //If creation mode is enabled, then we show a second plate to click on
  if(creationMode){
    rotateX(-PI/2); //We rotate it, so we can see it from top
    box(PLATE_SIZE, PLATE_THICKNESS, PLATE_SIZE);
    
    mover.display();
    popMatrix();
    return;
  }
  
  
  rotateZ(rx);
  rotateX(ry);
  rotateY(rotate);
    
  box(PLATE_SIZE, PLATE_THICKNESS, PLATE_SIZE);
  
  
  mover.update();
  mover.checkPlate(ry, rotate, rx);
  mover.checkBorder(ry, rotate, rx);
  mover.checkCylinder();
  mover.display();
  popMatrix();
  
}

void drawTopView(){
  image(topView, 0, WINDOW_SIZE - TOPVIEW_SIZE);
  topView.beginDraw();
    topView.noStroke();
    topView.fill(100, 100, 200);
    topView.rect(MARGIN, MARGIN, TOPVIEW_SIZE - 2*MARGIN, TOPVIEW_SIZE - 2*MARGIN);
    float ratio = (TOPVIEW_SIZE - 2*MARGIN) / (float)PLATE_SIZE;
    topView.fill(255);
    println(ratio);
    PVector pos;
    for(Cylinder c : mover.getCylinders()){
        pos = c.getPos();
        topView.ellipse((pos.x + WINDOW_SIZE/2) * ratio, (pos.y + WINDOW_SIZE/2) * ratio, 20, 20);
        println((pos.y + WINDOW_SIZE/2) * ratio);
        println((pos.x + WINDOW_SIZE/2) * ratio);
    }
    pos = mover.getBallPos();
    topView.ellipse((pos.x + WINDOW_SIZE/2) * ratio, (pos.y + WINDOW_SIZE/2) * ratio, 10, 10);
      
  topView.endDraw(); 
}
 
void mousePressed(){
    dx = mouseX; //-50.25*
    dy = mouseY;
    rotate_en = true;
    
    
    if(creationMode){
      
      //We project the cursor position on the plate
      /*
      float ratio = PLATE_SIZE / (screenX(width/2 + PLATE_SIZE/2, height/2 + PLATE_SIZE/2, 0) -
                                  screenX(width/2 - PLATE_SIZE/2, height/2 - PLATE_SIZE/2, 0));
      int cylX = (int)((mouseX - width/2) * ratio),
          cylY = (int)((mouseY - height/2) * ratio);
       */
     float ratio = PLATE_SIZE / (screenX(PLATE_SIZE/2, PLATE_SIZE/2, -depth) -
                                  screenX(-PLATE_SIZE/2, PLATE_SIZE/2, -depth));
     int cylX = (int)((mouseX - width/2) * ratio),
         cylY = (int)((mouseY - height/2) * ratio);
         
      //Check if cylinder is on the plate
      if(!(cylX < -500 - Cylinder.BASE_SIZE || 
            cylX > 500 + Cylinder.BASE_SIZE || 
            cylY < -500 - Cylinder.BASE_SIZE || 
            cylY > 500 + Cylinder.BASE_SIZE))
        mover.add(new Cylinder(cylX, - (int)Cylinder.HEIGHT - PLATE_THICKNESS/2, cylY));
    }
    
}
 
void mouseReleased(){
    orx = rx;
    ory = ry;
    rotate_en = false;
}

void mouseWheel(MouseEvent event){
    motionSpeed += event.getCount() * 5;
    if(motionSpeed < MIN_MOTION_SPEED) //limits the motion speed
       motionSpeed = MIN_MOTION_SPEED;
}
void mouseDragged()
{
  if(!creationMode && rotate_en){ //we dont want rotation while placing cylinders
    rx = clamp(orx + (mouseX - dx)/motionSpeed);
    ry = clamp(ory + (mouseY - dy)/motionSpeed);
  }
}

float clamp(float angle){
    if(angle < -MAX_ANGLE)  return -MAX_ANGLE;
    if(angle > MAX_ANGLE)   return MAX_ANGLE;
    return angle;
}
 
void keyPressed() {
  if (key == CODED) {
      switch(keyCode){
         case UP:
           depth -= 10;
           break;
         case DOWN:
           depth += 10;
           break;
         case LEFT:
           rotate -= 0.07;
           break;
         case RIGHT:
           rotate += 0.07;
           break;
         case SHIFT: 
          rotate_en = false;  //avoid unwanted behaviour (shift while mouse button pressed)
          creationMode = true;
      }
  }
}
 
void keyReleased(){
  if (key==CODED){
      if(keyCode == SHIFT)
        creationMode = false;
  }
}


