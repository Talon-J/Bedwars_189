package me.camm.productions.bedwars.Items.ItemDatabases;

public enum DefaultQuickItemConfig
{

    //the default configuration of the items in the quick buy inventory.
    // See defaultQuickTemplate enum items for separators, empty, and nav.
    WOOL(GameItem.WOOL,new int[]{19}),
    STONE_SWORD(GameItem.STONE_SWORD,new int[] {20}),
    CHAIN(GameItem.CHAIN_MAIL,new int[] {21}),
    ARROW(GameItem.ARROW,new int[] {23}),
    SPEED_POT(GameItem.SPEED_POT,new int[] {24}),
    TNT(GameItem.TNT,new int[] {25}),
    PLANKS(GameItem.PLANKS,new int[] {28}),
    IRON_SWORD(GameItem.IRON_SWORD,new int[] {29}),
    IRON_ARMOR(GameItem.IRON_ARMOR,new int[] {30}),
    SHEARS(GameItem.SHEARS,new int[] {31}),
    BOW(GameItem.BOW,new int[] {32}),
    INVIS_POT(GameItem.INVIS_POT,new int[] {33}),
    WATER(GameItem.WATER,new int[] {34});


    private final GameItem item;
    private final int[] slots;

    DefaultQuickItemConfig(GameItem item, int[] slot)
    {
        this.item = item;
        this.slots = slot;
    }

    public int[] getSlots()
    {
        return slots;
    }

    public GameItem getItem()
    {
        return item;
    }
}
