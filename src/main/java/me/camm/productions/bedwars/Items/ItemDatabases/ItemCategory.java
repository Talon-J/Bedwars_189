package me.camm.productions.bedwars.Items.ItemDatabases;

import org.bukkit.ChatColor;

public enum ItemCategory   //this is used for interfacing with the shop, specifically the hotbar mngr
{
    BLOCK(ChatColor.AQUA+"Blocks Section"),
    MELEE(ChatColor.AQUA+"Melee Section"),
    ARMOR(ChatColor.AQUA+"Armor Section"),
    TOOLS(ChatColor.AQUA+"Tools Section"),
    RANGED(ChatColor.AQUA+"Ranged Section"),
    POTION(ChatColor.AQUA+"Potion"),
    UTILITY(ChatColor.AQUA+"Utility"),
    NAV(ChatColor.GOLD+"Navigation"),
    SEPARATOR(ChatColor.GRAY+"Separator"),
    NONE(ChatColor.AQUA+"Empty Slot");

    public String type;
   private ItemCategory(String type)
    {
        this.type = type;
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

