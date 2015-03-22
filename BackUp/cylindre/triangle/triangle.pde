PShape triangle = new PShape();
void setup() {
size(400, 400, P3D);
fill(100,100,100);
triangle = createShape();
triangle.beginShape(TRIANGLE_FAN);
triangle.vertex(0, 0);
triangle.vertex(50, 0);
triangle.vertex(50, 50);
triangle.endShape();
}
void draw() {
background(0);
translate(mouseX, mouseY);
shape(triangle);
}
