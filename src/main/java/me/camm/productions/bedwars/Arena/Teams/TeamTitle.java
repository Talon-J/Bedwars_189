package me.camm.productions.bedwars.Arena.Teams;

import org.bukkit.ChatColor;

public enum TeamTitle
{
    BED_DESTROYED("{\"text\":\""+ChatColor.RED+"BED DESTROYED\"}"),
    ALL_BEDS_DESTROYED("{\"text\":\""+ChatColor.RED+"ALL BEDS DESTROYED\"}"),
    YOU_DIED("{\"text\":\""+ChatColor.RED+"You Died!\"}"),

    ALARM("{\"text\":\""+ChatColor.RED+"Alarm!\"}"),
    TRIGGERED("{\"text\":\"Trap Triggered!\",\"color\":\"red\"}"),

    //See playerUtil interface for implementation of RESPAWN_AFTER
    RESPAWN_AFTER("{\"text\":\""+ChatColor.RED+"Respawn in "),
    RESPAWNED("{\"text\":\""+ChatColor.GREEN+"Respawned! \"}"),

    NOW_SPECTATOR("{\"text\":\""+ChatColor.RED+"You are now a Spectator!\"}"),
    LAST_LIFE_WARNING("{\"text\":\""+ChatColor.RED+"You will no longer respawn!\"}");

    private final String message;

    TeamTitle(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
