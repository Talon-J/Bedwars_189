package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.QUICK_INV_BORDER_START;

public enum ToolsConfig
{
    SHEARS(QUICK_INV_BORDER_START.getValue(), GameItem.SHEARS),
    PICKAXE(QUICK_INV_BORDER_START.getValue()+1, GameItem.WOODEN_PICKAXE),
    AXE(QUICK_INV_BORDER_START.getValue()+2, GameItem.WOOD_AXE);

    private final int slot;
    private final GameItem item;

    ToolsConfig(int slot, GameItem item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public GameItem getItem() {
        return item;
    }

    //this enum is to hold the slots and the inventory items that should go in the tools section


    //note: both this inventory and the quickbuy inventory of the player must be synced
    //up to match what they buy in the shop in terms of tools.

    //also, perm items also need to be synced (so like armor and tools that are tiered)
}
