package me.camm.productions.bedwars.Explosions.Vectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * TODO unfinished. Need to refactor.
 * @author CAMM
 * Models a vector for tracing the path of an explosion hit
 */
public class TracerVector extends GameVector
{
    private final double magnitude;

    public TracerVector(Vector directionNormalized, Vector origin, double magnitude, World world)
    {
        this.direction = directionNormalized;
        this.origin = origin;
        this.world = world;
        this.magnitude = magnitude;
    }

    @Override
    public Vector getPosition(double blocks) {
        return super.getPosition(blocks);
    }


    public ArrayList<Material> getObstructionLayers()
    {
        double distance = 0;
        final double DISTANCE_CHANGE = 0.1;

        HashSet<Block> blocks = new HashSet<>();
        ArrayList<Material> layers = new ArrayList<>();

        while (distance < magnitude)
        {
            Vector currentPosition = getPosition(distance);
            Location current = currentPosition.toLocation(world);

            Block currentBlock = current.getBlock();
            if (!blocks.contains(currentBlock))
            {
                if (currentBlock.getType() == Material.AIR) {
                    distance += DISTANCE_CHANGE;
                    continue;
                }
                else
                    layers.add(currentBlock.getType());

                blocks.add(currentBlock);
            }
            distance += DISTANCE_CHANGE;
        }
        return layers;
    }
}
