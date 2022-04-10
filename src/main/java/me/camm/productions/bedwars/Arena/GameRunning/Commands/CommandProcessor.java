package me.camm.productions.bedwars.Arena.GameRunning.Commands;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamColors;
import me.camm.productions.bedwars.Files.FileStreams.TeamFileReader;
import me.camm.productions.bedwars.Files.FileStreams.WorldFileReader;
import me.camm.productions.bedwars.Util.Helpers.ChatSender;
import me.camm.productions.bedwars.Validation.BedWarsException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Arena.GameRunning.Commands.CommandKeyword.*;


/**
 * @author CAMM
 * This class handles command processing from the game initializer
 */
public class CommandProcessor {


    private GameRunner runner;
    private final ChatSender messager;

    public CommandProcessor(){
        runner = null;
        messager = ChatSender.getInstance();
    }

    /**
     *
     * @param sender commandsender
     * @param plugin plugin
     * @return a game runner
     * @throws BedWarsException if the sender has no perms, or if a problem occurred
     */
    public GameRunner initRunner(CommandSender sender, Plugin plugin) throws BedWarsException{

        //make sure they have the permission
        if (noPermission(sender, SETUP))
            throw new PermissionException("You don't have permission!");

        messager.sendMessage("Attempting to register the map. Expect some lag.");

        //try to read the config from the config files and make a new
        //arena object
        Arena arena;
        GameRunner runner;

        ArrayList<BattleTeam> teams;
        WorldFileReader fileReader = new WorldFileReader(plugin);
        arena = fileReader.read();


        if (arena==null)
            throw new InitializationException("Was not able to init the arena! (Check the config)");

           teams = new TeamFileReader(plugin, arena).read();
            arena.registerMap();

            //ensure that there are teams for opposition in the game
            //if valid, then register their areas from the config and put them into
            //the arena
            if (teams==null||teams.size()<=1)
            throw new InitializationException("The teams are invalid!"+
                    (teams==null?("teams are not defined"):("There must be more than 1 team")));

                arena.addTeams(teams);
                runner = new GameRunner(plugin, arena);
                arena.registerTeamZones();

                plugin.getServer().getPluginManager().registerEvents(runner, plugin);

                this.runner = runner;
                return runner;
    }


    /**
     * method for private chatting between team members in a team
     *
     * @param sender commandsender
     * @param args arguments
     * @throws BedWarsException if an error occurs
     */
    public void shout(CommandSender sender, String[] args) throws BedWarsException{

        //check if they have perms
        if (noPermission(sender, SHOUT))
        throw new PermissionException("You don't have permission!");

        if (runner==null || !runner.isRunning())
            throw new StateException("The game is not running!");
        //You can't shout if the game isn't running


        //check if the player is registered.
        ConcurrentHashMap<UUID, BattlePlayer> players = runner.getArena().getPlayers();
        BattlePlayer current = players.getOrDefault(((Player) sender).getUniqueId(),null);
        if (current == null)
            return;


        //use the arguments of the command to create the message.
        StringBuilder message = new StringBuilder();
        for (String string: args) {
            message.append(string).append(" ");
        }

        //send the message
        TeamColors color = current.getTeam().getTeamColor();
       this.messager.sendPlayerMessage(ChatColor.YELLOW+"[SHOUT]"+color.getChatColor()+"["+color.getName()+"]"+
                color.getChatColor()+"<"+current.getRawPlayer().getName()+">"+ChatColor.GRAY+message,null);

    }


    /**
     * Attempts to start the game.
     *
     *
     * @param sender sender
     * @throws BedWarsException if an error occurs
     */
    public void startGame(CommandSender sender) throws BedWarsException {
        if (noPermission(sender, START))
            throw new PermissionException("You don't have permission!");

        if (runner==null)
            throw new StateException("The arena is not set up!");

        if (runner.isRunning())
            throw new StateException("The game is already running!");


            Collection<BattleTeam> values = runner.getArena().getTeams().values();


           int notOpposed = values.size();
            for (BattleTeam team: values)
            {
                if (team.getRemainingPlayers()==0)
                    notOpposed--;
            }
            /////////////////////////////////////////////////////////////////
            //The check for opposing teams is disabled for testing purposes only.

           //    if (!(notOpposed<2)) //game can start b/c there are at least 2 teams
                   runner.prepareAndStart();
           //     else
            //       throw new StateException("There must be opposition!");



    }


    /**
     * ends the game manually
     * @param sender command sender
     */
    public void manualEndGame(CommandSender sender) throws BedWarsException {

        if (noPermission(sender, END)) {
            throw new PermissionException("You have no permission!");
        }

        if (runner == null || !runner.isRunning())
            throw new StateException(ChatColor.RED+"The game is not running!");


        sender.sendMessage(ChatColor.GOLD+sender.getName()+" has ended the game with a manual override.");
        runner.endGame(null);
    }


    /**
     * Attempts to register a player
     *
     *
     * @param sender sender of the command
     * @throws BedWarsException if conditions are not met
     */
    public void registerPlayer(Player sender) throws BedWarsException{
        if (noPermission(sender, REGISTER))
            throw new PermissionException("You have no permission!");

        if (runner==null)
        throw new StateException("The arena is not setup!");

        if (runner.isRunning())
            throw new StateException("The game is running! Wait for it to finish first!");

        sender.openInventory(runner.getJoinInventory());
    }


    /**
     *
     * @param player sender of the command
     * @throws BedWarsException if several conditions are not met
     */
    public void unregister(CommandSender player) throws BedWarsException {

        //at this point, the commandsender should be a player
        // (this method is only called from the game initializer, which already checks that the sender is a player)
        Player p = (Player)player;


        if (noPermission(player, UNREGISTER))
            throw new PermissionException("You have no permission!");

        if (runner == null) {
            throw new StateException("The arena is not set up!");
        }

        if (runner.isRunning()){
            throw new StateException("The game is running! You can't unregister now!");
        }

        Arena arena = runner.getArena();



        if (arena!=null){
            arena.getTeams().values().forEach((team) -> team.removePlayer(p));


            BattlePlayer unregistered = arena.getPlayers().getOrDefault(p.getUniqueId(),null);
            if (unregistered != null) {
                unregistered.getBoard().unregister();
                arena.removePlayer(p.getUniqueId());
            }
        }

        this.messager.sendMessage(ChatColor.YELLOW+p.getName()+" has unregistered!");

    }


    /**
     * Checks if the player has permission to run a command
     *
     * @param sender sender of the command
     * @param word command label to check for permissions
     * @return if the sender does not have permission
     */
    private boolean noPermission(CommandSender sender, CommandKeyword word) {
        return !sender.hasPermission(word.getPerm());
    }

}







class PermissionException extends BedWarsException{
    public PermissionException(String message){
        super(message);
    }
}
class InitializationException extends BedWarsException{
    public InitializationException(String message) {
        super(message);
    }
}

class StateException extends BedWarsException {
    public StateException(String message) {
        super(message);
    }
}
