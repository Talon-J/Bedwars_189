package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum MeleeConfig
{
    STONE_SWORD(QUICK_INV_BORDER_START.getValue(), GameItem.STONE_SWORD),
    IRON_SWORD(QUICK_INV_BORDER_START.getValue()+1, GameItem.IRON_SWORD),
    DIAMOND_SWORD(QUICK_INV_BORDER_START.getValue()+2, GameItem.DIAMOND_SWORD),
    KB_STICK(QUICK_INV_BORDER_START.getValue()+3, GameItem.STICK);

    private final int slot;
    private final GameItem item;

    MeleeConfig(int slot, GameItem item)
    {
        this.item = item;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public GameItem getItem() {
        return item;
    }
}
