package me.camm.productions.bedwars.Entities.Consumables;

import me.camm.productions.bedwars.Util.Locations.BridgeFiller;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.MAP;

public class BridgeEgg
{
    private final Egg egg;
    private final Plugin plugin;
    private final byte data;

    public BridgeEgg(Egg egg, Plugin plugin, byte data)
    {
        this.egg = egg;
        this.plugin = plugin;
        this.data = data;
        trackFlying();
    }

    //At this point the egg is already flying
    public void trackFlying()
    {

        new BukkitRunnable()
        {
            final World world = egg.getWorld();
            int iterations = 0;

            @Override
            public void run()
            {
                if (egg.isDead() || !egg.isValid() || egg.getLocation().getY() <0) {
                    cancel();
                    return;
                }

                if (iterations > 90)
                {
                    egg.remove();
                    cancel();
                    return;
                }

                Vector currentLocation = egg.getLocation().toVector().add(egg.getVelocity().multiply(-1.25));
                Vector fillTo = egg.getLocation().toVector().add(egg.getVelocity().multiply(-2));

                Location current = currentLocation.toLocation(world);
                Location filler = fillTo.toLocation(world);

                BridgeFiller fillZone = new BridgeFiller(current,filler);
                fillZone.fill(Material.WOOL,data, world,MAP.getData(),plugin);
                iterations ++;

            }

        }.runTaskTimer(plugin,5,1);



    }
}