package me.camm.productions.bedwars.Items.ItemDatabases;


public enum DefaultTemplateNavigation
{
    HOME(new int[] {0}, GameItem.HOME_NAV),
    BLOCKS(new int[] {1}, GameItem.BLOCKS_NAV),
    MELEE(new int[] {2}, GameItem.MELEE_NAV),
    ARMOR(new int[] {3}, GameItem.ARMOR_NAV),
    TOOLS(new int[] {4}, GameItem.TOOLS_NAV),
    RANGED(new int[] {5}, GameItem.RANGED_NAV),
    POTIONS(new int[] {6}, GameItem.POTIONS_NAV),
    UTILITY(new int[] {7}, GameItem.UTILITY_NAV),
    HOTBAR(new int[] {53}, GameItem.HOTBAR_NAV),
    SEPARATOR(new int[]{9,10,11,12,13,14,15,16,17},GameItem.SEPARATOR),
    EMPTY(new int[] {19,22,37,38,39,40,41,42,43}, GameItem.EMPTY_SLOT);

    private final int[] range;
    private final GameItem item;

    DefaultTemplateNavigation(int[] range, GameItem item)
    {
        this.range = range;
        this.item = item;
    }

    public int[] getRange()
    {
        return range;
    }

    public GameItem getItem()
    {
        return item;
    }

}


