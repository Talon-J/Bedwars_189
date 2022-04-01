package me.camm.productions.bedwars.Arena.Players.DeathMessages;

import me.camm.productions.bedwars.Arena.Players.BattlePlayer;
import me.camm.productions.bedwars.Entities.ActiveEntities.Hierarchy.IGameTeamable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

public class DeathFormatter
{

    private final static Random rand;
    private final static DirectDeathMessage[] messages;

    static {
        rand = new Random();
        messages = DirectDeathMessage.values();
    }

    public static String format(Player victim){
         return victim.getName()+" died";
    }

    public static String format(BattlePlayer victim, Cause cause) {
        return victim.getTeam().getTeamColor().getChatColor()+
                victim.getRawPlayer().getName()+ChatColor.RESET+" "+cause.format()+".";
    }

    public static String formatDirectDeath(BattlePlayer victim, BattlePlayer killer){
        return victim.getTeam().getTeamColor().getChatColor()+victim.getRawPlayer().getName()+
                ChatColor.RESET+" "+messages[rand.nextInt(messages.length)].getMessage()+" "+
                killer.getTeam().getTeamColor().getChatColor()+killer.getRawPlayer().getName()
                +ChatColor.RESET+".";
    }

    public static String format(Player victim, IGameTeamable teamable){
        return victim.getName()+" "+Cause.NORMAL.format()+" by "+teamable.getType()+".";
    }


    public static String format(BattlePlayer victim, BattlePlayer killer, Cause cause){
        return victim.getTeam().getTeamColor().getChatColor()+
                victim.getRawPlayer().getName()+ChatColor.RESET+" "+cause.format()+" by "+
                killer.getTeam().getTeamColor().getChatColor()+killer.getRawPlayer().getName()+
                ChatColor.RESET+".";
    }

   public static String format(BattlePlayer victim, BattlePlayer killer, Cause cause, IGameTeamable initiator){
        return victim.getTeam().getTeamColor().getChatColor()+
                victim.getRawPlayer().getName()+ChatColor.RESET+" "+cause.format()+" by "+
                killer.getTeam().getTeamColor().getChatColor()+
                killer.getRawPlayer().getName()+"'s "+initiator.getType()+ChatColor.RESET+".";
   }

   public static String format(Player victim, Player killer, IGameTeamable teamable){
        return victim.getName()+" "+messages[rand.nextInt(messages.length)].getMessage()+" "+killer.getName()+"'s "+teamable.getType();
   }


   //this is for natural actions
   public static Cause toNaturalCause(EntityDamageEvent.DamageCause cause){
        Cause damage = Cause.NORMAL;

        switch (cause) {
            case VOID:
                damage = Cause.VOID_NATURAL;
                break;

            case FALL:
                damage = Cause.FALL_NATURAL;
                break;

            case FIRE:
            case LAVA:
            case FIRE_TICK:
                damage = Cause.HEAT;
                break;

            case DROWNING:
                damage = Cause.WATER_NATURAL;
                break;

            case SUFFOCATION:
                damage = Cause.SUFFOCATE_NATURAL;
                break;
        }

        return damage;
   }

    //this is not for entity actions, only the end action
   public static Cause toInitiatedCause(EntityDamageEvent.DamageCause cause){
        Cause damage = Cause.NORMAL;

        switch (cause) {
            case DROWNING:
                damage = Cause.WATER;
                break;

            case FIRE_TICK:
            case LAVA:
            case FIRE:
                damage = Cause.HEAT;
                break;

            case VOID:
                damage = Cause.VOID;
                break;

            case SUFFOCATION:
                damage = Cause.SUFFOCATE;
                break;

            case FALL:
                damage = Cause.FALL;
                break;
        }
        return damage;

   }

}




    /*

    entityVictim was deathCause by entityKiller's entityInitiator

    entityVictim deathCauseNatural

    entityVictim was deathCause by entityKiller

    entityVictim directMessage by entityKiller

     */

