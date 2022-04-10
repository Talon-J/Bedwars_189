package me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;

/**
 * @author CAMM
 * Interface for modelling entities that have an owner
 */
public interface IGameOwnable extends IGameTeamable
{
    BattlePlayer getOwner();
    String getName();
}
