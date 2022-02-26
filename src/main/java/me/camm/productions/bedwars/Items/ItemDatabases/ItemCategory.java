package me.camm.productions.bedwars.Items.ItemDatabases;

import org.bukkit.ChatColor;

public enum ItemCategory   //this is used for interfacing with the shop, specifically the hotbar mngr
{
    BLOCK(ChatColor.AQUA+"Blocks Section",ShopItem.BLOCKS_NAV),
    MELEE(ChatColor.AQUA+"Melee Section", ShopItem.MELEE_NAV),
    ARMOR(ChatColor.AQUA+"Armor Section",ShopItem.ARMOR_NAV),
    TOOLS(ChatColor.AQUA+"Tools Section",ShopItem.TOOLS_NAV),
    RANGED(ChatColor.AQUA+"Ranged Section",ShopItem.RANGED_NAV),
    POTION(ChatColor.AQUA+"Potion",ShopItem.POTIONS_NAV),
    UTILITY(ChatColor.AQUA+"Utility",ShopItem.UTILITY_NAV),
    NAV(ChatColor.GOLD+"Navigation",null),
    OPERATOR(ChatColor.GOLD+"Operator",null),
    TRACKER("Tracker",ShopItem.TRACKER_NAV),
    SEPARATOR(ChatColor.GRAY+"Separator",null),
    NONE(ChatColor.AQUA+"Empty Slot",null);

    public String type;
    public ShopItem associate;
   ItemCategory(String type, ShopItem associate)
    {
        this.type = type;
        this.associate = associate;
    }

    @Override
    public String toString()
    {
        return this.name();
    }



}

    /*
    BLOCK
MELEE
ARMOR
TOOLS
RANGED
POTION
UTILITY
     */

