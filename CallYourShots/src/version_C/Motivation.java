package version_C;

import java.util.List;

public class Motivation {
  private Entity owner = null;
  private int counterCur = 0;
  private int xpCap = 5;
  
  private short tiredMin = 0;
  private short tiredLess = 0;
  private short tiredCur = 0;  // 3
  private short tiredMore = 0;
  private short tiredMax = 0;
  /*
  private short hungryMin = 0;
  private short hungryLess = 0;
  private short hungryCur = 0;
  private short hungryMore = 0;
  private short hungryMax = 0;
  */
  private short angerMin = 0;
  private short angerLess = 0;
  private short angerCur = 0; // 3
  private short angerMore = 0;
  private short angerMax = 0;
  
  private short greedMin = 0;
  private short greedLess = 0;
  private short greedCur = 0;  // -3  ! change to 3 !
  private short greedMore = 0;
  private short greedMax = 0;
  
  private short fearMin = 0;
  private short fearLess = 0;
  private short fearCur = 0;
  private short fearMore = 0;
  private short fearMax = 0;
  
  //private short ego = 0;
  
  private short levelMin = 0;
  private short levelLess = 0;
  private short levelCur = 0; // 3
  private short levelMore = 0;
  private short levelMax = 0;
  
  //private short equip = 0;
  
  // tired: unconscious - alert/responsive/cunning  , hunger: starved - stuffed , anger: enraged - pacified , greed: klepto - giver/provider , fear: terrified - brave/adventurous
  
  // TODO: sample String: T:0,H:0,A:0,G:0,F:0,E:0
  
  Motivation(String mot){
    String[] parts = mot.split(",");
    for(String s : parts){
      String[] bits = s.split(":");
      switch(bits[0]){
      case "t": 
        if(bits[1]==null) break;
        tiredCur = Short.parseShort(bits[1]); break;  
      case "f":
        if(bits[1]==null) break;
        fearCur = Short.parseShort(bits[1]); break;
      case "h": 
      case "a": 
        if(bits[1]==null) break;
        angerCur = Short.parseShort(bits[1]); break;
      case "g": 
        if(bits[1]==null) break;
        greedCur = Short.parseShort(bits[1]); break;
      case "l":
        if(bits[1]==null) break;
        levelCur = Short.parseShort(bits[1]); break;
      }
    }
  }
  public void update(){
    if(counterCur-- <= 0){
      counterCur = 60;
      updateGreed();
      updateAnger();
      updateTired();
      updateFear();
      updateLevel();
    }
  }
  public void updateTired(){
    switch(tiredCur){
    case -3: break;
    case -2: rest(); tiredCur = 3; break;
    case -1: rest(); tiredCur = -2; break;  //maybe fear check or anger check to keep moving
    case 0: rest(); tiredCur = -1; break;
    case 1: movement(); tiredCur = 0; break;
    case 2: movement(); tiredCur = 1; break;
    case 3: movement(); tiredCur = 2; break;
    default:
    }
  }
  public void updateFear(){
    if(owner.hasStats()&&owner.getActions().size()>0)
      switch(fearCur){
      case -3: break;
      case -2: break;
      case -1: break;
      case 0: break;
      case 1: if(owner.getHealth()<=owner.getStats().getMaxHealth()/4) owner.flee(); fearCur=0; break;
      case 2: if(owner.getHealth()<=owner.getStats().getMaxHealth()/2) owner.flee(); fearCur=0; break;
      case 3: if(owner.getHealth()<=owner.getStats().getMaxHealth()/4*3) owner.flee();// owner.getActions().nextAction(flee()); fearCur=0; break;
      
      default: 
      }
  }
  public void updateGreed(){
    switch(greedCur){
    case -3: findEquipment(); break;
    case -2: searchInventory(); break;
    case -1: break;
    case 0: break;
    case 1: break;
    case 2: break;
    case 3: break;
    default: 
     // System.out.println("motivation: "+owner.getMotivation().toString());
    }
  } 
  
