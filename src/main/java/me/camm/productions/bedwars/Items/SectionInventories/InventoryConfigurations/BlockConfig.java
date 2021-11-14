package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum BlockConfig
{
    WOOL(QUICK_INV_BORDER_START.getValue(), GameItem.WOOL),
    CLAY(QUICK_INV_BORDER_START.getValue()+1, GameItem.HARDENED_CLAY),
    GLASS(QUICK_INV_BORDER_START.getValue()+2, GameItem.STAINED_GLASS),
    END_STONE(QUICK_INV_BORDER_START.getValue()+3, GameItem.ENDER_STONE),
    LADDER(QUICK_INV_BORDER_START.getValue()+4, GameItem.LADDER),
    WOOD(QUICK_INV_BORDER_START.getValue()+5, GameItem.PLANKS),
    OBSIDIAN(QUICK_INV_BORDER_START.getValue()+6, GameItem.OBSIDIAN);


    private final int slot;
    private final GameItem item;

    BlockConfig(int slot, GameItem item)
    {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot()
    {
        return slot;
    }

    public GameItem getItem() {
        return item;
    }
}
