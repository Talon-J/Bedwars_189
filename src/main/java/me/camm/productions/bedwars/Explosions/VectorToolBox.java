package me.camm.productions.bedwars.Explosions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Random;

import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.*;

public class VectorToolBox
{
    private static final String MAP_DATA;
    private static final String BED_DATA;
    private static final String CHEST_DATA;
    private static final Random rand;

    static {
        MAP_DATA = MAP.getData();
        BED_DATA = BED.getData();
        CHEST_DATA = CHEST.getData();
        rand = new Random();
    }


    /*
    @Author CAMM_H87
    Returns boolean whether a block meets certain parameters, depending on the registered teams, the block
    check type, and the block data.
    SEE: checkWoodData() and checkColoredData()

    isDistinctlyColorable refers to colors.
    if is wood, then false, else, if clay or wool, true

     */
    public static boolean isDataDestructable(byte data, boolean isDistinctlyColorable, Block block, int[] colors)
    {

        return isDistinctlyColorable ? checkColoredData(data, block, colors):checkWoodData(data, block);
    }

    public static boolean isValidVelocityType(Entity entity) {
        boolean valid = true;
        EntityType type = entity.getType();

        switch (type)
        {
            case FIREBALL:
            case ARMOR_STAND:
            case ENDER_DRAGON:
            case DROPPED_ITEM:
            case ITEM_FRAME:
            case EXPERIENCE_ORB:
                valid = false;
        }
        return valid;
    }

    public static boolean isValidDamageType(Entity entity)
    {
        boolean valid = true;
        EntityType type = entity.getType();

        switch (type)
        {
            case ARMOR_STAND:
            case DROPPED_ITEM:
            case FIREBALL:
                valid = false;


        }
        return valid;
    }











    /*
    @Author CAMM_H87
    This method returns whether the block has metadata, and if the block type is oak wood (data==0)
    If returns true, the block can be broken. Else, it cannot.
     */
    private static boolean checkWoodData(byte data, Block block)
    {
        return data==0&& !block.hasMetadata(MAP_DATA);
    }



    /*
    @Author CAMM_H87
    Checks for whether a colored block was placed.
    If the block color is the same as a team, and the block doesn't have metadata, it can be broken.
     */
    private static boolean checkColoredData(byte data, Block block, int[] teamColors)
    {
        if (teamColors==null||teamColors.length==0)
            return false;

        boolean canBreak  = false;
        for (int current: teamColors)
        {
            if (data==current)
            {
                canBreak = true;
                break;
            }
        }

        return canBreak && (!block.hasMetadata(MAP_DATA) && !block.hasMetadata(BED_DATA) && !block.hasMetadata(CHEST_DATA));

    }




    /*
    @Author CAMM_H87
   Checks the resistance a block would put up in an explosion.
     */
    public static double calculateResistance(double blockStrength)
    {
        return (blockStrength+VectorParameter.BLOCK_ADDITION.getValue())/
                VectorParameter.BLOCK_DIVISOR.getValue();
    }



    /*
    @Author CAMM_H87
    Rolls a random chance with a maximum of the ceiling vector parameter.
    If the chance is greater than the drop chance, then the block breaks naturally, and drops an item.
    Else, it is just set to air.
     */
    public static void breakAtPosition(Block block)
    {
        int chance = rand.nextInt((int)VectorParameter.CEILING.getValue());
        if (chance>VectorParameter.DROP_CHANCE.getValue())
            block.breakNaturally();
        else
            block.setType(Material.AIR);
    }
}
