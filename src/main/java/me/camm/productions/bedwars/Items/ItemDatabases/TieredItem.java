package me.camm.productions.bedwars.Items.ItemDatabases;

//For sorting the items in the quick buy shop in order

import static me.camm.productions.bedwars.Items.ItemDatabases.GameItem.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.TieredCategory.*;

public enum TieredItem
{
    WOOD_PICK(-1,PICK,WOODEN_PICKAXE,true,false,true),
    IRON_PICK(0,PICK, GameItem.IRON_PICKAXE,false,true,true),
    GOLD_PICK(1,PICK,GOLD_PICKAXE,false,true,true),
    DIAMOND_PICK(2,PICK,DIAMOND_PICKAXE,false,true,true),

    WOOD_AXE(-1,AXE, GameItem.WOOD_AXE,true,false,true),
    STONE_AXE(0,AXE, GameItem.STONE_AXE,false,true,true),
    IRON_AXE(1,AXE, GameItem.IRON_AXE,false,true,true),
    DIAMOND_AXE(2,AXE, GameItem.DIAMOND_AXE,false,true,true),

    //-1 denotes that the item is given by default
    //Swords are not tiered, but a better sword will replace a wood sword. (not other swords though)
    WOOD_SWORD(-1,SWORD, WOODEN_SWORD,false,false,true),
    STONE_SWORD(0, SWORD, GameItem.STONE_SWORD,true,false,false),
    IRON_SWORD(1,SWORD, GameItem.IRON_SWORD, true,false,false),
    DIAMOND_SWORD(2, SWORD, GameItem.DIAMOND_SWORD, true,false,false),

    LEATHER_ARMOR(-1,ARMOR, GameItem.LEATHER_ARMOR,false,false,true),
    CHAIN_MAIL(0,ARMOR, GameItem.CHAIN_MAIL,true,true,true),
    IRON_ARMOR(1,ARMOR, GameItem.IRON_ARMOR,true,true,true),
    DIAMOND_ARMOR(2,ARMOR, GameItem.DIAMOND_ARMOR,true,true,true);

    private final int index;
    private final TieredCategory category;
    private final GameItem item;
    private final boolean isFileValid;
    private final boolean totalReplacing;
    private final boolean isReplaced;


    TieredItem(int index, TieredCategory category, GameItem item, boolean isFileValid,boolean isTotalReplacing, boolean isReplaced)
    {
        this.index = index;
        this.category = category;
        this.item = item;
        this.isFileValid  = isFileValid;
        this.totalReplacing = isTotalReplacing;
        this.isReplaced = isReplaced;
    }

    public int getIndex()
    {
        return index;
    }

    public TieredCategory getCategory()
    {
        return category;
    }

    public GameItem getItem()
    {
        return item;
    }

    public boolean getIsFileValid()
    {
        return isFileValid;
    }

    public boolean isTotalReplacing() {
        return totalReplacing;
    }

    public boolean isReplaced() {
        return isReplaced;
    }
}



    /*
      WOOD_PICK
      IRON_PICK
      GOLD_PICK
      DIAMOND_PICK

     WOOD_AXE
     STONE_AXE
     IRON_AXE
     DIAMOND_AXE

     WOODEN_SWORD
     STONE_SWORD
     IRON_SWORD
     DIAMOND_SWORD

     CHAIN_MAIL
     IRON_ARMOR
     DIAMOND_ARMOR
     */
