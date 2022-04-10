package me.camm.productions.bedwars.Explosions.Vectors;

import me.camm.productions.bedwars.Explosions.BlockResistance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import static me.camm.productions.bedwars.Explosions.VectorToolBox.calculateResistance;
import static me.camm.productions.bedwars.Explosions.VectorToolBox.isDataDestructable;


/**
 * TODO unfinished. Need to refactor.
 * @author CAMM
 * Models a vector that breaks blocks
 */
public class ExplosionVector extends GameVector
{
    private double strength;
    private final World world;
    private final boolean incendiary;
    private final int[] colors;

    public ExplosionVector(Vector direction, Vector origin, World world, boolean incendiary, int[] colors)   //construct for explosion fragments
    {
        this.direction = direction.normalize();  //Length of 1 b/c velocity is 1?
        this.origin = origin;
        this.world = world;
        this.incendiary = incendiary;
        this.colors = colors;

        if (this.incendiary) //fireballs have explosionpower 3
            strength = (0.7+(Math.random()*0.6))*5;  //is a fireball
        else
            strength = (0.7+(Math.random()*0.6))*5; //is not
    }


    public boolean validate()  //checking if a explosion vector has lost all of it's strength
    {
        return this.strength > 0;
    }


    public Block blockAtDistance(double distance)
    {
        Vector position = getPosition(distance);
        Location loc = position.toLocation(world);
        this.strength -= 0.3;  //0.375
        return loc.getBlock();
    }



    @SuppressWarnings("deprecation")
    public boolean conflict(Block block)
    {
        double blockStrength;
        double blockResistance;

        Material type = block.getType();
        boolean isAir = false;


        switch (type)
        {
            case ENDER_STONE:
                blockStrength = incendiary ? BlockResistance.UNBREAKABLE.getResistance():
                        BlockResistance.END_STONE.getResistance();
            break;

            case STAINED_CLAY:   //Account for colors
                blockStrength = isDataDestructable(block.getData(),true,block,colors) ? BlockResistance.CLAY.getResistance():
                        BlockResistance.UNBREAKABLE.getResistance();
            break;


            case WOOL: 
                blockStrength = isDataDestructable(block.getData(),true,block, colors) ? BlockResistance.WOOD.getResistance():
                        BlockResistance.UNBREAKABLE.getResistance();
            break;

            case WOOD:
                blockStrength = isDataDestructable(block.getData(),false,block, colors) ? BlockResistance.WOOD.getResistance():
                        BlockResistance.UNBREAKABLE.getResistance();
            break;

            case AIR:
            case FIRE:
                isAir = true;
                blockStrength = BlockResistance.NON_SOLID.getResistance();
            break;

            case LADDER:
                blockStrength = BlockResistance.LADDER.getResistance();
            break;

            default:
                blockStrength = BlockResistance.UNBREAKABLE.getResistance();  // Make it like bedrock
        }//switch

      blockResistance = isAir? 0: calculateResistance(blockStrength);
        this.strength -= blockResistance;
        return this.strength >= blockResistance;


    }

}
