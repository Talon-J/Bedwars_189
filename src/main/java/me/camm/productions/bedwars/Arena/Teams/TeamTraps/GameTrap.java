package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;

/**
 * @author CAMM
 * Abstract class for traps
 */
public abstract class GameTrap implements ITrap {
    protected BattleTeam team;
    protected GameBoundary bounds;

}
