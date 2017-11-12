import processing.net.*;

class Connexion {

  Server s;
  Client c;
  String input;
  String data[];

  HashMap<String, Boat> clients;
  ArrayList<Client> recievers;

  final boolean isServer;

  Connexion(PApplet source, int port, color colour) {
    isServer = true;
    s = new Server(source, port);
    println("Lauching as server");
    clients = new HashMap();
    recievers = new ArrayList();
  }

  Connexion(PApplet source, String ip, int port, color colour) {
    isServer = false;
    println("Trying to connect to server ...");
    c = new Client(source, ip, port);
    println("Lauching as client");
    clients = new HashMap();
    //players.add(new Boat("ME", colour, new char[]{'e', 'd', 's', 'f', 'i', 'k'}));
    //me = players.get(0);
    c.write("HELLO>"+red(colour)+" "+green(colour)+" "+blue(colour)+" "+random(2*PI)+"\n\n\n");
  }

  void update() {
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
                Boat n = new Boat(c.ip(), color(int(params[0]), int(params[1]), int(params[2])), null);
                n.angle = float(params[3]);
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
                p.x = float(params[0]);
                p.y = float(params[1]);
                p.angle = float(params[2]);
                p.speed = float(params[3]);
                p.refreshAngles();
                servNotify("MOVING>"+p.name+" "+p.x+" "+p.y+" "+p.angle+" "+p.speed, c);
                break;
              case "SHOOT": //SHOOT>DAMAGE
                p = clients.get(c.ip());
                p.dmg = int(params[0]);
                p.shoot();
                break;
              case "DIED": //SHOOT>BOOST
                Boat h = clients.get(c.ip());
                h.boost = int(params[1]);
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
                int x = int(params[1]), y = int(params[2]);
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
                Boat n = new Boat(params[0], color(int(params[1]), int(params[2]), int(params[3])), null);
                n.angle = float(params[4]);
                clients.put(params[0], n);
                players.add(n);
                break;
              case "YOU": //YOU>ID MAPX MAPY
                clients.put(params[0], clients.remove(me.name));
                me.name = params[0];
                println("I am "+me.name);
                mapSizeX = int(params[1]);
                mapSizeY = int(params[2]);
                break;
              case "MOVING": //MOVING>ID X Y ANGLE SPEED
                p = clients.get(params[0]);
                if (p == null) {
                  System.err.println("Couldn't find player #"+params[0]+"... (me : #"+me.name+")");
                  return;
                }
                p.x = float(params[1]);
                p.y = float(params[2]);
                p.angle = float(params[3]);
                p.speed = float(params[4]);
                p.refreshAngles();
                break;
              case "SHOOT": //SHOOT>ID DAMAGE
                Boat g = clients.get(params[0]);
                if (g == null) {
                  System.err.println("Couldn't find player #"+params[0]+"... (me : #"+me.name+")");
                  return;
                }
                g.dmg = int(params[1]);
                println(g.dmg);
                g.shoot();
                break;
              case "DIED": //SHOOT>ID BOOST
                Boat h = clients.get(params[0]);
                if (h == null) {
                  System.err.println("Couldn't find player #"+params[0]+"... (me : #"+me.name+")");
                  return;
                }
                h.boost = int(params[1]);
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

  void notify(String cmd) {
    if (!isServer) {
      c.write(cmd+"\n\n\n");
    }
  }

  void servNotify(String cmd) {
    if (isServer)
      for (Client c1 : recievers)
        c1.write(cmd+"\n\n\n");
  }

  void servNotify(String cmd, Client except) {
    if (isServer)
      for (Client c1 : recievers)
        if (c1 != except) 
          c1.write(cmd+"\n\n\n");
  }

  void disco(Client c2) {
    recievers.remove(c2);
    clients.get(c2.ip()).health = -1;
    println(c2.ip() + " logged off or was banned.");
  }
}