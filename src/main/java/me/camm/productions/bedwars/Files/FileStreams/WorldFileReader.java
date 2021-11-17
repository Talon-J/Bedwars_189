package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Files.FileKeywords.DataSeparatorKeys;
import me.camm.productions.bedwars.Files.FileKeywords.WorldFileKeyword;
import me.camm.productions.bedwars.Generators.Generator;
import me.camm.productions.bedwars.Util.Helpers.StringToolBox;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Util.Locations.GameBoundary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class WorldFileReader extends StringToolBox
{
   // private final String path;
    private World world;
    private final Plugin plugin;
    private GameBoundary bounds;
    private Coordinate spectatorSpawn;
    private Arena arena;

    private Coordinate generatorSpawn;
    private String generatorType;

    private String line;

    public WorldFileReader(Plugin plugin)
    {
        super(plugin);
       // this.path = path;
        this.plugin = plugin;
        this.line = null;

        this.world = null;
        bounds =  null;
        arena = null;
    }

    public Arena read()
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

            for (String string: values)
            {
                this.line = string;
                WorldFileKeyword key = getKeyword(string);
                if (key==null)
                    continue;

                if (index!=key.getIndex()) {
                    printConfigErrorReport(string, getWorldPath(),key,index);
                    continue;
                }



                if (!isArenaComplete)
                {
                    switch (key)
                    {
                        case WORLD:
                            this.world = Bukkit.getWorld(getInfoSection(string));
                            index = key.getIndex()+1;
                            break;

                        case ARENA_BOUNDS:
                            this.bounds = new GameBoundary(doubleToIntArray(getNumbers(string)));
                            index = key.getIndex()+1;
                            break;

                        case SPEC_SPAWN:
                            this.spectatorSpawn = new Coordinate(getNumbers(string));
                            index = key.getIndex()+1;
                            break;

                        case VOID:
                            double[] voidProcessed = getNumbers(string);

                            double voidLevel = 0;
                            if (voidProcessed.length>0)
                                voidLevel = voidProcessed[0];

                            if (this.world!=null&&this.bounds!=null&&spectatorSpawn!=null)
                            {
                                isArenaComplete = true;
                                this.arena = new Arena(bounds,spectatorSpawn,(int)voidLevel,world,plugin);
                                index = 0;
                            }
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
                            index  = key.getIndex()+1;
                            break;

                        case GEN_SPAWN:
                            this.generatorSpawn = new Coordinate(getNumbers(string));
                            index =  key.getIndex()+1;

                            break;

                        case GEN_BOX:
                            GameBoundary generatorBounds = new GameBoundary(doubleToIntArray(getNumbers(string)));
                            index = 0;

                            // public Generator(double x, double y, double z, World world, String spawning, Plugin plugin, RegisteredBoundary box)
                            if (generatorType!=null&&generatorSpawn!=null&&arena!=null)
                                generators.add(new Generator(generatorSpawn.getX(), generatorSpawn.getY(), generatorSpawn.getZ(), world, generatorType,plugin, generatorBounds));
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
        int index = original.indexOf(DataSeparatorKeys.DECLARATION.getKey());
        return index==-1? null: toWorldKey((original.substring(0,index)).trim());
    }

    //returns the keyword representation of a dissected string. Else returns null
    private WorldFileKeyword toWorldKey(String dissected)
    {
      WorldFileKeyword[] words = WorldFileKeyword.values();
      for (WorldFileKeyword word: words) {
          if (word.getKey().equalsIgnoreCase(dissected))
              return word;
      }
      return null;
    }

    //changes a string to a double. Returns 0 and prints an error message if fails.
    @Override
    public double toNumber(String format)
    {
        try
        {
            return Double.parseDouble(format);
        }
        catch (NumberFormatException e)
        {
            printFormatWarning(line);
            return 0;
        }
    }

    private void printConfigErrorReport(String errorWhere, String fileName, WorldFileKeyword expected, int received)
    {
        ConsoleCommandSender sender = this.plugin.getServer().getConsoleSender();
        sender.sendMessage(ChatColor.RED+"=======ERROR REPORT======");
        sender.sendMessage(ChatColor.RED+"Error Type: Configuration Error");
        sender.sendMessage(ChatColor.AQUA+"We expected to get "+expected.getKey()+" since it is the next word we need from the config");
        sender.sendMessage(ChatColor.AQUA+"Since it's order number is "+expected.getIndex()+" but we received:");
        sender.sendMessage(ChatColor.AQUA+errorWhere+" with the order number "+received);
        sender.sendMessage(ChatColor.AQUA+"We may not be able to setup the arena in this case.");
        sender.sendMessage(ChatColor.GOLD+"File location name: "+fileName);
        sender.sendMessage(ChatColor.RED+"=======END OF REPORT======");

    }

    private void printFormatWarning(String line)
    {
        ConsoleCommandSender sender = this.plugin.getServer().getConsoleSender();
        sender.sendMessage(ChatColor.GOLD+"====Warning====");
        sender.sendMessage(ChatColor.GOLD+"Format error detected:");
        sender.sendMessage(ChatColor.GOLD+line+"<--Here");
    }
}
