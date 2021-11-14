package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamColors;
import me.camm.productions.bedwars.Files.FileKeywords.DataSeparatorKeys;
import me.camm.productions.bedwars.Files.FileKeywords.TeamFileKeywords;
import me.camm.productions.bedwars.Generators.Forge;
import me.camm.productions.bedwars.Util.Helpers.StringToolBox;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.Locations.GameBoundary;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TeamFileReader extends StringToolBox
{
    private ArrayList<BattleTeam> teams;
    private Forge currentForge;
    private Coordinate currentForgeCoordinate;
    private long currentTime;
    private Coordinate currentTeamSpawn;
    private GameBoundary currentTeamBed;
    private Coordinate currentTeamChest;
    private GameBoundary currentTeamBox;
    private Coordinate currentTeamQuick;
    private Coordinate currentTeamGroup;
    private GameBoundary aura;
    private GameBoundary trap;

    private Plugin plugin;
  //  private String path;
    private TeamColors color;
    private final Arena arena;


   public TeamFileReader(Plugin plugin, Arena arena)
   {
       super(plugin);
       this.plugin = plugin;
       teams = new ArrayList<>();
      // this.path = path + Paths.TEAMS.value;
       this.arena = arena;
       this.color = null;
   }

   public ArrayList<BattleTeam> read()
   {
       ArrayList<BattleTeam> teams = new ArrayList<>();
       try {
           BufferedReader reader = new BufferedReader(new FileReader(getTeamPath()));
           ArrayList<String> values = new ArrayList<>();
           String currentLine = reader.readLine();
           while (currentLine!=null)
           {
              currentLine =  checkForComments(currentLine);
              if (currentLine!=null)
                  values.add(currentLine);
               currentLine = reader.readLine();
           }
           reader.close();

           TeamColors[] colors = TeamColors.values();


           int index = 0;
           for (String string: values)
           {
              TeamFileKeywords key = getKeyword(string);
              if (key==null)
                  continue;

              if (index!=key.getIndex())
                  continue;

              switch (key)
              {
                  case TEAM_GENERATE:
                     String color = getInfoSection(string);
                     if (color==null)
                         continue;

                     for (TeamColors currentColor: colors)
                     {
                         if (currentColor.getName().equalsIgnoreCase(color)) {
                             this.color = currentColor;
                             index = key.getIndex()+1;
                             break;
                         }
                     }

                      break;

                  case FORGE_TIME:
                      int[] time = doubleToIntArray(getNumbers(string));
                      if (time.length!=1)
                          continue;

                      if (time[0]<=0)
                          continue;

                      currentTime = time[0];
                          index = key.getIndex()+1;

                      break;
                      //  public Forge(double x, double y, double z, World world, String color, int initialTime, Plugin plugin)
                  case FORGE_SPAWN:
                      currentForgeCoordinate = new Coordinate(getNumbers(string));

                      if (currentTime>0&&this.color!=null) {
                          currentForge = new Forge(currentForgeCoordinate.getX(),currentForgeCoordinate.getY(),currentForgeCoordinate.getZ(),arena.getWorld(),this.color, currentTime,plugin);
                          index = key.getIndex() + 1;
                          continue;
                      }
                      index = 0;

                      break;

                  case SPAWN:
                      currentTeamSpawn = new Coordinate(getNumbers(string));
                      index = key.getIndex()+1;
                      break;

                  case BED:
                      currentTeamBed = new GameBoundary(doubleToIntArray(getNumbers(string)));
                      index = key.getIndex()+1;
                      break;

                  case CHEST:
                      currentTeamChest = new Coordinate(getNumbers(string));
                      index = key.getIndex()+1;
                      break;

                  case QUICK_BUY:
                      currentTeamQuick = new Coordinate(getNumbers(string));
                      index = key.getIndex()+1;
                      break;

                  case TEAM_BUY:
                      currentTeamGroup = new Coordinate(getNumbers(string));
                      index = key.getIndex()+1;
                      break;

                  case REGISTERED_BOUNDS:
                      currentTeamBox = new GameBoundary(doubleToIntArray(getNumbers(string)));
                      index = key.getIndex()+1;
                      break;

                  case AURA:
                      aura = new GameBoundary(doubleToIntArray(getNumbers(string)));
                      index = key.getIndex()+1;
                      break;

                  case TRIGGER_AREA:
                      index = 0;
                      trap = new GameBoundary(doubleToIntArray(getNumbers(string)));
                      if (currentForge!=null&&this.color!=null)
                      teams.add(new BattleTeam(arena,this.color,currentForge,currentTeamSpawn,currentTeamBed,currentTeamChest,currentTeamQuick,currentTeamGroup,currentTeamBox,aura,trap));
                      break;

                      // public BattleTeam(Arena arena, ColorsREFACTORED teamColor, Forge forge, Coordinate teamSpawn, RegisteredBoundary bed, Coordinate chest, Coordinate quickBuy, Coordinate teamBuy, RegisteredBoundary unbreakable, RegisteredBoundary aura, RegisteredBoundary trapArea)
              }
           }
       }
       catch (IOException ignored)
       {

       }
      return teams;
   }

   private TeamFileKeywords getKeyword(String original)
   {
       int index = original.indexOf(DataSeparatorKeys.DECLARATION.getKey());
       return index==-1? null: toTeamKey((original.substring(0,index)).trim());
   }


    private TeamFileKeywords toTeamKey(String dissected)
    {
        TeamFileKeywords[] words = TeamFileKeywords.values();
        for (TeamFileKeywords word: words) {
            if (word.getKey().equalsIgnoreCase(dissected))
                return word;
        }
        return null;
    }


    public void printTeamErrorMessage(String errorWhere)
    {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"---Error Report---");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"Could not initialize a team variable at the line:");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+""+errorWhere+" <-- Here");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"Please make sure that the file is configured correctly.");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"---End Of Report---");

    }

    public void printColorMessage()
    {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [Notice]: Please note that the colors supported for the plugin are as follows:");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"Red, Blue, Green, Yellow, Aqua, White, Pink, Gray");
    }

    public void printForgeMessage()
    {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [Notice]: The forge time must be a number greater than 0.");
    }
   }



