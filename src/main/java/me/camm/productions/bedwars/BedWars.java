package me.camm.productions.bedwars;

import me.camm.productions.bedwars.Arena.GameRunning.Commands.CommandKeyword;
import me.camm.productions.bedwars.Arena.GameRunning.Commands.SetUp;
import me.camm.productions.bedwars.Entities.PacketHandler;
import me.camm.productions.bedwars.Files.FileCreators.DirectoryCreator;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public final class BedWars extends JavaPlugin
{
    private SetUp initialization;

    @Override
    public void onEnable()
    {
        sendMessage(ChatColor.GREEN+"[BEDWARS] - STARTING UP");
        DirectoryCreator fileCreator = new DirectoryCreator(this);

       if (!fileCreator.createFolders())
       {
           sendMessage(ChatColor.RED+"[BEDWARS] [ERROR]: FILES ARE NOT CONFIGURED. CANNOT CONTINUE UNTIL CONFIGURATION IS COMPLETE.");
           sendMessage(ChatColor.RED+"[BEDWARS] PLEASE RELOAD THE PLUGIN AFTER CONFIGURATION IS COMPLETE.");
       }
       else
       {
           initialization = new SetUp(this);
           getCommand(CommandKeyword.SETUP.getWord()).setExecutor(initialization);
           getCommand(CommandKeyword.REGISTER.getWord()).setExecutor(initialization);
           getCommand(CommandKeyword.START.getWord()).setExecutor(initialization);
          // EntityTypes
         //  BiomeBase
       }
    }

    @Override
    public void onDisable()
    {
        if (initialization==null)
            return;

        if (initialization.getRunner()==null)
            return;

        initialization.getRunner().setIsRunning(false);
        initialization.getArena().getTeams().forEach((string, team) -> {
            if (team!=null&&team.getForge()!=null)
            team.getForge().disableForge();
        });
      PacketHandler handler = initialization.getRunner().getPacketHandler();

      if (handler!=null) {
          for (Player player : Bukkit.getOnlinePlayers())
              handler.removePlayer(player);
      }

        World world = initialization.getArena().getWorld();
        Scoreboard initial = ((CraftWorld)world).getHandle().getScoreboard();
        Collection<ScoreboardObjective> objectives = initial.getObjectives();
        for (ScoreboardObjective objective: objectives)
        {
            for (Player player: Bukkit.getOnlinePlayers()) {
                try {
                    initial.unregisterObjective(objective);
                    initial.handleObjectiveRemoved(objective);
                    send(new PacketPlayOutScoreboardObjective(objective, 1), player);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }


        //Save all the players things here and put them into their files.
    }

    private void send(Packet packet, Player player)
    {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    private void sendMessage(String message)
    {
        getServer().getConsoleSender().sendMessage(message);
    }

    /*
TODO: (In the very near future)
 - Inventories for quick buy. Create. [DONE]. Do the team inventories now. [Add the functionality later]
 - Bridge egg. [DONE] Please Integrate after enderpearls.
 - for some reason, players can shift-click items into the shop. Fix.
 - Health of players is still not accurate for below name. Fix. (Use packets)
 - One of the events, the bed destroyed one does not play a title for certain players. Fix.
 - The scoreboard gets stuck on emerald II at 5 mins. Should be good. Verify please. [DONE]
 - Invis is done. Check to see if there are glitches regarding distances (Invis far away, come close, see if they can see.) [Seems to be good...]
 - Do the research for overriding the EntityEnderDraon class [IN PROGRESS] - remember to register it!
 - Find a better solution to the explosives problem.
 - Do research on packets, specifically those for scores relating to health, and packet 1018 in playOutWorldEvent (See dragonAPI)
 - Do testing while the player is in spectator
 - see why the player's armor is not removed when in spectator
 - determine if the hotbarmanager should be active or passive. Change the code in battleplayer to reflect these changes
 - verify that enchantments for tools are actually applied.


 NOTE: The check for opposition in the CLASS "Setup" is currently ----   ---- disabled for testing purposes.

 */



}
