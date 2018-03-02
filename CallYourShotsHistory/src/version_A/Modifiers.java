package version_A;
 // blunt ~ padded / pierce ~  / slash ~ leather / pierce & slash ~ scale /  
public class Modifiers {
  private int dRolls = 0; //damage
  private int dDice = 0;
  private int aRolls = 0; //armor
  private int aDice = 0;
  // MAGIC
  private int light = 0;
  private int dark = 0;
  private int fire = 0;
  private int water = 0;
  private int earth = 0;
  private int air = 0;
  private int chaos = 0;
  //PHYSICAL DAMAGE vs armor
  private String dType = "unarmed"; //damage
  private String aType = ""; //armor
  
  private int speed = 6; //default
  // PYSICAL EFFECTS
  private int bleed = 0;
  private int bleedCur = 0;
  private int bleedDur = 0;
  private int poison = 0;
  private int poinsonCur = 0;
  private int poisonDur = 0;
  private int fear = 0;
  private int fearCur = 0;
  private int fearDur = 0;
  private int blind = 0;
  private int blindCur = 0;
  private int blindDur = 0;
  private int slow = 0;
  private int slowCur = 0;
  private int slowDur = 0 ;
  private int haste = 0;
  private int hasteCur = 0;
  private int hasteDur = 0;
  private int curse = 0;
  private int curseCur = 0;
  private int curseDur = 0;
  private int disease = 0;
  private int diseaseDel = 0;
  private int diseaseTic= 0;
  
  Modifiers(String mods){
     String[] parts = mods.split(",");
     for(String s : parts){
       String[] bits = s.split(":");
       switch(bits[0]){
         case "dmg": // "dmg:1d4 blunt,?"    
           String dRolls = bits[1].substring(0,bits[1].indexOf('d')); 
           String dDice = bits[1].substring(bits[1].indexOf('d')+1 , bits[1].indexOf(' '));
           String dType = bits[1].substring(bits[1].indexOf(' ')+1);
           setDamage(Integer.parseInt(dRolls), Integer.parseInt(dDice));
           setDamageType(dType); break;
         case "arm": // "arm-1d1-natural"
          // System.out.println("here");
           String aRolls = bits[1].substring(0,bits[1].indexOf('d')); 
           String aDice = bits[1].substring(bits[1].indexOf('d')+1 , bits[1].indexOf(' '));
           String aType = bits[1].substring(bits[1].indexOf(' ')+1);
           setArmor(Integer.parseInt(aRolls), Integer.parseInt(aDice));
           setArmorType(aType); break;
         default: 
           System.out.println("bits "+bits[1]);
           String[] nibs = bits[1].split(" "); // dmg, del, dur
           if(nibs.length!=3) continue;
           int[] nums = {Integer.parseInt(nibs[0]), Integer.parseInt(nibs[1]), Integer.parseInt(nibs[2])};
           switch(bits[0]){
           case "disease": disease = nums[0]; diseaseDel = nums[1]; diseaseTic = nums[2];
           if(diseaseDel==0){
             if(diseaseTic>0){
               setDamage(disease,1);
               diseaseTic--;
             }
           }
           diseaseDel--;
           }
       }
     } 
  } 
  public int getArmorLow(){
    return aRolls;
  }
  public int getArmorHigh(){
    return aRolls * aDice;
  }
  public Modifiers getArmor(){
    int total = 0;
    for(int i=0;i<aRolls;i++){
      total+=Math.ceil(Math.random()*aDice);
    }
    return this;
  }
  public Modifiers setArmor(int rolls, int dice) {
    aRolls = rolls;
    aDice = dice;
    return this;
  }
  public Modifiers setArmorType(String type) {
    aType = type;
    return this;
  }
  public String getArmorType(){
    return aType;
  }
  public String getArmorDice(){
    return aRolls+"d"+aDice;
  }
  // update?
  public int getSpeed(){
    return this.speed;
  }
  public Modifiers setSpeed(int speed){
    this.speed = speed;
    return this;
  }
  public Modifiers setDamage(int rolls, int dice){
    dRolls = rolls;
    dDice = dice;
    return this;
  }
  public int getDamage(){
    int total = 0;
    for(int i=0;i<dRolls;i++){
      total+=Math.ceil(Math.random()*dDice);
    }
    return total;
  }
  public Modifiers setDamageType(String type){
    aType = type;
    return this;
  }
  public String getDamageType(){
    return aType;
  }
  public String getDamageDice(){
    return dRolls+"d"+dDice;
  }
}
