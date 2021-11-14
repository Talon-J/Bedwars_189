package me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy;

public interface ILifeTimed extends IGameOwnable,IGameAutonomous
{
  void handleLifeTime();
}
