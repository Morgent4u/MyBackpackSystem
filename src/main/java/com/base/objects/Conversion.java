package com.base.objects;

import com.backpack.utils.MBS;
import com.base.ancestor.Objekt;
import com.base.sys.Sys;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Map;

/**
 * @Created 08.08.2022
 * @Author Nihar
 * @Description
 * This java-class has been created
 * to convert several data-types into
 * other data-types.
 * The conversion of data-types has been
 * classified by the MRS.
 */
public class Conversion extends Objekt
{
    /* ************************* */
    /* OBJECT - METHODS */
    /* ************************* */

    /**
     *  This method is used to return all necessary information
     *  about an item-stack as object-array for the database-insertion!
     * @param item A minecraft-item.
     * @return An object-array with following order:<br/>
     *         Material/Type - As String<br/>
     *         Display-name - As String (can be empty)<br/>
     *         Lore - As one big string (can be NULL)<br/>
     *         Enchant - As one big string (can be NULL)<br/>
     *         Amount - As integer
     */
    public Object[] of_convertItemStack2DatabaseItemStackInformation(ItemStack item)
    {
        if(item != null)
        {
            String material = item.getType().toString().toUpperCase();
            String displayName = "";
            String loreString = null;
            String enchantString = null;
            int amount = item.getAmount();
            int durability = item.getDurability();

            ItemMeta meta = item.getItemMeta();

            if(meta != null)
            {
                loreString = Sys.of_getList2OneBigStringByAddingSeparatorAsPattern(meta.getLore(), "<Lore/>");
                displayName = meta.getDisplayName().replace("§", "&");
                if(loreString != null)
                {
                    loreString = loreString.replace("§", "&");
                }

                //  Check for enchants...
                Map<Enchantment, Integer> enchants = meta.getEnchants();

                if(!enchants.isEmpty())
                {
                    //  Define some attributes.
                    StringBuilder enchantBuilder = new StringBuilder();
                    int size = enchants.size();
                    int enchantCounter = 0;

                    for(Enchantment enchant : enchants.keySet())
                    {
                        String enchantName = enchant.getName().toUpperCase();
                        int enchantLevel = enchants.get(enchant);

                        //  Add the current enchant + level to the large string.
                        enchantBuilder.append(enchantName).append("<EnchantLvl/>").append(enchantLevel);

                        if(enchantCounter != ( size - 1 ))
                        {
                            enchantBuilder.append("<Enchant/>");
                        }

                        enchantCounter++;
                    }

                    enchantString = enchantBuilder.toString();
                }
            }

            return new Object[] { material, displayName, loreString, enchantString, amount, durability };
        }

        return null;
    }

    public ItemStack of_convertDatabaseItemStack2ItemStack(String type, String displayName, String loreString, String enchantString, int amount, int durability)
    {
        ItemStack item = null;
        Material material = Material.getMaterial(type);

        if(material != null)
        {
            //  We need to handle null-values.
            if(displayName.equals("null"))
            {
                displayName = "";
            }

            if(loreString == null || loreString.equals("null"))
            {
                loreString = null;
            }

            if(enchantString == null || enchantString.equals("null"))
            {
                enchantString = null;
            }

            //  Define the lore.
            String[] lore = null;

            if(loreString != null)
            {
                lore = loreString.split("<Lore/>");

                for(int i = 0; i < lore.length; i++)
                {
                    lore[i] = lore[i].replace("&", "§");
                }
            }

            //  Create the item-stack.
            item = MBS.of_getInventoryService().of_createItemStack(material, displayName.replace("&", "§"), lore, amount);

            if(item != null)
            {
                //  Check for enchantments.
                ItemMeta meta = item.getItemMeta();

                if(meta != null)
                {
                    Damageable damageable = (Damageable) meta;
                    damageable.setUnbreakable(false);
                    damageable.setDamage(durability);
                    item.setItemMeta(meta);

                    if(enchantString != null)
                    {
                        String[] enchant = enchantString.split("<Enchant/>");

                        //  Iterate through all enchantments...
                        for(String enchantAttribute : enchant)
                        {
                            String[] enchantAttributes = enchantAttribute.split("<EnchantLvl/>");

                            if(enchantAttributes.length == 2)
                            {
                                String enchantmentName = enchantAttributes[0];
                                int enchantLevel = Sys.of_getString2Int(enchantAttributes[1]);

                                if(enchantLevel > 0)
                                {
                                    // Create the enchantment by the given name and level and add it to the itemStack.
                                    Enchantment enchantment = Enchantment.getByName(enchantmentName);

                                    if(enchantment != null)
                                    {
                                        meta.addEnchant(enchantment, enchantLevel, true);
                                    }
                                }
                            }
                        }
                    }

                    item.setItemMeta(meta);
                }
            }
        }
        else
        {
            Sys.of_debug("Conversion.of_convertItemStackInformationFromDatabase2ItemStack(); There was an error while identify '" + type + "' as material-type.");
        }

        return item;
    }
}