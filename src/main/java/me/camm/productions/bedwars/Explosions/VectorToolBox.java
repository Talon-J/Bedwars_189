package me.camm.productions.bedwars.Explosions;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

import static me.camm.productions.bedwars.Util.Locations.BlockRegisterType.*;

public class VectorToolBox
{
    private final String MAP_DATA;
    private final String BED_DATA;
    private final String CHEST_DATA;

    public VectorToolBox() {
        this.MAP_DATA = MAP.getData();
        this.BED_DATA = BED.getData();
        this.CHEST_DATA = CHEST.getData();
    }


    /*
    @Author CAMM_H87
    Returns boolean whether a block meets certain parameters, depending on the registered teams, the block
    check type, and the block data.

    SEE: checkWoodData() and checkColoredData()

     */
    public boolean confirmData(byte data, boolean type, Block block, int[] colors)
    {
        //by true, references colored materials, false --> wood
        if (type)
            return checkColoredData(data, block, colors);
        else
            return checkWoodData(data,MAP_DATA,block);

    }


    /*
    @Author CAMM_H87
    This method returns whether the block has metadata, and if the block type is oak wood (data==0)
    If returns true, the block can be broken. Else, it cannot.
     */
    private boolean checkWoodData(byte data, String blockCheck, Block block)
    {
        return data==0&& !block.hasMetadata(blockCheck);
    }

    /*
    @Author CAMM_H87
    Checks for whether a colored block was placed.
    If the block color is the same as a team, and the block doesn't have metadata, it can be broken.
     */
    private boolean checkColoredData(byte data, Block block, int[] teamColors)
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

        if (!canBreak) //If it's not one of the colors, return false
            return false;
        else {
            return !block.hasMetadata(MAP_DATA) && !block.hasMetadata(BED_DATA) && !block.hasMetadata(CHEST_DATA); //block can't be broken
        }

    }

    /*
    @Author CAMM_H87
   Checks the resistance a block would put up in an explosion.
     */
    public double calculateResistance(double blockStrength)
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
    public void breakAtPosition(Block block)
    {
        Random rand = new Random();
        int chance = rand.nextInt((int)VectorParameter.CEILING.getValue());
        if (chance>VectorParameter.DROP_CHANCE.getValue())
            block.breakNaturally();
        else
            block.setType(Material.AIR);

    }
}
