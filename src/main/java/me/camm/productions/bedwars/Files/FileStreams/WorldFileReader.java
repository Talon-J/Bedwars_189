package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Files.FileKeywords.DataSeparatorKeys;
import me.camm.productions.bedwars.Files.FileKeywords.WorldFileKeyword;
import me.camm.productions.bedwars.Generators.Generator;
import me.camm.productions.bedwars.Generators.GeneratorType;
import me.camm.productions.bedwars.Util.Helpers.StringHelper;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import me.camm.productions.bedwars.Validation.BedWarsException;
import me.camm.productions.bedwars.Validation.EquationException;
import me.camm.productions.bedwars.Validation.OrderException;
import me.camm.productions.bedwars.Validation.ParameterException;
import org.bukkit.Bukkit;

import org.bukkit.World;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static me.camm.productions.bedwars.Files.FileKeywords.FilePaths.WORLD;


public class WorldFileReader extends StringHelper
{

    private World world;
    private final Plugin plugin;
    private GameBoundary bounds;
    private Coordinate spectatorSpawn;
    private Arena arena;

    private Coordinate generatorSpawn;
    private String generatorType;

    public WorldFileReader(Plugin plugin)
    {
        super(plugin);
       // this.path = path;
        this.plugin = plugin;

        this.world = null;
        bounds =  null;
        arena = null;
    }

    public Arena read() throws BedWarsException
    {
        ArrayList<String> values = new ArrayList<>();
        ArrayList<Generator> generators = new ArrayList<>();
        try {
            int index = 0;
            boolean isArenaComplete = false;
            BufferedReader reader = new BufferedReader(new FileReader(getWorldPath()));
            String valueGiven = reader.readLine();

            while (valueGiven!=null) {
                String commentChecked = checkForComments(valueGiven);
                if (commentChecked!=null)
                values.add(commentChecked);
                valueGiven = reader.readLine();
            }
            reader.close();

            int line = 0;
            for (String string: values)
            {
                line++;
                WorldFileKeyword key = getKeyword(string);
                if (key==null)
                    continue;

                if (index!=key.getIndex()) {
                   throw new OrderException(key.getIndex(), index, string);
                }



                if (!isArenaComplete)
                {
                    switch (key)
                    {
                        case WORLD:
                            String name = getInfoSection(string);
                            if (name == null)
                                throw new ParameterException(WORLD.getValue(),line,"non null value",null);

                            this.world = Bukkit.getWorld(name);
                            if (world == null)
                                throw new EquationException(WORLD.getValue(),line,"non null world",string, null);



                            index = key.getIndex()+1;
                            break;

                        case ARENA_BOUNDS: {
                            double[] numbers = getNumbers(string);
                            if (numbers == null)
                                throw new ParameterException(WORLD.getValue(), line, "valid boundaries for the arena", string);

                            if (numbers.length != 6)
                                throw new ParameterException(WORLD.getValue(), line, "arena bounds to have 6 parameters", "" + string);

                            this.bounds = new GameBoundary(doubleToIntArray(numbers));
                            index = key.getIndex() + 1;
                        }
                            break;

                        case SPEC_SPAWN: {
                            double[] numbers = getNumbers(string);
                            if (numbers == null)
                                throw new ParameterException(WORLD.getValue(),line, "valid coordinates for spectator spawn",string);

                            if (numbers.length != 3)
                                throw new ParameterException(WORLD.getValue(), line, "spectator spawn to have 3 parameters", "" + string);

                            this.spectatorSpawn = new Coordinate(numbers);
                            index = key.getIndex() + 1;
                        }
                            break;

                        case VOID:
                            double[] voidProcessed = getNumbers(string);
                            if (voidProcessed == null)
                                throw new ParameterException(WORLD.getValue(),line, "valid y value for void",string);

                            double voidLevel;
                            if (voidProcessed.length>0)
                                voidLevel = voidProcessed[0];
                            else
                                throw new ParameterException(WORLD.getValue(),line, "void to only have 1 parameter",string);


                            if (this.world!=null&&this.bounds!=null&&spectatorSpawn!=null)
                            {
                                isArenaComplete = true;
                                this.arena = new Arena(bounds,spectatorSpawn,(int)voidLevel,world,plugin);
                                index = 0;
                            }
                            else
                                throw new EquationException(WORLD.getValue(),line, "all values to be defined",string,null);
                            break;
                    }
                }
                else
                {
                    switch (key)
                    {
                        case GENERATOR:
                           index = key.getIndex()+1;
                            break;

                        case GEN_TYPE:
                            this.generatorType = getInfoSection(string);
                            if (generatorType == null)
                                throw new ParameterException(WORLD.getValue(),line, "valid value for generator type",string);

                            if (!(GeneratorType.DIAMOND.getSimpleName().equalsIgnoreCase(generatorType) ||GeneratorType.EMERALD.getSimpleName().equalsIgnoreCase(generatorType)))
                                throw new ParameterException(WORLD.getValue(),line, "either emerald or diamond for generator type",string);

                            index  = key.getIndex()+1;
                            break;

                        case GEN_SPAWN: {
                            double[] numbers = getNumbers(string);
                            if (numbers == null)
                                throw new ParameterException(WORLD.getValue(), line, "valid value for generator spawn", string);

                            if (numbers.length != 3)
                                throw new ParameterException(WORLD.getValue(), line, "generator spawn to have 3 parameters", "" + string);

                            this.generatorSpawn = new Coordinate(numbers);
                            index = key.getIndex() + 1;
                        }

                            break;

                        case GEN_BOX: {
                            double[] numbers = getNumbers(string);
                            if (numbers == null)
                                throw new ParameterException(WORLD.getValue(),line, "valid coordinates for generator box",string);

                            if (numbers.length != 6)
                                throw new ParameterException(WORLD.getValue(), line, "generator area to have 6 parameters", "" + string);

                            GameBoundary generatorBounds = new GameBoundary(doubleToIntArray(numbers));
                            index = 0;

                            // public Generator(double x, double y, double z, World world, String spawning, Plugin plugin, RegisteredBoundary box)
                            if (generatorType != null && generatorSpawn != null && arena != null)
                                generators.add(new Generator(generatorSpawn.getX(), generatorSpawn.getY(), generatorSpawn.getZ(), world, generatorType, plugin, generatorBounds));
                        }
                            break;
                    }
                }
            }

        }
        catch (IOException ignored)
        {

        }
        if (arena!=null)
            arena.setGenerators(generators);
        return arena;
    }


    //Dissects a string and returns a keyword, if possible, from the string part before the ":", trimmed.
    //returns null if the ":" dne
    public WorldFileKeyword getKeyword(String original)
    {
        if (original==null)
            return null;
        int index = original.indexOf(DataSeparatorKeys.DECLARATION.getKey());
        return index==-1? null: toWorldKey((original.substring(0,index)).trim());
    }

    //returns the keyword representation of a dissected string. Else returns null
    private WorldFileKeyword toWorldKey(String dissected)
    {
        if (dissected==null)
            return null;

      WorldFileKeyword[] words = WorldFileKeyword.values();
      for (WorldFileKeyword word: words) {
          if (word.getKey().equalsIgnoreCase(dissected))
              return word;
      }
      return null;
    }
}
