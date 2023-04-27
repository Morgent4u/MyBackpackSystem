package com.backpack.inventory;

import com.base.ancestor.Objekt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Created 14.04.2022
 * @Author Nihar
 * @Description
 * This class contains several methods to manage the inventar-object or
 * a default inventory.
 */
public class InventoryService extends Objekt
{
    /* ************************************* */
    /* OBJECT - METHODS */
    /* ************************************* */

    /**
     * This function is used to create an itemStack by the given attributes.
     * @param material The material of the itemStack.
     * @param displayName The displayName of the itemStack.
     * @param arrayLore The lore of the itemStack.
     * @param amount The amount of the itemStack.
     * @return The itemStack.
     */
    public ItemStack of_createItemStack(Material material, String displayName, String[] arrayLore, int amount)
    {
        //  Create the item.
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if(meta != null)
        {
            //  Set the meta attributes.
            meta.setDisplayName(displayName);
            meta.setUnbreakable(true);
            meta.setUnbreakable(true);
            meta = of_setDefaultAttributes2ItemMeta(meta);

            //  Set the lore.
            if(arrayLore != null && arrayLore.length > 0)
            {
                List<String> lore = Arrays.asList(arrayLore);
                meta.setLore(lore);
            }

            if(amount <= 0)
            {
                amount = 1;
            }

            item.setAmount(amount);
            item.setItemMeta(meta);

            return item;
        }

        return null;
    }

    /**
     * This function is used to copy the content of the inventory to a new one and return it.
     * @param inv The inventory which should be copied.
     * @param invTitle The title of the new inventory.
     * @return The new inventory.
     */
    public Inventory of_copyInv(Inventory inv, String invTitle)
    {
        Inventory tmpInv;

        //  Check if the inventory is of the type chest, otherwise there will be
        //  a problem while using the size.
        if(inv.getType() == InventoryType.CHEST)
        {
            tmpInv = Bukkit.createInventory(null, inv.getSize(), invTitle);
        }
        else
        {
            //  To avoid an inventory-size error we use the inventory-type instead!
            tmpInv = Bukkit.createInventory(null, inv.getType(), invTitle);
        }

        //  Copy the content.
        tmpInv.setContents(inv.getContents());

        return tmpInv;
    }

    public ItemStack of_copyItemStack(ItemStack original) {
        if (original == null) {
            return null;
        }
        ItemStack copy = new ItemStack(original.getType(), original.getAmount());
        ItemMeta meta = original.getItemMeta();
        if (meta != null) {
            copy.setItemMeta(meta.clone());
        }
        copy.setDurability(original.getDurability());
        copy.setAmount(original.getAmount());
        copy.setDurability(original.getDurability());
        copy.setItemMeta(meta);
        return copy;
    }

    /**
     * This function is used to replace the display-name or lore-items
     * of an item by searching for a specific pattern and replacing it.
     * @param item The itemStack which should be updated.
     * @param searchValue The searchValue.
     * @param replaceValue The replaceValue.
     * @return The itemStack with the updated display-name or lore-items.
     */
    public ItemStack of_replaceItemStackValues(ItemStack item, String searchValue, String replaceValue)
    {
        ItemMeta meta = item.getItemMeta();

        if(meta != null)
        {
            if(meta.hasDisplayName())
            {
                meta.setDisplayName(meta.getDisplayName().replace(searchValue, replaceValue));
            }

            if(meta.hasLore())
            {
                List<String> lore = meta.getLore();

                if(lore != null && lore.size() > 0)
                {
                    lore.replaceAll(s -> s.replace(searchValue, replaceValue));
                }

                meta.setLore(lore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /* ************************************* */
    /* SETTER // ADDER // REMOVER */
    /* ************************************* */

    /**
     * This function sets default attributes to the ItemMeta.
     * @param meta The ItemMeta which should be set.
     * @return The ItemMeta with the default attributes.
     */
    public ItemMeta of_setDefaultAttributes2ItemMeta(ItemMeta meta)
    {
        if(meta != null)
        {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            return meta;
        }

        return null;
    }

    /* ************************************* */
    /* BOOLS */
    /* ************************************* */

    /**
     * This function checks if the given ItemStack is containing the given pattern.
     * @param item The ItemStack which should be checked.
     * @param pattern The pattern which should be checked.
     * @return The string segment where the pattern has been found in!
     */
    public String of_getItemStackStringSegmentWithSpecificPattern(ItemStack item, String pattern)
    {
        if(item != null && item.hasItemMeta())
        {
            if(Objects.requireNonNull(item.getItemMeta()).hasDisplayName())
            {
                if(item.getItemMeta().getDisplayName().contains(pattern))
                {
                    return item.getItemMeta().getDisplayName();
                }
                //  Check the lore...
                else
                {
                    if(item.getItemMeta().hasLore())
                    {
                        for(String lore : Objects.requireNonNull(item.getItemMeta().getLore()))
                        {
                            if(lore.contains(pattern))
                            {
                                return lore;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
