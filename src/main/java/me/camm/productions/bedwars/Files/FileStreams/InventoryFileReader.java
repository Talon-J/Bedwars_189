package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Util.Helpers.StringToolBox;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InventoryFileReader extends StringToolBox
{
    private final Player player;
    private final Plugin plugin;
    public InventoryFileReader(Plugin plugin, Player player)
    {
        super(plugin);
        this.plugin = plugin;
        this.player = player;
    }

    public HashMap<Integer, ShopItem> read()
    {
        final int UPPER_BOUND = 18;  //greater than 18, less than 44
        final int LOWER_BOUND = 44;
        /*
        FIREBALL: 1
        PICKAXE: 2

         */


        HashMap<Integer, ShopItem> setup = new HashMap<>();
        ShopItem[] items = ShopItem.values();

      try {
            BufferedReader reader = new BufferedReader(new FileReader(getInventoryPath(player)));
            ArrayList<String> values = new ArrayList<>();

            String current = reader.readLine();
            while (current!=null)
            {
               current =  checkForComments(current);
               if (current!=null)
                   values.add(current);
            }

            for (String string: values)
            {
            }


        }
        catch (IOException ignored)
        {

        }
        return null;
    }


}
