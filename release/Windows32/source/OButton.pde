

class Button extends InteractiveObject {
  
  Act act;
  
  Button(int X, int Y, int SX, int SY, String NAME, Act ACTION){
    super(X, Y, SX, SY, NAME);
    act = ACTION;
  }
  
  void draw(){
    fill(focus == this ? 255 : 100);
    rectMode(CORNER);
    rect(x, y, sx, sy);
    fill(focus == this ? 0 : 255);
    textAlign(LEFT, TOP);
    text(name, x+5, y+5);
  }
  
  void onMousePressed(){
    act.act();
  }
  
}


interface Act{
  
  public void act();
  
}