package com.base.extern;

import com.backpack.utils.MBS;
import com.base.ancestor.Objekt;
import com.backpack.main.main;
import com.base.sys.Sys;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Created 02.04.2022
 * @Rework  17.07.2022
 * @Author Nihar
 * @Description
 * This java-class is used to represent the object
 * UPDService. The UPD-Service has been implemented as
 * a singleton-pattern.
 *
 * The UPD-Service is used to update the database version
 * and executed the necessary sql-statements for this plugin-version.
 */
public class UPDService extends Objekt
{
    //  Attributes:
    public static final UPDService instance = new UPDService();
    private String[] updsToExecute = new String[] {};
    private String latestUPDVersionByFile = "unknown";
    private int updLow;
    private int updHigh;

    /* ************************* */
    /* LOADER */
    /* ************************* */

    @Override
    public int of_load()
    {
        //  Read entries in the UPD.sql-file.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipInputStream zipInputStream = null;

        try
        {
            File jarFile = new File("plugins//" + main.of_getPlugin().getName() + ".jar");
            zipInputStream = new ZipInputStream(Files.newInputStream(jarFile.toPath()));
            ZipEntry zipEntry;

            while((zipEntry  = zipInputStream.getNextEntry()) != null)
            {
                if(zipEntry.getName().equals("UPD.sql"))
                {
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = zipInputStream.read(buffer)) > 0)
                    {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
        }
        catch (Exception ignored) { }

        //  Close all opened streams.
        try
        {
            if(zipInputStream != null)
            {
                zipInputStream.close();
            }

            outputStream.close();
        }
        catch (Exception ignored) { }

        //  Begin with finding the upd-statements.
        String updFileContentAsString = outputStream.toString();
        String[] updFragmentsAsLines = updFileContentAsString.split("\r");
        StringBuilder currentSQLStmt = new StringBuilder();

        for(String currentUpdLine : updFragmentsAsLines)
        {
            if(currentUpdLine.contains("-- UPDv="))
            {
                String currentUPDVersion = currentUpdLine.replace("-- UPDv=", "");
                latestUPDVersionByFile = currentUPDVersion.trim();
                updsToExecute = Arrays.copyOf(updsToExecute, updsToExecute.length + 1);
                updsToExecute[updsToExecute.length - 1] = "UPDVersion=" + currentUPDVersion;
            }
            else if(!currentUpdLine.contains("--"))
            {
                currentSQLStmt.append(currentUpdLine);

                if(currentUpdLine.endsWith(";"))
                {
                    updsToExecute = Arrays.copyOf(updsToExecute, updsToExecute.length + 1);
                    updsToExecute[updsToExecute.length - 1] = currentSQLStmt.toString();
                    currentSQLStmt = new StringBuilder();
                }
            }
        }

        return 1;
    }

    /* ************************* */
    /* OBJECT - METHODS */
    /* ************************* */

    /**
     * This method is used to check if
     * the given database needs to be updated.<br/>
     * If an update is available it runs all necessary
     * sql-statements which has been defined in the upd-file.
     * <br/>
     * <br/>
     * If the latest upd-version-number does not match
     * with the plugin needed upd-version the <b>plugin will be disabled.<b/>
     * @return It can return:<br/>
     *         NULL = OK (upd has been executed)<br/>
     *         Error-message = If an error occurs.
     */
    public String of_runUPD()
    {
        of_sendMessage("Collecting necessary sql-statements up to version: " + Sys.of_getNeededUPDVersion());

        //  Check if the needed UPD-Version matches the latest UPD-Version in the upd-file.
        if(Sys.of_getNeededUPDVersion().equals(of_getLatestUPDVersionFromFile()))
        {
            //  Send information to the console...
            of_sendMessage("Check if the database needs to be updated.");

            //  Check for new database update...
            if(of_isNewUpdateAvailable())
            {
                of_sendMessage("New updates available!");
                of_sendMessage("========= UPD-Service: Start updating... =========");

                if(updsToExecute != null)
                {
                    //  Initialize variables for the upd-process.
                    String currentUPDSection = null;
                    String errorMessage = null;
                    boolean lb_executeSQL = false;
                    boolean lb_skipFirstDbVersionSetting = of_getDbVersionNumber() == 0;
                    int currentUPDVersion;
                    int sqlStatementCounter = 0;

                    for(String currentUpdLine : updsToExecute)
                    {
                        if(currentUpdLine.contains("UPDVersion="))
                        {
                            //  Get the current-upd version.
                            currentUPDSection = currentUpdLine.replace("UPDVersion=", "").trim();

                            //  If we do not have any tables yet, we skip
                            //  updating the db-version table!
                            if(lb_skipFirstDbVersionSetting)
                            {
                                lb_skipFirstDbVersionSetting = false;
                            }
                            else
                            {
                                errorMessage = of_setGivenDbVersion2DbVersionTable(currentUPDSection);
                            }

                            //  Check for any error-messages...
                            if(errorMessage == null)
                            {
                                currentUPDVersion = of_getUPDNumber(currentUPDSection);
                                lb_executeSQL = ( currentUPDVersion != -1 && currentUPDVersion > of_getDbVersionNumber() && currentUPDVersion <= of_getUPDVersionNumber() );

                                if(lb_executeSQL)
                                {
                                    of_sendMessage("Updating database to version: '" + currentUPDSection + "'");
                                    sqlStatementCounter = 0;
                                }
                            }
                            else
                            {
                                return errorMessage;
                            }
                        }
                        else if(lb_executeSQL)
                        {
                            sqlStatementCounter++;

                            if(MBS.of_getSQL().of_run_update(currentUpdLine))
                            {
                                of_sendMessage("SQL-Statement executed: " + sqlStatementCounter);
                            }
                            else
                            {
                                of_sendMessage("Error for the upd-section: '" + currentUPDSection + "'. SQL-Statement-count: " + sqlStatementCounter);
                                return "Error while executing the following sql-statement (for more information check the previous log).\nSQL:\n" + currentUpdLine;
                            }
                        }
                    }
                }

                of_sendMessage("========= UPD-Service: Conclusion =========");
                of_sendMessage("Successfully updated the database-version!");
                of_sendMessage("Database version: " + of_getLatestUPDVersionFromFile());
                of_sendMessage("===========================================");
                return null;
            }

            of_sendMessage("No new updates available, your database is up to date!");
            return null;
        }

        of_sendMessage("========= UPD-Service: Validation-Check =========");
        of_sendMessage("The needed upd-version does not match with the latest upd-file version!");
        of_sendMessage(Sys.of_getNeededUPDVersion() + " != " + latestUPDVersionByFile);
        of_sendMessage("To avoid errors we shutdown the server!");
        of_sendMessage("=================================================");
        return "The needed upd-version could not be found in the upd-file!";
    }

    private String of_setGivenDbVersion2DbVersionTable(String currentDbVersion)
    {
        //  Set the db-version to avoid that the upd will be executed twice!
        String updateDatabaseVersion = "UPDATE "+MBS.of_getSQL().of_getTableNotation()+"dbversion SET dbVersion = '" + currentDbVersion + "';";

        if(!MBS.of_getSQL().of_run_update_suppress(updateDatabaseVersion))
        {
            String errorMessage = "Could not set the db-version to '" + currentDbVersion + "'. The database update-process will be stopped!";
            of_sendMessage(errorMessage);
            return errorMessage;
        }

        return null;
    }

    private void of_sendMessage(String message)
    {
        Sys.of_sendMessage("[UPD-Service]: " + message);
    }

    /* ************************* */
    /* BOOLS */
    /* ************************* */

    /**
     * This method is used to check if some sql-statements
     * in the upd-file needs to be executed to the database.
     * @return TRUE = Database needs to be updated. FALSE = No update needed!
     */
    private boolean of_isNewUpdateAvailable()
    {
        if(latestUPDVersionByFile != null)
        {
            String sqlSelect = "SELECT dbVersion FROM "+ MBS.of_getSQL().of_getTableNotation()+"dbversion;";
            String updVersionByDb = MBS.of_getSQL().of_getRowValue_suppress(sqlSelect, "dbVersion");

            if(updVersionByDb != null)
            {
                //  If the UPD-File-Version and the DB-Version are match, we do not
                //  need to update the database.
                if(updVersionByDb.equals(latestUPDVersionByFile))
                {
                    return false;
                }
            }
            //  Set the base-version.
            else
            {
                updVersionByDb = "22.1.0.00";
            }

            int updVersionNumberByFile = of_getUPDNumber(latestUPDVersionByFile);
            int updVersionNumberByDb = of_getUPDNumber(updVersionByDb);
            int updVersionNumberByPlugin = of_getUPDNumber(Sys.of_getVersion());

            if(updVersionNumberByFile <= updVersionNumberByPlugin)
            {
                //  Store the current upd-version-range. Is needed for the of_runUPD()-method.
                updHigh = updVersionNumberByFile;
                updLow = updVersionNumberByDb;

                //  Continue if the database is not on the same version as the UPD-File.
                return updVersionNumberByFile != -1 && updVersionNumberByDb != -1 && updVersionNumberByFile >= updVersionNumberByDb;
            }
            else
            {
                of_sendErrorMessage(null, "UPDService.of_isNewUpdateAvailable();", "The UPD-File is not compatible with this plugin-version!");
                return false;
            }
        }

        return false;
    }

    /* ************************* */
    /* GETTER */
    /* ************************* */

    public static UPDService of_getInstance()
    {
        return instance;
    }

    private int of_getUPDNumber(String updVersion)
    {
        return Sys.of_getString2Int(updVersion.split("\\.")[3]);
    }

    private int of_getUPDVersionNumber()
    {
        return updHigh;
    }

    private int of_getDbVersionNumber()
    {
        return updLow;
    }

    public String of_getLatestUPDVersionFromFile()
    {
        return latestUPDVersionByFile;
    }
}
