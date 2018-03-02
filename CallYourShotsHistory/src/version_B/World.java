package version_B;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;

public class World implements Runnable{
  final Map<Long, Entity> ROOMS = new HashMap<>();
  final Map<Long, Entity> PLAYERS = new ConcurrentHashMap<>();
  long pid = 0;
  
  
  // ROOMS 
/*
  Entity goblinDen = new Entity().setShortName("goblin den").setDescription("a goblin den").setInventory(new Inventory().setWeightMax(0))
      .setExits(new Exits());

  /*      
  Entity goblin = new Entity().setNames("goblin").setShortName("a goblin").setDescription("a small green-skinned humanoid").setHealth(14).setWeight(0)
      .setEquipment(new Equipment()).setInventory(new Inventory()).setStats(new Stats().setMaxHealth(7).setXp(1)).setActions(new Actions())
      .setModifiers(new Modifiers().setDamage(1, 2).setDamageType("blunt"));
  
  Entity goblin2 = new Entity().setNames("hob","hobgoblin").setShortName("a beefy goblin").setDescription("a small green-skinned humanoid").setHealth(14).setWeight(0)
      .setEquipment(new Equipment()).setInventory(new Inventory()).setStats(new Stats().setMaxHealth(14).setXp(3)).setActions(new Actions())
      .setModifiers(new Modifiers().setDamage(1, 2).setDamageType("blunt"));
 * /
  
  //lootBox.getInventory().add(tophat).add(dagger).add(zweihander);
  Entity spear = new Entity().setNames("spear","crude").setShortName("a crude spear").setDescription("A pointy stick").setWearable(new Wearable()
      .setSlot("twoHand")).setWeight(1).setModifiers(new Modifiers().setDamage(1, 3).setDamageType("pierce"));
  
  Entity spear2 = new Entity().setNames("spear","crude").setShortName("a crude spear").setDescription("A pointy stick").setWearable(new Wearable()
      .setSlot("twoHand")).setWeight(1).setModifiers(new Modifiers().setDamage(1, 3).setDamageType("pierce"));
  
  Entity dagger = new Entity().setNames("dagger").setShortName("a dagger").setDescription("A sharpened dagger").setWearable(new Wearable()
      .setSlot("oneHand")).setWeight(2).setModifiers(new Modifiers().setDamage(1,4).setDamageType("pierce"));
  
  Entity dagger2 = new Entity().setNames("dagger").setShortName("a cruddy dagger").setDescription("A unsharpened dagger").setWearable(new Wearable()
      .setSlot("oneHand")).setWeight(2).setModifiers(new Modifiers().setDamage(1,4).setDamageType("pierce"));
  
  Entity tophat = new Entity().setNames("hat","tophat").setShortName("a tophat").setDescription("a fine hat").setWearable(new Wearable()
      .setSlot("head").setArmor(1)).setWeight(1);
  
  Entity zweihander = new Entity().setNames("zwei","zweihander").setShortName("zweihander").setDescription("A huge two-handed sword").setWearable(new Wearable()
        .setSlot("twoHand")).setWeight(1).setModifiers(new Modifiers().setDamage(2, 6).setDamageType("slash")); //TODO: check weight/add weight
  
  Entity club = new Entity().setNames("club").setShortName("a wooden club").setDescription("A glorified stick").setWearable(new Wearable()
      .setSlot("oneHand")).setWeight(2).setModifiers(new Modifiers().setDamage(1,4).setDamageType("pierce"));
  */
  public long addPlayer(){
    Entity player = new Entity().setDescription("wannabe adventurer").setHealth(1).setWeight(0).setContainer(ROOMS.get(1L))
      .setEquipment(new Equipment()).setInventory(new Inventory()).setStats(new Stats("").setStrength(1).setStamina(1).setAgility(1).setIntelligence(1).setMaxHealth(6).setMaxStamina(3));
    player.setExits(new Exits().setExit("out", null)).setActions(new Actions()).setModifiers(new Modifiers("dmg:"+1+"d"+2+" punch")).setSpawnRoom(ROOMS.get(1L));
    PLAYERS.put(pid, player);
    PLAYERS.put(pid, player);
    return pid++;
  }
  public void removePlayer(long id){//, Entity room){
    Entity player = PLAYERS.remove(id);
    String name = player.getShortName(); //TODO: figure out if this was for a broadcast or something.
    player.getContainer().getInventory().remove(name);
  }
  public Connection startUp() throws ClassNotFoundException, SQLException{
    System.out.println("startUp****************************");
    Class.forName("org.h2.Driver");
    System.out.println("test**************************************");
    Connection con = DriverManager.getConnection("jdbc:h2:~/Desktop/CYS;AUTO_SERVER=TRUE");
    startUpRooms(con);
    startUpExits(con);
    startUpMobs(con);
    startUpItems(con);
    startUpMobSpawner(con);
    startUpItemSpawner(con);
    generateRooms(con);
    //generateItems(con);
    //generateMobs(con); 
    generateMobSpawner(con);
    generateItemSpawner(con);
    
    return con;
  }
  public void startUpRooms(Connection con) throws SQLException{
    System.out.println("startUpRooms****************************");
    con.createStatement().executeUpdate("drop table if exists rooms");
    con.createStatement().executeUpdate("create table if not exists rooms(id identity primary key, name varchar(30), description varchar(200))");
    try(PreparedStatement ps = con.prepareStatement("insert into rooms(name,description) values(?,?)")){
     ps.setString(1, "the spawn room"); // 1
     ps.setString(2, "to start the tutorial, type 'east'. Or to leave, type 'south'");
     ps.addBatch(); 
     ps.setString(1, "movement"); // 2
     ps.setString(2, "you can enter any exits the room has listed. you can also 'look'+(exit) to look before you enter.");
     ps.addBatch(); 
     ps.setString(1, "looking"); // 3
     ps.setString(2, "you can look at things in the room as well.\ntry 'look'+(box)");
     ps.addBatch(); 
     ps.setString(1, "geting"); // 4
     ps.setString(2, "to 'get' an item from somthing type 'get'+(the thing you want)+(where you are getting it from)");
     ps.addBatch();
     ps.setString(1, "attacking"); // 5
     ps.setString(2, "oh noes! St.SpawnRats is living up to his name. try either attack or kill+(thing you want to kill)");
     ps.addBatch();
     ps.setString(1, "looting"); // 6
     ps.setString(2, "eesh...well that corpse isnt gonna loot itself...at least i hope not...\ntry 'get'+(item)+(corpse#(1 or 2))");
     ps.addBatch();
     ps.setString(1, "equiping"); // 7
     ps.setString(2, "type 'i' for inventory. type 'stats' you guessed it..stats\n"+"now ignore the smell and 'wear'+(item)");
     ps.addBatch();
     ps.setString(1, "cathedral"); // 8
     ps.setString(2, "the entrance to a large ornate cathedral, when you die you will respawn here");
     ps.addBatch();
     ps.setString(1, "town square"); // 9
     ps.setString(2, "the center of a small town");
     ps.addBatch();
     ps.setString(1, "small shop"); // 10
     ps.setString(2, "a small shop");
     ps.addBatch();
     ps.setString(1, "bank?"); // 11
     ps.setString(2, "maybe a bank?");
     ps.addBatch();
     ps.setString(1, "entrance to town"); // 12
     ps.setString(2, "a large gated entrance to the wooden walls around town");
     ps.addBatch();
     ps.setString(1, "plains"); // 13
     ps.setString(2, "a grassy plain, with a road running south of town");
     ps.addBatch();
     ps.setString(1, "forests edge"); // 14
     ps.setString(2, "where sparse trees begin thicken");
     ps.addBatch();
     ps.setString(1, "ne. forest"); // 15
     ps.setString(2, "a fair amount of trees, still allow sunlight through");
     ps.addBatch();
     ps.setString(1, "n. forest"); // 16
     ps.setString(2, "the northern part of the forest");
     ps.addBatch();
     ps.setString(1, "e. forest"); // 17
     ps.setString(2, "the eastern part of the forest");
     ps.addBatch();
     ps.setString(1, "heart of the forest"); // 18
     ps.setString(2, "the center of the forest");
     ps.addBatch();
     ps.setString(1, "s. forest"); // 19
     ps.setString(2, "the southern part of the forest");
     ps.addBatch();
     ps.setString(1, "w. forest"); // 20
     ps.setString(2, "the eastern part of the forest");
     ps.addBatch();
     ps.setString(1, "goblin den entrance"); // 21
     ps.setString(2, "the canopy blocks out alot of light");
     ps.addBatch();
     ps.setString(1, "small pond"); // 22
     ps.setString(2, "a ssmall body of water");
     ps.addBatch();
     ps.setString(1, "road"); // 23
     ps.setString(2, "a simple road running north");
     ps.addBatch();
     ps.setString(1, "marsh"); // 24
     ps.setString(2, "a damp marsh");
     ps.addBatch();
     ps.setString(1, "swamp"); // 25
     ps.setString(2, "a swampy bog");
     ps.addBatch();
     ps.setString(1, "ambush ally"); // 26
     ps.setString(2, "im sure the name is meaningless...");
     ps.addBatch();
     
     ps.setString(1, "goblin bootcamp 1"); // 27
     ps.setString(2, "welcome to life, now get to work.");
     ps.addBatch();
     ps.setString(1, "goblin bootcamp 2"); // 28
     ps.setString(2, "g2");
     ps.addBatch();
     ps.setString(1, "goblin bootcamp 3"); // 29
     ps.setString(2, "g3");
     ps.addBatch();
     ps.setString(1, "goblin bootcamp 4"); // 30
     ps.setString(2, "g4");
     ps.addBatch();
     
     ps.executeBatch();
    }
  }
  public void startUpExits(Connection con) throws SQLException{
    System.out.println("startUpExits****************************");
    con.createStatement().executeUpdate("drop table if exists exits");
    con.createStatement().executeUpdate("create table if not exists exits(id identity primary key, startroomid long, direction varchar(30), endroomid long)");
    try(PreparedStatement ps = con.prepareStatement("insert into exits(startroomid,direction,endroomid) values(?,?,?)")){
      ps.setLong(1, 1); ps.setString(2, "east"); ps.setLong(3, 2); ps.addBatch(); 
      ps.setLong(1, 1); ps.setString(2, "south"); ps.setLong(3, 8); ps.addBatch(); 
      ps.setLong(1, 2); ps.setString(2, "east"); ps.setLong(3, 3); ps.addBatch();
      ps.setLong(1, 3); ps.setString(2, "east"); ps.setLong(3, 4); ps.addBatch();
      ps.setLong(1, 4); ps.setString(2, "east"); ps.setLong(3, 5); ps.addBatch();
      ps.setLong(1, 5); ps.setString(2, "east"); ps.setLong(3, 6); ps.addBatch();
      ps.setLong(1, 6); ps.setString(2, "east"); ps.setLong(3, 7); ps.addBatch();
      ps.setLong(1, 7); ps.setString(2, "south"); ps.setLong(3, 8); ps.addBatch();
      
      ps.setLong(1, 8); ps.setString(2, "south"); ps.setLong(3, 9); ps.addBatch();  // to town
      ps.setLong(1, 9); ps.setString(2, "north"); ps.setLong(3, 8); ps.addBatch();  // to cathedral
      ps.setLong(1, 9); ps.setString(2, "east"); ps.setLong(3, 11); ps.addBatch();  // to bank
      ps.setLong(1, 9); ps.setString(2, "south"); ps.setLong(3, 12); ps.addBatch(); // to entrance 
      ps.setLong(1, 9); ps.setString(2, "west"); ps.setLong(3, 10); ps.addBatch();  // to shop
      ps.setLong(1, 10); ps.setString(2, "east"); ps.setLong(3, 9); ps.addBatch();  // to town
      ps.setLong(1, 11); ps.setString(2, "west"); ps.setLong(3, 9); ps.addBatch();  // to town
      ps.setLong(1, 12); ps.setString(2, "north"); ps.setLong(3, 9); ps.addBatch(); // to town
      ps.setLong(1, 12); ps.setString(2, "south"); ps.setLong(3, 13); ps.addBatch();// to plains
      ps.setLong(1, 13); ps.setString(2, "north"); ps.setLong(3, 12); ps.addBatch();// to entrance
      ps.setLong(1, 13); ps.setString(2, "south"); ps.setLong(3, 22); ps.addBatch();// to pond 
      ps.setLong(1, 13); ps.setString(2, "west"); ps.setLong(3, 14); ps.addBatch(); // to forest edge
      ps.setLong(1, 14); ps.setString(2, "east"); ps.setLong(3, 13); ps.addBatch();// to plains
      ps.setLong(1, 14); ps.setString(2, "south"); ps.setLong(3, 15); ps.addBatch();// to ne.forest
      ps.setLong(1, 15); ps.setString(2, "north"); ps.setLong(3, 14); ps.addBatch();  // to forest edge
      ps.setLong(1, 15); ps.setString(2, "east"); ps.setLong(3, 22); ps.addBatch();  // to pond
      ps.setLong(1, 15); ps.setString(2, "south"); ps.setLong(3, 17); ps.addBatch(); // to e.forest 
      ps.setLong(1, 15); ps.setString(2, "west"); ps.setLong(3, 16); ps.addBatch();  // to n.forest    
      ps.setLong(1, 16); ps.setString(2, "east"); ps.setLong(3, 15); ps.addBatch();  // to ne.forest
      ps.setLong(1, 16); ps.setString(2, "south"); ps.setLong(3, 18); ps.addBatch(); // to c.forest     
      ps.setLong(1, 17); ps.setString(2, "north"); ps.setLong(3, 15); ps.addBatch();  // to ne.forest
      ps.setLong(1, 17); ps.setString(2, "east"); ps.setLong(3, 24); ps.addBatch();  // to marsh
      ps.setLong(1, 17); ps.setString(2, "south"); ps.setLong(3, 25); ps.addBatch(); // to swamp 
      ps.setLong(1, 17); ps.setString(2, "west"); ps.setLong(3, 18); ps.addBatch();  // to c.forest
      ps.setLong(1, 18); ps.setString(2, "north"); ps.setLong(3, 16); ps.addBatch();  // to n.forest
      ps.setLong(1, 18); ps.setString(2, "east"); ps.setLong(3, 17); ps.addBatch();  // to e.forest
      ps.setLong(1, 18); ps.setString(2, "south"); ps.setLong(3, 19); ps.addBatch(); // to s.forest
      ps.setLong(1, 18); ps.setString(2, "west"); ps.setLong(3, 20); ps.addBatch();  // to w.forest   
      ps.setLong(1, 19); ps.setString(2, "north"); ps.setLong(3, 18); ps.addBatch();  // to c.forest
      ps.setLong(1, 19); ps.setString(2, "east"); ps.setLong(3, 25); ps.addBatch();  // to swamp
      ps.setLong(1, 19); ps.setString(2, "west"); ps.setLong(3, 21); ps.addBatch();  // to goblin den entrance   
   
      ps.setLong(1, 20); ps.setString(2, "east"); ps.setLong(3, 18); ps.addBatch();  // to c.forest
      ps.setLong(1, 20); ps.setString(2, "south"); ps.setLong(3, 21); ps.addBatch();  // to goblin den entrance
      
      ps.setLong(1, 21); ps.setString(2, "north"); ps.setLong(3, 20); ps.addBatch();  // to w.forest
      ps.setLong(1, 21); ps.setString(2, "east"); ps.setLong(3, 19); ps.addBatch();  // to s.forest
      
      ps.setLong(1, 22); ps.setString(2, "north"); ps.setLong(3, 13); ps.addBatch();  // to plains
      ps.setLong(1, 22); ps.setString(2, "east"); ps.setLong(3, 23); ps.addBatch();  // to road
      ps.setLong(1, 22); ps.setString(2, "south"); ps.setLong(3, 24); ps.addBatch(); // to marsh
      ps.setLong(1, 22); ps.setString(2, "west"); ps.setLong(3, 15); ps.addBatch();  // to ne.forest
      
      ps.setLong(1, 23); ps.setString(2, "west"); ps.setLong(3, 22); ps.addBatch();  // pond
      
      ps.setLong(1, 24); ps.setString(2, "north"); ps.setLong(3, 22); ps.addBatch();  // to pond
      ps.setLong(1, 24); ps.setString(2, "west"); ps.setLong(3, 17); ps.addBatch();  // to e.forest
      
      ps.setLong(1, 25); ps.setString(2, "north"); ps.setLong(3, 17); ps.addBatch();  // to ne.forest
      ps.setLong(1, 25); ps.setString(2, "west"); ps.setLong(3, 19); ps.addBatch();  // to marsh
      
      //ps.setLong(1, 26); ps.setString(2, "north"); ps.setLong(3, 17); ps.addBatch();  // to...
      // Goblin Boot-Camp V
      ps.setLong(1, 27); ps.setString(2, "east"); ps.setLong(3, 28); ps.addBatch();  
      ps.setLong(1, 28); ps.setString(2, "east"); ps.setLong(3, 29); ps.addBatch(); 
      ps.setLong(1, 29); ps.setString(2, "east"); ps.setLong(3, 30); ps.addBatch();
      ps.setLong(1, 30); ps.setString(2, "east"); ps.setLong(3, 21); ps.addBatch();  // to goblin den
      
      ps.executeBatch();
     }
  }
  public void startUpItems(Connection con) throws SQLException{
    System.out.println("startUpItems****************************");
    con.createStatement().executeUpdate("drop table if exists items");
    con.createStatement().executeUpdate("create table if not exists items(id identity primary key, names varchar(80), shortname varchar(80), description varchar(200),"
        +" modifiers varchar(200), wearable varchar(10), inventory boolean)");
    try(PreparedStatement ps = con.prepareStatement("insert into items(names, shortname, description, modifiers ,wearable ,inventory) values(?,?,?,?,?,?)")){
      // head
      ps.setString(1, "fez,red"); ps.setString(2, "a red fez"); ps.setString(3, "a cute little hat");
      ps.setString(4, "arm:0d0 none"); ps.setString(5, "head"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "cap,leather"); ps.setString(2, "a leather cap"); ps.setString(3, "an old worn leather cap");
      ps.setString(4, "arm:1d1 blunt"); ps.setString(5, "head"); ps.setBoolean(6, false); ps.addBatch();
      // oneHand
      ps.setString(1, "dagger,copper"); ps.setString(2, "a copper dagger"); ps.setString(3, "an old copper dagger");
      ps.setString(4, "dmg:1d4 pierce"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(4, "dmg:1d5 slash"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "sword,long"); ps.setString(2, "a long sword"); ps.setString(3, "a full sized iron blade");
      ps.setString(4, "dmg:1d6 pierce"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "sword,twohanded"); ps.setString(2, "a twohanded sword"); ps.setString(3, "a mid sized iron blade");
      ps.setString(4, "dmg:1d8 slash"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "club,makeshift"); ps.setString(2, "a makeshift club"); ps.setString(3, "a glorified stick");
      ps.setString(4, "dmg:1d4 blunt"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "club,studded"); ps.setString(2, "a studded club"); ps.setString(3, "a iron studded club");
      ps.setString(4, "dmg:1d5 blunt"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "mace,simple"); ps.setString(2, "a simple mace"); ps.setString(3, "an iron mace");
      ps.setString(4, "dmg:1d5 blunt"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();
     // twoHand
      ps.setString(1, "spear,crude"); ps.setString(2, "a crude spear"); ps.setString(3, "a poorly made iron tipped spear");
      ps.setString(4, "dmg:1d6 pierce"); ps.setString(5, "twoHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "spear,broadhead"); ps.setString(2, "a broadhead spear"); ps.setString(3, "a sturdy iron tipped spear");
      ps.setString(4, "dmg:1d8 pierce"); ps.setString(5, "twoHand"); ps.setBoolean(6, false); ps.addBatch();
      ps.setString(1, "sword,short"); ps.setString(2, "a short sword"); ps.setString(3, "a mid sized iron blade");
     // shields
      ps.setString(1, "shield,leather"); ps.setString(2, "a leather shield"); ps.setString(3, "a small leather bound shield"); 
      ps.setString(4, "arm:1d1 blunt"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch(); 
      ps.setString(1, "shield,iron"); ps.setString(2, "a iron shield"); ps.setString(3, "a mid sized iron shield"); 
      ps.setString(4, "arm:1d1 blunt"); ps.setString(5, "oneHand"); ps.setBoolean(6, false); ps.addBatch();  
      
     // Containers
      ps.setString(1, "box,simple"); ps.setString(2, "a simple box"); ps.setString(3, "a small simple wooden box"); 
      ps.setString(4, ""); ps.setString(5, ""); ps.setBoolean(6, true); ps.addBatch();
      ps.setString(1, "chest,iron"); ps.setString(2, "an iron chest"); ps.setString(3, "a medium iron bound chest"); 
      ps.setString(4, ""); ps.setString(5, ""); ps.setBoolean(6, true); ps.addBatch();
      ps.setString(1, "noob,corpse"); ps.setString(2, "a noob's corpse"); ps.setString(3, "a noob adventurer slain by a mere rat"); 
      ps.setString(4, ""); ps.setString(5, ""); ps.setBoolean(6, true); ps.addBatch();
      ps.setString(1, "stump,hollow"); ps.setString(2, "a hollow stump"); ps.setString(3, "a natural and cleaver hiding spot"); 
      ps.setString(4, ""); ps.setString(5, ""); ps.setBoolean(6, true); ps.addBatch();
      
      ps.executeBatch();
    }
  }
  public void generateItems(Connection con) throws SQLException{
    System.out.println("generateItems****************************");
    try(ResultSet rs = con.createStatement().executeQuery("select names, shortname, description, modifiers ,wearable ,inventory from items")){
      while(rs.next()){
        Entity item = new Entity().setNames(rs.getString(1)).setShortName(rs.getString(2)).setDescription(rs.getString(3));
        if(!rs.getString(5).isEmpty()){
          System.out.println(rs.getString(5)); // edit
          item.setWearable(new Wearable().setSlot(rs.getString(5)));
        }
        if(rs.getBoolean(6)) item.setInventory(new Inventory()); // TODO: add weight?
        if(!rs.getString(4).isEmpty()) item.setModifiers(new Modifiers(rs.getString(4)));
        item.setContainer(ROOMS.get(1L));
      }
    }
  }
  public void startUpItemSpawner(Connection con) throws SQLException{
    System.out.println("startUpItemSpawner*************");
    con.createStatement().executeUpdate("drop table if exists itemspawner");
    con.createStatement().executeUpdate("create table itemspawner(id identity primary key, itemid long, roomid long, timer long, container varchar(30))");
    try(PreparedStatement ps = con.prepareStatement("insert into itemspawner(itemid, roomid, timer, container) select id, ?,?,? from items where shortname=?")){
      ps.setLong(1, 3L);
      ps.setLong(2, 60);
      ps.setString(3, ""); // names search
      ps.setString(4, "a simple box");
      ps.addBatch();
      
      ps.setLong(1, 4L);
      ps.setLong(2, 60);
      ps.setString(3, ""); // names search
      ps.setString(4, "a simple box");
      ps.addBatch();
      
      ps.setLong(1, 4L);
      ps.setLong(2, 60);
      ps.setString(3, "box"); // names search
      ps.setString(4, "a makeshift club");
      ps.addBatch();
       
      ps.setLong(1, 6L);
      ps.setLong(2, 60);
      ps.setString(3, ""); // names search
      ps.setString(4, "a noob's corpse");
      ps.addBatch();
      
      ps.setLong(1, 6L);
      ps.setLong(2, 60);
      ps.setString(3, "noob"); // names search
      ps.setString(4, "a copper dagger");
      ps.addBatch();
      
      ps.setLong(1, 14L);
      ps.setLong(2, 60);
      ps.setString(3, ""); // names search
      ps.setString(4, "a hollow stump");
      ps.addBatch();
      
      ps.setLong(1, 14L);
      ps.setLong(2, 60);
      ps.setString(3, ""); // names search
      ps.setString(4, "a crude spear");
      ps.addBatch();
      
      ps.setLong(1, 14L);
      ps.setLong(2, 60);
      ps.setString(3, "stump"); // names search
      ps.setString(4, "a red fez");
      ps.addBatch();
      
      ps.setLong(1, 28L);
      ps.setLong(2, 10);
      ps.setString(3, ""); // names search
      ps.setString(4, "a simple box");
      ps.addBatch();
      
      ps.setLong(1, 28L);
      ps.setLong(2, 10);
      ps.setString(3, "box"); // names search
      ps.setString(4, "a makeshift club");
      ps.addBatch();
     
      ps.executeBatch();
    }
  }
  public void generateItemSpawner(Connection con) throws SQLException{
    System.out.println("generateItemSpawner**************");
    try(ResultSet rs = con.createStatement().executeQuery("select roomid, timer, container, items.* from itemspawner as its join items on items.id=itemid")){
      while(rs.next()){
        System.out.println("***adding "+rs.getString(6)+" to "+rs.getLong(1));
        Entity room = ROOMS.get(rs.getLong(1)); // if no container spawn in room, 
        //Entity container = null;
        if(room==null){
          System.out.println("_______room "+rs.getLong(1)+" does not exist_______");
          continue;
        }
        if(!room.hasSpawner()) room.setSpawner(new Spawner());  // long id,int timer,String description,String shortName,String names
        Definition def = new Definition(rs.getLong(4), rs.getInt(2), rs.getString(7), rs.getString(6), rs.getString(5)).setModifiers(rs.getString(8))
        .setWearable(rs.getString(9)).setContainer(rs.getString(3));
        if(rs.getBoolean(10)) def.setInventory();
        room.getSpawner().addDefinition(def); //container
        System.out.print(def.toString());
      }
    }
  }
  public void startUpMobs(Connection con) throws SQLException{
    System.out.println("startUpMobs****************************");
    con.createStatement().executeUpdate("drop table if exists mobs");
    con.createStatement().executeUpdate("create table if not exists mobs(id identity primary key, names varchar(30), shortname varchar(30), description varchar(60), modifiers varchar(60), motivation varchar(60), stats varchar(60), inventory boolean, equipment boolean)");
    try(PreparedStatement ps = con.prepareStatement("insert into mobs(names, shortname, description, modifiers, motivation, stats, inventory, equipment) values(?,?,?,?,?,?,?,?)")){
    // names, shortname, description, modifiers, maxhealth, stats, inventory, equipment
     //spawner.addDefinition(new Definition(60, 1, "a small green-skinned humanoid", "a goblin", "goblin").setInventory().setStats(goblinStats).setModifiers("1d3 blunt")); 
    ps.setString(1, "rat"); ps.setString(2, "a rat"); ps.setString(3, "its just a rat"); ps.setString(4, "dmg:1d2 gnaw,disease:1 1 4"); 
    ps.setString(5, "t:3,g:0");
    ps.setString(6, "hp:1,con:1,str:1,agi:2,int:1"); ps.setBoolean(7, false); ps.setBoolean(8, false); ps.addBatch();
    ps.setString(1, "goblin"); ps.setString(2, "a goblin"); ps.setString(3, "a green-skined goblin"); ps.setString(4, "dmg:1d2 unarmed");
    ps.setString(5, "t:3,g:-3");
    ps.setString(6, "hp:2,con:1,str:1,agi:2,int:1"); ps.setBoolean(7, false); ps.setBoolean(8, false); ps.addBatch();
    ps.executeBatch();
    
    }
  }
  public void generateMobs(Connection con) throws SQLException{
    System.out.println("generatesMobs****************************");
    try(ResultSet rs = con.createStatement().executeQuery("select names, shortname, description, modifiers, motivation, stats, inventory, equipment from mobs")){
      while(rs.next()){
        Entity mob = new Entity().setNames(rs.getString(1)).setShortName(rs.getString(2)).setDescription(rs.getString(3))
            .setActions(new Actions()).setStats(new Stats(rs.getString(6))).setInventory(new Inventory()).setMotivation(new Motivation(rs.getString(5)));
        System.out.println(rs.getString(2));
        if(!rs.getString(4).isEmpty()) mob.setModifiers(new Modifiers(rs.getString(4)));
        if(!rs.getString(8).isEmpty()) mob.setEquipment(new Equipment());
        mob.setContainer(ROOMS.get(1L));
      }
    }
  }
  public void startUpMobSpawner(Connection con) throws SQLException{
    System.out.println("startUpMobSpawner****************************");
    con.createStatement().executeUpdate("drop table if exists mobspawner");
    con.createStatement().executeUpdate("create table if not exists mobspawner(id identity primary key, mobid long, roomid long, timer long)");
    // mob_id, container_id, timer, item limit
    try(PreparedStatement ps = con.prepareStatement("insert into mobspawner(mobid, roomid, timer) select id, ?, ? from mobs where shortname=?")){
      ps.setLong(1, 5L);
      ps.setLong(2, 60);
      ps.setString(3, "a rat");
      ps.addBatch();
      
      ps.setLong(1, 5L);
      ps.setLong(2, 60);
      ps.setString(3, "a rat");
      ps.addBatch();
      
      ps.setLong(1, 12L);
      ps.setLong(2, 60);
      ps.setString(3, "a rat");
      ps.addBatch();
      
      ps.setLong(1, 27L);
      ps.setLong(2, 50);
      ps.setString(3, "a goblin");
      ps.addBatch();
      ps.executeBatch();
      
      ps.setLong(1, 27L);
      ps.setLong(2, 60);
      ps.setString(3, "a goblin");
      ps.addBatch();
      ps.executeBatch();
      
      ps.setLong(1, 27L);
      ps.setLong(2, 70);
      ps.setString(3, "a goblin");
      ps.addBatch();
      ps.executeBatch();
    }
  }
  public void generateMobSpawner(Connection con) throws SQLException{
    System.out.println("generateMobSpawner****************************");
    try(ResultSet rs = con.createStatement().executeQuery("select roomid, timer, mobs.* from mobspawner as ms join mobs on mobs.id=mobid")){
      while(rs.next()){
        System.out.println("***adding "+rs.getString(4)+" to room "+rs.getLong(1));
        Entity room = ROOMS.get(rs.getLong(1));
        if(room==null){
          System.out.println("_______room "+rs.getLong(1)+" does not exist_______");
          continue;
        }
        if(!room.hasSpawner()) room.setSpawner(new Spawner());
        Definition d = new Definition(rs.getLong(3), rs.getInt(2), rs.getString(6), rs.getString(5), rs.getString(4)).setStats(rs.getString(9)).setModifiers(rs.getString(7))
            .setMotivation(rs.getString(8)).setInventory();               
        room.getSpawner().addDefinition(d);
      }
    }
  }
  public void generateRooms(Connection con) throws SQLException{
    System.out.println("generatesRooms****************************");
    try(ResultSet rs = con.createStatement().executeQuery("select id,name,description from rooms")){
     while(rs.next()){
      ROOMS.put(rs.getLong(1), new Entity().setShortName(rs.getString(2)).setDescription(rs.getString(3)).setInventory(new Inventory().setWeightMax(0))); 
     }
    }
//    System.out.println(ROOMS);
    try(ResultSet rs = con.createStatement().executeQuery("select startroomid,direction,endroomid from exits")){
      while(rs.next()){
//        System.out.println(rs.getLong(1));
        Entity room = ROOMS.get(rs.getLong(1));
        if(!room.hasExits()) room.setExits(new Exits());
        room.getExits().setExit(rs.getString(2), ROOMS.get(rs.getLong(3)));
      }
     }
  }
  @Override
  public void run() { 
    System.out.println("You should only ever be here once! (run)****************************************************");
    Connection con = null;
    
    try{
      con = startUp();
    } catch (ClassNotFoundException | SQLException e1){  
      if(con!=null)
        try {
          con.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      e1.printStackTrace();
      System.exit(0);
    } 

    // ROOMS
    /*
    ROOMS.put(0, new Entity().setShortName("the spawn room").setDescription("to start the tutorial, type 'east'. Or to leave, type 'south'").setInventory(new Inventory().setWeightMax(0)));   
    ROOMS.put(1,new Entity().setShortName("movement").setDescription("you can enter any exits the room has listed. you can also 'look'+(exit) to look before you enter.").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(2,new Entity().setShortName("looking").setDescription("you can look at things in the room as well.\n"
        +" try 'look'+(box)").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(3,new Entity().setShortName("geting").setDescription("to 'get' an item from somthing type 'get'+(the thing you want)+(where you are getting it from)").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(4,new Entity().setShortName("attacking").setDescription("oh noes! St.SpawnRats is living up to his name. try either attack or kill+(thing you want to kill)").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(5,new Entity().setShortName("looting").setDescription("eesh...well that corpse isnt gonna loot itself...at least i hope not...\n"
        +" try 'get'+(item)+(corpse#(1 or 2))").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(6,new Entity().setShortName("equiping").setDescription("type 'i' for inventory. type 'stats' you guessed it..stats\n"+"now ignore the smell and 'wear'+(item)").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(7,new Entity().setShortName("cathedral").setDescription("the entrance to a large ornate cathedral, when you die you will respawn here").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(8,new Entity().setShortName("town square").setDescription("the center of a small town").setInventory(new Inventory().setWeightMax(0)));
    //TODO: set exits & re-order  V
    ROOMS.put(9,new Entity().setShortName("small shop").setDescription("a small trading post").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(10,new Entity().setShortName("bank?").setDescription("a bank maybe?").setInventory(new Inventory().setWeightMax(0)));
    // Forest
    ROOMS.put(11,new Entity().setShortName("entrance to town").setDescription("a large gated entrance to the wooden walls around town").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(12,new Entity().setShortName("plains").setDescription("a grassy plain, with a road running south of town").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(13,new Entity().setShortName("forests edge").setDescription("where sparse trees begin thicken").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(14,new Entity().setShortName("ne.forest").setDescription("a fair amount of trees, still allow sunlight through").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(15,new Entity().setShortName("n.forest").setDescription("the canopy blocks out alot of light").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(16,new Entity().setShortName("e.forest").setDescription("dense forest").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(17,new Entity().setShortName("c.forest").setDescription("the deepest darkest part of the forest").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(18,new Entity().setShortName("s.forest").setDescription("the canopy blocks out alot of light").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(19,new Entity().setShortName("w.forest").setDescription("the canopy blocks out alot of light").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(20,new Entity().setShortName("goblin den").setDescription("the canopy blocks out alot of light").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(21,new Entity().setShortName("small pond").setDescription("a road winds around a small body of water").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(22,new Entity().setShortName("road").setDescription("a road").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(23,new Entity().setShortName("marsh").setDescription("a damp marsh bog").setInventory(new Inventory().setWeightMax(0)));
    ROOMS.put(24,new Entity().setShortName("swamp").setDescription("a muddy sludge forest").setInventory(new Inventory().setWeightMax(0)));
    
    ROOMS.put(30,new Entity().setShortName("ambush ally").setDescription("im sure the name is meaningless...").setInventory(new Inventory().setWeightMax(0)));
    
     // Exits
    ROOMS.get(0).setExits(new Exits().setExit("east", ROOMS.get(1)).setExit("south", ROOMS.get(7)));
    ROOMS.get(1).setExits(new Exits().setExit("east", ROOMS.get(2)));
    ROOMS.get(2).setExits(new Exits().setExit("east", ROOMS.get(3)));
    ROOMS.get(3).setExits(new Exits().setExit("east", ROOMS.get(4)));
    ROOMS.get(4).setExits(new Exits().setExit("east", ROOMS.get(5)));
    ROOMS.get(5).setExits(new Exits().setExit("east", ROOMS.get(6)));
    ROOMS.get(6).setExits(new Exits().setExit("east", ROOMS.get(7)));
    ROOMS.get(7).setExits(new Exits().setExit("south", ROOMS.get(8)));
    ROOMS.get(8).setExits(new Exits().setExit("east", ROOMS.get(10)).setExit("south",ROOMS.get(11)).setExit("west", ROOMS.get(9)).setExit("north", ROOMS.get(7)));
    ROOMS.get(9).setExits(new Exits().setExit("east", ROOMS.get(8)));
    ROOMS.get(10).setExits(new Exits().setExit("west", ROOMS.get(8)));
    ROOMS.get(11).setExits(new Exits().setExit("south", ROOMS.get(12)).setExit("north", ROOMS.get(8)));
    ROOMS.get(12).setExits(new Exits().setExit("west", ROOMS.get(13)).setExit("north", ROOMS.get(11)).setExit("south", ROOMS.get(21)));
    ROOMS.get(13).setExits(new Exits().setExit("south", ROOMS.get(14)).setExit("east", ROOMS.get(12)));
    ROOMS.get(14).setExits(new Exits().setExit("south", ROOMS.get(16)).setExit("west", ROOMS.get(15)).setExit("north", ROOMS.get(13)).setExit("east", ROOMS.get(21)));
    ROOMS.get(15).setExits(new Exits().setExit("south", ROOMS.get(17)).setExit("east", ROOMS.get(14)));
    ROOMS.get(16).setExits(new Exits().setExit("west", ROOMS.get(17)).setExit("north", ROOMS.get(14)).setExit("east", ROOMS.get(23)).setExit("south", ROOMS.get(24)));
    ROOMS.get(17).setExits(new Exits().setExit("south", ROOMS.get(18)).setExit("west", ROOMS.get(19)).setExit("north", ROOMS.get(15)).setExit("east", ROOMS.remove(16)));
    ROOMS.get(18).setExits(new Exits().setExit("west", ROOMS.get(20)).setExit("north", ROOMS.get(17)).setExit("east", ROOMS.get(24)));
    ROOMS.get(19).setExits(new Exits().setExit("south", ROOMS.get(20)).setExit("east", ROOMS.get(17)));
    ROOMS.get(20).setExits(new Exits().setExit("north", ROOMS.get(19)).setExit("east", ROOMS.get(18)));
    ROOMS.get(21).setExits(new Exits().setExit("east", ROOMS.get(22)).setExit("south",ROOMS.get(23)).setExit("west", ROOMS.get(14)).setExit("north", ROOMS.get(12)));
    ROOMS.get(22).setExits(new Exits().setExit("west", ROOMS.get(21)));
    ROOMS.get(23).setExits(new Exits().setExit("north", ROOMS.get(21)).setExit("west", ROOMS.get(24))); // TODO: fix me!
    //ROOMS.get(23).setExits(new Exits().setExit("north",ROOMS.get(21)).setExit("west", ROOMS.get(16)));
    ROOMS.get(24).setExits(new Exits().setExit("east", ROOMS.get(23)).setExit("west", ROOMS.get(18)).setExit("north", ROOMS.get(16)));
    
    Entity piper = new Entity().setNames("corpse").setShortName("a rat gnawed corpse").setDescription("he was a well know piper").setInventory(new Inventory().setWeightMax(0));
    Entity emptyBox = new Entity().setNames("simple","box").setShortName("a simple box").setDescription("an empty box").setInventory(new Inventory().setWeightMax(0));
    Entity box = new Entity().setNames("simple","box").setShortName("a simple box").setDescription("a wooden box").setInventory(new Inventory().setWeightMax(0));
    
    emptyBox.setContainer(ROOMS.get(2));
    box.setContainer(ROOMS.get(3));
    Spawner spawner = new Spawner();
    box.setSpawner(spawner);
    spawner.addDefinition(new Definition(40,1,"a leather cap","a cap","cap").setWearable("head"));
    spawner = new Spawner();
    piper.setContainer(ROOMS.get(5));
    piper.setSpawner(spawner);
    spawner.addDefinition(new Definition(40,1,"a simple flute","a flute","flute").setWearable("oneHand").setModifiers("1d3 blunt"));
    spawner = new Spawner();
    ROOMS.get(4).setSpawner(spawner);
    Map<String, Integer> ratStats = new HashMap<>();
    ratStats.put("maxHealth", 1);
    spawner.addDefinition(new Definition(20,1,"a plain ol' rat","a rat","rat").setInventory().setStats(ratStats).setModifiers("1d1 slash"));
    */
    /*
    room1.setExits(new Exits().setExit("south", spawnRoom));
    spawnRoom.setExits(new Exits().setExit("north",room1));
    //System.out.println(spawnRoom.getExits().getExit(new Exits().setExit("north",room1).toString()));
    Entity room2 = new Entity().setShortName("a forest room").setDescription("a wooded area").setInventory(new Inventory().setWeightMax(0))
        .setExits(new Exits().setExit("south", room1));   
    
    Entity lootBox = new Entity().setNames("simple","box").setShortName("a simple box").setInventory(new Inventory().setWeightMax(0));
      lootBox.getInventory().add(tophat).add(dagger).add(zweihander);
    
    spawnRoom.getExits().setExit("north", room1); //add text via console
    //spawnRoom.getInventory().add(lootBox);
    lootBox.setContainer(room1);
    room1.getExits().setExit("north", room2);
    room2.getExits().setExit("south", room1).setExit("north", goblinDen);
    goblinDen.getExits().setExit("south", room2);
    ENTITIES.put(pid++, goblinDen);
    ENTITIES.put(pid++, room1);
    Spawner spawner = new Spawner();
    goblinDen.setSpawner(spawner);
    Map<String,Integer> goblinStats = new HashMap<>();
    goblinStats.put("maxHealth", 7);
    goblinStats.put("xp",1);
    spawner.addDefinition(new Definition(60, 1, "a small green-skinned humanoid", "a goblin", "goblin").setInventory().setStats(goblinStats).setModifiers("1d3 blunt"));
    goblinStats.put("maxHealth", 10);
    goblinStats.put("xp",5);
    spawner.addDefinition(new Definition(80, 1, "a beefy orange-skinned humanoid", "a hobgoblin", "goblin","hobgoblin").setInventory().setStats(goblinStats).setModifiers("1d6 blunt"));
    spawner = new Spawner();
    room1.setSpawner(spawner);
    spawner.addDefinition(new Definition(40, 0, "a small pointy blade", "a dagger", "dagger").setWearable("oneHand").setModifiers("1d4 pierce"));
    */
    /*
    ENTITIES.put(pid++, goblin);
    ENTITIES.put(pid++, goblin2);
    goblin.setContainer(room2);
    goblin.setSpawnRoom(room2);
    goblin.getEquipment().wear(spear);
    goblin2.setContainer(room2);
    goblin2.setSpawnRoom(room2);
    goblin2.getEquipment().wear(spear);
    */
    try{
      while(true){
       Thread.sleep(500); //TODO: adjust timer
       for(Entity e: ROOMS
           .values()){ 
         e.update();
       }
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
  }

  public void execute(Session ses) {
   
  }
  
}
