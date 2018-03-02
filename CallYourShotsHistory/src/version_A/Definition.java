package version_A;

import java.util.HashMap;
import java.util.Map;

class Definition{
  Entity e = null;
  final int HEALTH;
  final String DESCRIPTION;
  final String SHORTNAME;
  final String[] NAMES;
  Entity owner = null;
  String inventory = "";
  Map<String,Integer> stats = null;
  String wearable = "";
  String modifiers = "";
  int timer = 0;
  int timerMax = 0;
  
  Definition(int timer,int health,String description,String shortName,String... names){
    this.timerMax = timer;
    HEALTH = health;
    this.DESCRIPTION = description;
    this.SHORTNAME = shortName;
    this.NAMES = names.length==0?new String[]{shortName.split(" ")[1]}:names;     
  }
  public Definition setOwner(Entity owner){
    this.owner = owner;
    return this;
  }
  public Definition setInventory(){
    inventory = "true";
    return this;
  }
  public Definition setStats(Map<String,Integer> stats){  
    this.stats = new HashMap<>(stats);
    //this.stats.putAll(stats);
    return this;
  }
  public Definition setWearable(String wear){
    wearable = wear;
    return this;
  }
  public Definition setModifiers(String mod){
    modifiers = mod; // "1d6 slash"
    return this;
  }
  void update(){
    if(stats==null && owner.getInventory().get(NAMES[0])!=null) return; // item already exists in room. done.
    if(stats!=null && stats.size()>0 &&e!=null && e.hasStats()){ // entity is alive. don't make another.
      e.update();
      return;
    } 
    if(timer-->0) return;
      e = new Entity().setShortName(SHORTNAME).setDescription(DESCRIPTION).setNames(NAMES);
      if(!inventory.isEmpty())e.setInventory(new Inventory());
      if(stats!=null)e.setStats(newStats()).setEquipment(new Equipment()).setActions(new Actions());
      if(!wearable.isEmpty())e.setWearable(new Wearable().setSlot(wearable));
      if(!modifiers.isEmpty())e.setModifiers(new Modifiers(modifiers));
      System.out.println("generating: "+SHORTNAME);
      e.setContainer(owner);
      timer = timerMax;
  }
  private Stats newStats(){
    Stats s = new Stats("");
    for(String k:stats.keySet()){
      switch(k){
        case "maxHealth": s.setMaxHealth(stats.get(k));break;
        case "xp": s.setXp(stats.get(k));break;
      }
    }
    return s;
  }
 
}
