package version_A;

import java.lang.reflect.Modifier;
import java.util.LinkedList;

public class Actions {
  final LinkedList<Action> actions = new LinkedList<>();
  private String target = null;
  private Entity owner = null;
  
  public Actions nextAction(Action a){
    actions.add(a);
    return this;
  }
  public Actions setOwner(Entity owner){
    this.owner = owner;
    return this;
  }
  public void setTarget(String target){
    this.target = target;
  }
  public void setSoftTarget(String target){ 
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
        a.damage = owner.getEquipment().getDamage(); //TODO: make stats dependent
        if(a.mes.isEmpty()) a.mes = owner.getEquipment().getDamageType();  //TODO: override at some point, damage type
        return actions.remove(0);
      }
    }
    else if(target!=null) actions.add(new Action(6,target));
    return null;
  }

  class Action{
    int timer = 0;
    String target = null;
    String mes = "";
    int damage = 0;
    
    Action(int timer,String target){  
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
}
