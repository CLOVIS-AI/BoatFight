

class IntTextArea extends TextArea{
  
  int min, max;
  
  IntTextArea(int X, int Y, int SX, int SY, String name, int MIN, int MAX){
    super(X, Y, SX, name);
    min = MIN;
    max = MAX;
  }
  
  void onKeyPressed(){
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