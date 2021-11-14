package me.camm.productions.bedwars.Util.DataSets;

import org.bukkit.event.entity.EntityDamageByEntityEvent;


/*
class for checking when a player was last damaged so we can associate
killers with victims.

note that Entitydamagebyentityevent is a subclass of entitydamageevent.
 */
public class DamageSet
{
    private final EntityDamageByEntityEvent event;
    private final Long systemTime;

    //maybe refactor to add context? (E.g x was fireballed off the edge by y)
    /*
    Death reason: falling into void
    Why falling: fireball
    fireball owner: y

     */

    public DamageSet(EntityDamageByEntityEvent event, Long systemTime) {
        this.event = event;
        this.systemTime = systemTime;
    }

    public EntityDamageByEntityEvent getEvent() {
        return event;
    }

    public Long getSystemTime() {
        return systemTime;
    }
}
