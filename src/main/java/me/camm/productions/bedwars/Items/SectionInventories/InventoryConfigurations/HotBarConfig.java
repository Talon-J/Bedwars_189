package me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations;

import me.camm.productions.bedwars.Items.ItemDatabases.InventoryProperty;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum HotBarConfig
{
    RETURN(Material.ARROW, "Return to Quick Buy", ItemCategory.OPERATOR, new int[]{3}),
    RESET(Material.BARRIER, "Reset Configuration", ItemCategory.OPERATOR, new int[]{5}),
    SEPARATOR(ShopItem.SEPARATOR.sellMaterial, ChatColor.DARK_GRAY+"\u21e7 Categories "+ChatColor.GRAY+"\u21e9 Configuration",null, new int[]{27,28,29,30,31,32,33,34,35}),
    BLOCK(ShopItem.BLOCKS_NAV.sellMaterial,ChatColor.GOLD+"Blocks",ItemCategory.BLOCK, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+1}),
    MELEE(ShopItem.MELEE_NAV.sellMaterial, ChatColor.GOLD+"Melee", ItemCategory.MELEE, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+2}),
    ARMOR(ShopItem.ARMOR_NAV.sellMaterial,ChatColor.GOLD+"Armor",ItemCategory.ARMOR, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+3}),
    TOOLS(ShopItem.TOOLS_NAV.sellMaterial,ChatColor.GOLD+"Tools",ItemCategory.TOOLS, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+4}),
    RANGED(ShopItem.RANGED_NAV.sellMaterial,ChatColor.GOLD+"Ranged",ItemCategory.RANGED, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+5}),
    POTION(ShopItem.POTIONS_NAV.sellMaterial,ChatColor.GOLD+"Potion",ItemCategory.POTION, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+6}),
    UTILITY(ShopItem.UTILITY_NAV.sellMaterial,ChatColor.GOLD+"Utility",ItemCategory.UTILITY, new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+7}),
    TRACKER(ShopItem.TRACKER_NAV.sellMaterial,ChatColor.GOLD+"Tracker",ItemCategory.TRACKER,new int[]{InventoryProperty.LARGE_ROW_THREE_START.getValue()+8});

    private final Material mat;
    private final String name;
    private final ItemCategory category;
    int[] slots;

    HotBarConfig(Material mat, String name, ItemCategory category, int[] slots) {
        this.mat = mat;
        this.name = name;
        this.category = category;
        this.slots = slots;
    }

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public int[] getSlots() {
        return slots;
    }

    //return                      reset
    //arrow: return to quick buy, barrier: reset to default
}
