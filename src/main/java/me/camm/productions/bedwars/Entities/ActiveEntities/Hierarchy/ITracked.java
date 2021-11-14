package me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy;

public interface ITracked extends IGameTeamable
{
    void register();
    void unregister();
}
