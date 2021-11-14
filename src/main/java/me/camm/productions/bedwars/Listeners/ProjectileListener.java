package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Entities.ActiveEntities.BedBug;
import me.camm.productions.bedwars.Entities.Consumables.BridgeEgg;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileListener implements Listener
{
    private final Plugin plugin;
    private final Arena arena;
    private final EntityActionListener listener;

    public ProjectileListener(Plugin plugin, Arena arena,EntityActionListener actionListener) {
        this.plugin = plugin;
        this.arena = arena;
        this.listener = actionListener;
    }

    @EventHandler
    public void onEnderPearlTeleport(PlayerTeleportEvent event)
    {
       PlayerTeleportEvent.TeleportCause cause = event.getCause();
       if (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
           event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
    {
        Projectile p = event.getEntity();
        if (!(p instanceof EnderPearl || p instanceof Snowball))
            return;

        ProjectileSource source = p.getShooter();
        if (!(source instanceof Player))
            return;

        Player shooter = (Player)source;






        //TODO code for the enderpearls. (Use vector stuff to prevent them from suffocating.)
        //Arrow code should not be here since we're talking about damage.
    }

    public void handlePearlPlacement()
    {

    }

    public void handleSnowBallHit(Snowball ball, Player shooter)
    {
        ConcurrentHashMap<UUID, BattlePlayer> registeredPlayers = arena.getPlayers();
        if (!registeredPlayers.containsKey(shooter.getUniqueId()))
            return;

        BattlePlayer player = registeredPlayers.get(shooter.getUniqueId());
        BedBug bug = null;



    }



    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event)
    {
        Projectile p = event.getEntity();
        if (p.getShooter() == null || !(p.getShooter() instanceof Player))
            return;

        Player shooter = ((Player) p.getShooter());

        if (!arena.getPlayers().containsKey(shooter.getUniqueId()))
            return;

        if (p instanceof Egg)
            new BridgeEgg((Egg)p,plugin,(byte)arena.getPlayers().get(shooter.getUniqueId()).getTeam().getTeamColor().getValue());






        /*
        TODO also require code for enderpearls and snowballs.
         */
    }
}
