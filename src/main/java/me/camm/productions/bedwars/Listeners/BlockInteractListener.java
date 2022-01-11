package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamTitle;
import me.camm.productions.bedwars.Entities.ActiveEntities.GameDragon;
import me.camm.productions.bedwars.Entities.ActiveEntities.GameTNT;
import me.camm.productions.bedwars.Structures.SoakerSponge;
import me.camm.productions.bedwars.Structures.Tower;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.PacketSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.*;


public class BlockInteractListener implements Listener   //unfinished
{
    private final Plugin plugin;
    private final Arena arena;
    private final HashSet<String> activeSponges;

    public BlockInteractListener(Plugin plugin, Arena arena)  //construct
    {
        this.plugin = plugin;
        this.arena = arena;
        this.activeSponges = new HashSet<>();


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doFireTick false");
    }


    @EventHandler (priority = EventPriority.HIGHEST)   //determines the state of the block
    public void onBlockPlace(BlockPlaceEvent event)
    {

        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
        Block block = event.getBlockPlaced();
        Player placer = event.getPlayer();

        if (!players.containsKey(placer.getUniqueId()))
            return;

        BattlePlayer player = players.get(placer.getUniqueId());


        if ((block.hasMetadata(BASE.getData())||block.hasMetadata(GENERATOR.getData()))||!block.hasMetadata(ARENA.getData()))
        {
            event.setCancelled(true);
            placer.sendMessage(ChatColor.RED+"You cannot place blocks here!");
        }
        else if (block.hasMetadata(MAP.getData()))  //if it's a map block and it is air [broken]
        {
            block.removeMetadata(MAP.getData(),plugin);
        }

        if (!event.isCancelled())
        {
            Material type = event.getBlockPlaced().getType();
            switch (type)       //test if tnt, sponge, chest
            {
                case TNT:
                {
                  //  event.getPlayer().sendMessage("[test:] placed tnt");
                    summonTNT(event,player);
                    //summon tnt
                }
                break;

                case SPONGE:
                {
                   new SoakerSponge(plugin,block,this).soak();
                    //make a sponge water soaker-upper
                }
                break;

                case CHEST:
                {

                    if (players.containsKey(event.getPlayer().getUniqueId()))
                        new Tower(event, plugin, (byte)players.get(event.getPlayer().getUniqueId()).getTeam().getColor().getValue());
                    //get the player team and create a popup tower

                }
                break;

                //For debug testing
                case BEDROCK:
                {


                  if (players.containsKey(event.getPlayer().getUniqueId())) {

                      BattlePlayer test = players.get(event.getPlayer().getUniqueId());
                      BattleTeam testTeam = test.getTeam();
                      test.sendMessage("Test - SPAWNING");


                      GameDragon dragon = new GameDragon(((CraftWorld)test.getRawPlayer().getWorld()).getHandle(),testTeam.getArena().getBounds().getCenter(test.getRawPlayer().getWorld()),test.getRawPlayer().getLocation(),testTeam,plugin,null,testTeam.getArena());
                      dragon.spawn();

                  }


                }
                break;
            }
        }
    }

    @EventHandler
    public void onBlockCanPlaceEvent(BlockCanBuildEvent event)
    {
        event.setBuildable(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();

        Player whoBroke = event.getPlayer();
        if (!arena.getPlayers().containsKey(whoBroke.getUniqueId()))
            return;

        BattlePlayer broke = arena.getPlayers().get(whoBroke.getUniqueId());

        if (block.hasMetadata(GENERATOR.getData())||block.hasMetadata(CHEST.getData())||
                block.hasMetadata(MAP.getData())||block.hasMetadata(BASE.getData()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED+"You can't break blocks here!");
            // event.getPlayer().sendMessage("[DEBUG:] Has generator or chest or map or base");
            return;
        }
        else if (block.hasMetadata(BED.getData()))
        {
            //A bed has been broken. Get the team and send message and modify the variables.
            event.getPlayer().sendMessage("[DEBUG:] Broken bed");
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();


            //Attempt to find the team that the bed belonged to.
            BattleTeam broken = null;
            for (BattleTeam team: arena.getTeams().values())
            {
                if (team.getBed().containsCoordinate(x,y,z))
                {
                    broken = team;
                    break;
                }
            }


            //If we can't find a team that the bed belonged to, send an error message.
            if (broken == null) {
                arena.sendMessage(ChatColor.RED+"[ERROR]: A bed was broken at "+x+" "+y+" "+z+", " +
                        "but we couldn't find a team that it belonged to! (Error with configuration???)");
                return;
            }



            //we need to cancel the event regardless so that putOnLastStand() can work,
            // or if the player isn't on a team, or if the player isn't alive.
            event.setCancelled(true);


            //If the player tried to break their own bed.

            if ((!broke.getIsAlive())||broke.getIsEliminated())
                return;


            if (broken.equals(broke.getTeam()))
            {
                broke.sendMessage(ChatColor.RED+"You can't break your own bed!");
                return;
            }

            broken.putOnLastStand();
            broke.setBeds(broke.getBeds()+1);





            /*
            Playing sounds for all players on the team who's bed was broken.
            if the player on the team is alive, then we send a subtitle about them being on their last life.
            If the player is dead but not eliminated, then we send them a packet about their bed destroyed with the respawn.
             */

            broken.getPlayers().values().forEach(battlePlayer ->
            {
                battlePlayer.playSound(PacketSound.WITHER);

                if (battlePlayer.getIsAlive())
                    battlePlayer.sendTitle(TeamTitle.BED_DESTROYED.getMessage(), TeamTitle.LAST_LIFE_WARNING.getMessage(), 10,40,10);
                else
                    battlePlayer.sendRespawnTitle(TeamTitle.BED_DESTROYED,TeamTitle.RESPAWN_AFTER, battlePlayer.getTimeTillRespawn(), 10,40,10);
           });


            for (BattlePlayer player: arena.getPlayers().values())
                player.getBoard().updateTeamStatuses();

            arena.sendMessage("[PLACEHOLDER MESSAGE] Bed destroyed:  "+broken.getColor().getName()+" Broken by: "+broke.getRawPlayer().getName());


            return;
        }

        if (block.getType() != Material.CHEST)
            return;


        if (!block.hasMetadata(CHEST.getData()))
            return;


        BattleTeam comparison = null;
        for (BattleTeam team: arena.getTeamList())
        {
            Coordinate chest = team.getChest();
           if  (chest.isBlock(arena.getWorld(),block))
           {
               comparison = team;
               break;
           }
        }

        if (comparison==null)
            return;

        if (comparison.isEliminated())
            return;

        if (!broke.getTeam().equals(comparison)) {
            event.setCancelled(true);
            broke.sendMessage("[PLACEHOLDER]You cannot open that chest while " + comparison.getColor().getName() + "is still alive!");

        }

    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent event)
    {
        //If a block has any metadata about the sponge, then we cancel the event and return.
        for (String id: activeSponges)
        {
            if (event.getToBlock().hasMetadata(id)) {
                event.setCancelled(true);
                return;
            }

        }
    }


    //Summoning tnt.
    private void summonTNT(BlockPlaceEvent event, BattlePlayer player)
    {
      new GameTNT(event,player);
    }



    public synchronized void addActiveSponge(UUID id)
    {
        activeSponges.add(id.toString());
    }

    public synchronized void removeActiveSponge(UUID id)
    {
        activeSponges.remove(id.toString());
    }



}
