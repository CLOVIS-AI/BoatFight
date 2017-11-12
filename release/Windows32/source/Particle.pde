


class Particle{
  
  float x, y;
  float speed;
  float speedX, speedY;
  
  Particle(float X, float Y, float speed, float angle){
    x = X;
    y = Y;
    angle += random(0.2)-0.1;
    this.speed = speed + random(0.1)-0.05;
    speedX = cos(angle);
    speedY = sin(angle);
  }
  
  void move(){
    speed -= 0.2;
    if(speed <= 0)
      particles.remove(this);
    x += speed * speedX;
    y += speed * speedY;
  }
  
  void paint(){
    move();
    fill(255);
    ellipse(x, y, speed*3, speed*3);
  }
  
}