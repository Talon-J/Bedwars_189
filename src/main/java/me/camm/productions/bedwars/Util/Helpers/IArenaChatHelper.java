package me.camm.productions.bedwars.Util.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface IArenaChatHelper
{

    default void sendMessage(String message, Plugin plugin)
    {
        plugin.getServer().broadcastMessage(message);
    }

    //This was made due to the Log4j stuff
    default void sendMessage(String message)
    {
        for (Player player: Bukkit.getOnlinePlayers())
            player.sendMessage(message);

    }
}
