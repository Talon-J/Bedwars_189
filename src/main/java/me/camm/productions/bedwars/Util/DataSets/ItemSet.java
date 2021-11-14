package me.camm.productions.bedwars.Util.DataSets;

import me.camm.productions.bedwars.Items.ItemDatabases.GameItem;
import me.camm.productions.bedwars.Util.Helpers.ItemHelper;

/*
This class is a package for configuration of items, preserving slot and the item.
For a list of items that should not appear as the gameItem, refer to the
ItemToolBox class, method isFileRestricted(GameItem item)
 */
public class ItemSet
{
    private final GameItem item;
    private final int slot;

  public ItemSet(GameItem item, int slot)
  {
      this.item = ItemHelper.isFileRestricted(item) ? GameItem.EMPTY_SLOT:item;
      this.slot = slot;
  }

  public int getSlot()
  {
      return slot;
  }

  public GameItem getItem()
  {
      return item;
  }
}







    /*

    public ItemTypes getType(Material mat) {
        ItemTypes type;
        switch (mat) {

            case WOOL:    //blocks
            case ENDER_STONE:
            case WOOD:
            case STAINED_CLAY:
            case HARD_CLAY:
            case LADDER:
            case OBSIDIAN:
                type = ItemTypes.BLOCK;
                break;

            case STICK:    //melee
            case WOOD_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
                type = MELEE;
                break;

            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
                type = ARMOR;
                break;

            case SHEARS:       //wood iron gold dia
            case WOOD_PICKAXE:
            case WOOD_AXE:
            case IRON_PICKAXE:
            case IRON_AXE:
            case GOLD_PICKAXE:
            case GOLD_AXE:
            case DIAMOND_AXE:
            case DIAMOND_PICKAXE:
                type = TOOLS;
                break;

            case BOW:
            case ARROW:
                type = RANGED;
                break;

            case POTION:
                type = POTION;
                break;

            case SPONGE:
            case SNOW_BALL:
            case CHEST:
            case TNT:
            case FIREBALL:
            case ENDER_PEARL:
            case EGG:
            case MILK_BUCKET:
            case MONSTER_EGG:
            case MONSTER_EGGS:
                type = UTILITY;
                break;

            default:
                type = BLOCK;


        }

        return type;
    }




    public synchronized void setSoldItem(ItemStack newSoldItem) {
        this.soldItem = newSoldItem;
    }

    public BattlePlayer getPlayer() {
        return player;
    }


    public synchronized void setPrice(int price) {
        this.price = price;
    }

    public synchronized void setPriceType(Material priceType) {
        this.priceType = priceType;
    }
}



     */




/*


      public void doMaterialTrade()
      {
        ItemStack airHold = new ItemStack(Material.AIR, 1);  //itemstack for placehold

        player.sendMessage("Invoked transaction");

        if (player.getRawPlayer().getInventory().contains(priceType, price)) //if the inv contains the price
        {
            player.sendMessage("Passed amount required");
            int paid = 0;



            if (player.getRawPlayer().getInventory().firstEmpty()!=-1) //if player has space in inv
            {
                //send message
                playSound(false);
                player.sendMessage(ChatColor.RED+"Your inventory is full!");
                return;
            }


            for (int slot = 0; slot < player.getRawPlayer().getInventory().getSize(); slot++)
            {
                player.sendMessage("current slot " + slot);
                if (player.getRawPlayer().getInventory().getItem(slot) != null) //if the item is not air
                {
                    player.sendMessage("Passed null check 1");
                    ItemStack current = player.getRawPlayer().getInventory().getItem(slot);  //get the item

                    if (current != null && current.getItemMeta() != null)
                    {
                        player.sendMessage("Passed null check 2 && inv not full");
                        if (current.isSimilar(new ItemStack(priceType,1))) //if the item&meta is not null
                        {
                            System.out.println("Item is valid.");
                            int amount = current.getAmount(); //get the amount
                            System.out.println("slot:" + slot + " material:" + current.getType() + " init amount:" + current.getAmount());

                            /////////////////////////////
                            while (amount > 0) { //while the amount is > 0
                                paid++;
                                amount--;
                                current.setAmount(amount);  //update the stack

                                if (amount >= 1)  //if the amount is >=1, then update the item in the inv
                                    player.getRawPlayer().getInventory().setItem(slot, current);
                                else
                                    player.getRawPlayer().getInventory().setItem(slot, airHold);

                                System.out.println("Current amount remaining:" + amount);
                                System.out.println("paid " + paid + "/" + price);

                                if (paid >= price) {  // if the paid amount is greater or equal to price [> to catch incase overshoot to exit loop]
                                    System.out.println("if check: paid " + paid + "/" + price);

                                    amount = 0;  //exit the  2 loops
                                    slot = player.getRawPlayer().getInventory().getSize() + 1;


                                    player.getRawPlayer().getInventory().addItem(soldItem);
                                   playSound(true);

                                   //also check for their inv management

                                    ///////////////////////////////////////////////
                                    ////////////////////////Replace with sound packets

                                }

                            }


                        }
                    }//if not null


                }




            }
        } //the player does not have the req amount

        else {
            player.sendMessage("Not enough " + priceType);  //getting the amount that they still need
            int below = 0;
            for (int slot = 0; slot < player.getRawPlayer().getInventory().getSize(); slot++)
            {
                if (player.getRawPlayer().getInventory().getItem(slot) != null) {
                    player.sendMessage("Passed null check 1");
                    ItemStack current = player.getRawPlayer().getInventory().getItem(slot);

                    if (current != null && current.getItemMeta() != null)
                    {
                        player.sendMessage("Passed null check 2");
                        if (current.isSimilar(new ItemStack(priceType,1)))
                        {
                          below = below + current.getAmount();
                        }
                    }
                }
            }

            String name;
            switch (priceType)
            {
                case IRON_INGOT:
                    name = "Iron Ingots!";
                    break;

                case GOLD_INGOT:
                    name = "Gold Ingots";
                    break;

                case EMERALD:
                    name = "Emeralds";
                    break;

                case DIAMOND:
                    name = "Diamonds";
                    break;

                default:
                    name = priceType.name();

            }
            playSound(false);
            player.sendMessage(ChatColor.RED+"Don't have enough "+name+"! Need "+(price-below)+" more!");



          //  player.getWorld().playSound(player.getLocation(),fewer,1,1);
////////////////////////Replace with sound packets

        }
    }//method







    private void playSound(boolean didTrade)
    {
        String string = didTrade ? "NOTE_PLING" : "ENDERMAN_TELEPORT";
        Location loc = player.getRawPlayer().getLocation();

        try {
            PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(string, loc.getX(), loc.getY(), loc.getZ(), 1, 1);
            ((CraftPlayer)player.getRawPlayer()).getHandle().playerConnection.sendPacket(packet);
        }
        catch (Exception e)
        {
            string = didTrade ? "BLOCK_NOTE_PLING" : "ENTITY_ENDERMEN_HURT";
            PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(string, loc.getX(), loc.getY(), loc.getZ(), 1, 1);
            ((CraftPlayer)player.getRawPlayer()).getHandle().playerConnection.sendPacket(packet);

        }
    }

    //PacketPlayOutNamedSoundEffect(String var1, double var2, double var4, double var6, float var8, float var9)





}

 */

