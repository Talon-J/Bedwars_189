package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamTitle;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OffensiveTrap extends GameTrap
{
    public OffensiveTrap(BattleTeam team, GameBoundary bounds) {
        this.team = team;
        this.bounds = bounds;
    }

    @Override
    public void activate()
    {
        World world = team.getArena().getWorld();
        ConcurrentHashMap<UUID, BattlePlayer> teamPlayers = team.getPlayers();

        new BukkitRunnable(){
            @Override
            public void run() {

                   for (BattlePlayer player: teamPlayers.values()) {

                       Player current = player.getRawPlayer();
                       //t(PotionEffectType type, int duration, int amplifier, boolean ambient)
                       current.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1, false,false));
                       current.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 1, false,false));
                   }

                cancel();
            }
        }.runTask(team.getArena().getPlugin());




    }

    @Override
    public BattleTeam getTeam() {
        return team;
    }

    @Override
    public TeamInventoryConfig getTrapConfig() {
        return TeamInventoryConfig.OFFENSE_TRAP;
    }
    //spd and jump to those on same team.


    @Override
    public String toString() {
        return "Counter-offensive trap";
    }

    @Override
    public TeamTitle getTrapTitle() {
        return TeamTitle.TRIGGERED;
    }
}
