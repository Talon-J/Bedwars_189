package me.camm.productions.bedwars.Entities.ActiveEntities;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameOwnable;
import me.camm.productions.bedwars.Listeners.EntityActionListener;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class GameTNT implements IGameOwnable
{
    private final BlockPlaceEvent event;
    private TNTPrimed tnt;
    private final BattlePlayer owner;
    private final BattleTeam team;
    private final EntityActionListener actionListener;


    public GameTNT(BlockPlaceEvent event, BattlePlayer player,EntityActionListener actionListener)
    {
        this.event = event;
        this.owner = player;
        this.team = player.getTeam();
        this.actionListener = actionListener;
        spawn();
    }

    private void spawn()
    {
        Block block = event.getBlockPlaced();
        World world;
        if (!event.isCancelled())  //if the event is not cancelled by another plugin [Register block]
        {
            if (block.getType()== Material.TNT)
            {
                world = block.getWorld();
                block.setType(Material.AIR);
                tnt = world.spawn(block.getLocation().add(0.5,0,0.5), TNTPrimed.class);

                actionListener.addEntity(this);

                tnt.setFuseTicks(54);
                tnt.setYield((float)6.0);
                tnt.setVelocity(new Vector( ((Math.random()*0.3)-0.15)*0.2,0.3, ((Math.random()*0.3)-0.15)*0.2));
            }
        }

    }

    @Override
    public UUID getUUID() {
        return tnt == null ? null : tnt.getUniqueId();
    }

    @Override
    public BattleTeam getTeam() {
        return team;
    }

    @Override
    public BattlePlayer getOwner() {
        return owner;
    }

    public String getName() {
        return tnt == null ? null: tnt.getCustomName();
    }
}