  // collector, equipment comparison
  /* ~Goblin~
   * Tired: 2
   * Hunger: 3
   * Anger: 2
   * Greed: 5
   * Fear: 3
   */
  public void updateAnger(){
    for(Entity e : owner.getContainer().getInventory().getAll()){
      if(e.hasStats()&&e!=owner){
        String name = owner.getShortName();
        if(angerCur==-3&&!e.hasSession()&&!e.getShortName().equals(name) && !e.hasName(name.substring(2))){
          attackTarget(e);
        }
        else if(angerCur==2&&!e.getShortName().equals(name) && !e.hasName(name.substring(2))){
          attackTarget(e);
        }
        else if(angerCur==3){
          //if(owner.getContainer())
          attackTarget(e);
        }
      } 
    }
  }
  public void updateLevel(){
    switch(levelCur){
    case -3: break;
    case -2: break;
    case -1: break;
    case 0: break;
    case 1: break;
    case 2: if(upgradeStamina())levelCur=3; break;
    case 3: if(upgradeStrength())levelCur=2; break;
    default: 
    }
  }
  public void movement(){
    if(owner.getActions().size()>0){
     // System.out.println(owner.getShortName()+" tried to move, in combat");
      return;
    }
    Entity con = owner.getContainer();
    if(con.hasExits()){
      String dir = con.getExits().getRandomExit();
      owner.setContainer(con.getExits().getExit(dir));
      owner.broadcast(owner.getShortName()+" leaves "+dir, con.getInventory());   
      owner.broadcast(owner.getShortName()+" has entered the room", owner.getContainer().getInventory());
      //if(con.hasExits())
        System.out.println("> moving "+owner.getShortName()+" "+dir+" to "+owner.getContainer().getShortName());
    }
  }
  public void rest(){
    if(owner.getActions().size()>0){
      return;
    }
   // do nothings
    System.out.println("zzz "+owner.getShortName()+" rests");
  }
  public boolean findEquipment(){
    boolean ret = false;
    for(Entity e : owner.getContainer().getInventory().getAll()){
      if(e.isWearable()){     
        wearEquipment(e); 
        ret = true;
      }
      else if(!e.hasStats()&&e.hasInventory()){
        for(Entity in : e.getInventory().getAll()){
          if(in.isWearable()){
            wearEquipment(in);
            ret = true;
          }
        }
      }
    }
    return ret;
  } 
  public void searchInventory(){
    for(Entity e : owner.getInventory().getAll()) if(e.isWearable()) wearEquipment(e); 
  }
  public void wearEquipment(Entity item){
    Equipment eq = owner.getEquipment();
    Entity cur = null;
    switch(item.getWearable().getSlot()){
    case "head": cur = eq.getHead();break;
    case "shoulders": cur = eq.getShoulders();break;
    case "chest": cur = eq.getChest();break;
    case "legs": cur = eq.getLegs();break;
    case "feet": cur = eq.getFeet();break;
    case "oneHand": cur = eq.getOneHand();break;
    case "twoHand": cur = eq.getTwoHand();break;
    }
    if(cur==null){
      Inventory inv = owner.getContainer().getInventory();
      /*
      if(inv.getItem(item.getId())!=null){
        eq.wear(inv.getItem(item.getId()));
        inv.remove(item);
        owner.broadcast(owner.getShortName()+" equips "+item.getShortName() , inv);
        System.out.println(owner.getShortName()+" equips a "+item.getShortName());
      }
      */
      eq.wear(item.getContainer().getInventory().getItem(item.getId())); // made 
      item.getContainer().getInventory().getItem(item.getId()).setContainer(owner);
      inv.remove(item);
      //item.setContainer(owner);
      owner.broadcast(owner.getShortName()+" equips "+item.getShortName() , inv);
      System.out.println("+ "+owner.getShortName()+" equips "+item.getShortName());
    }
  }
  public boolean findTarget(){
    boolean ret = false;
    for(Entity e : owner.getContainer().getInventory().getAll()){
      if(e.hasStats()&&e!=owner&&!e.getShortName().equals(owner.getShortName())){  // do checks for weaker / check weapon damage vs target hp 
        //System.out.println("! "+owner.getShortName()+" found target "+e.getShortName());
        attackTarget(e);
        ret = true;
      }
    }
    return ret;
  }
  public void attackTarget(Entity target){
   //if(target.getContainer() == owner.getContainer())
   owner.getActions().setTarget(target); 
  // target.getActions().setTarget(owner);
   // was target.getShortName
   System.out.println("X "+owner.getShortName()+" is attacking "+target.getShortName());
   owner.getActions().update();
   //target.getActions().setSoftTarget(owner.getShortName());
  }
    
