void setup(){
  size(640, 480, P3D);
  noStroke();
}

void draw(){
  background(200);
  lights();
  camera(mouseX*2, mouseY*2, 450, 250, 250, 0, 0, 0, 1);
  translate(width/2, height/2, 0);
  rotateX(PI/8);
  rotateY(PI/8);
  box(100, 80, 60);
  translate(100, 0, 0);
  sphere(50);
}
