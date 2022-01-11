package me.camm.productions.bedwars.Arena.GameRunning;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Generators.Generator;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import me.camm.productions.bedwars.Util.Locations.RegisterType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader.*;
import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.ARENA;
import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.MAP;


public class Arena
{
  private final GameBoundary bounds;
  private final int voidLevel;
  private final HashMap<String, BattleTeam> teams;  //the string is the team color
  private ArrayList<Generator> generators;
  private final World world;
 // private boolean isRunning;
  private final Plugin plugin;
  private final Location specSpawn;

  private static int playerAssignment;

 // private final HashMap<String, GameItem> quickBuyItems;

  private final Scoreboard healthBoard;
  private final Objective nameHealth;
  private final Objective tabHealth;


  private volatile ConcurrentHashMap<UUID, BattlePlayer> players;

  //we can't have a single scoreboard for each team because each board is different for each team, but we can have one
    //for displaying the players online.

  public Arena(GameBoundary bounds, Coordinate spectatorSpawn, int voidLevel, World world, Plugin plugin)
  {
     //  this.teams = new HashMap<>();

      playerAssignment = 0;
      this.bounds = bounds;
      this.voidLevel = voidLevel;
      //this.quickBuyItems = setShopItems();

      this.world = world;
      this.plugin = plugin;

      this.players = new ConcurrentHashMap<>();
      this.teams = new HashMap<>();

          specSpawn = spectatorSpawn.getAsLocation(world);

          healthBoard = Bukkit.getScoreboardManager().getNewScoreboard();

         nameHealth = healthBoard.registerNewObjective(HEALTH_CATEGORY.getPhrase(),HEALTH_CRITERIA.getPhrase());
         nameHealth.setDisplayName(HEART.getPhrase());
         nameHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);

      tabHealth = healthBoard.registerNewObjective(HEALTH_CATEGORY_TWO.getPhrase(),HEALTH_CRITERIA.getPhrase());
      tabHealth.setDisplayName(HEALTH_CATEGORY.getPhrase());
      tabHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);


     Score initOne = nameHealth.getScore(INITIALIZER_ONE.getPhrase()); //board must have atleast 1 score to display properly
      initOne.setScore(1);
      Score initTwo = tabHealth.getScore(INITIALIZER_TWO.getPhrase());
      initTwo.setScore(1);

  }

    private HashMap<String, ShopItem> setShopItems()
    {
        HashMap<String, ShopItem> shopItems = new HashMap<>();

        for (ShopItem item: ShopItem.values())
        {
            if (!shopItems.containsKey(item.name))
                shopItems.put(item.name, item);
        }
        return shopItems;
    }

    public synchronized void removePlayer(UUID uuid)
    {
        if (players.containsKey(uuid))
        {
            BattlePlayer player = players.get(uuid);
            player.getTeam().removePlayer(player.getRawPlayer()); // infinite recursion???
            players.remove(uuid);
        }
    }

    public synchronized void addPlayer(UUID uuid, BattlePlayer player)
    {
        players.put(uuid, player);
    }

  public void addTeams(ArrayList<BattleTeam> list)
  {
      for (BattleTeam team: list)
      {
          if (team!=null)
              this.teams.put(team.getColor().getName(),team);
      }
  }

  public ArrayList<BattleTeam> getTeamList()
  {
      ArrayList<BattleTeam> list = new ArrayList<>();
      teams.forEach((String, BattleTeam)->
              list.add(BattleTeam));
      return list;
  }


  public synchronized int assignPlayerNumber()
  {
      playerAssignment++;
      return playerAssignment;
  }

  public void registerMap()
  {
      //(World world, String type, Plugin plugin, Material notRegister
      bounds.register(world, ARENA.getData(), RegisterType.EVERYTHING.getType(),plugin); //registering the playable area
     bounds.registerButNotBlockOrAir(world,MAP.getData(),plugin,Material.BED_BLOCK);  //registering all blocks to map blocks except for beds


      for (Generator generator: generators)
          generator.registerBox();
  }

  public void registerTeamZones()
  {
      teams.forEach((String,team) -> team.registerBase());
  }

  public int[] getTeamColorsAsInt()
  {
      ArrayList<Integer> values = new ArrayList<>();

      teams.forEach((String, team) ->  //getting colors from each team
              values.add(team.getColor().getValue())
  );
      int[] arrayValues = new int[values.size()];  //transferring to int array

      for (int slot=0;slot<values.size();slot++)
          arrayValues[slot] = values.get(slot);

      return arrayValues;
  }

  public void hideEliminated(){
      for (BattlePlayer player: players.values())
          player.hideEliminatedPlayers();
  }


  public void sendMessage(String message)
  {
      plugin.getServer().broadcastMessage(message);
  }

    public Scoreboard getHealthBoard()
    {
        return healthBoard;
    }

    public HashMap<String, BattleTeam> getTeams()
    {
        return teams;
    }

    public ArrayList<Generator> getGenerators()
    {
        return generators;
    }

    public void setGenerators(ArrayList<Generator> generators)
    {
        this.generators = generators;
    }

    public World getWorld()
    {
        return world;
    }

    public Plugin getPlugin()
    {
        return plugin;
    }

    public synchronized ConcurrentHashMap<UUID, BattlePlayer> getPlayers()
    {
        return players;
    }

    public synchronized void setPlayers(ConcurrentHashMap<UUID,BattlePlayer> players)
    {
        this.players = players;
    }

    public int getVoidLevel()
    {
        return this.voidLevel;
    }

    public Location getSpecSpawn()
    {
        return specSpawn;
    }

    public Objective getNameHealth() {
        return nameHealth;
    }

    public Objective getTabHealth() {
        return tabHealth;
    }

    public GameBoundary getBounds(){
      return bounds;
    }
}







/*
public void print()
    {
        System.out.println("Void level: "+voidLevel);
        System.out.print("Bounds:");
        printArray(bounds);
        System.out.print("\n");
        System.out.print("HubSpawn: ");
        printArray(hubSpawn);
        System.out.print("\n");
        System.out.print("SpectatorSpawn: ");
        printArray(spectatorSpawn);
        System.out.println("\n\n");
        printGenerators();
    }
//testing
    private void printArray(int [] array)
    {
        for (int slot=0;slot<array.length;slot++)
        {
            System.out.print(array[slot]+" ");
        }
    }
   //testing
    private void printArray(double [] array)
    {
        for (int slot=0;slot<array.length;slot++)
        {
            System.out.print(array[slot]+" ");
        }
    }

    //testing
    private void printGenerators()
    {
        for (int slot=0;slot<generators.size();slot++)
        {
            System.out.println("\n\nGenerator #"+slot+"||"+generators.get(slot).getX()+"||"+generators.get(slot).getY()+"||"+generators.get(slot).getZ());
            System.out.println("Generator #"+slot+"|| type: "+generators.get(slot).getType());

            int[] blocks = generators.get(slot).getBox().getBounds();
            // System.out.println("Size: "+blocks.length);

            System.out.print("Generator #"+slot+" Box: ");
            for (int size=0;size<blocks.length;size++)
            {
                System.out.print(blocks[size]+" || ");
            }

        }
    }
 */
