package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamTitle;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;

public interface Trap
{
    /* Activates the trap*/
    void activate();

    BattleTeam getTeam();

    /* get the configuration item for the trap */
    TeamInventoryConfig getTrapConfig();

    TeamTitle getTrapTitle();
}
