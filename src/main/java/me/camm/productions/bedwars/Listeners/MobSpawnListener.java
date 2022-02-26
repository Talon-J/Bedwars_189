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
       switch (type) {
           case CHICKEN:
           case WITHER:
           case BAT:
           case SLIME:
           case WITCH:
           case ZOMBIE:
           case CREEPER:
           case ENDERMAN:
           case SPIDER:
           case SKELETON:
           case MAGMA_CUBE:
           case CAVE_SPIDER:
           case BLAZE:
           case GHAST:
           case GUARDIAN:
               event.setCancelled(true);
       }
    }
}
