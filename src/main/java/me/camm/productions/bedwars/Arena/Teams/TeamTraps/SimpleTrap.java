package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamTitle;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;




public class SimpleTrap extends GameTrap
{
    private static final String NAME = "Trap";
    public SimpleTrap(BattleTeam team, GameBoundary bounds) {
        this.team = team;
        this.bounds = bounds;
    }

    @Override
    public void activate() {

        World world = team.getArena().getWorld();

        ConcurrentHashMap<UUID, BattlePlayer> players = team.getPlayers();

        Collection<Entity> close = bounds.getCloseEntities(world);

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Entity entity: close)
                {
                    if (!(entity instanceof Player))
                        continue;

                    //If on another team
                    Player player = (Player)entity;

                    if (!players.containsKey(entity.getUniqueId()))
                    {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 0, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,160,0,false,false));
                    }
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
        return TeamInventoryConfig.SIMPLE_TRAP;
    }


    public String name() {
        return NAME;
    }

    @Override
    public TeamTitle getTrapTitle() {
        return TeamTitle.TRIGGERED;
    }
}
