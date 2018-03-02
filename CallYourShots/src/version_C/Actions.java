package version_C;

import java.lang.reflect.Modifier;
import java.util.LinkedList;

public class Actions {
  final LinkedList<Action> actions = new LinkedList<>();
  private Entity target = null;
  private Entity owner = null;
  
  public int size(){
    return actions.size();
  }
  public Actions nextAction(Action a){
    actions.add(a);
    return this;
  }
  public Actions setOwner(Entity owner){
    this.owner = owner;
    return this;
  }
  public void setTarget(String target){
   Entity e = owner.getContainer().getInventory().get(target);
   if(e!=null) this.target = e;
  }
  public void setTarget(Entity target){
    this.target = target;
  }
  public void setSoftTarget(Entity target){ 
    if(this.target==null) this.target = target;
  }
  public void removeTarget(){
    this.target = null;
    actions.clear();
  }
  public Action update(){
     //System.out.println(owner.getShortName()+" target "+target+", "+actions.size()); TODO: CHECK IF YOU ADDED TO ENTITTIES <('.')^
    if(actions.size()>0){
      Action a = actions.get(0);
      if(--a.timer<=0){
        a.damage = owner.getEquipment().getDamage() + (Math.round(owner.getStats().getStrength()/2));// - target.getStats().getArmor(); // TODO: make stats dependent
        if(a.mes.isEmpty()) a.mes = owner.getEquipment().getDamageType();  // TODO: override at some point, damage type
        return actions.remove(0);
      }
    }
    else if(target!=null) actions.add(new Action(6,target));
    return null;
  }

  class Action{
    int timer = 0;
    Entity target = null;
    String mes = "";
    int damage = 0;
    
    Action(int timer,Entity target){  
      this.timer = timer;
      this.target = target;
    }
    public Action setMessage(String mes){
     this.mes = mes; 
     return this;
    }
    public Action setModifiers(int rolls, int dice){
     new Modifiers(rolls+"d"+dice);//.setDamage(rolls, dice);
      return this;
    }
  }

  public boolean hasTarget() {
    return target!=null;
  }
  public void flee(){  //TODO: attack of opertuntity
    Entity con = owner.getContainer();
    if(con.hasExits()){
      owner.getActions().removeTarget();
      String dir = con.getExits().getRandomExit();
      //System.out.println("**** "+con.getExits().getExit(dir));
      owner.getContainer().getInventory().less.add(owner);
      owner.setContainer(con.getExits().getExit(dir));
      owner.broadcast("you flee to the "+dir+"\n"+owner.getContainer().toString());
      owner.broadcast(owner.getShortName()+" flees "+dir, con.getInventory());   
      owner.broadcast(owner.getShortName()+" has fled into the room", owner.getContainer().getInventory());
      //if(con.hasExits())
        System.out.println("<?> "+owner.getShortName()+" flees "+dir+" to "+owner.getContainer().getShortName());
    }
  }
}
