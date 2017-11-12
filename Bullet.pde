

class Bullet {

  float x, y;
  int size;
  int damage;
  float speedX, speedY;
  float angle;

  int lifetime = 0;

  Bullet(float x, float y, int damage, float speed, float angle) {
    this.x = x;
    this.y = y;
    this.size = 10/damage;
    this.damage = damage;
    speedX = speed * cos(angle);
    speedY = speed * sin(angle);
    this.angle = angle;
  }


  void move() {
    lifetime++;
    if (x > mapSizeX || x < -mapSizeX || y > mapSizeY || y < -mapSizeY)
      bullets.remove(this);
    x += speedX;
    y += speedY;
    if (lifetime > 20) {
      for (int i = 0; i < players.size(); i++) {
        if (sqrt(pow(players.get(i).x - x, 2) + pow(players.get(i).y - y, 2)) < size + 20) {
          players.get(i).damage(damage);
          bullets.remove(this);
        }
      }
    }
  }

  void paint() {
    move();
    fill(damage * 10, damage * 5, damage);
    ellipse(x, y, size, size);
    particles.add(new Particle(x, y, damage/3, angle + PI));
  }
}