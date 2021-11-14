package me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy;

import me.camm.productions.bedwars.Arena.Teams.BattleTeam;

import java.util.UUID;

public interface IGameTeamable
{
    UUID getUUID();
    BattleTeam getTeam();
    String getName();
}


/*
                IOwnable ------------------------------|
                getOwner()                             |
                ^                                      |
                |                                      --------> ILifeTimed
IGameTeamable --|                                      |         handleLifeTime()
getUUID()       v                                      |
getTeam()      ITrackable  ------> IAutonomous --------|
getName()       register()           isAlive()
                unregister()         spawn()
                                     remove()





 */