package me.camm.productions.bedwars.Listeners;


import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;


public class MobSpawnListener implements Listener
{


    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event)
    {
       EntityType type = event.getEntityType();
       if (type==EntityType.CHICKEN)
           event.setCancelled(true);
    }
}
