package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Files.FileKeywords.TeamFileKeywords;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.ARENA;

public class DroppedItemListener implements Listener
{
    private final Plugin plugin;
    private Arena arena;

    public DroppedItemListener(Plugin plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;


    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event)
    {
        if (event.getEntity().hasMetadata(TeamFileKeywords.FORGE_SPAWN.getKey())||event.getTarget().hasMetadata(TeamFileKeywords.FORGE_SPAWN.getKey()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event)
    {
        if (event.getLocation().getBlock().hasMetadata(ARENA.getData()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event)
    {
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
        if (players.containsKey(event.getPlayer().getUniqueId())&&event.getItem().hasMetadata(TeamFileKeywords.FORGE_SPAWN.getKey()))
        {
            BattlePlayer player = players.get(event.getPlayer().getUniqueId());

            if (!player.getIsAlive() || player.getIsEliminated())
            {
                event.setCancelled(true);
                return;
            }


            Location loc = player.getTeam().getForge().getForgeLocation();
            double distance = player.getTeam().getForge().getDistance();

           Collection<Entity> nearby =  loc.getWorld().getNearbyEntities(loc,distance,distance,distance);
            for (Entity entity: nearby)
            {
                if (!(entity instanceof Player))
                continue;

                    Player current = (Player)entity;
                 if (!players.containsKey(current.getUniqueId()))
                 continue;

                    BattlePlayer currentReceiver = players.get(current.getUniqueId());
                 if (!player.getTeam().equals(currentReceiver.getTeam()))  //if on the same team, then share
                 continue;

                 if (player.getUUID().equals(currentReceiver.getUUID()))  //if they are not the same player
                   continue;

                Item item =  event.getItem();
                 if (currentReceiver.getRawPlayer().getInventory().firstEmpty()!=-1 &&
                         (currentReceiver.getIsAlive() && !currentReceiver.getIsEliminated()))
                  currentReceiver.getRawPlayer().getInventory().addItem(item.getItemStack());
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event)  //test for when the player drops stuff if it's invalid/valid
    {
        ConcurrentHashMap<UUID, BattlePlayer> players = arena.getPlayers();
       Player player = event.getPlayer();

       if (!players.containsKey(player.getUniqueId()))
           return;







    }
}
