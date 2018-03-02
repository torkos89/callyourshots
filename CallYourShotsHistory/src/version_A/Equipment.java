package version_A;

public class Equipment {
  
  private Entity head = null;
  private Entity shoulders = null;
  private Entity chest = null;
  private Entity legs = null;
  private Entity feet = null;
  //private Entity leftHand = null;
  //private Entity rightHand = null;
  private Entity oneHand = null;
  private Entity twoHand = null;
  private int[] armorRange = {0,0};
  private Entity owner = null;
  public Entity[] wear(Entity e){
    if(!e.isWearable()) return new Entity[]{e};
    switch(e.getWearable().getSlot()){
      case "head": return wearHead(e);
      case "shoulders": return wearShoulders(e);
      case "chest": return wearChest(e);
      case "legs": return wearLegs(e);
      case "feet": return wearFeet(e);
      case "oneHand": return wearOneHand(e);
      case "twoHand": return wearTwoHand(e);
    }
    return null;
  }
  public int getDamage(){
//    System.out.println(oneHand.getShortName());
   // System.out.println(oneHand.getModifiers().getDamage());
    if(oneHand==null&&twoHand==null) return owner.getModifiers().getDamage();
    else if(oneHand==null) return twoHand.getModifiers().getDamage();
    else if(twoHand==null||oneHand==twoHand) return oneHand.getModifiers().getDamage();
    else return oneHand.getModifiers().getDamage()+twoHand.getModifiers().getDamage();
  }
  public String getDamageType(){
    if(oneHand==null&&twoHand==null) return owner.getModifiers().getDamageType();
    else if(oneHand==null) return twoHand.getModifiers().getDamageType();
    else if(twoHand==null||oneHand==twoHand) return oneHand.getModifiers().getDamageType();
    else return oneHand.getModifiers().getDamageType()+" and "+twoHand.getModifiers().getDamageType();
  }
  public String getDamageDice(){
    if(oneHand==null&&twoHand==null) return owner.getModifiers().getDamageDice();
    else if(oneHand==null) return twoHand.getModifiers().getDamageDice();
    else if(twoHand==null||oneHand==twoHand) return oneHand.getModifiers().getDamageDice();
    else return oneHand.getModifiers().getDamageDice()+" and "+twoHand.getModifiers().getDamageDice();
  }
  public Entity remove(String s){
    if(s==null) return null;
    if(head!=null && head.hasName(s)) return removeHead();
    else if(shoulders!=null && shoulders.hasName(s)) return removeShoulders();
    else if(chest!=null && chest.hasName(s)) return removeChest();
    else if(legs!=null && legs.hasName(s)) return removeLegs();
    else if(feet!=null && feet.hasName(s)) return removeFeet();
    else if(oneHand!=null && oneHand.hasName(s)) return removeOneHand();
    else if(twoHand!=null && twoHand.hasName(s)) return removeTwoHand();
    else return null;
  }
 public Entity[] removeAll(){
   return new Entity[]{head,shoulders,chest,legs,feet,oneHand,twoHand==oneHand?null:twoHand};
   /*
   LinkedList<Entity> items = new LinkedList<>();
   if(head!=null) items.add(head);
   if(shoulders!=null) items.add(shoulders);
   if(chest!=null) items.add(chest);
   if(legs!=null) items.add(legs);
   if(feet!=null) items.add(feet);
   if(oneHand!=null) items.add(oneHand);
   if(twoHand!=null && oneHand!=twoHand) items.add(twoHand);
   return items.toArray(new Entity[items.size()]);
   */
 }
  public void setOwner(Entity owner){
    this.owner = owner;
  }
  public Entity getOwner(){
    return this.owner;
  }
  public Entity[] wearHead(Entity e){
    Entity old = head;
    head = e;
    updateStats(old,e);
    return new Entity[]{old};
  }
  public Entity getHead(){
    return head;
  }
  public Entity removeHead(){
    Entity old = head;
    head = null;
    updateStats(old,null);
    return old;
  }
  public Entity[] wearShoulders(Entity e){
    Entity old = shoulders;
    shoulders = e;
    updateStats(old,e);
    return new Entity[]{old};
  }
  public Entity getShoulders(){
    return shoulders;
  }
  public Entity removeShoulders(){
    Entity old = shoulders;
    shoulders = null;
    updateStats(old,null);
    return old;
  }
  public Entity[] wearChest(Entity e){
    Entity old = chest;
    chest = e;
    updateStats(old,e);
    return new Entity[]{old};
  }
  public Entity getChest(){
    return chest;
  }
  public Entity removeChest(){
    Entity old = chest;
    chest = null;
    updateStats(old,null);
    return old;
  }
  public Entity[] wearLegs(Entity e){
    Entity old = legs;
    legs = e;
    updateStats(old,e);
    return new Entity[]{old};
  }
  public Entity getLegs(){
    return legs;
  }
  public Entity removeLegs(){
    Entity old = legs;
    legs = null;
    updateStats(old,null);
    return old;
  }
  public Entity[] wearFeet(Entity e){
    Entity old = feet;
    feet = e;
    updateStats(old,e);
    return new Entity[]{old};
  }
  public Entity getFeet(){
    return feet;
  }
  public Entity removeFeet(){
    Entity old = feet;
    feet = null;
    updateStats(old,null);
    return old;
  }
  public Entity[] wearOneHand(Entity e){
    if(e==null) return null;
    Entity old = null;
    if(oneHand==null){
      oneHand = e;
    }
    else if(twoHand==null) twoHand = e;     
    else if(oneHand==twoHand){
      old = oneHand;
      oneHand = e;
      twoHand = null;
    }
    else{
     old = oneHand;
     oneHand = twoHand; //fix 2 hand bug
     twoHand = e;
    }
    updateStats(old,e);
    return new Entity[]{old};
  }
  public Entity removeOneHand(){
    Entity one = oneHand;
    if(one==twoHand) twoHand = null;
    oneHand = null;
    updateStats(one,null);
    //else if(two!=null) twoHand = null;
    return one;//new Entity[]{one,two};
  }
  public Entity[] wearTwoHand(Entity e){ 
    Entity one = oneHand;
    Entity two = twoHand;
    oneHand = e;
    twoHand = e;
    if(one==two){
      updateStats(two,e);
      return new Entity[]{one};
    }
    updateStats(one,null);
    updateStats(two,e);
    return new Entity[]{one,two};
  }
  public Entity removeTwoHand(){
    Entity two = twoHand;
    twoHand = null;
    updateStats(two,null);
    return two;//new Entity[]{one,two};
  }
  /*
  public Entity placeLeftHand(Entity e){
    Entity oldL = leftHand;
    //Entity oldR = rightHand;
    Entity old2 = bothHands;
    if(bothHands!=null){
      removeBothHands(old2);
      updateStats(old2,null);
     //add old2 to inventory
    }
    //if()
    leftHand = e;
    updateStats(oldL,e);
    return oldL;
  }
  public Entity placeRightHand(Entity e){
    Entity oldR = rightHand;
    Entity old2 = bothHands;
    if(bothHands!=null) removeBothHands(old2);
    rightHand = e;
    updateStats(oldR,e);
    return oldR;
  }
  public Entity placeBothHands(Entity e){
    Entity old1 = leftHand;
    Entity old2 = rightHand;
    Entity old3 = bothHands;
    if(rightHand!=null&&leftHand!=null){
      removeLeftHand(old1);
      removeRightHand(old2);
    }
    leftHand = null; //null?
    rightHand = null; //null?
    bothHands = e;
    updateStats(old1,old2);
    updateStats(old3,e);
    return old3;
  }
  */
  public void updateStats(Entity old, Entity e){
    //has mods
    armorRange[0] += -(old ==null? 0:old.getModifiers().getArmorLow()) + (e==null? 0:e.getModifiers().getArmorLow());
    armorRange[1] += -(old ==null? 0:old.getModifiers().getArmorHigh()) + (e==null? 0:e.getModifiers().getArmorHigh());
    //damage += -(old ==null? 0:old.getWearable().getDamage()) + (e==null? 0:e.getWearable().getDamage());
  }
  public String getArmor(){
    return armorRange[0]+"-"+armorRange[1];
  }
  
  /*
  public void setArmor(int armor){
    this.armor = armor;
  }
  */
  public String toString(){
    return "{\"Armor\":\""+getArmor()+"\",\"head\":\""+(head==null? "":head.getShortName())
        +"\",\"chest\":\""+(chest==null? "":chest.getShortName())
        +"\","+(oneHand==twoHand&&oneHand!=null?"\"bothHands\":\""+oneHand.getShortName()+"\"":(
          "\"oneHand\":\""+(oneHand==null?"<empty>":oneHand.getShortName())+"\","
         +"\"twoHand\":\""+(twoHand==null?"<empty>":twoHand.getShortName())+"\""
        ))+"}";
  }
}
