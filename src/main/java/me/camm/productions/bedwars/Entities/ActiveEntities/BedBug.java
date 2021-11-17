package me.camm.productions.bedwars.Entities.ActiveEntities;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.ILifeTimed;
import me.camm.productions.bedwars.Listeners.EntityActionListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Silverfish;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class BedBug implements ILifeTimed
{
    private final BattleTeam team;
    private final BattlePlayer owner;
    private final Arena arena;
    private Silverfish bug;
    private final Location loc;

    private final EntityActionListener listener;

    private static final int MAX_TIME;

    private int aliveTime;

    static {
        MAX_TIME = 15;
    }

    public BedBug(BattleTeam team, BattlePlayer owner,Arena arena,EntityActionListener listener,Location loc) {
        this.team = team;
        this.owner = owner;
        this.arena = arena;
        this.aliveTime = MAX_TIME;
        this.listener = listener;
        this.loc = loc;
    }

    @Override
    public void spawn()
    {
        World world = arena.getWorld();
        new BukkitRunnable()
        {
            @Override
            public void run() {
                bug = world.spawn(loc,Silverfish.class);
                bug.setCustomName(team.getTeamColor().getName()+"'s"+"Bed bug");
                bug.setCustomNameVisible(true);
                register();
            }
        }.runTask(arena.getPlugin());
        handleLifeTime();


    }



    public synchronized void handleEntityTarget(LivingEntity toTarget)
    {
        bug.setTarget(toTarget);
    }

    @Override
    public void handleLifeTime()
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                if (bug.isDead() || !bug.isValid())
                {
                    unregister();
                    cancel();
                    return;
                }

                aliveTime --;
                if (aliveTime <=0)
                {
                    unregister();
                    remove();
                    cancel();
                }

            }
        }.runTaskTimer(arena.getPlugin(),0,20);
    }


    @Override
    public void remove()
    {
        bug.remove();
    }


    public UUID getUUID()
    {
        return bug==null? null: bug.getUniqueId();
    }

    public BattlePlayer getOwner()
    {
        return owner;
    }

    @Override
    public boolean isAlive() {
        return bug != null && !bug.isDead();
    }


    @Override
    public BattleTeam getTeam() {
        return team;
    }

    @Override
    public String getName() {
        return bug == null ? null: bug.getCustomName();
    }

    @Override
    public void register() {
       listener.addEntity(this);
    }

    @Override
    public void unregister() {
     listener.removeEntity(this.getUUID());
    }
}
