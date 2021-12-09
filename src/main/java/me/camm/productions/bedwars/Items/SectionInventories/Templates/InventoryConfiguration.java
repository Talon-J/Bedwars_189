package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

//This is a subclass of Inventory
public abstract class InventoryConfiguration extends CraftInventoryCustom implements ISectionInventory
{

    public InventoryConfiguration(InventoryHolder owner, InventoryType type) {
        super(owner, type);
    }

    public InventoryConfiguration(InventoryHolder owner, InventoryType type, String title) {
        super(owner, type, title);
    }

    public InventoryConfiguration(InventoryHolder owner, int size) {
        super(owner, size);
    }

    public InventoryConfiguration(InventoryHolder owner, int size, String title) {
        super(owner, size, title);
    }

}
/*
bukkit inventory > craftInventory > craftinventorycustom

CraftServer implements Server
plugin.getServer() returns an instance of Server

 */
