import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BoatFight extends PApplet {


final boolean out = true;
final boolean log = true;

enum GameState{ GAME, MENU }

public void settings(){
  if(out){
    fullScreen();
  }else{
    size(700, 600);
  }
}

Connexion co;
GameState gm = GameState.MENU;
int colSize;

public void setup(){
  println("Setup ...");
  frameRate(out ? 100 : 30);
  
  log("Generating menu ...");
  objects = new ArrayList(50);
  objectsMap = new HashMap(50);
  
  focus = new Button(20, 20, 100, 20, "Quit game", new Act(){ public void act(){log("Closing ..."); exit();}});     objects.add(focus);
  colSize = width/3;
  int Y = 110, X1 = 20, X2 = colSize+20, X3 = colSize*2+20;
  
  log("1", "User settings");
  int x = X1, y = Y;
  //Left column : User settings
  objects.add(new FinalTextArea(x, y, "Particles :"));
    focus = new IntTextArea(x+(int)textWidth("Particles :..."), y, 100, 20, "1000", 0, Integer.MAX_VALUE); objects.add(focus); objectsMap.put("PARTICLES", focus);
  objects.add(new FinalTextArea(x, y+=60, "FPS :"));
    focus = new IntTextArea(x+(int)textWidth("FPS :..."), y, 100, 20, out ? "100" : "30", 10, 500); objects.add(focus); objectsMap.put("FPS", focus);
  objects.add(new FinalTextArea(x, y+=60, "Your color :"));
    objects.add(new FinalTextArea(x, y+=25, "Red (0-255) : "));
    focus = new IntTextArea(x+150, y, 40, 20, "255", 0, 255);           objects.add(focus); objectsMap.put("RED", focus);
    objects.add(new FinalTextArea(x, y+=25, "Green (0-255) : "));
    focus = new IntTextArea(x+150, y, 40, 20, "0", 0, 255);           objects.add(focus); objectsMap.put("GREEN", focus);
    objects.add(new FinalTextArea(x, y+=25, "Blue (0-255) : "));
    focus = new IntTextArea(x+150, y, 40, 20, "0", 0, 255);           objects.add(focus); objectsMap.put("BLUE", focus);
  objects.add(new FinalTextArea(x, y+=60, "Controls :"));
    objects.add(new FinalTextArea(x, y+=25, "Forward : "));
    focus = new TextArea(x+100, y, 20, "e");                          objects.add(focus); objectsMap.put("FORWARD", focus);
    objects.add(new FinalTextArea(x, y+=25, "Backward : "));
    focus = new TextArea(x+100, y, 20, "d");                          objects.add(focus); objectsMap.put("BACKWARD", focus);
    objects.add(new FinalTextArea(x, y+=25, "Left : "));
    focus = new TextArea(x+100, y, 20, "s");                          objects.add(focus); objectsMap.put("LEFT", focus);
    objects.add(new FinalTextArea(x, y+=25, "Right : "));
    focus = new TextArea(x+100, y, 20, "f");                          objects.add(focus); objectsMap.put("RIGHT", focus);
    objects.add(new FinalTextArea(x, y+=25, "Shoot : "));
    focus = new TextArea(x+100, y, 20, "i");                          objects.add(focus); objectsMap.put("SHOOT", focus);
    objects.add(new FinalTextArea(x, y+=25, "Boost : "));
    focus = new TextArea(x+100, y, 20, "k");                          objects.add(focus); objectsMap.put("BOOST", focus);
  
  //Middle column : Server settings
  log("2", "Server settings");
  x = X2; y = Y;
  objects.add(new FinalTextArea(x, y, "Host settings"));
  objects.add(new FinalTextArea(x, y+=25, "Port :"));
    focus = new IntTextArea(x+(int)textWidth("Port :..."), y, 100, 20, "6666", 1024, 35535);                    objects.add(focus); objectsMap.put("SERV_PORT", focus);
  objects.add(new FinalTextArea(x, y+=60, "Map size"));
  objects.add(new FinalTextArea(x, y+=25, "Horizontally : "));
    focus = new IntTextArea(x+(int)textWidth("Horizontally :..."), y, 100, 20, "1000", 0, Integer.MAX_VALUE);            objects.add(focus); objectsMap.put("MAP_SIZE_X", focus);
  objects.add(new FinalTextArea(x, y+=25, "Vertically : "));
    focus = new IntTextArea(x+(int)textWidth("Vertically :..."), y, 100, 20, "1000", 0, Integer.MAX_VALUE);              objects.add(focus); objectsMap.put("MAP_SIZE_Y", focus);
  focus = new Button(x, height - 50, colSize-40, 20, "Create room", new Act(){ public void act(){init(); initAsServer();}});    objects.add(focus);
  objects.add(new FinalTextArea(x, y+=60, "Pick-up luck : 1/"));
    focus = new IntTextArea(x+(int)textWidth("Pick-up luck : 1/..."), y, 100, 20, "100", 1, 10000); objects.add(focus); objectsMap.put("LUCK", focus);
  
  //Right column : Client settings
  log("3", "Client settings");
  int largeur = (int)textWidth("255")+10;
  x = X3; y = Y;
  objects.add(new FinalTextArea(x, y, "Client settings"));
  objects.add(new FinalTextArea(x, y+=25, "Host IP :"));
    focus = new IntTextArea(x+(int)textWidth("Host IP :..."), y, largeur, 20, "127", 0, 255);                           objects.add(focus); objectsMap.put("SERV_IP_1", focus);
    focus = new IntTextArea(x+(int)textWidth("Host IP :....255..."), y, largeur, 20, "0", 0, 255);                    objects.add(focus); objectsMap.put("SERV_IP_2", focus);
    focus = new IntTextArea(x+(int)textWidth("Host IP :....255....255..."), y, largeur, 20, "0", 0, 255);             objects.add(focus); objectsMap.put("SERV_IP_3", focus);
    focus = new IntTextArea(x+(int)textWidth("Host IP :....255....255....255..."), y, largeur, 20, "1", 0, 255);      objects.add(focus); objectsMap.put("SERV_IP_4", focus);
  objects.add(new FinalTextArea(x, y+=25, "Port :"));
    focus = new IntTextArea(x+(int)textWidth("Port :..."), y, 100, 20, "6666", 1024, 65535);                   objects.add(focus); objectsMap.put("CLIENT_PORT", focus);
  focus = new Button(x, height - 50, colSize-40, 20, "Join room", new Act(){ public void act(){init(); initAsClient();}});     objects.add(focus);
  
  println("Ready !\n");
}

public void draw(){
  surface.setTitle("Boat Fight by CLOVIS | Version 1.4");
  switch(gm){
    case GAME: game(); break;
    case MENU: menu(); break;
  }
}

TextArea test;
InteractiveObject focus;
ArrayList<InteractiveObject> objects;
HashMap<String, InteractiveObject> objectsMap;

public void menu(){
  background(50, 40, 58);
  textSize(50);
  textAlign(CENTER);
  fill(255);
  text("Boat Fight", width/2, 50);
  
  textAlign(RIGHT, BOTTOM);
  textSize(12);
  text("by CLOVIS", width-5, 70);
  
  stroke(205, 245, 198);
  line(0, 70, width, 70);
  
  stroke(105, 145, 98);
  line(colSize, 70, colSize, height);
  line(colSize*2, 70, colSize*2, height);
  
  textSize(12);
  for(InteractiveObject o : objects){
    if(o.collide(mouseX, mouseY))
      focus = o;
    o.draw();
  }
  
}

ArrayList<Boat> players = new ArrayList();
Boat me;
ArrayList<Bullet> bullets = new ArrayList();
ArrayList<Particle> particles = new ArrayList();
int maxParticles = 1000;
ArrayList<PowerUp> powers = new ArrayList();

int mapSizeX = 1000;
int mapSizeY = 1000;
int luck;
boolean outside = false;
int mask;
int pain = 0;
float zoom;

public void game(){
  if(me == null)  return;
  outside = me.x > mapSizeX || me.x < -mapSizeX || me.y > mapSizeY || me.y < -mapSizeY;
  mask = outside ? mask+1 : mask < 3 ? 0 : mask-3;
  background(40 - mask + pain, 164 - mask - pain*2, 223 - mask - pain*2);
  if(pain > 0)
    pain-=1;
  pushMatrix();
  translate(width/2, height/2);
  zoom = map(me.speed, 0.1f, 20, 1.8f, 0.1f);
  scale(zoom < 0.001f ? 0.001f : zoom);
  translate(-me.x + random(pain/20), -me.y + random(pain/20));
  stroke(255, 0, 0);
  line(-mapSizeX, -mapSizeY, mapSizeX, -mapSizeY);
  line(-mapSizeX, -mapSizeY, -mapSizeX, mapSizeY);
  line(mapSizeX, mapSizeY, mapSizeX, -mapSizeY);
  line(mapSizeX, mapSizeY, -mapSizeX, mapSizeY);
  noStroke();
  for(int p = 0; p < particles.size(); p++){
    particles.get(p).paint();
  }
  while(particles.size() > maxParticles) particles.remove(0);
  for(int p = 0; p < powers.size(); p++){
    powers.get(p).paint();
  }
  if(co != null && co.isServer && random(luck) < 1){
    int x = (int)random(mapSizeX*2)-mapSizeX, y = (int)random(mapSizeY*2)-mapSizeY;
    String n = "";
    switch((int)random(15)){
      case 0: case 1: case 3: case 4: powers.add(new BonusSpeed(x, y)); n="SPEED"; break;
      case 5: powers.add(new BonusFire(x, y)); n="FIRE"; break;
      case 6: powers.add(new Sniper(x, y)); n="SNIPER"; break;
      case 7: powers.add(new MegaSpeed1(x, y)); n="MSPEED1"; break;
      case 8: powers.add(new LandMine(x, y)); n="MINE"; break;
      case 9: powers.add(new HealthBar(x, y)); n="HEALTH"; break;
      case 10: powers.add(new LaserGun(x, y)); n="LASER"; break;
      case 11: powers.add(new MiniGun(x, y)); n="MINI"; break;
      case 12: powers.add(new MegaSpeed2(x, y)); n="MSPEED2"; break;
      case 13: powers.add(new MegaSpeed3(x, y)); n="MSPEED3"; break;
      case 14: powers.add(new MegaSpeed4(x, y)); n="MSPEED4"; break;
    }
    co.servNotify("GIFT>"+n+" "+x+" "+y);
  }
  for(int b = 0; b < bullets.size(); b++){
    bullets.get(b).paint();
  }
  for(int k = 0; k < pressed.size(); k++){
    me.actOn(pressed.get(k));
  }
  for(int i = 0; i < players.size(); i++){
    players.get(i).paint();
  }
  popMatrix();
  fill(255);
  text("FPS : " + (int)frameRate + "\nParticles : "+particles.size()+"/"+maxParticles+"\nPower ups : "+powers.size()+"\nBullets : "+bullets.size()+"\n"+(co.isServer ? Server.ip() : ""), 30, 40);
  
  if(co != null)
    co.update();
  
  //Backgrounds
  fill(20, 80, 110);
  rect(width/2, height/2 + 100, me.coolTime/2, 10);
  rect(width/2, height/2 + 120, (int)(me.boost/10)*10, 10);
  rect(width/2, height/2 + 135, me.shieldMax, 10);
  //Values
  fill(255);
  rect(width/2, height/2 + 90, me.speed*5, 10);
  rect(width/2, height/2 + 100, me.coolDown/2, 10);
  rect(width/2, height/2 + 110, me.ammoLeft < 0 ? 0 : me.ammoLeft*5, 10);
  rect(width/2, height/2 + 120, me.boost, 10);
  
  rect(width/2, height/2 + 135, me.shield, 10);
  rect(width/2, height/2 + 145, me.health, 10);
  
  stroke(255, 0, 0);
  line(width-100, 0, width-100, 100);
  line(width-100, 100, width, 100);
  fill(255, 0, 0);
  rect(map(me.x, -mapSizeX, mapSizeX, width-100, width),
       map(me.y, -mapSizeY, mapSizeY, 0, 100), 2, 2);
  
  if(outside){
    float theta = atan(me.y / me.x) + (me.x < 0 ? 0 : PI);
    stroke(255, 0, 0);
    line(width/2 + 40*cos(theta), height/2 + 40*sin(theta), width/2 + 50*cos(theta), height/2 + 50*sin(theta));
    noStroke();
    me.damage(0.01f);
  }
  
  textSize(50);
  textAlign(CENTER, TOP);
  text((int)(me.speed*5), width/2, 0);
  textSize(12);
  textAlign(LEFT, BASELINE);
}

public void init(){
  log("Initializing game ...");
  rectMode(CENTER);
  ellipseMode(CENTER);
  log("modes", "Displaying modes");
  mapSizeX = PApplet.parseInt(objectsMap.get("MAP_SIZE_X").toString());
  log("MapSizeX", str(mapSizeX));
  mapSizeY = PApplet.parseInt(objectsMap.get("MAP_SIZE_Y").toString());
  log("MapSizeY", str(mapSizeY));
  maxParticles = PApplet.parseInt(objectsMap.get("PARTICLES").toString());
  log("maxParticles", str(maxParticles));
  luck = PApplet.parseInt(objectsMap.get("LUCK").toString());
  log("luck", str(luck));
  frameRate(PApplet.parseInt(objectsMap.get("FPS").toString()));
  log("FPS", objectsMap.get("FPS").toString());
  players.clear();
  log("players", "Cleared.");
  powers.clear();
  log("powers", "Cleared.");
  bullets.clear();
  log("bullets", "Cleared.");
  particles.clear();
  log("particles", "Cleared.");
}

public void initAsServer(){
  log("Initialiazing as server ...");
  int port = PApplet.parseInt(objectsMap.get("SERV_PORT").toString());
  log("port", str(port));
  int red = PApplet.parseInt(objectsMap.get("RED").toString());
  log("red", str(red));
  int green = PApplet.parseInt(objectsMap.get("GREEN").toString());
  log("green", str(green));
  int blue = PApplet.parseInt(objectsMap.get("BLUE").toString());
  log("blue", str(blue));
  log("co", "Launching a new connexion as a server ...");
  co = new Connexion(this, port, color(red, green, blue));
  log("co", "Launching was successful.");
  players.add(new Boat("ME", color(red, green, blue), new char[]{objectsMap.get("FORWARD").toString().charAt(0),
                                                                 objectsMap.get("BACKWARD").toString().charAt(0),
                                                                 objectsMap.get("LEFT").toString().charAt(0),
                                                                 objectsMap.get("RIGHT").toString().charAt(0),
                                                                 objectsMap.get("SHOOT").toString().charAt(0),
                                                                 objectsMap.get("BOOST").toString().charAt(0)}));
  log("players", "Added myself");
  me = players.get(0);
  log("me", "Assigned myself to myself");
  gm = GameState.GAME;
  log("gm", "Launching game ...");
}

public void initAsClient(){
  log("Initialiazing as client ...");
  int port = PApplet.parseInt(objectsMap.get("CLIENT_PORT").toString());
  log("port", str(port));
  String IP = objectsMap.get("SERV_IP_1").toString() + "."
             +objectsMap.get("SERV_IP_2").toString() + "."
             +objectsMap.get("SERV_IP_3").toString() + "."
             +objectsMap.get("SERV_IP_4").toString();
  log("IP", "Processing host's IP");
  int red = PApplet.parseInt(objectsMap.get("RED").toString());
  log("red", str(red));
  int green = PApplet.parseInt(objectsMap.get("GREEN").toString());
  log("green", str(green));
  int blue = PApplet.parseInt(objectsMap.get("BLUE").toString());
  log("blue", str(blue));
  log("co", "Launching a new connexion as a client ...");
  co = new Connexion(this, IP, port, color(red, green, blue));
  log("co", "Launching was successful.");
  players.add(new Boat("ME", color(red, green, blue), new char[]{objectsMap.get("FORWARD").toString().charAt(0),
                                                                 objectsMap.get("BACKWARD").toString().charAt(0),
                                                                 objectsMap.get("LEFT").toString().charAt(0),
                                                                 objectsMap.get("RIGHT").toString().charAt(0),
                                                                 objectsMap.get("SHOOT").toString().charAt(0),
                                                                 objectsMap.get("BOOST").toString().charAt(0)}));
  log("players", "Added myself");
  me = players.get(0);
  log("me", "Assigned myself to myself");
  gm = GameState.GAME;
  log("gm", "Launching game ...");
}

ArrayList<Character> pressed = new ArrayList();

public void keyPressed(){
  if(key == ESC){
    gm = GameState.MENU;
    if(co.isServer){
      co.s.stop();
    }else{
      co.notify("DIED");
    }
    co = null;
    key = 0;
  }
  switch(gm){
    case GAME:
      if(!pressed.contains(new Character(key)))
        pressed.add(new Character(key));
      break;
    case MENU:
      focus.onKeyPressed();
      break;
  }
}

public void keyReleased(){
  switch(gm){
    case GAME:
      if(pressed.contains(new Character(key)))
        pressed.remove(new Character(key));
      break;
    case MENU:
      focus.onKeyReleased();
      break;
  }
}

public void disconnectEvent(Client c){ co.disco(c); }

public void mousePressed(){
  if(focus.collide(mouseX, mouseY))
    focus.onMousePressed();
}

long prevTime;
public void log(String reason, String msg){
  if(!log)  return;
  long current = millis();
  System.out.println((current-prevTime) + "\t["+reason+"] " + msg);
  prevTime = current;
}

public void log(String msg){
  if(!log)  return;
  long current = millis();
  System.out.println("\n" + msg);
  prevTime = current;
}


class Boat{
  
  float x = 0, y = 0, px, py;
  float speed = 25, speedRate = 0.1f, boostRate = 0; //default speed = 25
  float angle = random(2*PI), angleRate = 0.05f;
  float speedX, speedY;
  
  float health = 100;
  float shield = 0, shieldMax = 20, shieldRate = 0.005f;
  
  char[] keys;
  
  int coolDown = 0, coolTime = 50;
  int dmg = 5;
  int ammoLeft = 0;
  
  float boost = 0;
  
  String name;
  int colour;
  
  Boat(String name, int colour, char[] keys){
    this.name = str(random(1000000));
    this.colour = colour;
    this.keys = keys;
    refreshAngles();
  }
  
  public void move(){
    if(health > 0) boost += 0.01f;
    speed += boostRate;
    if(shield < shieldMax)
      shield+=shieldRate;
    if(coolDown > 0)
      coolDown--;
    speed /= 1.03f;
    if(speed < 0)
      speed = 0.01f;
    speedRate /= 1.002f;
    if(speedRate < 0.1f)
      speedRate = 0.1f;
    boostRate /= 1.05f;
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
  
  public void turnLeft(){
    float r = angleRate/speed*2;
    angle -= r > 0.05f ? 0.05f : r;
    if(angle <= 2*PI)
      angle += 2*PI;
    particles.add(new Particle(x+15*speedX, y+15*speedY, speed-angleRate, angle + PI - angleRate/speed*100));
    refreshAngles();
  }
  
  public void turnRight(){
    float r = angleRate/speed*2;
    angle += r > 0.05f ? 0.05f : r;
    if(angle >= 2*PI)
      angle -= 2*PI;
    particles.add(new Particle(x+15*speedX, y+15*speedY, speed-angleRate, angle + PI + angleRate/speed*100));
    refreshAngles();
  }
  
  public void refreshAngles(){
    speedX = cos(angle);
    speedY = sin(angle);
  }
  
  public void paint(){
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
            particles.add(new Particle(x, y, 3+random(0.2f), random(2*PI)));
      }
      rect(0, 0, 30, 20);
    popMatrix();
  }
  
  public void actOn(char k){
    if(health < 0) return;
    if(k == keys[0]){  speed += speedRate;}
    if(k == keys[1]){  speed -= speedRate;}
    if(k == keys[2]){  turnLeft();}
    if(k == keys[3]){  turnRight();}
    if(k == keys[4] && coolDown <= 0){  shoot(); }  
    if(k == keys[5] && boost > 0.5f){ boost(); }
  }
  
  public void boost(){
    boostRate+=0.01f;
    boost-=0.5f;
  }
  
  public void shoot(){
    bullets.add(new Bullet((int)x, (int)y, (ammoLeft > 0) ? dmg : 5, 5+PApplet.parseInt(speed), angle));
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
  
  public void damage(float damage){
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


  public void move() {
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

  public void paint() {
    move();
    fill(damage * 10, damage * 5, damage);
    ellipse(x, y, size, size);
    particles.add(new Particle(x, y, damage/3, angle + PI));
  }
}


class Connexion {

  Server s;
  Client c;
  String input;
  String data[];

  HashMap<String, Boat> clients;
  ArrayList<Client> recievers;

  final boolean isServer;

  Connexion(PApplet source, int port, int colour) {
    isServer = true;
    s = new Server(source, port);
    println("Lauching as server");
    clients = new HashMap();
    recievers = new ArrayList();
  }

  Connexion(PApplet source, String ip, int port, int colour) {
    isServer = false;
    println("Trying to connect to server ...");
    c = new Client(source, ip, port);
    println("Lauching as client");
    clients = new HashMap();
    //players.add(new Boat("ME", colour, new char[]{'e', 'd', 's', 'f', 'i', 'k'}));
    //me = players.get(0);
    c.write("HELLO>"+red(colour)+" "+green(colour)+" "+blue(colour)+" "+random(2*PI)+"\n\n\n");
  }

  public void update() {
    if (isServer) {
      while ((c = s.available()) != null) {
        input = c.readString();
        String[] messages = input.split("\n");
        for (int i = 0; i < messages.length; i++) {
          if (messages[i] != null && !messages[i].equals("") && messages[i].contains(">")) {
            println("["+i+"] "+ messages[i]);
            try {
              data = messages[i].split(">");
              String[] params = data[1].split(" ");
              Boat p;
              switch(data[0]) {
              case "HELLO": // HELLO>RED GREEN BLUE ANGLE
                Boat n = new Boat(c.ip(), color(PApplet.parseInt(params[0]), PApplet.parseInt(params[1]), PApplet.parseInt(params[2])), null);
                n.angle = PApplet.parseFloat(params[3]);
                c.write("YOU>"+n.name+" "+mapSizeX+" "+mapSizeY+"\n\n\n");
                for (Client c3 : recievers) //Tell every players that a new player arrived
                  c3.write("NEW>"+n.name+" "+params[0]+" "+params[1]+" "+params[2]+" "+params[3]+"\n\n\n");
                for (int pl = 0; pl < players.size(); pl++) {
                  Boat player = players.get(pl);
                  c.write("NEW>"+player.name+" "+red(player.colour)+" "+green(player.colour)+" "+blue(player.colour)+" "+player.angle);
                }
                clients.put(c.ip(), n);
                players.add(n);
                recievers.add(c);
                break;
              case "MOVING": //MOVING>X Y ANGLE SPEED
                p = clients.get(c.ip());
                p.x = PApplet.parseFloat(params[0]);
                p.y = PApplet.parseFloat(params[1]);
                p.angle = PApplet.parseFloat(params[2]);
                p.speed = PApplet.parseFloat(params[3]);
                p.refreshAngles();
                servNotify("MOVING>"+p.name+" "+p.x+" "+p.y+" "+p.angle+" "+p.speed, c);
                break;
              case "SHOOT": //SHOOT>DAMAGE
                p = clients.get(c.ip());
                p.dmg = PApplet.parseInt(params[0]);
                p.shoot();
                break;
              case "DIED": //SHOOT>BOOST
                Boat h = clients.get(c.ip());
                h.boost = PApplet.parseInt(params[1]);
                h.health = -1;
                disco(c);
                break;
              }
            }
            catch(ArrayIndexOutOfBoundsException e) {
              println("Parsing error ...");
            }
          }
        }
      }
    } else {
      if(!c.active()) { key = ESC; keyPressed(); }
      while ((c.available()) > 0) {
        input = c.readString();
        String[] messages = input.split("\n");
        for (int i = 0; i < messages.length; i++) {
          if (messages[i] != null && !messages[i].equals("") && messages[i].contains(">")) {
            //println("["+i+"] "+ messages[i]);
            data = messages[i].split(">");
            try {
              String[] params = data[1].split(" ");
              Boat p;
              switch(data[0]) {
              case "GIFT": //GIFT>TYPE X Y
                int x = PApplet.parseInt(params[1]), y = PApplet.parseInt(params[2]);
                switch(params[0]) {
                case "SPEED":     
                  powers.add(new BonusSpeed(x, y)); 
                  break;
                case "MSPEED1":    
                  powers.add(new MegaSpeed1(x, y)); 
                  break;
                case "MSPEED2":    
                  powers.add(new MegaSpeed2(x, y)); 
                  break;
                case "MSPEED3":    
                  powers.add(new MegaSpeed3(x, y)); 
                  break;
                case "MSPEED4":    
                  powers.add(new MegaSpeed4(x, y)); 
                  break;
                case "FIRE":      
                  powers.add(new BonusFire(x, y)); 
                  break;
                case "SNIPER":    
                  powers.add(new Sniper(x, y)); 
                  break;
                case "MINE":      
                  powers.add(new LandMine(x, y)); 
                  break;
                case "HEALTH":    
                  powers.add(new HealthBar(x, y)); 
                  break;
                case "LASER":     
                  powers.add(new LaserGun(x, y)); 
                  break;
                case "MINI":      
                  powers.add(new MiniGun(x, y)); 
                  break;
                }
                break;
              case "NEW": // NEW>ID RED GREEN BLUE ANGLE
                Boat n = new Boat(params[0], color(PApplet.parseInt(params[1]), PApplet.parseInt(params[2]), PApplet.parseInt(params[3])), null);
                n.angle = PApplet.parseFloat(params[4]);
                clients.put(params[0], n);
                players.add(n);
                break;
              case "YOU": //YOU>ID MAPX MAPY
                clients.put(params[0], clients.remove(me.name));
                me.name = params[0];
                println("I am "+me.name);
                mapSizeX = PApplet.parseInt(params[1]);
                mapSizeY = PApplet.parseInt(params[2]);
                break;
              case "MOVING": //MOVING>ID X Y ANGLE SPEED
                p = clients.get(params[0]);
                if (p == null) {
                  System.err.println("Couldn't find player #"+params[0]+"... (me : #"+me.name+")");
                  return;
                }
                p.x = PApplet.parseFloat(params[1]);
                p.y = PApplet.parseFloat(params[2]);
                p.angle = PApplet.parseFloat(params[3]);
                p.speed = PApplet.parseFloat(params[4]);
                p.refreshAngles();
                break;
              case "SHOOT": //SHOOT>ID DAMAGE
                Boat g = clients.get(params[0]);
                if (g == null) {
                  System.err.println("Couldn't find player #"+params[0]+"... (me : #"+me.name+")");
                  return;
                }
                g.dmg = PApplet.parseInt(params[1]);
                println(g.dmg);
                g.shoot();
                break;
              case "DIED": //SHOOT>ID BOOST
                Boat h = clients.get(params[0]);
                if (h == null) {
                  System.err.println("Couldn't find player #"+params[0]+"... (me : #"+me.name+")");
                  return;
                }
                h.boost = PApplet.parseInt(params[1]);
                h.health = -1;
                break;
              }
            }
            catch(ArrayIndexOutOfBoundsException e) {
              println("Parsing error ...");
            }
          } else { 
            if (messages[i] != "" && messages[i] != null) println("Rejected message : "+messages[i]);
          }
        }
      }
    }
  }

  public void notify(String cmd) {
    if (!isServer) {
      c.write(cmd+"\n\n\n");
    }
  }

  public void servNotify(String cmd) {
    if (isServer)
      for (Client c1 : recievers)
        c1.write(cmd+"\n\n\n");
  }

  public void servNotify(String cmd, Client except) {
    if (isServer)
      for (Client c1 : recievers)
        if (c1 != except) 
          c1.write(cmd+"\n\n\n");
  }

  public void disco(Client c2) {
    recievers.remove(c2);
    clients.get(c2.ip()).health = -1;
    println(c2.ip() + " logged off or was banned.");
  }
}


class Button extends InteractiveObject {
  
  Act act;
  
  Button(int X, int Y, int SX, int SY, String NAME, Act ACTION){
    super(X, Y, SX, SY, NAME);
    act = ACTION;
  }
  
  public void draw(){
    fill(focus == this ? 255 : 100);
    rectMode(CORNER);
    rect(x, y, sx, sy);
    fill(focus == this ? 0 : 255);
    textAlign(LEFT, TOP);
    text(name, x+5, y+5);
  }
  
  public void onMousePressed(){
    act.act();
  }
  
}


interface Act{
  
  public void act();
  
}


class FinalTextArea extends InteractiveObject{
  
  String content;
  
  FinalTextArea(int X, int Y, String NAME){
    super(X, Y, 0, 20, NAME);
    textSize(12);
    content = NAME;
    sx = (int)textWidth(NAME);
  }
  
  public void draw(){
    fill(255);
    textAlign(LEFT, TOP);
    text(content, x+5, y+5);
  }
  
  public boolean collide(int x, int y){return false;}
  
}


class TextArea extends InteractiveObject{
  
  String content, display;
  int cursorPos;
  int colour;
  
  TextArea(int X, int Y, int SX, String name){
    super(X, Y, SX, 20, name);
    content = name;
    cursorPos = content.length()-1;
    //refreshDisplay();
    colour = color(255);
  }
  
  public void onKeyPressed(){
    if(key == CODED){
      if(keyCode == LEFT && cursorPos > 0)
        cursorPos--;
      if(keyCode == RIGHT && cursorPos < content.length()-1)
        cursorPos++;
    }else{
      if(key == BACKSPACE){
        if(content.length() <= 0)   return;
        content = content.substring(0, content.length()-1);
        //refreshDisplay();
        cursorPos--;
      }else if(cursorPos == content.length()-1)          {content += key; cursorPos++; }
      else{
        String part1 = content.substring(0, cursorPos+1),
               part2 = content.substring(cursorPos, content.length());
        content = part1 + key + part2;
        cursorPos++;
      }
      //refreshDisplay();
    }
  }
  
  /*void refreshDisplay(){
    String s = content;
    if(textWidth(s) < sx){ display = s; return; }
    while(textWidth(s+"...") > sx){
      s = s.substring(s.length()-2);
    }
    display = s;
  }*/
  
  public void draw(){
    rectMode(CORNER);
    noFill();
    stroke(colour);
    if(focus == this)
      fill(100);
    rect(x, y, sx, sy);
    fill(255);
    textAlign(LEFT, TOP);
    text(content, x+5, y+5);
    if(focus != this) return;
    float cp = textWidth(content.substring(0, cursorPos+1)) + 5;
    line(x+cp+1, y+5, x+cp+1, y+5+12);
  }
  
  public String toString(){ return content; }
}


class IntTextArea extends TextArea{
  
  int min, max;
  
  IntTextArea(int X, int Y, int SX, int SY, String name, int MIN, int MAX){
    super(X, Y, SX, name);
    min = MIN;
    max = MAX;
  }
  
  public void onKeyPressed(){
    if(key != CODED && key != BACKSPACE){
      int k = Character.getNumericValue(key);
      if(k >= 0 && k <= 9)
        super.onKeyPressed();
    }else{
      super.onKeyPressed();
    }
    try{
      int v = Integer.parseInt(content);
      if(v < min || v > max){
        colour = color(255, 0, 0);
      }else{
        colour = color(255);
      }
    }catch(NumberFormatException e){
      content = "";
      cursorPos = -1;
    }
  }
  
}

abstract class InteractiveObject{
  
  int x, y;
  int sx, sy;
  String name;
  
  InteractiveObject(int X, int Y, int SX, int SY, String NAME){
    x = X; y = Y; name = NAME;
    sx = SX; sy = SY;
  }
  
  
  public void onKeyPressed(){}
  public void onKeyReleased(){}
  
  public void onMousePressed(){}
  public void onMouseRelease(){}
  
  public boolean collide(int X, int Y){
    return X > x && X < x+sx && Y > y && Y < y+sy;
  }
  
  public abstract void draw();
  
  public String toString(){ return name; }
}



class Particle{
  
  float x, y;
  float speed;
  float speedX, speedY;
  
  Particle(float X, float Y, float speed, float angle){
    x = X;
    y = Y;
    angle += random(0.2f)-0.1f;
    this.speed = speed + random(0.1f)-0.05f;
    speedX = cos(angle);
    speedY = sin(angle);
  }
  
  public void move(){
    speed -= 0.2f;
    if(speed <= 0)
      particles.remove(this);
    x += speed * speedX;
    y += speed * speedY;
  }
  
  public void paint(){
    move();
    fill(255);
    ellipse(x, y, speed*3, speed*3);
  }
  
}


abstract class PowerUp {

  final float x, y;
  final int size = 30;

  String text = "";
  int colour = color(255), textColour = color(0);

  PowerUp(int X, int Y) {
    x = X;
    y = Y;
  }

  public void test() {
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

  public void paint() {
    test();
    fill(colour);
    ellipse(x, y, size, size);
    fill(textColour);
    text(text, x-5, y);
  }

  public abstract void act(Boat b);
}



class BonusSpeed extends PowerUp {
  BonusSpeed(int x, int y) { 
    super(x, y);
    text = ">";
    colour = color(0, 255, 0);
  }

  public void act(Boat b) {
    b.speedRate+=0.1f;
  }
}

class MegaSpeed1 extends PowerUp {
  MegaSpeed1(int x, int y) { 
    super(x, y); 
    text = "?"; 
    colour = color(255); 
    textColour = color(0);
  }
  public void act(Boat b) {
    b.boostRate+=random(0.1f, 1);
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
  public void act(Boat b) {

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
  public void act(Boat b) {
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
  public void act(Boat b) {
    b.boost += 100;
  }
}

class BonusFire extends PowerUp {
  BonusFire(int x, int y) { 
    super(x, y);
    text = "?";
    colour = color(255, 0, 0);
  }
  public void act(Boat b) {
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
  public void act(Boat b) {
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
  public void act(Boat b) {
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
  public void act(Boat b) {
    b.health += 20;
    b.shieldMax += 5;
  }
}

class LaserGun extends PowerUp {
  LaserGun(int x, int y) { 
    super(x, y); 
    colour = color(125, 125, 0);
  }
  public void act(Boat b) {
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
  public void act(Boat b) {
    b.coolTime = 1;
    b.dmg = 1;
    b.ammoLeft = 200;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BoatFight" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
