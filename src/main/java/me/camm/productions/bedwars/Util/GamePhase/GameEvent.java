package me.camm.productions.bedwars.Util.GamePhase;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Generators.Generator;
import me.camm.productions.bedwars.Generators.GeneratorType;

import static me.camm.productions.bedwars.Arena.Teams.TeamTitle.ALL_BEDS_DESTROYED;

/*

Unfinished.
The event for spawning dragons does not do so yet. (I need to code that) - Camm
 */

public class GameEvent
{
    private final GameEventPair event;

    public GameEvent(GameEventPair event)
    {
        this.event = event;
    }

    public GameEventPair getEvent()
    {
        return event;
    }


    public void activate(Arena arena, GameRunner runner)
    {
        GameEventText action = event.getTextAction();
        switch (action)
        {
            case UPGRADE_DIAMONDS: {
                boolean isAnnounced = false;
                for (Generator generator : arena.getGenerators())
                {
                    if (generator.getGenType()== GeneratorType.DIAMOND)
                    {
                        generator.setGeneratorTier(generator.getTier() + 1, generator.getNextNewSpawnTime());
                        if (!isAnnounced)
                        {
                            isAnnounced = true;
                            sendMessage(arena, event.getTextAction().getText() + (generator.getTier()));
                        }
                    }
                }
            }
                break;
            case UPGRADE_EMERALDS: {
                boolean isAnnounced = false;
                for (Generator generator : arena.getGenerators()) {
                    if (generator.getGenType() == GeneratorType.EMERALD)
                        generator.setGeneratorTier(generator.getTier() + 1, generator.getNextNewSpawnTime());
                    if (!isAnnounced) {
                        isAnnounced = true;
                        sendMessage(arena, event.getTextAction().getText() + (generator.getTier()));
                    }
                }
            }
                break;

            case BED_DESTROY_SCHEDULED_WARNING:
            case GAME_END_SCHEDULED_WARNING:
            case DRAGON_SPAWN_SCHEDULED_WARNING:
            {
                sendMessage(arena,event.getTextAction().getText());
            }
                break;

            case DESTROY_BEDS:
            {
                sendMessage(arena, event.getTextAction().getText());
                arena.getTeams().forEach((string, battleTeam) -> {

                    if (!battleTeam.doesBedExist())
                        battleTeam.sendTeamTitle(ALL_BEDS_DESTROYED.getMessage(), "",10,40,10);  //say that all beds have been destroyed

                    battleTeam.putOnLastStand();
                });
            }
                break;


            case SPAWN_DRAGONS:
                sendMessage(arena, "[DEBUG]: SPAWN DRAGONS NOW");

                //You would summon dragons here.
                break;


            case GAME_END_TEXT:
            {
                sendMessage(arena,event.getTextAction().getText());
                runner.endGame(null);
            }
                break;
        }

    }

    private void sendMessage(Arena arena, String message)
    {
        arena.getPlugin().getServer().broadcastMessage(message);
    }

}
