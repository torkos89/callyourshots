package version_A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import version_A.Actions.Action;
//TODO: add has stats for dead/not dead move from one entity to another on death
public class Entity {
  private int health = 1; // 2 per heart
  private ArrayList<String> names = new ArrayList<String>();
  private String shortName = "?";
  private String description = "???";
  //private short motivation = 0;
  //private int armor = 0; // damage soak, or reduces chance to hit
  //private List<Entity> inventory;
  //private Map<String,Entity> equipped;
  //private Map<String, Modifier> modifiers; //bleeding, on fire, slowed
  private int weight = 0;
  //private int weightMax = 0; // limits what you can carry, and move/roll speed(uses more time units)
  //private int damage = 0; // base without weapon modifiers
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
  
  //TODO: add copy method
  // Update
  
  Entity(){  
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
    if(this.hasSpawner()) spawner.update(); //TODO: motivational movement
    if(hasInventory()) inventory.update();
    if(this.hasActions()){
      Action a = actions.update();
      if(a!=null){
        Entity target = getContainer().getInventory().get(a.target);
        if(target!=null){
          target.getActions().setSoftTarget(getShortName());
          target.setHealth(target.getHealth()-a.damage);
          target.send(getShortName()+" hits you with "+a.mes+" for "+a.damage+" damage");
          for(Entity e: getContainer().getInventory().getAll()){
            if(e!=target&&e!=this) e.send(getShortName()+" hits "+ target.getShortName()+" with "+a.mes+" for "+a.damage+" damage");
          }
          send("you hit "+target.getShortName()+" with "+a.mes+" for "+a.damage+" damage"); 
        
          if(target.getHealth()<=0){
            int xp = target.getStats().getXp();
            this.getStats().setXp( getStats().getXp()+xp);
            getActions().removeTarget();
            send("you killed "+target.getShortName()+" with "+a.mes+" for "+a.damage+" damage"+"\nyou gain "+xp+" xp");
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
    this.spawner = spawner;
    spawner.setOwner(this);
    return this;
  }
  public Spawner getSpawner(){
    return spawner;
  }
  public boolean hasSpawner(){
    return spawner!=null;
  }
  public Entity setStats(Stats stats){
    this.stats = stats;
    stats.setOwner(this);
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
    
   //  send("you are dead");
    }
    container.getInventory().updateAdd(corpse);
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
  public Entity getContainer(){
    return this.container;
  }
  public Entity setContainer(Entity e){
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
  public Entity setNames(String... s){
    return setNames(Arrays.asList(s));
  }
  public Entity setNames(List<String> s){
    names.addAll(s);
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
    return this.description;
  }
  public Entity setDescription(String description) {
    this.description = description;
    return this;
  }
  /*
  public short getMotivation() {
    return this.motivation;
  }
  public Entity setMotivation(short motivation) {
    this.motivation = motivation;
    return this;
  }
  public int getArmor() {
    return this.armor;
  }
  public Entity setArmor(int armor) {
    this.armor = armor;
    return this;
  }
  */
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
    equipment = e;
    e.setOwner(this);
    return this;
  }
  public boolean hasInventory(){
    return inventory!=null;
  }
  public Inventory getInventory(){
    return this.inventory;
  }
  public Entity setInventory(Inventory i){
    inventory = i;
    inventory.setOwner(this);
    return this;
  }
  public boolean hasExits(){
    return exits!=null;
  }
  public Exits getExits(){
    return this.exits;
  }
  public Entity setExits(Exits e){
    this.exits = e;
    exits.setOwner(this);
    return this;
  }
  public Actions getActions(){
    return this.actions;
  }
  public Entity setActions(Actions a){
    this.actions = a;
    a.setOwner(this);
    return this;
  }
  
  public String toString(){
    return "Name: "+getShortName()+"\n "+getDescription()+(hasStats()?("\nHealth: "+getHealth()):"")
    +(hasEquipment()? ("\nStats: "+getEquipment().toString()) : "")
    +(hasInventory()? ("\nInventory: "+getInventory().toString()) : "")
    +(hasExits()?("\nExits: "+getExits().toString()):"");
  }
}