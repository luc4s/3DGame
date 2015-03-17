float cylinderBaseSize = 50;
float cylinderHeight = 50;
int cylinderResolution = 40;
PShape openCylinder = new PShape();
PShape couvercle = new PShape();
PShape couvercle2 = new PShape();
void setup() {
size(400, 400, P3D);
fill(255);
float angle;
float[] x = new float[cylinderResolution + 1];
float[] y = new float[cylinderResolution + 1];
//get the x and y position on a circle for all the sides
for(int i = 0; i < x.length; i++) {
angle = (TWO_PI / cylinderResolution) * i;
x[i] = sin(angle) * cylinderBaseSize;
y[i] = cos(angle) * cylinderBaseSize;
}
openCylinder = createShape();
openCylinder.beginShape(QUAD_STRIP);
//draw the border of the cylinder
for(int i = 0; i < x.length; i++) {
openCylinder.vertex(x[i], y[i] , 0);
openCylinder.vertex(x[i], y[i], cylinderHeight);
}
openCylinder.endShape();
couvercle = createShape();
couvercle.beginShape(TRIANGLE_FAN);
couvercle.vertex(mouseX,mouseY,0);
for(int i = 0; i < x.length; i++) {
couvercle.vertex(x[i],y[i] , 0);
}
couvercle.endShape();

couvercle2 = createShape();
couvercle2.beginShape(TRIANGLE_FAN);
couvercle2.vertex(mouseX,mouseY,cylinderHeight);
for(int i = 0; i < x.length; i++) {
couvercle2.vertex(x[i],y[i] , cylinderHeight);
}
couvercle2.endShape();

}
void draw() {
background(255);
translate(mouseX, mouseY, 0);
shape(openCylinder);
shape(couvercle);
shape(couvercle2);
}

