package me.camm.productions.bedwars.Entities.ActiveEntities;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.ILifeTimed;
import me.camm.productions.bedwars.Listeners.EntityActionListener;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class Golem implements ILifeTimed
{
    private final BattleTeam team;
    private final BattlePlayer owner;
    private final Arena arena;
    private IronGolem golem;
    private final EntityActionListener listener;

    private static final int MAX_TIME;
    private int timeLeft;
    private final Location toSpawn;

    static {
        MAX_TIME = 120;
    }

    public Golem(BattleTeam team, BattlePlayer owner, Arena arena,Location toSpawn,EntityActionListener listener) {
        this.team = team;
        this.owner = owner;
        this.arena = arena;
        this.timeLeft = MAX_TIME;
        this.toSpawn = toSpawn;
        this.listener = listener;
    }



    @Override
    public void handleLifeTime()
    {
        new BukkitRunnable()
        {
            @Override
            public void run() {

                timeLeft --;
                if (timeLeft <=0 || golem.isDead()) {
                    unregister();
                    cancel();
                }
            }
        }.runTaskTimer(arena.getPlugin(),0,20);

    }

    @Override
    public void handleEntityTarget(LivingEntity toTarget) {
        golem.setTarget(toTarget);

    }

    @Override
    public void remove() {
      golem.remove();
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public BattlePlayer getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return golem == null? null: golem.getCustomName();
    }

    @Override
    public void spawn() {

            new BukkitRunnable()
            {
                @Override
                public void run() {
                    golem = arena.getWorld().spawn(toSpawn,IronGolem.class);
                    golem.setPlayerCreated(false);
                    golem.setCustomName(team.getTeamColor().getName()+"'s Dream Defender");
                    golem.setHealth(20);
                    register();
                    handleLifeTime();
                }
            }.runTask(arena.getPlugin());
            handleLifeTime();


    }

    @Override
    public UUID getUUID() {
        return golem==null ? null : golem.getUniqueId();
    }

    @Override
    public BattleTeam getTeam() {
        return team;
    }


    @Override
    public void register()
    {
     listener.addEntity(this);
    }

    @Override
    public void unregister() {
        if (golem!=null)
      listener.removeEntity(golem.getUniqueId());
    }
}
