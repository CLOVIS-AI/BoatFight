

abstract class PowerUp {

  final float x, y;
  final int size = 30;

  String text = "";
  color colour = color(255), textColour = color(0);

  PowerUp(int X, int Y) {
    x = X;
    y = Y;
  }

  void test() {
    for (int i = 0; i < players.size(); i++) {
      if (pow(players.get(i).x - x, 2) + pow(players.get(i).y - y, 2) <= size * size) {
        powers.remove(this);
        for (int p = 0; p < 100; p++)
          particles.add(new Particle(x, y, random(10), random(5)));
        act(players.get(i));
        return;
      }
    }
  }

  void paint() {
    test();
    fill(colour);
    ellipse(x, y, size, size);
    fill(textColour);
    text(text, x-5, y);
  }

  abstract void act(Boat b);
}



class BonusSpeed extends PowerUp {
  BonusSpeed(int x, int y) { 
    super(x, y);
    text = ">";
    colour = color(0, 255, 0);
  }

  void act(Boat b) {
    b.speedRate+=0.1;
  }
}

class MegaSpeed1 extends PowerUp {
  MegaSpeed1(int x, int y) { 
    super(x, y); 
    text = "?"; 
    colour = color(255); 
    textColour = color(0);
  }
  void act(Boat b) {
    b.boostRate+=random(0.1, 1);
    for (int p = 0; p < 100; p++)
      particles.add(new Particle(x, y, random(30), b.angle + PI));
  }
}

class MegaSpeed2 extends PowerUp {
  MegaSpeed2(int x, int y) { 
    super(x, y); 
    text = "?"; 
    colour = color(255); 
    textColour = color(0);
  }
  void act(Boat b) {

    b.coolTime = 0;
    b.coolDown = 500;
    b.dmg = 100;
    b.ammoLeft = 1;
  }
}

class MegaSpeed3 extends PowerUp {
  MegaSpeed3(int x, int y) { 
    super(x, y); 
    text = "?"; 
    colour = color(255); 
    textColour = color(0);
  }
  void act(Boat b) {
    int n = (int)random(5, 50);
    for (int i = 0; i < n; i++) {
      bullets.add(new Bullet(x, y, 10, 10, 2*PI/n * i));
    }
  }
}

class MegaSpeed4 extends PowerUp {
  MegaSpeed4(int x, int y) { 
    super(x, y); 
    text = "?"; 
    colour = color(255); 
    textColour = color(0);
  }
  void act(Boat b) {
    b.boost += 100;
  }
}

class BonusFire extends PowerUp {
  BonusFire(int x, int y) { 
    super(x, y);
    text = "?";
    colour = color(255, 0, 0);
  }
  void act(Boat b) {
    b.coolTime=5;
    b.dmg = 5;
    b.ammoLeft = 50;
  }
}

class Sniper extends PowerUp {
  Sniper(int x, int y) { 
    super(x, y); 
    colour = color(255, 255, 0);
  }
  void act(Boat b) {
    b.coolTime = 50;
    b.dmg = 20;
    b.ammoLeft = 5;
  }
}

class LandMine extends PowerUp {
  LandMine(int x, int y) { 
    super(x, y); 
    colour = color(40, 160, 220);
  }
  void act(Boat b) {
    b.shield = 0;
    b.damage(50);
    int n = (int)random(5, 50);
    for (int i = 0; i < n; i++) {
      bullets.add(new Bullet(x, y, 10, 10, 2*PI/n * i));
    }
  }
}

class HealthBar extends PowerUp {
  HealthBar(int x, int y) { 
    super(x, y); 
    colour = color(0, 0, 255);
  }
  void act(Boat b) {
    b.health += 20;
    b.shieldMax += 5;
  }
}

class LaserGun extends PowerUp {
  LaserGun(int x, int y) { 
    super(x, y); 
    colour = color(125, 125, 0);
  }
  void act(Boat b) {
    b.coolTime = 10;
    b.dmg = 10;
    b.ammoLeft = 7;
  }
}

class MiniGun extends PowerUp {
  MiniGun(int x, int y) { 
    super(x, y); 
    colour = color(125);
  }
  void act(Boat b) {
    b.coolTime = 1;
    b.dmg = 1;
    b.ammoLeft = 200;
  }
}