  public boolean upgradeStrength(){
    boolean ret = false;
    if(!owner.hasStats()) return ret;
    if(owner.getStats().getXp()>=xpCap){
      owner.getStats().setXp(owner.getStats().getXp()-xpCap);
      owner.getStats().setStrength(owner.getStats().getStrength()+1);
      xpCap+=5;
      owner.getStats().setLevel(owner.getStats().getLevel()+1); 
      System.out.println("^ "+owner.getShortName()+" lv."+owner.getStats().getLevel()+" upgraded Strength");
      ret = true;
    }
    return ret;
  }
  public boolean upgradeStamina(){
    boolean ret = false;
    if(!owner.hasStats()) return ret;
    if(owner.getStats().getXp()>=xpCap){
      owner.getStats().setXp(owner.getStats().getXp()-xpCap);
      owner.getStats().setStamina(owner.getStats().getStamina()+1);
      owner.getStats().setMaxHealth(owner.getStats().getMaxHealth()+1);
      xpCap+=5;
      owner.getStats().setLevel(owner.getStats().getLevel()+1); 
      System.out.println("^ "+owner.getShortName()+" lv."+owner.getStats().getLevel()+" upgraded Stamina");
      ret = true;
    } 
    return ret;
  }
  
  public Motivation setOwner(Entity e){
    owner = e;
    return this;
  }
/*
  public int getTired() {
    return (tiredCur-tiredMin)/(tiredMax-tiredMin)*100;
  }
  public Motivation setTiredMin(short tiredMin) {
    this.tiredMin = tiredMin;
    return this;
  }
  public Motivation setTiredLess(short tiredLess) {
    this.tiredLess = tiredLess;
    return this;
  }
  public Motivation setTiredCur(short tiredCur) {
    this.tiredCur = tiredCur;
    return this;
  }
  public Motivation setTiredMore(short tiredMore) {
    this.tiredMore = tiredMore;
    return this;
  }
  public Motivation setTiredMax(short tiredMax) {
    this.tiredMax = tiredMax;
    return this;
  }
  /*
  public short getHungryMin() {
    return hungryMin;
  }
  public Motivation setHungryMin(short hungryMin) {
    this.hungryMin = hungryMin;
  }
  public short getHungryLess() {
    return hungryLess;
  }
  public Motivation setHungryLess(short hungryLess) {
    this.hungryLess = hungryLess;
  }
  public short getHungryCur() {
    return hungryCur;
  }
  public Motivation setHungryCur(short hungryCur) {
    this.hungryCur = hungryCur;
  }
  public short getHungryMore() {
    return hungryMore;
  }
  public Motivation setHungryMore(short hungryMore) {
    this.hungryMore = hungryMore;
  }
  public short getHungryMax() {
    return hungryMax;
  }
  public Motivation setHungryMax(short hungryMax) {
    this.hungryMax = hungryMax;
  }
  public short getAngryMin() {
    return angryMin;
  }
  public Motivation setAngryMin(short angryMin) {
    this.angryMin = angryMin;
  }
  public short getAngryLess() {
    return angryLess;
  }
  public Motivation setAngryLess(short angryLess) {
    this.angryLess = angryLess;
  }
  public short getAngryCur() {
    return angryCur;
  }
  public Motivation setAngryCur(short angryCur) {
    this.angryCur = angryCur;
  }
  public short getAngryMore() {
    return angryMore;
  }
  public Motivation setAngryMore(short angryMore) {
    this.angryMore = angryMore;
  }
  public short getAngryMax() {
    return angryMax;
  }
  public Motivation setAngryMax(short angryMax) {
    this.angryMax = angryMax;
  }
  public short getGreedMin() {
    return greedMin;
  }
  public Motivation setGreedMin(short greedMin) {
    this.greedMin = greedMin;
  }
  public short getGreedLess() {
    return greedLess;
  }
  public Motivation setGreedLess(short greedLess) {
    this.greedLess = greedLess;
  }
  public short getGreedCur() {
    return greedCur;
  }
  public Motivation setGreedCur(short greedCur) {
    this.greedCur = greedCur;
  }
  public short getGreedMore() {
    return greedMore;
  }
  public Motivation setGreedMore(short greedMore) {
    this.greedMore = greedMore;
  }
  public short getGreedMax() {
    return greedMax;
  }
  public Motivation setGreedMax(short greedMax) {
    this.greedMax = greedMax;
  }
  public short getFearMin() {
    return fearMin;
  }
  public Motivation setFearMin(short fearMin) {
    this.fearMin = fearMin;
  }
  public short getFearLess() {
    return fearLess;
  }
  public Motivation setFearLess(short fearLess) {
    this.fearLess = fearLess;
  }
  public short getFearCur() {
    return fearCur;
  }
  public Motivation setFearCur(short fearCur) {
    this.fearCur = fearCur;
  }
  public short getFearMore() {
    return fearMore;
  }
  public Motivation setFearMore(short fearMore) {
    this.fearMore = fearMore;
  }
  public short getFearMax() {
    return fearMax;
  }
  public Motivation setFearMax(short fearMax) {
    this.fearMax = fearMax;
  }
  
*/
}
