package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Players.IPlayerUtil;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.PacketHandler;
import me.camm.productions.bedwars.Entities.ShopKeeper;
import me.camm.productions.bedwars.Util.Helpers.IArenaChatHelper;
import me.camm.productions.bedwars.Util.Helpers.IArenaWorldHelper;
import me.camm.productions.bedwars.Util.Helpers.RunningTeamHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LogListener implements Listener, IArenaChatHelper, IArenaWorldHelper, IPlayerUtil
{
    private final PacketHandler packetHandler;
    private final Arena arena;
    private final Plugin plugin;
    private final GameRunner runner;

    private final ArrayList<ShopKeeper> keepers;

    public LogListener(Arena arena, GameRunner runner,ArrayList<ShopKeeper> keepers) {
        this.packetHandler = runner.getPacketHandler();
        this.arena = arena;
        this.plugin = arena.getPlugin();
        this.runner = runner;
        this.keepers = keepers;

    }

    /*
@author CAMM
 This method listens for a player to log out, and deals with both their spot on their team,
 and the scoreboards of other players.
 */

    @EventHandler
    public void onPlayerLogOut(PlayerQuitEvent event)
    {

        ConcurrentHashMap<UUID,BattlePlayer> registeredPlayers = arena.getPlayers();

        if (packetHandler!=null) {
            if (packetHandler.contains(event.getPlayer()))
                packetHandler.removePlayer(event.getPlayer());
        }

        if (!registeredPlayers.containsKey(event.getPlayer().getUniqueId()))
            return;



        //setting the quit message
        BattlePlayer current = registeredPlayers.get(event.getPlayer().getUniqueId());
        event.setQuitMessage(current.getTeam().getColor().getChatColor()+current.getRawPlayer().getName()+ ChatColor.YELLOW+" has Quit!");

        //Clear their inventory when they log out.
        Arrays.stream(current.getRawPlayer().getInventory().getContents()).filter(item -> {

            if (item == null)
                return false;

            Material mat = item.getType();

            return mat==Material.DIAMOND || mat == Material.IRON_INGOT || mat == Material.GOLD_INGOT || mat == Material.EMERALD;
        }).forEach(item -> dropItem(item,arena.getWorld(),current.getRawPlayer().getLocation(),plugin));
        clearInventory(current);



        if (!runner.isRunning()) {
            RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());
            return;
        }
        BattleTeam team = current.getTeam();

        //if is on last life
        if (!team.doesBedExist())
        {
            //eliminate them
            sendMessage(current.getTeam().getColor().getChatColor()+current.getRawPlayer().getName()+ChatColor.YELLOW+" was on their last life! They have been eliminated!",plugin);
            current.setEliminated(true);
            RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());
            //check for the team stuff to see if there's a win here.
        }



        if (team.getRemainingPlayers()==0)
        {
            team.eliminate();
            RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());
            BattleTeam candidate = RunningTeamHelper.isVictorFound(arena.getTeams().values());
            if (candidate!=null)
                runner.endGame(candidate);
        }
    }


    /*
 @Author CAMM
 This method listens for a player to join the game.
 It handles if a player was previously registered in the game, updating scoreboards,
 replacing the previous player object, and resetting their spot on the packet handler.
  */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        BattlePlayer current;
        Player player;
        BattleTeam team;
        boolean isBedExists;

        ConcurrentHashMap<UUID,BattlePlayer> registeredPlayers = arena.getPlayers();

        if (!registeredPlayers.containsKey(event.getPlayer().getUniqueId())) //if the uuids are the same
            return;


        current = registeredPlayers.get(event.getPlayer().getUniqueId());
        team = current.getTeam();
        player = event.getPlayer();


        current.refactorPlayer(player);

        isBedExists = team.doesBedExist();

        if (runner.isRunning())
        {
            if (!isBedExists)
                current.handlePlayerIntoSpectator(packetHandler,true);
            else
                current.handlePlayerRespawn(packetHandler);

        }

        current.getRawPlayer().setScoreboard(arena.getHealthBoard());  //refreshing the board.
        runner.initializeTimeBoardHead(current);
        RunningTeamHelper.updateTeamBoardStatus(registeredPlayers.values());


        if (!runner.isRunning())
            return;


        if (packetHandler.contains(player)) {
            packetHandler.removePlayer(player);
        }
        packetHandler.addPlayer(player);


        for (ShopKeeper keeper: keepers) {
            keeper.sendNPC(player);
            keeper.setRotation(player);
        }

    }
}
