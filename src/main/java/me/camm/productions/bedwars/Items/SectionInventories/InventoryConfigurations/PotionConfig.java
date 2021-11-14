package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum PotionConfig
{
    SPEED(QUICK_INV_BORDER_START.getValue(), GameItem.SPEED_POT),
    JUMP(QUICK_INV_BORDER_START.getValue()+1, GameItem.JUMP_POT),
    INVIS(QUICK_INV_BORDER_START.getValue()+2, GameItem.INVIS_POT);


    PotionConfig(int slot, GameItem item) {
        this.slot = slot;
        this.item = item;
    }

    private final int slot;
    private final GameItem item;

    public int getSlot() {
        return slot;
    }

    public GameItem getItem() {
        return item;
    }
}
