package me.camm.productions.bedwars;

import me.camm.productions.bedwars.Arena.GameRunning.Commands.CommandKeyword;
import me.camm.productions.bedwars.Arena.GameRunning.Commands.SetUp;
import me.camm.productions.bedwars.Entities.PacketHandler;
import me.camm.productions.bedwars.Files.FileCreators.DirectoryCreator;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
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
       else {
           initialization = new SetUp(this);
           getCommand(CommandKeyword.SETUP.getWord()).setExecutor(initialization);
           getCommand(CommandKeyword.REGISTER.getWord()).setExecutor(initialization);
           getCommand(CommandKeyword.START.getWord()).setExecutor(initialization);
       }
    }

    @Override
    public void onDisable()
    {
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
 - Inventories for quick buy. Create.
 - Bridge egg. Please Integrate after enderpearls.
 - Health of players is still not accurate for below name. Fix.
 - One of the events, the bed destroyed one does not play a title for certain players. Fix.
 - NPCS are disappearing when ppl die and respawn. Fix.
 - The scoreboard gets stuck on emerald II at 5 mins. Should be good. Verify please.
 - Invis is done. Check to see if there are glitches regarding distances (Invis far away, come close, see if they can see.) [Seems to be good...]


 NOTE: The check for opposition in the CLASS "Setup" is currently ----   ---- disabled for testing purposes.

 */



}
