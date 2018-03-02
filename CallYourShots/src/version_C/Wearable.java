package version_C;

public class Wearable {
  //private int damage = 0;
 // private int armor = 0;
  private String slot = "";
  
  public String getSlot(){
    return this.slot;
  }
  public Wearable setSlot(String s){
    this.slot = s;
    return this;
  }
  /*
  public int getDamage() {
    return damage;
  }
  public Wearable setDamage(int damage) {
    this.damage = damage;
    return this;
  }
  public int getArmor() {
    return armor;
  }
  public Wearable setArmor(int armor) {
    this.armor = armor;
    return this;
  }
  */
}
