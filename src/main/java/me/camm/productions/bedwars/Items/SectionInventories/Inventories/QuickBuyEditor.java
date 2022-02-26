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

    public void updateConfiguration(int slot) {
      QuickBuySection section = owner.getShopManager().getQuickBuy();
      ItemStack stack = section.getItem(slot);
      editor.setItem(slot, stack);
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
        ItemStack change = editor.getItem(slot);
        section.setItem(slot, change);
    }

    public Inventory getEditor(){
        return editor;
    }

    public ItemStack getCurrentAdding(){
        return currentAdding;
    }
}
