package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Teams.BattleTeam;

public interface Trap
{
    public void activate();
    public void coolDown(BattleTeam team);
}
