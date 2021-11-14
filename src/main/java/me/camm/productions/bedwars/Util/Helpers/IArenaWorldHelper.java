package me.camm.productions.bedwars.Util.Helpers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public interface IArenaWorldHelper
{

    default void dropItem(ItemStack stack, World world, Location loc, Plugin p)
    {

        new BukkitRunnable()
        {
            public void run()
            {
                world.dropItem(loc,stack);
               cancel();
            }
        }.runTask(p);

    }

    default void dropItem(Player player, ItemStack item, Plugin plugin)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                World world = player.getWorld();
                Item dropped =  world.dropItem(player.getLocation(),item);
                dropped.setVelocity(new Vector(0,0,0));
                cancel();
            }
        }.runTask(plugin);
    }
}
