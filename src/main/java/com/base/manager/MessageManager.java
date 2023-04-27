package com.base.manager;

import com.base.ancestor.Objekt;
import com.base.objects.SimpleFile;
import com.base.sys.Sys;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * @Created 15.04.2022
 * @Author Nihar
 * @Description
 * This object/class is used to create predefined messages which can be
 * edited in a config-file.
 */
public class MessageManager extends Objekt
{
    //  MessageKey - MessageManager
    private Map<String, String> messages = new HashMap<>();
    protected SimpleFile messageFile;
    protected String prefix;
    protected boolean useAlwaysPrefix;

    /* ************************* */
    /* CONSTRUCTOR // LOADER */
    /* ************************* */

    /**
     * Constructor for the MessageBoard.
     */
    public MessageManager()
    {
        messageFile = new SimpleFile(Sys.of_getMainFilePath() + "//Others//messagesSounds.yml");
    }

    /* ************************* */
    /* OBJECT METHODS */
    /* ************************* */

    /**
     * Is used to replace the given message with color codes and the prefix.
     * @param message The message which should be replaced.
     * @return The replaced message.
     */
    public String of_translateMessage(String message)
    {
        //  Replacements...
        message = message.replace("&", "§");
        message = message.replace("%prefix%", prefix);

        return message;
    }

    /**
     * This method is used to replace the given message with the player stats.
     * @param message The message which should be replaced.
     * @param p The player instance.
     * @return The replaced message.
     */
    public String of_translateMessageWithPlayerStats(String message, Player p)
    {
        message = of_translateMessage(message);

        //  Player Stats:
        message = message.replace("%p%", p.getName());
        message = message.replace("%displayName%", p.getDisplayName());
        message = message.replace("%uuid%", p.getUniqueId().toString());

        return message;
    }

    /**
     * This function is used to play a sound to the player.
     * @param soundKey The sound key.
     * @param p The player instance.
     */
    public void of_playSoundByKey(String soundKey, Player p)
    {
        String sound = messages.get(soundKey);

        //  If the sound could be found in the messagesSounds.yml use the defined minecraft-sound.
        if(sound != null)
        {
            sound = sound.toLowerCase();
        }
        //  Otherwise, the sound could be from an CommandSet, so we handle it as a minecraft-sound.
        else
        {
            sound = soundKey.toLowerCase();
        }

        if(!sound.isEmpty())
        {
            p.playSound(p.getLocation(), sound, 1, 1);
        }
    }

    /* ************************* */
    /* DEBUG CENTER */
    /* ************************* */

    @Override
    public void of_sendDebugDetailInformation()
    {
        Sys.of_sendMessage("Loaded messages/sounds: " + messages.size());
    }

    /* **************************** */
    /* ADDER // SETTER // REMOVER */
    /* *************************** */

    /**
     * This function adds the given configKey and message to the file if it doesn't exist.
     * If it exists it will be loaded and add to the message board.
     * @param configKey The configKey of the message.
     * @param messageSound The message/sound which should be added.
     */
    protected void of_addMessageOrSound2Board(String configKey, String messageSound)
    {
        //  Add or load the specific message/sound from the file. After this add it to the messages.
        messageSound = messageFile.of_getSetString(configKey, messageSound);
        messages.put(configKey, messageSound);
    }

    /* ************************* */
    /* GETTER */
    /* ************************* */

    /**
     * This function gets a message by the messageKey and replaces the message placeholders with
     * the player stats.
     * @param messageKey The messageKey of the message.
     * @param p The player instance.
     * @return The message.
     */
    public String of_getMessageWithPlayerStats(String messageKey, Player p)
    {
        String message  = of_translateMessageWithPlayerStats(of_getMessage(messageKey), p);

        //  Check if for this message is a sound available.
        String[] messageData = messageKey.split("\\.", 2);

        //  If the parameters are correct...
        if(messageData.length == 2)
        {
            of_playSoundByKey("Sounds." + messageData[1], p);
        }

        return message;
    }

    /**
     * This function returns the message which is defined for the given messageKey.
     * @param messageKey The messageKey of the message.
     * @return The message.
     */
    public String of_getMessage(String messageKey)
    {
        String message = messages.get(messageKey);

        if(message == null)
        {
            message = "This message is not defined in the messagesSounds-file! MessageKey: " + messageKey;
        }

        //  If prefix is needed...
        if(of_isUsingAlwaysPrefix())
        {
            message = prefix + " " + message;
        }

        return of_translateMessage(message);
    }

    /* ************************* */
    /* BOOLS */
    /* ************************* */

    public boolean of_isUsingAlwaysPrefix()
    {
        return useAlwaysPrefix;
    }
}
