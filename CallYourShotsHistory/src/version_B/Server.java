package version_B;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/serverB")
public class Server {
  World world = null;
  Actions actions = new Actions();
  
 // private Entity roomCur = null;
  //private Inventory roomInv = roomCur.getInventory();
  private String player = "";
  private Entity self = null;
  private long id = 0;
  private int cap = 5;
  //static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

  @OnOpen
  public void onOpen(Session ses) {
    world = WorldFactory.getWorld();
 //   roomCur = world.spawnRoom;
    //sessions.put(ses.getId(), ses);
    System.out.println("SessionID: " + ses.getId());
  }

  @OnMessage
  public void onMessage(String mes, Session ses) {
    if (mes.length() < 1)
      return;
    if (mes.startsWith("|:|")) {
      player = mes.substring(3);
      ses.getUserProperties().put("name", player);
      id = world.addPlayer(); 
      self = world.PLAYERS.get(id).setNames(player).setShortName(player).setSession(ses);
      try {
        self.broadcast(player + " logged in");  //TODO: make global
        Thread.sleep(50);
        self.broadcast(self.getContainer().toString());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return;
    }
    String[] parts = mes.toLowerCase().split(" ");
    switch (parts[0]) {
    case "i":
    case "in":
    //case "inv": use for invite
    case "inventory": self.broadcast(self.getInventory().toString(),mes); break;   
    case "eq":
    case "equipment": self.broadcast(self.getEquipment().toString(),mes); break;    
    case "l":
    case "lo":
    case "loo":
    case "look": look(parts); break;   
    case "g":
    case "ge":
    case "get": get(parts); break;   //TODO: fix player looting, add KILL command
    case "drop": drop(parts); break;  
    case "put": put(parts); break;
    case "we":
    case "wear": wear(parts); break;
    case "remove": remove(parts); break;
    case "kill":
    case "attack": attack(parts); break;
    case "st": case "stat":case "state":case "stats": stats(); break;
    case "say": say(mes); break;
    case "tel": case "tell": tell(parts,mes); break;
    case "kick": break;
    case "improve": improve(parts);break;
    
    default:
      String exit = self.getContainer().getExits().findExit(parts[0]);
      if (!exit.isEmpty()){  //self.getContainer().getExits().hasExit(parts[0])) {
        Entity next = self.getContainer().getExits().getExit(exit);
        //Entity oldRoom = self.getContainer();
        //oldRoom.getInventory().remove(self);
        self.broadcast(player+" enters the room", next.getInventory()); 
        self.broadcast(player+" leaves "+exit, self.getContainer().getInventory()); //was oldRoom
        self.setContainer(next);
        self.broadcast(self.getContainer().toString(),parts[0]);
      } else
        self.broadcast("invalid command: " + mes,mes);
    }
  }
  @OnClose
  public void onClose(Session ses){
   // sessions.remove(ses.getId());
    world.removePlayer(id);
    self.broadcast(ses.getUserProperties().get("name").toString() + " logged out"); //TODO: make global
  }
  
  public void look(String[] parts){
    if (parts.length == 2) {
      Entity search = self.getInventory().get(parts[1]);
      if (search == null)
        search = self.getContainer().getInventory().get(parts[1]);
      if (search != null)
        self.broadcast(search.toString(),parts[0]);
      else{
        if (self.getContainer().getExits().hasExit(parts[1])) {
          self.broadcast(self.getContainer().getExits().getExit(parts[1]).toString(),parts[0]);
        }
        else self.broadcast("can't find " + parts[1],parts[0]);
      }
    } else
      self.broadcast(self.getContainer().toString(),parts[0]);
  }
  
  public void get(String[] parts){
    if (parts.length == 1)
      self.broadcast("get what?!",parts[0]);
    else if (parts.length == 2) {
      Entity item = self.getContainer().getInventory().remove(parts[1]);
      if (item != null){
        if(item != self){
          item.setContainer(self);
          self.broadcast("you pick up " + item.getShortName(),parts[0]+" "+parts[1]);
          self.broadcast(player+" picks you up",item);
          self.broadcast(player+" picks up "+item.getShortName(),self.getContainer().getInventory(),item);
        }
        else self.broadcast("you can't pick yourself up");
      }
      else
        self.broadcast("you can't find " + parts[1],parts[0]+" "+parts[1]);
    } else {
      Entity container = self.getInventory().get(parts[2]);
      if (container == null)
        container = self.getContainer().getInventory().get(parts[2]);
      if (container != null) {
        if(container.hasStats()) self.broadcast(container.getShortName()+" doesn't let you take anything",parts[0]+" "+parts[1]+" "+parts[2]);//TODO: increase anger, reduce repore / start combat
        else{
          Entity item = container.getInventory().remove(parts[1]);
          if (item != null){
            item.setContainer(self);
            self.broadcast("you get " + item.getShortName() + " from " + container.getShortName(),parts[0]+" "+parts[1]+" "+parts[2]);
            self.broadcast(player+" picks you up",item);
            self.broadcast(player+" picks up "+item.getShortName(),self.getContainer().getInventory(),item);
          }
          else self.broadcast("you can't find " + parts[1] + " in " + container.getShortName(),parts[0]+" "+parts[1]+" "+parts[2]);
        }
      }else self.broadcast("you can't find " + parts[2],parts[0]+" "+parts[1]+" "+parts[2]);
    }
  }
  
  public void drop(String[] parts){
    if(parts.length==1) self.broadcast("drop what?!",parts[0]);
    Entity item = self.getInventory().get(parts[1]);
    if(item==null) self.broadcast("you dont have item "+parts[1],parts[0]+" "+parts[1]);
    else{
      self.getInventory().remove(parts[1]).setContainer(self.getContainer());
      self.broadcast("you drop "+item.getShortName()+" in the room",parts[0]+" "+parts[1]);
      self.broadcast(player+" drops "+item.getShortName()+" in the room", self.getContainer().getInventory(),item);
      self.broadcast(player+" drops you",item);
    }
  }

  public void put(String[] parts){
    if(parts.length==1) self.broadcast("put what, where?!",parts[0]);
    else if(parts.length==2)self.broadcast("put "+parts[1]+", where?!",parts[0]+" "+parts[1]);
    else{
      Entity search = self.getInventory().get(parts[1]);
      if(search==null) self.broadcast("can't find "+parts[1],parts[0]+" "+parts[1]);
      else{
        Entity container = self.getInventory().get(parts[2]);
        if(container==null) container = self.getContainer().getInventory().get(parts[2]);
        if(container==null) self.broadcast("can't find "+parts[2],parts[0]+" "+parts[1]+" "+parts[2]);
        else{
         self.getInventory().remove(parts[1]).setContainer(container);
         self.broadcast("you put "+search.getShortName()+" in "+container.getShortName(),parts[0]+" "+parts[1]+" "+parts[2]);
        }
      }
    }
  }
  
  public void wear(String[] parts){
    if(parts.length==1) self.broadcast("wear what?!",parts[0]);
    else if(parts.length==2){
      Entity item = self.getInventory().remove(parts[1]); 
      //System.out.println(item.getShortName()+", "+(item.isWearable()));
      if(item!=null){
        if(item.isWearable()){
          Entity[] old = self.getEquipment().wear(item);
//          System.out.println(old[0]==null?"null":old[0].getShortName());
          if(old==null||old[0]==null) self.broadcast("you wear "+item.getShortName(),parts[0]+" "+parts[1]);
          else{
            for(Entity e : old) self.getInventory().add(e);
            if(old.length==1)self.broadcast("you remove "+old[0].getShortName()+" and wear "+item.getShortName(),parts[0]+" "+parts[1]);
            else if(old.length==2){
              if(old[1]==null) self.broadcast("you remove "+old[0].getShortName()+" and wear "+item.getShortName(),parts[0]+" "+parts[1]);
              else if(old[0]==null) self.broadcast("you remove "+old[1].getShortName()+" and wear "+item.getShortName(),parts[0]+" "+parts[1]);
              else self.broadcast("you remove "+old[0].getShortName()+" and "+old[1].getShortName()+" and wear "+item.getShortName(),parts[0]+" "+parts[1]);
            }
          }
        }
        else self.broadcast("you can't wear "+item.getShortName(),parts[0]+" "+parts[1]);
      }
      else self.broadcast("you can't find "+parts[1],parts[0]+" "+parts[1]);
    }
  }
  
  public void remove(String[] parts){
    if(parts.length==1) self.broadcast("remove what?!",parts[0]);
    else if(parts.length==2){
      Entity item = self.getEquipment().remove(parts[1]);
      if(item!=null){
        self.getInventory().add(item);
        self.broadcast("you remove "+item.getShortName(),parts[0]+" "+parts[1]);
      }
      else self.broadcast("you're not wearing "+parts[1],parts[0]+" "+parts[1]);
    }
  }
  
  public void attack(String[] parts){
    if(parts.length==1) self.broadcast("attack what?!",parts[0]);
    else if(parts.length==2){
      Entity target = self.getContainer().getInventory().get(parts[1]);
      if(target==null) self.broadcast("you can't find "+parts[1],parts[0]+" "+parts[1]);
      else if(!target.hasStats()) self.broadcast("you can't attack "+target.getShortName(),parts[0]+" "+parts[1]);
      else{
        self.broadcast(parts[0]+" "+parts[1]);
        self.getActions().setTarget(parts[1]);
      }
    }
  }
  public void kick(String[] parts){
    if(parts.length==1 && self.getActions().hasTarget()){
      self.getActions().nextAction(self.getActions().new Action(2, parts[1]).setMessage("a kick")).setTarget(parts[1]);
    }
    else self.broadcast("you dont have a target");
    if(parts.length==2) self.getActions().nextAction(self.getActions().new Action(2, parts[1]).setMessage("a kick")).setTarget(parts[1]);
  }
  public void improve(String[] parts){
    if(parts.length==1) self.broadcast("improve what stat?!",parts[0]);
    else if(self.hasStats()&&parts.length==2){  //TODO: remove hasStats() check
      int xp = self.getStats().getXp();
      if(xp>=cap){
          switch(parts[1]){
          case "strength": self.getStats().setStrength(self.getStats().getStrength()+1).setXp(xp-cap); cap+=5;
          self.broadcast("you increase your strength by 1"); break;
          case "stamina": self.getStats().setStamina(self.getStats().getStamina()+1).setXp(xp-cap); cap+=5;
          self.broadcast("you increase your stamina by 1"); break;
          case "agility": self.getStats().setAgility(self.getStats().getAgility()+1).setXp(xp-cap); cap+=5;
          self.broadcast("you increase your agility by 1"); break; 
          case "intelligence": self.getStats().setIntelligence(self.getStats().getIntelligence()+1).setXp(xp-cap); cap+=5;
          self.broadcast("you increase your intelligence by 1"); break;
          }
      }
      else self.broadcast("you don't have enough xp");
    }
  }
  public void stats(){ // needs a plain damage readout
    self.broadcast("Name: "+self.getShortName()+", Title: "+("the noob")
        +"\nArmor: "+self.getEquipment().getArmor()+", Damage: "+self.getEquipment().getDamageDice()+" "+self.getEquipment().getDamageType()
        +"\nHP: "+self.getHealth()+"/"+self.getStats().getMaxHealth()+", Mana: "+self.getStats().getMana()+"/"+self.getStats().getMaxMana()
         +", Stamina: "+self.getStats().getStamina()+"/"+self.getStats().getMaxStamina()
        +"\nStrength: "+self.getStats().getStrength()+"\nStamina:  "+self.getStats().getStamina()
        +"\nAgility:  "+self.getStats().getAgility()+"\nIntelligence: "+self.getStats().getIntelligence()
        +"\nXp:  "+self.getStats().getXp()
     ,"stats");
  }
  public void say(String mes){
    self.broadcast(self.getShortName()+" says '"+mes.substring(4)+"'",self.getContainer().getInventory());
    self.broadcast("you say '"+ mes.substring(4)+"'");
  }
  public void tell(String[] parts, String mes){
    if(parts.length==1) self.broadcast("tell to who?!",parts[0]);
    else if(parts.length>=2){
      Entity target = null;
      for(Entity e: world.PLAYERS.values()){ 
        if(parts[1].equals(e.getShortName())) target = e;
      }
      if(target==null){
        System.out.println(parts[1]);
        self.broadcast("you can't find "+parts[1]+" to tell to");
      }
      
      else{

        self.broadcast(self.getShortName()+" tells you '"+mes.substring(parts[0].length()+parts[1].length()+2)+"'",target);
        self.broadcast("you tell '"+mes.substring(parts[0].length()+parts[1].length()+2)+"' to "+parts[1]);
      }
    }
  }
  /*
  public void kill(String[] parts){
    if(parts.length==1) broadcast("kill what?!");
    else if(parts.length==2){
      Entity target = self.getContainer().getInventory().get(parts[1]);
      if(target==null) broadcast("you can't find "+parts[1]);
      else if(!target.hasStats()) broadcast("you can't kill "+target.getShortName());
      else {
        for(Entity e: target.getEquipment().removeAll()) target.getInventory().add(e);
        target.makeDead();
        
        broadcast("you kill "+target.getShortName());
        broadcast(player+" kills "+target.getShortName(),self.getContainer().getInventory(),target);
        broadcast(player+" kills you", target);
        //TODO: add xp
      }
    }
  }
*//*
  public void broadcast(String mes, String owner) { // world
    for (Session s : sessions.values()) {           
      s.getAsyncRemote().sendText(owner + ": " + mes);
    }
  }
  */
  /*
  public void broadcast(String mes, Inventory inv){ // room minus self
    for(Entity e : inv.getAll()){
      if(e != self) e.send(mes);
    }
 }
  public void broadcast(String mes, Inventory inv, Entity t){ // room minus self/target
    for(Entity e : inv.getAll()){
      if(e!=self && t!=e) e.send(mes);
    }
 }
  public void broadcast(String mes, Entity e){ // target 
    e.send(mes);
  }
  public void broadcast(String mes){ // self
    broadcast(mes,"");
  }
  public void broadcast(String mes,String cmd){ // self
   self.send("> "+cmd+"\n"+mes+"\n{ "+self.getHealth()+"/"+self.getStats().getMaxHealth()+"HP "
   +self.getStats().getMana()+"/"+self.getStats().getMaxMana()+"M "
   +self.getStats().getStamina()+"/"+self.getStats().getMaxStamina()+"S } ");
 }
 */
}
