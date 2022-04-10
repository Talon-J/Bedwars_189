package me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy;



/**
 * @author CAMM
 * Interface for modelling entities that need to be tracked by the entity action listener
 */
public interface ITracked extends IGameTeamable
{
    void register();
    void unregister();
}
