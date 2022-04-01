package me.camm.productions.bedwars.Items.SectionInventories.Inventories;


import me.camm.productions.bedwars.Items.SectionInventories.Templates.ShopInventorySetter;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class TrackerSectionInventory extends ShopInventorySetter
{

    public TrackerSectionInventory(InventoryHolder owner, InventoryType type, String title, boolean isInflated) {
        super(owner, type, title, isInflated);
    }

    public TrackerSectionInventory(InventoryHolder owner, int size, boolean isInflated) {
        super(owner, size, isInflated);
    }

    public TrackerSectionInventory(InventoryHolder owner, int size, String title, boolean isInflated) {
        super(owner, size, title, isInflated);
    }
}
