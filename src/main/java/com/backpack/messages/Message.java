package com.backpack.messages;

import com.backpack.utils.MBS;
import com.base.manager.MessageManager;
import org.bukkit.entity.Player;

public class Message extends MessageManager  {

    @Override
    public int of_load() {
        //  Setting SystemPrefix (is needed for admin-messages (plugin-messages)).
        MBS.of_getSettings().of_setColoredPluginPrefix(messageFile.of_getSetString("Settings.SystemPrefix", "&7→&f") + " ");

        prefix = messageFile.of_getSetString("Settings.DefaultPrefix", "&8[&aMyBackpack&fSystem&8]&f:&7");
        prefix = prefix.replace("&", "§");
        useAlwaysPrefix = messageFile.of_getSetBoolean("Settings.UseAlwaysDefaultPrefix", true);

        //  Add messages or sounds to the board.
        of_addMessageOrSound2Board("Messages.General.ErrorMessage", "&fHey &a%p%&f an error occurred! Error: &c%errorMessage%");
        of_addMessageOrSound2Board("Messages.General.NoPermissions", "&cYou do not have permissions to do that!");
        of_addMessageOrSound2Board("Messages.MBS.BackpackInBackpackSelf", "&cYou cannot store your backpack inside your backpack!");
        of_addMessageOrSound2Board("Messages.MBS.BackpackNoInteractionPermission", "&cYou do not have permissions to do that!");

        //  Sounds:
        of_addMessageOrSound2Board("Sounds.General.NoPermissions", "block.sand.fall");
        of_addMessageOrSound2Board("Sounds.MBS.BackpackInBackpackSelf", "block.sand.fall");
        of_addMessageOrSound2Board("Sounds.MBS.BackpackNoInteractionPermission", "block.sand.fall");

        return messageFile.of_save("MessageBoard.of_load();");
    }

    public void of_sendMessage2Player(Player p, String message) {
        if(of_isUsingAlwaysPrefix()) {
            message = prefix + " " + message;
        }

        p.sendMessage(message);
    }

    public void of_sendMessage2PlayerByKey(Player p, String msgKey) {
        String msg = of_getMessageWithPlayerStats(msgKey, p);

        if(!msg.isEmpty()) {
            p.sendMessage(msg);
        }
    }

    public void of_sendPlayerNoPermissionMessage(Player p) {
        String message = of_getMessageWithPlayerStats("Messages.General.NoPermissions", p);

        if(message != null && !message.isEmpty()) {
            p.sendMessage(message);
        }
    }
}
