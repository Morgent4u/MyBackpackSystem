package com.base.sys;

import com.backpack.main.main;
import com.base.objects.SimpleFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Created 20.03.2021
 * @Recreated: 22.03.2022
 * @Author Nihar
 * @Description
 * This is the system-class of this plugin.
 * The system-class is used to check compatibility of the
 * current files in the plugins folder and this plugin version.
 * This class also contains useful methods or functions.
 */
public class Sys
{
    //	DebugMessages
    private static ArrayList<String> debugMessagesBuffer = new ArrayList<>();

    //  Attributes:
    private static String paket;
    private static String programVersion;
    private static String version;
    private static String updVersion;
    private static String mainRootPath;

    private static boolean ib_hotfix;
    private static boolean ib_debug = true;
    private static int debugCounter;

    //  System color-codes:
    public static final String COLOR_WHITE = "\u001B[0m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String COLOR_BLUE = "\u001B[34m";
    public static final String COLOR_PURPLE = "\u001B[35m";
    public static final String COLOR_CYAN = "\u001B[36m";

    /* ************************************* */
    /* MAIN METHOD */
    /* ************************************* */

    /**
     * This function checks the current plugin version with the given version from the
     * plugins' folder (version.yml). If the version are not compatible the plugin cannot
     * be used because it's outdated or way newer than the current files. This prevents that
     * the current files will be destroyed by this plugin-version.
     * @param paketName Plugin name
     * @param versionNumber Version-number in the following format (example): 22.1.0.01
     * @param updVersionNumber The needed UPD-Version-Number.
     * @param fileRootPath The main-path for the plugin files (this should be the plugins' folder).
     * @return TRUE = Compatible, FALSE = Not compatible - disabling the plugin.
     */
    public static boolean of_isSystemVersionCompatible(String paketName, String versionNumber, String updVersionNumber, String fileRootPath)
    {
        boolean versionCompatible = false;

        //  Get the paket and main-folder.
        paket = paketName;
        version = versionNumber;
        updVersion = updVersionNumber;
        programVersion = of_getPaket() + " v"+version;
        mainRootPath = fileRootPath + "//"+paketName+"//";

        //  Send a message into the console that the plugin will be enabled.
        of_sendMessage("[" + of_getPaket() + "]: Enabling the plugin in version: '" + versionNumber + "'...");

        //  Create or load the version.yml to check the compatibility.
        SimpleFile datei = new SimpleFile(mainRootPath + "version.yml");

        //  Get the information about the version.
        String oldVersion = datei.of_getSetString("Version", of_getProgramVersion());
        ib_debug = datei.of_getSetBoolean("DebugMessages", true);
        datei.of_save("Sys.of_isSystemVersionCompatible();");

        //	Example:
        //	Current-Version: 22.1.[1].[01]
        //	Old-Version:	 21.1.[1].[22]
        //	The first  [] => hotfix
        //	The second [] => program version

        String[] currentSysVersion = version.split("\\.");
        String[] oldSysVersion = oldVersion.replace(of_getPaket(), "").replace(" v", "").split("\\.");

        // Check if the base version-stuff is valid.
        if(currentSysVersion.length == 4 && oldSysVersion.length == 4)
        {
            //	The first second numbers must be equal!
            //	Example:
            //  22.1 equals 22.1
            //	The first number: Year
            //	The second number: release-number (e.g 1, 2)

            //  Does the first 2 numbers match?
            if(currentSysVersion[0].equals(oldSysVersion[0]) && currentSysVersion[1].equals(oldSysVersion[1]))
            {
                //  Get the third number from the version and check if the plugin version is newer or equal than the file-version.
                int currentProgramVersionNumber = Integer.parseInt(currentSysVersion[3]);
                int oldProgramVersionNumber = Integer.parseInt(oldSysVersion[3]);

                //  All fine?
                if(currentProgramVersionNumber >= oldProgramVersionNumber)
                {
                    versionCompatible = true;

                    //  The plugin version is newer than the file-version, send a hint that this can cause some problems.
                    if(currentProgramVersionNumber > oldProgramVersionNumber)
                    {
                        of_sendWarningMessage("This plugin-version is newer than your system files. This can possible cause some problems.");
                    }

                    //  If this plugin is a hotfix?
                    if(currentSysVersion[2].equals("1"))
                    {
                        ib_hotfix = true;
                    }
                }
            }
        }

        //  If the version is not compatible, send a warning message.
        if(!versionCompatible)
        {
            of_sendErrorMessage(null, "Sys", "Versionscheck", "This plugin-version does not match with the 'version.yml'. To continue with a not supported plugin-version, you can delete the 'version.yml' and reload the server.");
        }

        return versionCompatible;
    }

    /* ************************************* */
    /* METHODS OF THE CLASS */
    /* ************************************* */

    /**
     * This function sends stored debug-messages (while reloading the server)
     * to the console after to reload is done.
     */
    public static void of_sendDebugMessages2Console()
    {
        if(!debugMessagesBuffer.isEmpty())
        {
            for(String debugMessage : debugMessagesBuffer)
            {
                of_sendMessage(debugMessage);
            }

            debugMessagesBuffer.clear();
        }
    }

    /**
     * This function sends a message to the console with using the default plugin-header.
     * @param message MessageManager
     */
    public static void of_sendMessage(String message)
    {
        main.of_getPlugin().getLogger().info(message);
    }

    /**
     * This function sends a message to the console with using the extended default-plugin header.
     * After the plugin-header a WARNING-prefix is used.
     * @param message MessageManager
     */
    public static void of_sendWarningMessage(String message)
    {
        String red = "\u001B[31m";
        String white = "\u001B[0m";

        main.of_getPlugin().getLogger().info(red+"[WARNING]: "+white+message);
    }

    /* ************************************* */
    /* DEBUG CENTER */
    /* ************************************* */

    /**
     * This function is used for printing messages only to the console.
     * Every debug-messages gets a following number.
     * @param message MessageManager (this should be the invoke function or a description what currently happens).
     */
    public static void of_debug(String message)
    {
        if(of_isDebugModeEnabled())
        {
            debugCounter++;
            message = "DEBUG[#"+debugCounter+"]: " + message;

            of_sendMessage(message);
        }
        else
        {
            debugMessagesBuffer.add(message);
        }
    }

    /* ************************************* */
    /* ERROR HANDLER */
    /* ************************************* */

    /**
     * This function is used to send a default error message by the system-class.
     * @param exception Exception (if no exception is given use NULL instead).
     * @param systemArea The system area or invoker-object which calls this function.
     * @param invoker The current function/method which is calling this function explicit.
     * @param errorMessage The error message which will be displayed. <b>This message should be easy to understand for the server owner</b>.
     */
    public static void of_sendErrorMessage(Exception exception, String systemArea, String invoker, String errorMessage)
    {
        //	Color codes
        String red = "\u001B[31m";
        String white = "\u001B[0m";
        String yellow = "\u001B[33m";
        String blue = "\u001B[36m";

        Sys.of_sendMessage("=====================================");
        Sys.of_sendMessage(red+"[ERROR] "+yellow+Sys.of_getProgramVersion()+white);
        Sys.of_sendMessage(blue+"Hotfix: "+white+of_isHotfix());
        Sys.of_sendMessage(blue+"System area: "+white+systemArea);
        Sys.of_sendMessage(blue+"Invoker: "+white+invoker);
        Sys.of_sendMessage(blue+"Error message:"+white);
        Sys.of_sendMessage(red+errorMessage+white);
        Sys.of_sendMessage("Time: "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        Sys.of_sendMessage("=====================================");

        if(exception != null)
        {
            Sys.of_sendMessage("[Auto-generated exception]:");
            Sys.of_sendMessage(exception.getMessage());
        }
    }

    /* ************************************* */
    /* ADDER // SETTER // REMOVER */
    /* ************************************* */

    /**
     * This function adds an element to the given string-array.
     * @param myArray Array of type string.
     * @param addValue Value which should be added to the given array.
     * @return Array with the added element-value.
     */
    public static String[] of_addArrayValue(String[] myArray, String addValue)
    {
        if(myArray != null)
        {
            int size = myArray.length;
            String[] tmpArray = new String[size+1];

            //	ArrayCopy :)
            System.arraycopy(myArray, 0, tmpArray, 0, size);
            tmpArray[size] = addValue;

            return tmpArray;
        }

        return new String[] { addValue };
    }

    /**
     * This function is used to remove an array entry by using the given index.
     * @param myArray The Array from which a value needs to be removed.
     * @param indexId The index-id of the array-element.
     * @return Array with the removed element-value.
     */
    public static String[] of_removeArrayValueByIndex(String[] myArray, int indexId)
    {
        //  Check if index is valid.
        if(myArray == null || indexId < 0 || indexId >= myArray.length)
        {
            return new String[0];
        }

        String[] temp = new String[myArray.length - 1];
        System.arraycopy(myArray, 0, temp, 0, indexId);
        System.arraycopy(myArray, indexId + 1, temp, indexId, myArray.length - indexId - 1);

        myArray = temp;

        return myArray;
    }

    /**
     * This function enables the debug-mode. If the debug-mode is disabled the
     * debug-messages will be stored in an arraylist.
     * @param lb_bool Use the debug-mode.
     */
    public static void of_setDebugMode(boolean lb_bool)
    {
        if(lb_bool)
        {
            of_sendDebugMessages2Console();
        }

        ib_debug = lb_bool;
    }

    /* ************************************* */
    /* GETTER */
    /* ************************************* */

    public static String of_getList2OneBigStringByAddingSeparatorAsPattern(List<String> list, String pattern)
    {
        if(list != null && !list.isEmpty())
        {
            StringBuilder listBuilder = new StringBuilder();

            for(int i = 0; i < list.size(); i++)
            {
                listBuilder.append(list.get(i));

                if(i != ( list.size() - 1 ))
                {
                    listBuilder.append(pattern);
                }
            }

            return listBuilder.toString();
        }

        return null;
    }

    /**
     * This function replaced all elements of an arraylist with the given replace value.
     * @param list ArrayList of type string.
     * @param search The value which should be replaced in the string-text.
     * @param replace The replace text which should be replaced.
     * @return An arraylist with the replaced values.
     */
    public static ArrayList<String> of_getReplacedArrayList(ArrayList<String> list, String search, String replace)
    {
        ArrayList<String> tmpList = new ArrayList<>();

        if(list != null)
        {
            for(String value : list)
            {
                tmpList.add(value.replace(search, replace));
            }
        }

        return tmpList;
    }

    /**
     * This function is similar to the function of_getReplacedArrayList() but it is using an array-string, instead.
     * @param myArray Array of type string.
     * @param searchValue Search pattern.
     * @param replaceValue Replace value.
     * @return An array with the replaced values.
     */
    public static String[] of_getReplacedArrayString(String[] myArray, String searchValue, String replaceValue)
    {
        if(myArray != null)
        {
            for(int i = 0; i < myArray.length; i++)
            {
                myArray[i] = myArray[i].replace(searchValue, replaceValue);
            }
        }

        return myArray;
    }

    /**
     * This function gets the placeholder value of a replaced string by using the placeholder.
     * @param placeholder Placeholder example: '&8[&c%group%&8]'
     * @param symbol Placeholder start pattern example: '%'
     * @param searchString String which has already the replaced placeholder. For example: '&8[&cAdmin&8]'
     * @return The placeholder value of the parameter 'searchString'. For this example the result is: 'Admin'
     */
    public static String of_getStringWithoutPlaceholder(String placeholder, String symbol, String searchString)
    {
        String[] placeHolderFragments = placeholder.split(symbol, 3);

        if(placeHolderFragments.length == 3)
        {
            searchString = searchString.replace(placeHolderFragments[0], "").replace(placeHolderFragments[2], "");
        }

        return searchString;
    }

    /**
     * This function parsed a string to an int.
     * If the string value is not valid the default value -1
     * will be returned.
     * @param string2Integer String which contains a number.
     * @return Int-number or default error-value: -1
     */
    public static int of_getString2Int(String string2Integer)
    {
        int rc = -1;

        try
        {
            rc = Integer.parseInt(string2Integer);
        }
        catch (Exception ignore) { }

        return rc;
    }

    /**
     * Returns a current timestamp in the given format.
     * @param withDate Use date
     * @param dateFormat Date format for example: dd.MM.yyyy
     * @param hourFormat Hour format for example: HH:mm:ss
     * @return A timestamp string with the given format.
     */
    public static String of_getTimeStamp(boolean withDate, String dateFormat, String hourFormat, String patternBetween)
    {
        String timeStamp = new SimpleDateFormat(hourFormat).format(new Date());

        if(withDate)
        {
            timeStamp = new SimpleDateFormat(dateFormat).format(new Date()) + patternBetween +  timeStamp;
        }

        return timeStamp;
    }

    public static String of_getTimeStamp(boolean withDate, String dateFormat, String hourFormat)
    {
        return of_getTimeStamp(withDate, dateFormat, hourFormat, " ");
    }

    /**
     * Overload of function of_getTimeStamp(withDate, dateFormat, hourFormat);
     * @param withDate Use date
     * @return A timestamp string with the given format.
     */
    public static String of_getTimeStamp(boolean withDate)
    {
        return of_getTimeStamp(withDate, "dd.MM.yyyy", "HH:mm:ss");
    }

    public static String of_getPaket()
    {
        return paket;
    }

    public static String of_getVersion()
    {
        return version;
    }

    public static String of_getProgramVersion()
    {
        return programVersion;
    }

    public static String of_getMainFilePath()
    {
        return mainRootPath;
    }

    public static String of_getNeededUPDVersion()
    {
        return updVersion;
    }

    /* ************************************* */
    /* BOOLS */
    /* ************************************* */

    public static boolean of_isHotfix()
    {
        return ib_hotfix;
    }

    public static boolean of_isDebugModeEnabled()
    {
        return ib_debug;
    }
}