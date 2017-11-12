

class TextArea extends InteractiveObject{
  
  String content, display;
  int cursorPos;
  color colour;
  
  TextArea(int X, int Y, int SX, String name){
    super(X, Y, SX, 20, name);
    content = name;
    cursorPos = content.length()-1;
    //refreshDisplay();
    colour = color(255);
  }
  
  void onKeyPressed(){
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
  
  void draw(){
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
  
  String toString(){ return content; }
}