package version_C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.List;


import javax.websocket.Session;

import version_C.Actions.Action;
public class Entity {
  private long id = 0;
  private int health = 1; // 2 per heart
  private ArrayList<String> names = new ArrayList<String>();
  private String shortName = "?";
  private String description = "???";
  private int weight = 0;
  private Wearable wearable = null;
  private Equipment equipment = null;
  private Stats stats = null;
  private Inventory inventory = null;
  private Exits exits = null;
  private Actions actions = null;
  private Entity container = null;
  private Session session = null;
  private Modifiers modifiers = null;
  private Entity spawnRoom = null;
  private Spawner spawner = null;
  private Motivation motivation = null;
  private boolean decay = false;
  private int decayTimer = 120;
  Entity(){
  }
  public long getId(){
    return id;
  } 
  public Entity setId(long id){
    this.id = id;
    return this;
  }
  public Entity getSpawnRoom(){
    return this.spawnRoom;
  }
  public Entity setSpawnRoom(Entity spawn){
    this.spawnRoom = spawn;
    return this;
  }
  public boolean hasModifiers(){
    return this.modifiers!=null;
  }
  public Modifiers getModifiers(){
    return modifiers;
  }
  public Entity setModifiers(Modifiers mod){
    this.modifiers = mod;
    return this;
  }
  public boolean hasActions(){
    return this.actions!=null;
  }
  public void update(){
    if(this.hasStats()) stats.update();
    if(this.hasMotivation()) motivation.update();
    if(this.hasSpawner()) spawner.update(); //TODO: motivational movement
    if(hasInventory()) inventory.update();
    if(decay){
      if(decayTimer--== 0){
        container.inventory.less.add(this);
        System.out.println("# burning "+this.getShortName());
      }
    }
    if(this.hasActions()){
      Action a = actions.update();
      if(a!=null){
        Entity target = getContainer().getInventory().get(a.target);
        if(target!=null){
          target.getActions().setSoftTarget(this);
          target.setHealth(target.getHealth()-a.damage);
          target.send(getShortName()+" hits you with "+a.mes+" for "+a.damage+" damage");
          for(Entity e: getContainer().getInventory().getAll()){
            if(e!=target&&e!=this) e.send(getShortName()+" hits "+ target.getShortName()+" with "+a.mes+" for "+a.damage+" damage");
          }
          send("you hit "+target.getShortName()+" with "+a.mes+" for "+a.damage+" damage"); 
        
          if(target.getHealth()<=0){
            //System.out.println("********** target health = "+target.getHealth());
            int xp = 0;
            if(target.hasStats()) xp = target.getStats().getXp();
            if(this.hasStats()) this.getStats().setXp( getStats().getXp()+xp);
            getActions().removeTarget();
            send("you killed "+target.getShortName()+" with "+a.mes+" for "+a.damage+" damage"+"\nyou gain "+xp+" xp");
            System.out.println("* "+this.getShortName()+" has killed "+target.getShortName()+" with "+a.mes+" and gains "+xp+" xp");
            broadcast("* "+this.getShortName()+" has killed "+target.getShortName()+" with "+a.mes+" and gains "+xp+" xp", getContainer().getInventory());
            target.makeDead(); //TODO: make better ;p
          }
        }
        else {
          getActions().removeTarget();
          send("can't find "+a.target);
        }
      }
    }
    //return null;
  }
  public Entity setSpawner(Spawner spawner){
    this.spawner = spawner.setOwner(this);
    return this;
  }
  public Spawner getSpawner(){
    return spawner;
  }
  public boolean hasSpawner(){
    return spawner!=null;
  }
  public Entity setStats(Stats stats){
    this.stats = stats.setOwner(this);
    return this;
  }
  public Stats getStats(){
    return stats;
  }
  public boolean hasStats(){
    return stats!=null;
  }
  public void makeDead(){ //TODO: make new entity corpse in current room, place items in inventory, put player/mob in its spawn room, 
    Entity corpse = new Entity().setShortName("the corpse of "+this.getShortName()).setNames("corpse").setDescription("a slain "+this.getShortName()).setInventory(new Inventory());
    for(Entity e: getEquipment().removeAll()) corpse.getInventory().add(e);
    getActions().removeTarget();
    this.getContainer().getInventory().updateRemove(this);
    if(this.session!=null){
      this.spawnRoom.getInventory().updateAdd(this);
      int xp = this.getStats().getXp();
      int loss = xp - xp/2;
      this.getStats().setXp(loss);  
      send("You have been killed, but are respawning...\nYou lose "+loss+" XP");
    }
    else{ stats = null;
    motivation = null;
    broadcast(getShortName()+" has died",this.getContainer().getInventory());
   //  send("you are dead");
    }
    corpse.startDecay();
    container.getInventory().updateAdd(corpse);
  }
  public void startDecay(){
    decay = true;
    //System.out.println("starting decay");
  }
  public void flee(){  //TODO: attack of opertuntity
    Entity con = getContainer();
    if(con.hasExits()){
      getActions().removeTarget();
      String dir = con.getExits().getRandomExit();
      //System.out.println("**** "+con.getExits().getExit(dir));
      inventory.less.add(this);
      setContainer(con.getExits().getExit(dir));
      broadcast("you flee to the "+dir+"\n"+getContainer().toString());
      broadcast(getShortName()+" flees "+dir, con.getInventory());   
      broadcast(getShortName()+" has fled into the room", getContainer().getInventory());
      //if(con.hasExits())
        System.out.println("<?> "+getShortName()+" flees "+dir+" to "+getContainer().getShortName());
    }
  }
  public void send(String s){
    send(s,0);
  }
  public void send(String s, int count){
    if(count<5){
      try{
        if(session != null&&session.isOpen()) session.getAsyncRemote().sendText(s);
      }catch (IllegalStateException e){
        try {
          Thread.sleep(20);
          send(s,++count);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }
    else System.out.println(getShortName()+" failed to send message "+s);
  }
  public Entity setSession(Session ses){
    this.session = ses;
    return this;
  }
  public boolean hasSession(){
   return session!=null? true:false;
  }
  public Entity getContainer(){
    return this.container;
  }
  public Entity setContainer(Entity e){
    if(getContainer()!=null) getContainer().getInventory().remove(this); 
    this.container = e;
    container.getInventory().add(this);
    return this; 
  }
  
  public int getHealth() {
    return this.health;
  }
  public Entity setHealth(int health) {
    this.health = health;
    return this;
  }
  public boolean hasName(String s){
    return names.contains(s);
  }
  /*
  public Entity setNames(String... s){
    return setNames(Arrays.asList(s));
  }
  */
  /*
  public Entity setNames(List<String> s){
    names.addAll(s);
    return this;
  }
  */
  public Entity setNames(String s){
    names.addAll(Arrays.asList(s.split(",")));
    return this;
  }
  public String getShortName() {
    return this.shortName;
  }
  public Entity setShortName(String shortName) {
    this.shortName = shortName;
    return this;
  }
  public String getDescription() {
    return hasStats()? this.description.replaceAll("\\{lv}", getStats().getLevel()+""):this.description;
  }
  public Entity setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public Motivation getMotivation() {
    return this.motivation;
  }
  public Boolean hasMotivation(){
    return motivation!=null;
  }
  public Entity setMotivation(Motivation motivation) {
    this.motivation = motivation.setOwner(this);
    return this;
  }
  public int getWeight() {
    return this.weight;
  }
  public Entity setWeight(int weight) {
    this.weight = weight;
    return this;
  }
  public boolean isWearable(){
    return wearable!=null;
  }
  public Wearable getWearable(){
    return this.wearable;
  }
  public Entity setWearable(Wearable w){
    wearable = w;
    return this;
  }
  public boolean hasEquipment(){
    return equipment!=null;
  }
  public Equipment getEquipment(){
    return this.equipment;
  }
  public Entity setEquipment(Equipment e){
    equipment = e.setOwner(this);
    return this;
  }
  public boolean hasInventory(){
    return inventory!=null;
  }
  public Inventory getInventory(){
    return this.inventory;
  }
  public Entity setInventory(Inventory i){
    inventory = i.setOwner(this);
    return this;
  }
  public boolean hasExits(){
    return exits!=null;
  }
  public Exits getExits(){
    return this.exits;
  }
  public Entity setExits(Exits e){
    this.exits = e.setOwner(this);
    return this;
  }
  public Actions getActions(){
    return this.actions;
  }
  public Entity setActions(Actions a){
    this.actions = a.setOwner(this);
    return this;
  }
  public void broadcast(String mes, Inventory inv){ // everyone in room minus self
    for(Entity e : inv.getAll()){
      if(e!=this) e.send(mes);
    }  
  }
  public void broadcast(String mes, Inventory inv, Entity t){ // room minus self/target
    for(Entity e : inv.getAll()){
      if(e!=this && t!=e) e.send(mes);
    }
 }
  public void broadcast(String mes, Entity e){ // target 
    e.send(mes);
  }
  public void broadcast(String mes){ // self
    broadcast(mes,"");
  }
  public void broadcast(String mes,String cmd){ // self
    
   send("> "+cmd+"\n"+mes+"\n{ "+getHealth()+"/"+getStats().getMaxHealth()+"HP "
   +getStats().getMana()+"/"+getStats().getMaxMana()+"M "
   +getStats().getStamina()+"/"+getStats().getMaxStamina()+"S } ");
   
    
 }
  public String toString(){
    return "Name: "+getShortName()+"\n "+getDescription()+(hasStats()?("\nHealth: "+getHealth()):"")
    +(hasEquipment()? ("\nStats: "+getEquipment().toString()) : "")
    +(hasInventory()? ("\nInventory: "+getInventory().toString()) : "")
    +(hasExits()?("\nExits: "+getExits().toString()):"");
  }
}