package me.camm.productions.bedwars.Items.SectionInventories.Inventories;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.InventoryName;
import me.camm.productions.bedwars.Util.DataSets.ItemStackSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QuickBuyEditor
{
    private final BattlePlayer owner;
    private final Inventory editor;
    private ItemStack currentAdding;

    public QuickBuyEditor(BattlePlayer owner) {
        this.owner = owner;
        editor = Bukkit.createInventory(null, InventoryProperty.SHOP_SIZE.getValue(), InventoryName.EDIT_QUICKBUY.getTitle());
        currentAdding = new ItemStack(Material.AIR);
        updateConfiguration();
    }

    public void updateConfiguration(){
        QuickBuySection section = owner.getShopManager().getQuickBuy();
         for (ItemStackSet set: section.getItems())
             editor.setItem(set.getSlot(),set.getStack());
         setCurrentAdding(currentAdding);
    }


    public void setCurrentAdding(ItemStack stack){
        currentAdding = stack;

        // ensures that we get 1/2 of the hotbar length. Integer division.
        editor.setItem(InventoryProperty.HOT_BAR_END.getValue()/2,currentAdding);
    }


    public void display() {
        owner.getRawPlayer().openInventory(editor);
    }

    public void applyConfigChange(int slot){
        QuickBuySection section = owner.getShopManager().getQuickBuy();

        section.setItem(slot, new ItemStack(Material.AIR));
        section.setItem(slot, currentAdding);

        editor.setItem(slot, new ItemStack(Material.AIR));
        editor.setItem(slot, currentAdding);
    }

    public void setItem(int slot, ItemStack stack) {
        editor.setItem(slot, stack);
    }

    public Inventory getEditor(){
        return editor;
    }
}
