package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Items.SectionInventories.InventoryConfigurations.TeamInventoryConfig;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import me.camm.productions.bedwars.Util.PacketSound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AlarmTrap extends GameTrap
{

    public AlarmTrap(BattleTeam team, GameBoundary effectiveRange) {
        this.team = team;
        this.bounds = effectiveRange;
    }

    @Override
    public void activate() {
        World world = team.getArena().getWorld();
        ConcurrentHashMap<UUID, BattlePlayer> arenaPlayers = team.getArena().getPlayers();
        ConcurrentHashMap<UUID, BattlePlayer> teamPlayers = team.getPlayers();


        new BukkitRunnable() {
            public void run() {

                BattlePlayer activator = null;

                for (Entity entity: bounds.getCloseEntities(world))
                {
                    if (!teamPlayers.containsKey(entity.getUniqueId()) &&
                            entity instanceof Player &&
                            arenaPlayers.containsKey(entity.getUniqueId())) {

                        BattlePlayer current = arenaPlayers.get(entity.getUniqueId());

                        if ( (System.currentTimeMillis() - current.getLastMilk()) > 30000) {
                            if (activator == null)
                            activator = current;
                            current.getRawPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                        //this should send a packet remove effect to the packet handler, so it should be fine.
                    }
                }

                final BattlePlayer trigger = activator;

                new BukkitRunnable()
                {
                    int iterations = 0;
                    boolean note;
                    @Override
                    public void run()
                    {
                        if (trigger !=null && trigger.getIsAlive()) {

                            Location loc = trigger.getRawPlayer().getLocation();
                            if (bounds.containsCoordinate(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                                if (note)
                                    team.sendTeamSoundPacket(PacketSound.ALARM);
                                else
                                    team.sendTeamSoundPacket(PacketSound.ALARM_TWO);
                                note = !note;
                            }
                        }

                        if (iterations > 120)
                            cancel();

                        iterations ++;



                    }
                }.runTaskTimer(team.getArena().getPlugin(),0,2);

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
        return TeamInventoryConfig.ALARM_TRAP;
    }

    @Override
    public String toString() {
        return "Alarm Trap";
    }
}

//removes invis from players if not drank magic milk for 30 secs