/*
TeamGenerate: blue
Forge: [1] [1] [1]
Time: [1000]
TeamSpawn: [-1] [-2] [-2] [0]
TeamBed: [2,2] [2,2] [2,2]
TeamChest: [-3] [-3] [-3]
TeamBox: [4,5] [6,7] [8,9]
QuickBuy: [10] [11] [12] [13]
TeamBuy: [14] [15] [16] [17]
*/

/*

    public ArrayList<BattleTeam> readFiles()
    {
        boolean isColorValid = false;
        boolean isForgeValid = false;
        boolean isTimeValid = false;
        boolean isSpawnValid = false;
        boolean isBedValid = false;
        boolean isChestValid = false;
        boolean isBoxValid = false;
        boolean isQuickValid = false;

        try
        {
            reader = new BufferedReader(new FileReader(new File(path)));
            String currentLine = "";
            String currentStatement;

            while (currentLine!=null)
            {
                currentStatement =  new DissectString(currentLine,plugin).readComma(currentLine);

                // System.out.println(currentStatement);
                switch (currentStatement)
                {

                    case KeyWords.TEAM_CREATE:
                    {
                        isColorValid = true;
                        // System.out.println("Team");
                        currentColor = new DissectString(currentLine,plugin).arrayListToString(new DissectString(currentLine,plugin).partition());

                        currentColor = new DissectString(currentLine,plugin).verifyColor(currentColor);
                        if (currentColor==null)
                        {
                            isColorValid = false;
                            printColorMessage();
                          printTeamErrorMessage(currentLine);

                        }

                    }
                    break;

                    case KeyWords.TIME:
                    {
                        if (isColorValid)
                        {
                            isTimeValid = true;
                            currentTime = (long)(new DissectString(currentLine,plugin).translateDouble(new DissectString(currentLine,plugin).partition()));

                            System.out.println(currentTime);
                            if (currentTime<=0)
                            {
                             printTeamErrorMessage(currentLine);
                               printForgeMessage();
                                isColorValid = false;
                                isTimeValid = false;
                            }

                        }

                    }
                    break;

                    case KeyWords.FORGE:
                    {
                        if (isTimeValid)
                        {
                            isTimeValid = false;
                            Coordinate forgeCoordinate = new DissectString(currentLine,plugin).stringToCoordinate(new DissectString(currentLine,plugin).partition());
                            if (forgeCoordinate!=null)
                            {

                                isForgeValid = true;
                                //(double x, double y, double z, World world, String color, int initialTime, Plugin plugin)
                                currentForge = new Forge(forgeCoordinate.getX(),forgeCoordinate.getY(),forgeCoordinate.getZ(),arena.getWorld(),currentColor,(int)currentTime,plugin);
                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }

                        }
                    }
                    break;

                    case KeyWords.TEAM_SPAWN:
                    {
                        if (isForgeValid)
                        {
                            isForgeValid = false;
                            currentTeamSpawn = new DissectString(currentLine,plugin).stringToCoordinate(new DissectString(currentLine,plugin).partition());

                            if (currentTeamSpawn!=null)
                            {

                                isSpawnValid = true;
                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }


                        }
                    }
                    break;

                    case KeyWords.TEAM_BED:
                    {
                        if (isSpawnValid)
                        {

                            isSpawnValid = false;
                            currentTeamBed = new DissectString(currentLine,plugin).stringsToBound(new DissectString(currentLine,plugin).partition());

                            if (currentTeamBed!=null)
                            {
                                isBedValid = true;

                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }

                        }
                    }
                    break;

                    case KeyWords.TEAM_CHEST:
                    {
                        if (isBedValid)
                        {
                            isBedValid = false;
                            currentTeamChest = new DissectString(currentLine,plugin).stringToCoordinate(new DissectString(currentLine,plugin).partition());

                            if (currentTeamChest!=null)
                            {
                                isChestValid = true;
                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }
                        }
                    }
                    break;

                    case KeyWords.TEAM_BOX:
                    {
                        if (isChestValid)
                        {
                            isChestValid = false;
                            currentTeamBox = new DissectString(currentLine,plugin).stringsToBound(new DissectString(currentLine,plugin).partition());

                            if (currentTeamBox!=null)
                            {
                                isBoxValid = true;
                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }

                        }
                    }
                    break;

                    case KeyWords.QUICK_BUY:
                    {
                        if (isBoxValid)
                        {

                            isBoxValid = false;
                            currentTeamQuick = new DissectString(currentLine,plugin).stringToCoordinate(new DissectString(currentLine,plugin).partition());

                            if (currentTeamQuick!=null)
                            {
                                isQuickValid = true;
                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }
                        }
                    }
                    break;

                    case KeyWords.TEAM_BUY:
                    {
                        if (isQuickValid)
                        {
                            isQuickValid = false;
                            currentTeamGroup = new DissectString(currentLine,plugin).stringToCoordinate(new DissectString(currentLine,plugin).partition());

                            if (currentTeamGroup!=null)
                            {
                                //public BattleTeam(String color, Forge forge, BoundRegister bed, Coordinate chest, BoundRegister box, Coordinate quickBuy, Coordinate teamBuy)
                                teams.add(new BattleTeam(currentColor,currentForge,currentTeamBed,currentTeamChest,currentTeamBox,currentTeamQuick,currentTeamGroup,currentTeamSpawn,arena));
                            }
                            else
                            {
                                printTeamErrorMessage(currentLine);
                            }
                        }
                    }
                    break;
                }
                currentLine = reader.readLine();
            }

            if (teams.size()!=0)
            {
                return teams;
            }
            return null;

        }
        catch(FileNotFoundException exception)
        {
            exception.printStackTrace();
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR]: Could not find the Teams data file. [Where did it go?]");
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }//method
 */
