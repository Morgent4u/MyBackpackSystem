package com.backpack.cmds;

import com.backpack.inventory.InventoryBackpack;
import com.backpack.utils.MBS;
import com.base.sys.Sys;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMD_Backpack implements CommandExecutor {

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("Backpack")) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if(MBS.of_getPermission().of_isAdmin(p)) {
                    MBS.of_getMessage().of_sendMessage2Player(p, "This command is currently only available for the console!");
                } else {
                    MBS.of_getMessage().of_sendPlayerNoPermissionMessage(p);
                }
            } else {
                if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("give")) {
                        Player p = Bukkit.getPlayer(args[1]);

                        if(p != null) {
                            InventoryBackpack invBackpack = MBS.of_getSettings().of_getBackpackInventory();

                            if(invBackpack != null) {
                                ItemStack item = MBS.of_getBackpackService().of_getBackpackItem(invBackpack, p);
                                p.getInventory().addItem(item);
                                Sys.of_debug("Added a backpack to "+p.getName()+"'s inventory.");
                                return true;
                            }
                        }
                    }
                }

                Sys.of_debug("Wrong command usage! Use: /Backpack give <player-name>");
            }

            return true;
        }

        return false;
    }
}
