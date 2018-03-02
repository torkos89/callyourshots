package version_C;

class Definition{
  Entity e = null;
  final String DESCRIPTION;
  final String SHORTNAME;
  final String NAMES;
  String stats = null;
  Entity owner = null;
  String inventory = "";
  String container = "";
  //ap<String,Integer> stats = null;
  String wearable = "";
  String modifiers = "";
  String motivation = "";
  int timer = 0;
  int timerMax = 0;
  long id = 0;
  
  Definition(long id,int timer,String description,String shortName,String names){
    //e = new Entity().setId(id);
    this.id = id;
    timerMax = timer;
    DESCRIPTION = description;
    SHORTNAME = shortName;
    NAMES = names;
  }
  public Definition setOwner(Entity owner){
    this.owner = owner;
    return this;
  }
  public Definition setInventory(){
    inventory = "true";
    return this;
  }
  public Definition setStats(String stats){  
    this.stats = stats;
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
  public Definition setMotivation(String mot){
    motivation = mot;
    return this;
  }
  public String toString(){ 
    return "id: "+id+" , timer: "+timer+" , description: "+DESCRIPTION+" , shortname: "+SHORTNAME+" , names: "+NAMES+" , owner: "+owner+" , stats: "+(stats!=null?stats:"")
        +" , modifiers: "+modifiers+" , wearable: "+wearable+" , motivation: "+motivation;
  }
  void update(){ // owner.getInventory().get(NAMES[0]!=null
    Entity target = null;
    if(!container.isEmpty()){
      target = owner.getInventory().get(container);
      if(target==null) target = owner;
    }
    else target = owner;
    if(stats==null&& target.getInventory().getItem(id)!=null) return; // item already exists in room. done.
    if(stats!=null && !stats.isEmpty() && e!=null && e.hasStats()){ // entity is alive. don't make another.
      e.update();
      return;
    } 
    if(timer-->0) return;
    e = new Entity().setShortName(SHORTNAME).setDescription(DESCRIPTION).setNames(NAMES).setId(id);
    if(!inventory.isEmpty()) e.setInventory(new Inventory());
    if(stats!=null&&!stats.isEmpty())e.setStats(new Stats(stats)).setEquipment(new Equipment()).setActions(new Actions());
    if(!wearable.isEmpty()) e.setWearable(new Wearable().setSlot(wearable));
    if(!modifiers.isEmpty()) e.setModifiers(new Modifiers(modifiers));
    if(!motivation.isEmpty()) e.setMotivation(new Motivation(motivation));
   
    
    System.out.println("generating: "+SHORTNAME+" , inventory: "+inventory);
    e.setContainer(target);
    timer = timerMax;
  }
  public Definition setContainer(String con) {
    container = con;
    return this;
  } 
}
