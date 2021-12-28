package me.camm.productions.bedwars.Arena.GameRunning.Events;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.GameRunning.GameRunner;
import me.camm.productions.bedwars.Generators.Generator;
import me.camm.productions.bedwars.Generators.GeneratorType;

public class GeneratorAction extends GameActionPhysical
{

    private final GameRunner runner;
    private final GeneratorType type;


    public GeneratorAction(GameRunner runner, GeneratorType type, String header) {
        this.runner = runner;
        this.type = type;
        spent = false;
        this.header = header;
    }

    @Override
    public void activate()
    {
        if (spent)
            return;

        spent = true;
        Arena arena = runner.getArena();

        int tier = 0;

        if (type==GeneratorType.DIAMOND)
        {
            for (Generator generator : arena.getGenerators())
            {
                if (generator.getGenType()== GeneratorType.DIAMOND)
                {
                    generator.setGeneratorTier(generator.getTier() + 1, generator.getNextNewSpawnTime());
                    tier = generator.getTier();
                }
            }
        }
        else
        {
            for (Generator generator : arena.getGenerators())
            {
                if (generator.getGenType()== GeneratorType.EMERALD)
                {
                    generator.setGeneratorTier(generator.getTier() + 1, generator.getNextNewSpawnTime());
                    tier = generator.getTier();
                }
            }

        }

        String coloredMessage = type == GeneratorType.DIAMOND ?
                GameEventText.UPGRADE_DIAMONDS.getText() : GameEventText.UPGRADE_EMERALDS.getText();

        runner.sendMessage(coloredMessage + tier);
    }

    @Override
    String getHeader() {
        return header;
    }
}
