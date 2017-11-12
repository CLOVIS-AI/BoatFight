

class FinalTextArea extends InteractiveObject{
  
  String content;
  
  FinalTextArea(int X, int Y, String NAME){
    super(X, Y, 0, 20, NAME);
    textSize(12);
    content = NAME;
    sx = (int)textWidth(NAME);
  }
  
  void draw(){
    fill(255);
    textAlign(LEFT, TOP);
    text(content, x+5, y+5);
  }
  
  boolean collide(int x, int y){return false;}
  
}