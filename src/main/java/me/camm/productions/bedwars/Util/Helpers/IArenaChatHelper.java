package me.camm.productions.bedwars.Util.Helpers;

import org.bukkit.plugin.Plugin;

public interface IArenaChatHelper
{

    default void sendMessage(String message, Plugin plugin)
    {
        plugin.getServer().broadcastMessage(message);
    }
}
