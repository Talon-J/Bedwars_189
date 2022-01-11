package me.camm.productions.bedwars.Items.ItemDatabases;

import org.bukkit.ChatColor;

public enum InventoryName
{
    QUICK_BUY("Quick Buy"),
    TEAM_JOIN(ChatColor.DARK_AQUA+""+ChatColor.BOLD+"Join a Team"),
    TEAM_BUY("Team Upgrades"),
    BLOCKS("Blocks"),
    MELEE("Melee"),
    ARMOR("Armor"),
    TOOLS("Tools"),
    RANGED("Ranged"),
    POTION("Potions"),
    TRACKER("Tracker"),
    HOTBAR_MANAGER("Hotbar Manager"),
    UTILITY("Utility");

    private final String title;

    InventoryName(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
}
