package me.camm.productions.bedwars.Entities.ActiveEntities;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameAutonomous;
import me.camm.productions.bedwars.Listeners.EntityActionListener;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

//Unfinished
public class Dragon implements IGameAutonomous
{
    private final BattleTeam team;
    private EnderDragon dragon;
    private final Arena arena;

    public Dragon(BattleTeam team, Arena arena, Location spawn, EntityActionListener listener) {
        this.team = team;
        this.arena = arena;
    }


    @Override
    public String getName() {
        return dragon == null? null : dragon.getCustomName();
    }

    @Override
    public void spawn() {

    }

    @Override
    public void remove() {
      if (dragon == null)
      return;

      dragon.remove();
    }

    @Override
    public boolean isAlive() {
        return dragon != null && (!dragon.isDead() || dragon.isValid());
    }

    @Override
    public void handleEntityTarget(LivingEntity toTarget) {

    }


    @Override
    public UUID getUUID() {
        return dragon == null ? null: dragon.getUniqueId();
    }

    @Override
    public BattleTeam getTeam() {
        return team;
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }


}
