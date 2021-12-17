package me.camm.productions.bedwars.Listeners;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Entities.ActiveEntities.BedBug;
import me.camm.productions.bedwars.Entities.Consumables.BridgeEgg;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

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

        ProjectileSource source = p.getShooter();
        if (!(source instanceof Player))
            return;

        Player shooter = (Player)source;

        if (p instanceof EnderPearl)
            handlePearlPlacement((EnderPearl) p, shooter);
        else if (p instanceof Snowball)
            handleSnowBallHit((Snowball)p, shooter);

        //So now it's either a pearl, snowball, or arrow.
        //we'll handle arrows in the damage listener.

    }

    public void handlePearlPlacement(EnderPearl pearl, Player shooter)
    {
        //please test this. Not finished.
        Vector direction = pearl.getVelocity().normalize().multiply(-1);
        Vector origin = pearl.getLocation().toVector();
        Location loc = pearl.getLocation();
        double distance = shooter.getLocation().distance(loc);
        World w = shooter.getWorld();

        int travelled = 0;
        do
        {
            Location foot = direction.add(origin).toLocation(w);
            Block bottom = foot.getBlock();
            Block top = foot.clone().add(0,1,0).getBlock();

            if (!bottom.getType().isSolid() || !top.getType().isSolid())
            {
                shooter.teleport(foot, PlayerTeleportEvent.TeleportCause.PLUGIN);
                break;
            }

            origin.add(direction.clone());
            travelled += 1;
        }
        while (travelled < distance);
    }

    public void handleSnowBallHit(Snowball ball, Player shooter)
    {
        ConcurrentHashMap<UUID, BattlePlayer> registeredPlayers = arena.getPlayers();
        if (!registeredPlayers.containsKey(shooter.getUniqueId()))
            return;

        BattlePlayer player = registeredPlayers.get(shooter.getUniqueId());
        BedBug bug = new BedBug(player.getTeam(),player, arena,listener,ball.getLocation());
        bug.spawn();
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
