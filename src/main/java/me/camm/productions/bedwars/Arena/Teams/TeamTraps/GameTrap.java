package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Util.Locations.Boundaries.Boundary;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;

public abstract class GameTrap implements Trap {
    protected BattleTeam team;
    protected GameBoundary bounds;
}
