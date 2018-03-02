package version_B;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class WorldListener
 *
 */
@WebListener
public class WorldListener implements ServletContextListener {
  //private World world = WorldFactory.getWorld();
  
  public WorldListener() {
      
  }
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
   WorldFactory.getWorld(); 
   System.out.println("started");
    
  }
  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    WorldFactory.stop();
    System.out.println("iz ded");

  }


	
}
