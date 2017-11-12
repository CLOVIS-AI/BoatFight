

class Boat{
  
  float x = 0, y = 0, px, py;
  float speed = 25, speedRate = 0.1, boostRate = 0; //default speed = 25
  float angle = random(2*PI), angleRate = 0.05;
  float speedX, speedY;
  
  float health = 100;
  float shield = 0, shieldMax = 20, shieldRate = 0.005;
  
  char[] keys;
  
  int coolDown = 0, coolTime = 50;
  int dmg = 5;
  int ammoLeft = 0;
  
  float boost = 0;
  
  String name;
  color colour;
  
  Boat(String name, color colour, char[] keys){
    this.name = str(random(1000000));
    this.colour = colour;
    this.keys = keys;
    refreshAngles();
  }
  
  void move(){
    if(health > 0) boost += 0.01;
    speed += boostRate;
    if(shield < shieldMax)
      shield+=shieldRate;
    if(coolDown > 0)
      coolDown--;
    speed /= 1.03;
    if(speed < 0)
      speed = 0.01;
    speedRate /= 1.002;
    if(speedRate < 0.1)
      speedRate = 0.1;
    boostRate /= 1.05;
    if(boostRate < 0)
      boostRate = 0;
    if(health <= 0 && boost > 0)  boost();
    x += speed * speedX;
    y += speed * speedY;
    for(int i = 0; i < speed/5; i++){
      particles.add(new Particle(x, y, speed + 1, angle + PI));
    }
    if(this == me && speed != 0){
      co.notify("MOVING>"+x+" "+y+" "+angle+" "+speed);
      co.servNotify("MOVING>"+name+" "+x+" "+y+" "+angle+" "+speed);
    }
  }
  
  void turnLeft(){
    float r = angleRate/speed*2;
    angle -= r > 0.05 ? 0.05 : r;
    if(angle <= 2*PI)
      angle += 2*PI;
    particles.add(new Particle(x+15*speedX, y+15*speedY, speed-angleRate, angle + PI - angleRate/speed*100));
    refreshAngles();
  }
  
  void turnRight(){
    float r = angleRate/speed*2;
    angle += r > 0.05 ? 0.05 : r;
    if(angle >= 2*PI)
      angle -= 2*PI;
    particles.add(new Particle(x+15*speedX, y+15*speedY, speed-angleRate, angle + PI + angleRate/speed*100));
    refreshAngles();
  }
  
  void refreshAngles(){
    speedX = cos(angle);
    speedY = sin(angle);
  }
  
  void paint(){
    move();
    pushMatrix();
      translate(x, y);
      rotate(angle);
      fill(colour);
      line(0, 0, 500, 0);
      if(health < 0){
        fill(0);
        if(random(50) < 1)
          for(int i = 0; i < 50; i++)
            particles.add(new Particle(x, y, 3+random(0.2), random(2*PI)));
      }
      rect(0, 0, 30, 20);
    popMatrix();
  }
  
  void actOn(char k){
    if(health < 0) return;
    if(k == keys[0]){  speed += speedRate;}
    if(k == keys[1]){  speed -= speedRate;}
    if(k == keys[2]){  turnLeft();}
    if(k == keys[3]){  turnRight();}
    if(k == keys[4] && coolDown <= 0){  shoot(); }  
    if(k == keys[5] && boost > 0.5f){ boost(); }
  }
  
  void boost(){
    boostRate+=0.01f;
    boost-=0.5f;
  }
  
  void shoot(){
    bullets.add(new Bullet((int)x, (int)y, (ammoLeft > 0) ? dmg : 5, 5+int(speed), angle));
    coolDown = coolTime;
    ammoLeft--;
    if(ammoLeft <= 0){
      coolTime = 50;
      dmg = 5;
    }
    if(this == me){
      co.notify("SHOOT>"+(ammoLeft > 0 ? dmg : 5));
      co.servNotify("SHOOT>"+name+" "+(ammoLeft > 0 ? dmg : 5));
    }
  }
  
  void damage(float damage){
    shield -= damage;
    if(shield < 0){
      if(this == me)
        pain = 10*(int)damage;
      for(int i = 0; i < 10; i++)
        particles.add(new Particle(x, y, -shield, random(2*PI)));
      health -= -shield;
      shield = 0;
      if(this == me && health <= 0){
        co.notify("DIED>"+boost);
        co.servNotify("DIED>"+name+" "+boost);
      }
    }
  }
  
}