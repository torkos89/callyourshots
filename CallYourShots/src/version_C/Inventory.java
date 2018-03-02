package version_C;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
//TODO: make a compare function for items w/ switch -1 , 0  , 1
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
      //System.out.println("here  "+e.getShortName());
      e.update();     
    }
    for(Entity e: less){
      //System.out.println("removing "+e.getShortName());
      inventory.remove(e);
      //updateAdd(e);
    }
    less.clear();
    for(Entity e: more){
      //System.out.println("adding "+e.toString());
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
  /** try to remove Entity, returns Boolean
   * 
   * @param e
   * @return
   */
  public boolean remove(Entity e){
    if(e==null) return false;
    less.add(e);
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
  
  public Entity getItem(long l){ 
    if(inventory.size()==0) return null;
    for(Entity e : inventory){
      //System.out.println(e.getShortName()+l+" , "+e.getId());
      if(e.getId()==l&&!e.hasStats()) return e;  // item check
    }
    return null;
  }
  public Entity get(Entity e){
    if(inventory.contains(e)) return e;
    return null;
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
  public List<Entity> getAll(){
    List<Entity> inv = new LinkedList<>();
    //inv.addAll(inventory);
    for(Entity e : inventory){
      if(!less.contains(e)) inv.add(e);
    }
    return inv;
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

  public void compair(){
    if(inventory.size()>0&&inventory!=null){
      for(Entity e : inventory){
        String slot = e.isWearable()? e.getWearable().getSlot():"";
        int itmCur = 0;
        int armCur = 0;
        switch(slot){
        case "head": armCur = owner.getEquipment().getHead().getStats().getArmor(); 
          itmCur = e.getEquipment().getHead().getStats().getArmor();
          if(armCur>itmCur) owner.getEquipment().wearHead(e);        
        case "shoulders": 
        case "chest": 
        case "legs": 
        case "feet": 
        case "oneHand": 
        case "twoHand": 
        }
      }
    }
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
