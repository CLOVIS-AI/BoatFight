
abstract class InteractiveObject{
  
  int x, y;
  int sx, sy;
  String name;
  
  InteractiveObject(int X, int Y, int SX, int SY, String NAME){
    x = X; y = Y; name = NAME;
    sx = SX; sy = SY;
  }
  
  
  void onKeyPressed(){}
  void onKeyReleased(){}
  
  void onMousePressed(){}
  void onMouseRelease(){}
  
  boolean collide(int X, int Y){
    return X > x && X < x+sx && Y > y && Y < y+sy;
  }
  
  abstract void draw();
  
  String toString(){ return name; }
}