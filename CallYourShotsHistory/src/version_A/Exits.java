package version_A;

import java.util.LinkedHashMap;

public class Exits {
  private LinkedHashMap<String,Entity> exits = new LinkedHashMap<>();
  private Entity owner = null;
  
  public Exits setOwner(Entity owner){
    this.owner = owner;
    return this;
  }
  public Entity getOwner(){
    return owner;
  }
  public Exits setExit(String s, Entity e){
     exits.put(s, e);
     return this;
  }
  public boolean hasExit(String s){
    if(s.isEmpty()) return false;
    if(exits.containsKey(s)) return true;
    for(String ex : exits.keySet()){
      if(ex.startsWith(s)) return true;
    }
    return false;
  }
  public Entity getExit(String s){
    if(s.isEmpty()) return null;
    if((s.equals("o")||s.equals("ou")||s.equals("out"))&&exits.containsKey("out")) return owner.getContainer();
    if(exits.containsKey(s)) return exits.get(s);
    for(String ex : exits.keySet()){
      if(ex.startsWith(s)) return exits.get(ex);
    }
    return null;
  }
public String findExit(String s){
  if(s==null || s.isEmpty()) return "";
  if(exits.containsKey(s)) return s;
  for(String ex : exits.keySet()){
    if(ex.startsWith(s)) return ex;
  }
  return "";
}
  public String toString(){
    return exits.keySet().toString();
  }
}
