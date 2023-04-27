package com.base.objects;

import com.base.sys.Sys;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created 11.10.2021
 * @Author Nihar
 * @Description
 * This object is used to create or edit
 * fast '.YML'-Files.
 */
public class SimpleFile
{
    private File file;
    public YamlConfiguration cfg;

    /* ************************************* */
    /* CONSTRUCTOR */
    /* ************************************* */

    /**
     * Constructor
     * @param absolutePath Absolute file path for example: 'plugins\\Plugin\\others\\settings.yml'
     */
    public SimpleFile(String absolutePath)
    {
        //	We correct the file-path in case if it's wrong!
        absolutePath = absolutePath.replace("\\", "//");

        //	Add a '.yml' to avoid errors.
        if(!absolutePath.contains(".yml"))
        {
            absolutePath += ".yml";
        }

        this.file = new File(absolutePath);
        this.cfg = new YamlConfiguration();

        //  Initialize the configuration.
        of_initializeData("constructor(String);");
    }

    /**
     * Constructor
     * @param file File object.
     */
    public SimpleFile(File file)
    {
        this.file = file;
        this.cfg = new YamlConfiguration();

        //  Initialize the configuration.
        of_initializeData("constructor(File);");
    }

    private void of_initializeData(String invokerName)
    {
        try
        {
            this.cfg.load(file);
        }
        catch (Exception ignored) { }
    }

    /* ************************************* */
    /* GET-SET-Method */
    /* ************************************* */

    /**
     * This function checks if the current configKey is already set.
     * If the configKey is not set the defaultValue will be set and also returned.
     * If the configKey is already set, the value will be returned.
     * @param configKey Section in the .YML-File.
     * @param defaultValue Default value which will be used as initialize value for the given section.
     * @return The value in the configKey or the defaultValue if the configKey is not given.
     */
    public String of_getSetString(String configKey, String defaultValue)  {
        String tmpValue;

        if(cfg.isSet(configKey))  {
            tmpValue = cfg.getString(configKey);

            if(tmpValue == null)  {
                tmpValue = defaultValue;
            }
        }  else {
            cfg.set(configKey, defaultValue);
            tmpValue = defaultValue;
        }

        return tmpValue;
    }

    /**
     * This function checks if the current configKey is already set.
     * If the configKey is not set the defaultValue will be set and also returned.
     * If the configKey is already set, the value will be returned.
     * @param configKey Section in the .YML-File.
     * @param defaultValue Default value which will be used as initialize value for the given section.
     * @return The value in the configKey or the defaultValue if the configKey is not given.
     */
    public int of_getSetInt(String configKey, int defaultValue)
    {
        int tmpValue;

        if(cfg.isSet(configKey))
        {
            tmpValue = Sys.of_getString2Int(cfg.getString(configKey));

            if(tmpValue == -1)
            {
                tmpValue = defaultValue;
            }
        }
        else
        {
            cfg.set(configKey, defaultValue);
            tmpValue = defaultValue;
        }

        return tmpValue;
    }

    /**
     * This function checks if the current configKey is already set.
     * If the configKey is not set the defaultValue will be set and also returned.
     * If the configKey is already set, the value will be returned.
     * @param configKey Section in the .YML-File.
     * @param defaultBool Default value which will be used as initialize value for the given section.
     * @return The value in the configKey or the defaultValue if the configKey is not given.
     */
    public boolean of_getSetBoolean(String configKey, boolean defaultBool)
    {
        boolean tmpValue;

        if(cfg.isSet(configKey))
        {
            tmpValue = cfg.getBoolean(configKey);
        }
        else
        {
            cfg.set(configKey, defaultBool);
            tmpValue = defaultBool;
        }

        return tmpValue;
    }

    /**
     * This function checks if the current configKey is already set.
     * If the configKey is not set the defaultValue will be set and also returned.
     * If the configKey is already set, the value will be returned.
     * @param configKey Section in the .YML-File.
     * @param arrayList Default value which will be used as initialize value for the given section.
     * @return The value in the configKey or the defaultValue if the configKey is not given.
     */
    public String[] of_getSetStringArrayList(String configKey, ArrayList<String> arrayList)
    {
        String[] tmp = null;

        //  Does the path already exist?
        if(cfg.isSet(configKey))
        {
            //  Load existing one...
            tmp = of_getStringArrayByKey(configKey);
        }
        //  Create new...
        else
        {
            cfg.set(configKey, arrayList);
        }

        //  Error while getting the value? We ignore this...
        if(tmp == null)
        {
            //  We send back the default....
            return arrayList.toArray(new String[0]);
        }

        return tmp;
    }

    /* ************************************* */
    /* SAVE */
    /* ************************************* */

    /**
     * This function is used to save the current file.
     * @param invoker Invoker name or system area which calls this function.
     * @return 1 if the file was saved successfully, otherwise -1.
     */
    public int of_save(String invoker)
    {
        try
        {
            cfg.save(file);
            return 1;
        }
        catch (Exception e)
        {
            Sys.of_sendErrorMessage(e, "SimpleFile", "of_save(String)", "Error while saving the file!");
        }

        return -1;
    }

    /* ************************************* */
    /* SETTER // ADDER // REMOVER */
    /* ************************************* */

    /**
     * This function sets the given value to the configKey-section.
     * @param configKey ConfigKey Section in the .YML
     * @param object Object which will be set.
     */
    public void of_set(String configKey, Object object)
    {
        if(cfg != null)
        {
            if(object == null)
            {
                Sys.of_sendErrorMessage(null, "SimpleFile", "of_set(String, Object);", "The config-section-path is not valid! "+configKey);
                return;
            }

            cfg.set(configKey, object);
        }
    }

    /* ************************************* */
    /* GETTER */
    /* ************************************* */

    /**
     * This function returns a string from a configKey which contains
     * multiple lines of values.
     * @param configKey ConfigSection to the multiple lines.
     * @return String array with the multiple lines.
     */
    public String[] of_getStringArrayByKey(String configKey)
    {
        if(cfg != null)
        {
            try
            {
                List<String> values = cfg.getStringList(configKey);

                if(!values.isEmpty())
                {
                    return values.toArray(new String[0]);
                }
            }
            catch (Exception ignored) { }
        }

        return null;
    }

    /**
     * This function is used to get a string value from
     * a configKey in the YML-file.
     * @param configKey Section in the .YML-File.
     * @return The value in the configKey.
     */
    public String of_getString(String configKey)
    {
        return cfg.getString(configKey);
    }

    public int of_getIntByKey(String configKey)
    {
        int value = -1;

        if(cfg != null)
        {
            try
            {
                value = cfg.getInt(configKey);
            }
            catch (Exception ignored) { }
        }

        return value;
    }

    public String of_getFileName()
    {
        if(file != null)
        {
            return file.getName();
        }

        return "NoFileName";
    }
}