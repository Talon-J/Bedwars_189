package me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;

public interface IGameOwnable extends IGameTeamable
{
    BattlePlayer getOwner();
    String getName();
}
