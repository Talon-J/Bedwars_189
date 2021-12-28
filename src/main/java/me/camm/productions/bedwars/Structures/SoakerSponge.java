package me.camm.productions.bedwars.Structures;

import me.camm.productions.bedwars.Listeners.BlockInteractListener;
import me.camm.productions.bedwars.Util.Locations.*;
import me.camm.productions.bedwars.Util.Locations.Boundaries.PointWall;
import me.camm.productions.bedwars.Util.Locations.Boundaries.SoakBoundary;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class SoakerSponge
{
    private static final int EXPANSION = 4;
  //  private final double PRECISION = 0.5;


    private final Plugin plugin;
    private final Block sponge;
    private final UUID uuid;
    private final BlockInteractListener listener;

    public SoakerSponge(Plugin plugin, Block sponge, BlockInteractListener listener) {
        this.plugin = plugin;
        this.sponge = sponge;
        this.uuid = UUID.randomUUID();
        this.listener = listener;
    }

    public String getUUID()
    {
        return uuid.toString();
    }

    //make sure to copy update the registered boundary thing in the actual plugin:
    //if (!block.hasMetadata(type)) <-- in registering blocks

    public void soak()
    {

        final SoakBoundary soakRadius = new Coordinate(getBlockCentre(sponge)).toBoundaryPoint();
        //  soakRadius.expand(1);
        listener.addActiveSponge(uuid);

        new BukkitRunnable()
        {
            int iterations = 0;
            final World world = sponge.getWorld();

            @Override
            public void run()
            {
                if (iterations<EXPANSION && sponge.getType() == Material.SPONGE)
                {
                    iterations++;
                    soakRadius.expand(1);
                    PointWall[] wave = generateSurfaceArea(soakRadius);
                    soakRadius.register(world, uuid.toString(), RegisterType.EVERYTHING.getType(), plugin);
                    ArrayList<Location> locations = generateFigurePoints(wave);

                    int alteration = 0;
                    for (Location loc: locations)
                    {
                        dry(world.getBlockAt(loc));
                        alteration++;
                        if (alteration%3==0)
                            playParticles(loc);
                    }
                }
                else {
                    soakRadius.unregister(uuid.toString(),world,plugin);
                    listener.removeActiveSponge(uuid);
                    Block finished = world.getBlockAt(sponge.getLocation());
                    if (finished.getType()==Material.SPONGE)
                        finished.setType(Material.AIR);
                    cancel();
                }
            }
        }.runTaskTimer(plugin,0,10);
    }

    private PointWall[] generateSurfaceArea(SoakBoundary b)
    {
        return new PointWall[] {
                b.generateWall(WallFace.UP), b.generateWall(WallFace.DOWN),
                b.generateWall(WallFace.NORTH), b.generateWall(WallFace.SOUTH),
                b.generateWall(WallFace.EAST), b.generateWall(WallFace.WEST)};
    }

    private ArrayList<Location> generateFigurePoints(PointWall[] surfaceArea)
    {
        ArrayList<Location> points = new ArrayList<>();
        World world = sponge.getWorld();
        for (PointWall wall: surfaceArea)
            points.addAll(wall.iterate(0.5,world));
        return points;
    }

    private void dry(Block block)
    {
        //accounts for metadata
        if (block.getType()==Material.WATER || block.getType() == Material.STATIONARY_WATER)
            block.setType(Material.AIR);

    }

    private void playParticles(Location loc)
    {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.CLOUD,true,(float)loc.getX(),(float)loc.getY(),(float)loc.getZ(),0,0,0,(float)0.1,3);
        for (Player player: Bukkit.getOnlinePlayers())
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

    }

    public Location getBlockCentre(Block block)
    {
        return block.getLocation().clone().add(0.5,0.5,0.5);
    }
}
