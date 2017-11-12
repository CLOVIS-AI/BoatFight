
final boolean out = true;
final boolean log = true;

enum GameState{ GAME, MENU }

void settings(){
  if(out){
    fullScreen();
  }else{
    size(700, 600);
  }
}

Connexion co;
GameState gm = GameState.MENU;
int colSize;

void setup(){
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

void draw(){
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

void menu(){
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

void game(){
  if(me == null)  return;
  outside = me.x > mapSizeX || me.x < -mapSizeX || me.y > mapSizeY || me.y < -mapSizeY;
  mask = outside ? mask+1 : mask < 3 ? 0 : mask-3;
  background(40 - mask + pain, 164 - mask - pain*2, 223 - mask - pain*2);
  if(pain > 0)
    pain-=1;
  pushMatrix();
  translate(width/2, height/2);
  zoom = map(me.speed, 0.1, 20, 1.8, 0.1);
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
    me.damage(0.01);
  }
  
  textSize(50);
  textAlign(CENTER, TOP);
  text((int)(me.speed*5), width/2, 0);
  textSize(12);
  textAlign(LEFT, BASELINE);
}

void init(){
  log("Initializing game ...");
  rectMode(CENTER);
  ellipseMode(CENTER);
  log("modes", "Displaying modes");
  mapSizeX = int(objectsMap.get("MAP_SIZE_X").toString());
  log("MapSizeX", str(mapSizeX));
  mapSizeY = int(objectsMap.get("MAP_SIZE_Y").toString());
  log("MapSizeY", str(mapSizeY));
  maxParticles = int(objectsMap.get("PARTICLES").toString());
  log("maxParticles", str(maxParticles));
  luck = int(objectsMap.get("LUCK").toString());
  log("luck", str(luck));
  frameRate(int(objectsMap.get("FPS").toString()));
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

void initAsServer(){
  log("Initialiazing as server ...");
  int port = int(objectsMap.get("SERV_PORT").toString());
  log("port", str(port));
  int red = int(objectsMap.get("RED").toString());
  log("red", str(red));
  int green = int(objectsMap.get("GREEN").toString());
  log("green", str(green));
  int blue = int(objectsMap.get("BLUE").toString());
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

void initAsClient(){
  log("Initialiazing as client ...");
  int port = int(objectsMap.get("CLIENT_PORT").toString());
  log("port", str(port));
  String IP = objectsMap.get("SERV_IP_1").toString() + "."
             +objectsMap.get("SERV_IP_2").toString() + "."
             +objectsMap.get("SERV_IP_3").toString() + "."
             +objectsMap.get("SERV_IP_4").toString();
  log("IP", "Processing host's IP");
  int red = int(objectsMap.get("RED").toString());
  log("red", str(red));
  int green = int(objectsMap.get("GREEN").toString());
  log("green", str(green));
  int blue = int(objectsMap.get("BLUE").toString());
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

void keyPressed(){
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

void keyReleased(){
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

void disconnectEvent(Client c){ co.disco(c); }

void mousePressed(){
  if(focus.collide(mouseX, mouseY))
    focus.onMousePressed();
}

long prevTime;
void log(String reason, String msg){
  if(!log)  return;
  long current = millis();
  System.out.println((current-prevTime) + "\t["+reason+"] " + msg);
  prevTime = current;
}

void log(String msg){
  if(!log)  return;
  long current = millis();
  System.out.println("\n" + msg);
  prevTime = current;
}