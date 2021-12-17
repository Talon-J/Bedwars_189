package me.camm.productions.bedwars.Explosions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;



public class TraceVector extends VectorToolBox
{
    private final Vector direction, origin;
    private double strength;
    private final World world;
    private final boolean incendiary;
    private final int[] colors;

    public TraceVector(Vector direction, Vector origin, World world, boolean incendiary, int[] colors)   //construct for explosion fragments
    {
        this.direction = direction.normalize();  //Length of 1 b/c velocity is 1?
        this.origin = origin;
        this.world = world;
        this.incendiary = incendiary;
        this.colors = colors;

        if (this.incendiary) //fireballs have explosionpower 3
            strength = (0.7+(Math.random()*0.6))*3;  //is a fireball
        else
            strength = (0.7+(Math.random()*0.6))*4; //is not
    }


    public boolean validate()  //checking if a explosion vector has lost all of it's strength
    {
        return this.strength > 0;
    }

    /*
    public Vector getDirection() {    return direction; }
     */

    public Vector getPosition(double blocks) //Should be fixed now...I think?
    {
        return direction.clone().multiply(blocks).add(origin.clone());
    }

    public Block blockAtDistance(double distance)  //might give nullpointerexception?
    {
        Vector position = getPosition(distance);
        Location loc = position.toLocation(world);
        this.strength -= 0.375;  //subtract 0.375 for each block
        return loc.getBlock();
    }


    //("Block data:"+currentBlocks.get(slot).getData());

    @SuppressWarnings("deprecation")
    public boolean conflict(Block block)  //Might be air causing blast resistance? Yup it is and it is fixed.
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
                blockStrength = confirmData(block.getData(),true,block,colors) ? BlockResistance.CLAY.getResistance():
                        BlockResistance.UNBREAKABLE.getResistance();
            break;


            case WOOL: 
                blockStrength = confirmData(block.getData(),true,block, colors) ? BlockResistance.WOOD.getResistance():
                        BlockResistance.UNBREAKABLE.getResistance();
            break;

            case WOOD:
                blockStrength = confirmData(block.getData(),false,block, colors) ? BlockResistance.WOOD.getResistance():
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

        if (this.strength>=blockResistance)
        {
            this.strength = this.strength - blockResistance;  //block is broken
            return true;
        }
        else {
            this.strength = this.strength - blockResistance;  //updating the strength to invalidate
            return false;  //Block not broken
        }

    }

}
