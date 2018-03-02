package version_A;

import java.util.LinkedList;
import java.util.List;

public class Spawner {
  private final List<Definition> DEFS = new LinkedList<>();
  private Entity owner = null;

  Spawner(){}
  
  public void update(){
//    if(defs.size()==0) return; // doesn't save anything anymore
    for(Definition d : DEFS)d.update();
  }
  public Spawner setOwner(Entity e){
    owner = e;
    return this;
  }
  public Spawner addDefinition(Definition def){
    System.out.println(owner.toString()+" = owner");
    def.setOwner(owner);
    DEFS.add(def);
    return this;
  }
}
