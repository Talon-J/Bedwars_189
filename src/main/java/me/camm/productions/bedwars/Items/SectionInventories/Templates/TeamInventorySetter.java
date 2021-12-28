package me.camm.productions.bedwars.Items.SectionInventories.Templates;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class TeamInventorySetter extends CraftInventoryCustom implements ISectionInventory
{
    public TeamInventorySetter(InventoryHolder owner, InventoryType type) {
        super(owner, type);
    }

    public TeamInventorySetter(InventoryHolder owner, InventoryType type, String title) {
        super(owner, type, title);
    }

    public TeamInventorySetter(InventoryHolder owner, int size) {
        super(owner, size);
    }

    public TeamInventorySetter(InventoryHolder owner, int size, String title) {
        super(owner, size, title);
    }

    @Override
    public abstract void setItem(int slot, ItemStack item);

}
