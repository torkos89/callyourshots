package version_B;

public class WorldFactory {
  private static World world = null;
  private static Thread thread = null;
  public static World getWorld(){
    if(world == null||thread==null||thread.isInterrupted()){
      world = new World();
      thread = new Thread(world); 
      thread.start();
    }
    return world;
  }
  public static void stop() {
    thread.interrupt();
  }
}