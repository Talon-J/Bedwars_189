package me.camm.productions.bedwars.Arena.GameRunning.Commands;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamColors;
import me.camm.productions.bedwars.Files.FileStreams.TeamFileReader;
import me.camm.productions.bedwars.Files.FileStreams.WorldFileReader;

import me.camm.productions.bedwars.Validation.BedWarsException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class GameIntializer implements CommandExecutor
{
    private boolean isSetUp;
    private final Plugin plugin;
    private static Arena arena = null;
    private GameRunner runner;
    private Inventory joinInventory;
    private boolean isGameRunning;

    public GameIntializer(Plugin plugin)
    {
       this.plugin = plugin;
       this.isSetUp = false;
       this.isGameRunning = false;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Must be a player to use this command.");
            return true;
        }

        label = label.toLowerCase().trim();
        CommandKeyword word = null;
        for (CommandKeyword keyword: CommandKeyword.values()) {
            if (keyword.getWord().equalsIgnoreCase(label)) {
                word = keyword;
                break;
            }
        }

        if (word  == null) {
            return false;
        }



        switch (word) {
            case SETUP:

                if (!sender.hasPermission("setup.do"))
                    return true;

                if (isSetUp) {
                    sender.sendMessage(ChatColor.YELLOW+"The arena already is set up!");
                    return true;
                }

            ArrayList<BattleTeam> teams;
            WorldFileReader fileReader = new WorldFileReader(plugin);

            try {
                arena = fileReader.read();
            }
            catch (BedWarsException e) {
                broadcastMessage(e.getMessage());
                return true;
            }

            if (arena!=null)
            {

                broadcastMessage(ChatColor.AQUA+"[BEDWARS] Registering the map. Expect some lag.");

                try {
                    teams = new TeamFileReader(plugin, arena).read();
                }
                catch (BedWarsException e) {
                    plugin.getServer().broadcastMessage(e.getMessage());
                     return true;
                }

                arena.registerMap();
                if (teams!=null&&teams.size()!=0)
                {
                    arena.addTeams(teams);
                    runner = new GameRunner(plugin, arena);
                    arena.registerTeamZones();

                    plugin.getServer().getPluginManager().registerEvents(runner, plugin);
                    this.joinInventory = runner.getJoinInventory();
                    this.isSetUp = true;
                   broadcastMessage(ChatColor.GREEN+"[BEDWARS] Map is registered! Do /register to join teams.");

                }
                else
                    broadcastMessage(ChatColor.RED+"[BEDWARS] Could not initialize teams. Make sure the teams are configured correctly. [TEAMS DNE]");

            }
            else
                broadcastMessage(ChatColor.RED+"[BEDWARS] Could not Initialize the Arena. Please make sure that the configuration is initialized. [ARENA DNE]");
            break;


            case SHOUT:

                if (!sender.hasPermission(CommandKeyword.SHOUT.getPerm())) {
                    return true;
                }

               if (!isGameRunning) {
                   sender.sendMessage(ChatColor.YELLOW+"The game is not running!");
                   return true;
               }

               if (arena == null) {
                   sender.sendMessage(ChatColor.YELLOW+"Your message could not be delivered since the arena does not exist!");
                   return true;
               }

                ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
               BattlePlayer current = players.getOrDefault(((Player) sender).getUniqueId(),null);
               if (current == null)
                   return true;

               StringBuilder message = new StringBuilder();
               for (String string: args) {
                   message.append(string).append(" ");
               }

               TeamColors color = current.getTeam().getTeamColor();
               broadcastMessage(ChatColor.YELLOW+"[SHOUT]"+color.getChatColor()+"["+color.getName()+"]"+
                color.getChatColor()+"<"+current.getRawPlayer().getName()+">"+ChatColor.GRAY+message);

                break;

            case START:
                if (!sender.hasPermission(CommandKeyword.START.getPerm()))
                    return true;


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


                    /////////////////////////////////////////////////////////////////
                    //The check for opposing teams is disabled for testing purposes only.

                    //   if (!(notOpposed<2)) //game can start b/c there are at least 2 teams
                    //   {
                    isGameRunning = true;
                    runner.prepareAndStart();

                    //     }
                    //    else {
                    //       sender.sendMessage(ChatColor.YELLOW + "The game cannot start without opposition!");

                    //   }
                    /////////////////////////////////////////////////////////////////////



                }
                else
                    sender.sendMessage(ChatColor.YELLOW+"The game is already running!");
                break;

            case REGISTER:
                if (!sender.hasPermission(CommandKeyword.REGISTER.getPerm()))
                    return true;

                if (runner!=null&&joinInventory!=null)
                {
                    if (!isGameRunning)
                        ((Player) sender).openInventory(joinInventory);
                    else
                        sender.sendMessage(ChatColor.RED+"Please wait for the current game to finish first!");
                }
                break;

            case UNREGISTER:
                if (!sender.hasPermission(CommandKeyword.UNREGISTER.getPerm()))
                    return true;



                if (isGameRunning){
                    sender.sendMessage(ChatColor.RED+"Sorry bud, game's already running.");
                    return true;
                }

                if (arena!=null){
                    arena.getTeamList().forEach((team) -> team.removePlayer(((Player) sender)));

                    BattlePlayer unregistered = arena.getPlayers().get(((Player)sender).getUniqueId());
                    unregistered.getBoard().unregister();
                    arena.removePlayer(((Player) sender).getUniqueId());
                }

                if (runner!=null){
                runner.removePlayer(((Player) sender).getUniqueId());
                }
                broadcastMessage(ChatColor.YELLOW+sender.getName()+" has unregistered!");

                break;


            case END:
                break;
        }
        return true;
    }

    public Arena getArena()
    {
        return arena;
    }

    public void broadcastMessage(String message) {
        plugin.getServer().broadcastMessage(message);
    }

    public GameRunner getRunner()
    {
        return runner;
    }
}


