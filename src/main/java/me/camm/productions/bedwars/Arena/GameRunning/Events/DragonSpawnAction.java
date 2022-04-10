package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Arena.Players.Scoreboards.ScoreBoardHeader;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Entities.ActiveEntities.GameDragon;
import me.camm.productions.bedwars.Listeners.EntityActionListener;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;


import java.util.Collection;


/**
 * @author CAMM
 * This class models a dragon spawning action.
 */
public class DragonSpawnAction extends GameActionPhysical {
    private final GameRunner runner;

    //constructor
    public DragonSpawnAction(GameRunner runner) {
        this.runner = runner;
        spent = false;
        header = ScoreBoardHeader.SUDDEN_DEATH_HEADER.getPhrase();
    }

    @Override
    public void activate() {

        //if the event has already been activated, we do not want to activate it again
        if (spent)
            return;
        spent = true;

        EntityActionListener listener = runner.getDamageListener();
        World world = runner.getArena().getWorld();

       final Coordinate centre = runner.getArena().getBounds().getCoordinateAverage();
       final double y = centre.getY();

        Collection<BattleTeam> teams = runner.getArena().getTeams().values();

        teams.forEach(battleTeam ->
        {

            //BLOCK with label
            CURRENT_TEAM:
            {
                if (battleTeam.isEliminated())
                    break CURRENT_TEAM;
                Coordinate current = battleTeam.getBed().getCoordinateAverage();
                double delta = Math.abs(current.getY() - y);

                int iterations = battleTeam.getDragonSpawnNumber();
                sender.sendMessage(battleTeam.getTeamColor().getChatColor() + " +" + iterations + " " + battleTeam.getTeamColor().getName() + " dragon");

                while (iterations > 0) {
                    Location spawn = new Location(world, current.getX(), current.getY() + (delta * iterations), current.getZ());
                    GameDragon dragon = new GameDragon(((CraftWorld) world).getHandle(), spawn, runner.getArena(), battleTeam, new Location(world, centre.getX(), centre.getY(), centre.getZ()), listener);
                   // battleTeam.sendTeamMessage("[DEBUG] Dragon spawned at :" + spawn.getX() + " " + spawn.getY() + " " + spawn.getZ());
                    dragon.spawn();
                    dragon.register();


                    iterations--;
                }
            }
        });


    }

    @Override
    String getHeader() {
        return header;
    }
}
