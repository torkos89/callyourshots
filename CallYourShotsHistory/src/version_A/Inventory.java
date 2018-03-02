package version_A;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class Inventory{
  private LinkedList<Entity> inventory = new LinkedList<Entity>(); //was linkedList
  private int weightMax = 10;
  private int weightCur = 0;
  private Entity owner = null;
  LinkedList<Entity> more = new LinkedList<>();
  LinkedList<Entity> less = new LinkedList<>();
  
  public void update(){   
   // System.out.println(owner.getShortName()+" inventory "+inventory.toString());  
    for(Entity e : inventory){
      e.update();     
     // System.out.println("  "+e.getShortName());
    }
    for(Entity e: less){
      System.out.println("removing "+e.toString());
      inventory.remove(e);
      //updateAdd(e);
    }
    less.clear();
    for(Entity e: more){
      System.out.println("adding "+e.toString());
      e.setContainer(owner);
      //inventory.add(e);
      //updateRemove(e);
    }
    more.clear();
  }
  public void updateAdd(Entity e){
    more.add(e);
  }
  public void updateRemove(Entity e){
    less.add(e);
  }
  
  public Inventory setOwner(Entity owner){
    this.owner = owner;
    return this;
  }
  public Entity getOwner(){
    return owner;
  }
  public Inventory add(Entity e){
    if(e ==null) return null;
    if(weightMax != 0 && e.getWeight()>weightMax-weightCur) return null;
    inventory.add(e); //was add
    weightCur += e.getWeight();
    return this;
  }
  
  public boolean remove(Entity e){
    if(e==null) return false;
    inventory.remove(e);
    weightCur -= e.getWeight();
    return true;
  }
  public Entity remove(String s){
    Entity e = get(s);
    if(!remove(e)) return null;
    return e;
  }
  
  public Entity getFirst(){
    return inventory.getFirst();
  }
  public Entity get(int i){
   return inventory.get(i); 
  }
    
  public Entity get(String s){
    if(inventory.size()==0) return null;
    Iterator<Entity> i = inventory.iterator();
    int count = 1;
    if(s.indexOf('#')>-1){
      try{
        count = Integer.parseInt(s.substring(s.indexOf('#')+1));
      }catch(ClassCastException e){
        
      }
      s = s.substring(0, s.indexOf('#'));
    }
    while(i.hasNext()&&count>0){
      Entity e = i.next();
      if(e.hasName(s)){
        if(count==1) return e;
        count--;
      }
      //else return e;
    }
    return null;
  }
   
  public int getWeightMax(){
    return this.weightMax;
  }
  public Inventory setWeightMax(int i){
    weightMax = i;
    return this;
  }
  public int getWeightCur(){
    return weightCur;
  }
  
  public String toString(){
    if(inventory.isEmpty()) return "[]";
    String s = "";
    for(Entity e : inventory){ //Entity e : inventory
      s+=",\""+e.getShortName()+"\""; 
    }
    return  "["+(s.substring(1))+"]";
  }
  public List<Entity> getAll(){
    return inventory;
  }
  /*
 public Boolean has(String s){
   return inventory.get(index);
 }
 */
  /*
public Entity inspect(){
  for(Entity e:inventory){
    if()
  }
  return null;
}
*/
  /*
  public int size(){
    return inventory.size();
  }
  */
}
