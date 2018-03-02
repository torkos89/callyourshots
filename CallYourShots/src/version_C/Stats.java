package version_C; 
//TODO: sunder world, moves rooms apart from one another

import java.sql.ResultSet;

public class Stats {
  // primary stats
  private int constitution = 0;
  private int strength = 0;
  private int agility = 0;
  private int intelligence = 0;
  // calculated
  private int mana = 0;
  private int stamina = 0;
  private int maxHealth = 0;
  private int maxMana = 0;
  private int maxStamina = 0;
  private int xp = 0;
  private int level = 1;
  // cumulative
  private int armor = 0;
  private Entity owner = null;
  
  Stats(String s){
    if(s!=null&&!s.isEmpty()){
     for(String piece : s.split(",")){
       String[] parts = piece.split(":");
       if(parts.length>1){
         switch(parts[0].trim()){
         case "hp": setMaxHealth(Integer.parseInt(parts[1])); break;
         case "con": setConstitution(Integer.parseInt(parts[1])); break;
         case "str": setStrength(Integer.parseInt(parts[1])); break;
         case "agi": setAgility(Integer.parseInt(parts[1])); break;
         case "int": setIntelligence(Integer.parseInt(parts[1])); break;
         case "xp": setXp(Integer.parseInt(parts[1])); break;
         case "lv": setLevel(Integer.parseInt(parts[1])); break;
         }
       }
     }
    }
  }
  public int getXp(){
    return xp;
  }
  public Stats setXp(int xp){
    this.xp = xp;
    return this;
  }
  public int getLevel(){
    return level;
  }
  public Stats setLevel(int lv){
    this.level = lv;
    return this;
  }
  public void update(){
    if(!owner
        .getActions()
        .hasTarget()){
      if(owner.getHealth()<maxHealth) owner.setHealth(owner.getHealth()+1);
      else owner.setHealth(maxHealth);
    }
    else if(owner.getHealth()>maxHealth*2){
      owner.setHealth(maxHealth*2);
    }
    if(mana<maxMana) mana++;
    else mana = maxMana;
    if(stamina<maxStamina) stamina++;
    else stamina = maxStamina; 
  }
  public Stats setOwner(Entity e){
    this.owner = e;
    return this;
  }
  public int getConstitution(){
    return constitution;
  }
  public Stats setConstitution(int constitution){
    this.constitution = constitution;
    return this;
  }
  public int getStrength(){
    return strength;
  }
  public Stats setStrength(int strength){
    this.strength = strength;
    return this;
  }
  public int getAgility(){
    return agility;
  }
  public Stats setAgility(int agility){
    this.agility = agility;
    return this;
  }
  public int getIntelligence(){
    return intelligence;
  }
  public Stats setIntelligence(int intelligence){
    this.intelligence = intelligence;
    return this;
  }
  public int getMaxHealth(){
    return maxHealth;
  }
  public Stats setMaxHealth(int health){
    this.maxHealth = health;
    return this;
  }
  public int getMana(){
    return mana;
  }
  public Stats setMana(int mana){
    this.mana = mana;
    return this;
  }
  public int getMaxMana(){
    return maxMana;
  }
  public Stats setMaxMana(int maxMana){
    this.maxMana = maxMana;
    return this;
  }
  public int getStamina(){
    return stamina;
  }
  public Stats setStamina(int stamina){
    this.stamina = stamina;
    return this;
  }
  public int getMaxStamina(){
    return maxStamina;
  }
  public Stats setMaxStamina(int maxStamina){
    this.maxStamina = maxStamina;
    return this;
  }
  public int getArmor(){
    return armor;
  }
  public Stats setArmor(int armor){
    this.armor = armor;
    return this;
  }
}
