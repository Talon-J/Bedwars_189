package me.camm.productions.bedwars.Entities.ActiveEntities;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameAutonomous;
import me.camm.productions.bedwars.Listeners.EntityActionListener;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

//Unfinished
//Still need to do the nms methods
public class Dragon extends EntityEnderDragon implements IGameAutonomous
{
    private final BattleTeam team;
    private final Arena arena;
    private final Location orbitCentre;
    private final World world;
    private final EntityActionListener listener;

    public Dragon(BattleTeam team, Arena arena, Location spawn, Location orbit, EntityActionListener listener) {
        super(((CraftWorld)arena.getWorld()).getHandle());
        this.listener = listener;
        this.team = team;
        this.arena = arena;
        this.orbitCentre = orbit;
        this.world = ((CraftWorld)(arena.getWorld())).getHandle();
        setPosition(spawn.getX(),spawn.getY(),spawn.getZ());
        this.dimension = 0;
        this.dead = false;
    }



    @Override
    public String getName() {
        return hasCustomName() ? getCustomName(): null;
    }

    @Override
    public void spawn() {
        register();
     world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void remove() {
     world.removeEntity(this);
    }

    @Override
    public boolean isAlive() {
        return this.getHealth() > 0;
    }

    @Override
    public void handleEntityTarget(Entity entity) {
        this.target = entity == null? null:((CraftEntity)entity).getHandle();
    }

    @Override
    public UUID getUUID() {
        return uniqueID;
    }

    @Override
    public BattleTeam getTeam() {
        return team;
    }

    @Override
    public void register() {
        listener.addEntity(this);
    }

    @Override
    public void unregister() {
    listener.removeEntity(uniqueID);
    }


}
