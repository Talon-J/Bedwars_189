package me.camm.productions.bedwars.Items.ItemDatabases;

//For sorting the items in the quick buy shop in order


import static me.camm.productions.bedwars.Items.ItemDatabases.GameItem.*;
import static me.camm.productions.bedwars.Items.ItemDatabases.TieredCategory.*;

public enum TieredItem
{
    WOOD_PICK(0,PICK,WOODEN_PICKAXE,true),
    IRON_PICK(1,PICK, GameItem.IRON_PICKAXE,false),
    GOLD_PICK(2,PICK,GOLD_PICKAXE,false),
    DIAMOND_PICK(3,PICK,DIAMOND_PICKAXE,false),

    WOOD_AXE(0,AXE, GameItem.WOOD_AXE,true),
    STONE_AXE(1,AXE, GameItem.STONE_AXE,false),
    IRON_AXE(2,AXE, GameItem.IRON_AXE,false),
    DIAMOND_AXE(3,AXE, GameItem.DIAMOND_AXE,false),

    //-1 denotes that the item is given by default
    //Swords are not tiered, but a better sword will replace a worse one.
    WOOD_SWORD(-1,SWORD, WOODEN_SWORD,false),

    LEATHER_ARMOR(-1,ARMOR, GameItem.LEATHER_ARMOR,false),
    CHAIN_MAIL(0,ARMOR, GameItem.CHAIN_MAIL,true),
    IRON_ARMOR(1,ARMOR, GameItem.IRON_ARMOR,true),
    DIAMOND_ARMOR(2,ARMOR, GameItem.DIAMOND_ARMOR,true);

    private final int index;
    private final TieredCategory category;
    private final GameItem item;
    private final boolean isFileValid;


    TieredItem(int index, TieredCategory category, GameItem item, boolean isFileValid)
    {
        this.index = index;
        this.category = category;
        this.item = item;
        this.isFileValid  = isFileValid;
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
