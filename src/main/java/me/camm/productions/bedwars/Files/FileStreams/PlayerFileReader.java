package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Arena.Players.Managers.HotbarManager;
import me.camm.productions.bedwars.Arena.Players.Managers.PlayerInventoryManager;
import me.camm.productions.bedwars.Items.ItemDatabases.ShopItem;
import me.camm.productions.bedwars.Items.ItemDatabases.ItemCategory;
import me.camm.productions.bedwars.Util.DataSets.ItemSet;
import me.camm.productions.bedwars.Util.Helpers.StringToolBox;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static me.camm.productions.bedwars.Items.ItemDatabases.InventoryLocation.HOT_BAR_END;

public class PlayerFileReader extends StringToolBox
{
    private final Plugin plugin;
    private final boolean isInflated;

    private final File inventoryFile;
    private final File barFile;


    public PlayerFileReader(Plugin plugin, Player player, boolean isInflated)
    {
        super(plugin);
        this.plugin = plugin;
        this.isInflated = isInflated;

        this.inventoryFile = new File(getInventoryPath(player));
        this.barFile = new File(getHotBarPath(player));
    }

    public HotbarManager readBarFile()
    {
        HotbarManager manager;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(barFile));
            ArrayList<String> lines = new ArrayList<>();
            String current = reader.readLine();

            while (current!=null)
            {
                current = checkForComments(current);
                lines.add(current);
                current = reader.readLine();
            }
            reader.close();

            ItemCategory[] set = new ItemCategory[HOT_BAR_END.getValue()];

            int currentSlot = 0;
            for (String string: lines)
            {
               string = getKey(string);
               if (string==null)
                   continue;

               try {
                   ItemCategory currentCategory = ItemCategory.valueOf(string);
                   if (currentCategory==ItemCategory.NONE||currentCategory==ItemCategory.NAV)
                       continue;

                   set[currentSlot] = currentCategory;
                   currentSlot++;

                   if (currentSlot==set.length-1)
                       break;
               }
               catch (IllegalArgumentException | NullPointerException ignored)
               {

               }
            }
            manager = new HotbarManager(plugin,set);
            return manager;
        }
        catch (IOException e)
        {
            manager = new HotbarManager(plugin);
            return manager;
        }
    }

    public PlayerInventoryManager readInvFile()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(inventoryFile));

            //deal with the possibility of restricted items in the manager.
            ArrayList<ItemSet> items = new ArrayList<>();
            String current = reader.readLine();

            while (current!=null)
            {
                //check for comments. returns null if entire line is a comment
                current = checkForComments(current);

                //if the entire line is a comment
                if (current==null)
                    continue;

               String key = getKey(current);
               Double slotNumber = getNumber(current);


               if (key==null||slotNumber==null)
                   continue;
               try
               {
                   //get the inventoryitems representation, and put into an itemset
                   ShopItem item = ShopItem.valueOf(key);
                   items.add(new ItemSet(item,slotNumber.intValue()));
               }
               catch (IllegalArgumentException | NullPointerException ignored)
               {

               }

                current = reader.readLine();
            }

            return new PlayerInventoryManager(items, isInflated);
        }
        catch (IOException e)
        {
            return new PlayerInventoryManager(isInflated);
        }
    }

}
