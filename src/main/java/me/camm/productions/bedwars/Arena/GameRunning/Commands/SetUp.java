package me.camm.productions.bedwars.Arena.GameRunning.Commands;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Files.FileStreams.TeamFileReader;
import me.camm.productions.bedwars.Files.FileStreams.WorldFileReader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;


public class SetUp implements CommandExecutor
{
    private boolean isSetUp;
    private final Plugin plugin;
    private static Arena arena = null;
   // private final String path;
    private GameRunner runner;
    private Inventory joinInventory;
    private boolean isGameRunning;

    public SetUp(Plugin plugin)
    {
       this.plugin = plugin;
       this.isSetUp = false;
     //  this.path = path+ Paths.MAIN.value;
       this.isGameRunning = false;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Must be a player to use this command.");
            return true;
        }

        if (!isSetUp&&label.equalsIgnoreCase("setup"))
        {
                //public ReadWorldFile(World world, String path, Plugin plugin)
            ArrayList<BattleTeam> teams;
                WorldFileReader fileReader = new WorldFileReader(plugin);
                arena = fileReader.read();

                if (arena!=null)
                {

                    teams = new TeamFileReader(plugin, arena).read();

                    plugin.getServer().broadcastMessage(ChatColor.AQUA+"[BEDWARS] Registering the map. Expect some lag.");
                    arena.registerMap();
                    if (teams!=null&&teams.size()!=0)
                    {
                        arena.addTeams(teams);
                       runner = new GameRunner(plugin, arena);
                       arena.registerTeamZones();

                        plugin.getServer().getPluginManager().registerEvents(runner, plugin);
                        this.joinInventory = runner.getJoinInventory();
                        this.isSetUp = true;
                        plugin.getServer().broadcastMessage(ChatColor.GREEN+"[BEDWARS] Map is registered! Do /register to join teams.");
                    }
                    else
                    {
                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] Could not initialize teams. Make sure the teams are configured correctly. [TEAMS DNE]");

                       if (teams==null) {
                           sendStackTrace(true);
                       }
                       else
                       {
                          sendStackTrace(teams.size(),false);
                       }
                    }
                }
                else
                {
                   plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] Could not Initialize the Arena. Please make sure that the configuration is initialized. [ARENA DNE]");
                }

        }
        else if (isSetUp) //if the arena is setup
        {
            if (label.equalsIgnoreCase("register"))
            {
                if (runner!=null&&joinInventory!=null)
                {
                    if (!isGameRunning)
                        ((Player) sender).openInventory(joinInventory);
                    else
                        sender.sendMessage(ChatColor.RED+"Please wait for the current game to finish first!");
                }
                else
                {
                    if (runner==null)
                        sender.sendMessage("[DEBUG] TJ is null");

                    if (joinInventory==null)
                        sender.sendMessage("[DEBUG] INV is null");

                    sender.sendMessage("[DEBUG]Is game running: "+isGameRunning+" setup: "+isSetUp);
                }

            }
            else if (label.equalsIgnoreCase("start"))
            {
                int notOpposed;

                if (!isGameRunning)
                {
                    Collection<BattleTeam> values = arena.getTeams().values();

                    notOpposed = values.size();
                    for (BattleTeam team: values)
                    {
                        if (team.getRemainingPlayers()==0)
                            notOpposed--;
                    }

                    System.out.println("[DEBUG]Not Opposed:"+notOpposed);


  /////////////////////////////////////////////////////////////////
            //The check for opposing teams is disabled for testing purposes only.

                  //  if (!(notOpposed<2)) //game can start b/c there are at least 2 teams
                 //   {
                        isGameRunning = true;
                        sender.sendMessage("[DEBUG] Invoked run");
                         runner.prepareAndStart();
                         //PrepareInst() sets running = true and also starts the game

                  //  }
                   // else {
                  //      sender.sendMessage(ChatColor.YELLOW + "The game cannot start without opposition!");

                  //  }
        /////////////////////////////////////////////////////////////////////



                }
                else
                    sender.sendMessage(ChatColor.YELLOW+"The game is already running!");
            }
            else if (label.equalsIgnoreCase("setup"))
                sender.sendMessage(ChatColor.YELLOW+"The arena already is set up!");
        }
        else
            sender.sendMessage(ChatColor.RED+"Setup the arena first!");


        return true;
    }




    public Arena getArena()
    {
        return arena;
    }

    public void sendStackTrace( boolean isNull)
    {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] ---  REPORT ---");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] - Nature: "+ isNull);
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] ---  REPORT ---\n");

    }

    public void sendStackTrace(int size, boolean isNull)
    {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] ---  REPORT ---");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] - Nullable: "+ isNull);
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] - Size: "+ size);
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"[BEDWARS] [STACK] ---  REPORT ---\n");

    }

    public GameRunner getRunner()
    {
        return runner;
    }
}


