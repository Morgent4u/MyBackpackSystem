----------------------------------------------------------------
-- Created: 2022.04.02
-- Author: Nihar
-- Filename: UPD_MyBackpackSystem.upd
-- Last updates:
-- 2022.04.02: Add MBS-SQL-Statements to this file.
----------------------------------------------------------------

-- UPDv=22.1.0.01
-- Create a table to store the db-version.
CREATE TABLE mbs_dbversion( dbVersion VARCHAR(20) NOT NULL, updTime DATETIME NOT NULL );

INSERT INTO mbs_dbversion ( dbVersion, updTime )
     VALUES ( '22.1.0.01', NOW() );

-- Key table for the key-control.
CREATE TABLE mbs_key ( tableName VARCHAR(40) NOT NULL, lastKey INT DEFAULT 0 );

-- Additional table to store player item-stacks.
CREATE TABLE mbs_user_backpack (
             mbs_user_backpack INT(5) NOT NULL PRIMARY KEY
           , playerName VARCHAR(21) NOT NULL
           , serialNumber VARCHAR(5) NOT NULL );

-- Additional table to store player item-stacks.
CREATE TABLE mbs_user_backpack_item (
             mbs_user_backpack INT(5) NOT NULL
           , slot INT NOT NULL
           , type VARCHAR(40) NOT NULL
           , amount INT NOT NULL
           , display VARCHAR(40) NOT NULL
           , lore VARCHAR(255) NULL
           , enchant VARCHAR(255) NULL
           , durability INT);

-- Add a foreign key constraint.
ALTER TABLE mbs_user_backpack_item ADD CONSTRAINT fk_mbs_user_backpack_item_mbs_user_backpack FOREIGN KEY (mbs_user_backpack) REFERENCES mbs_user_backpack(mbs_user_backpack);

-- Add the table mbs_user_backpack to the key-control-table.
INSERT INTO mbs_key ( tableName )
     VALUES ( 'mbs_user_backpack' );

-- UPDv=23.1.0.02
-- Placeholder version! Do not add sql-statements to this version!