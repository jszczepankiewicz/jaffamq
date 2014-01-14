-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/db.changelog-master.xml
-- Ran at: 12.01.14 21:50
-- Against: @jdbc:h2:mem:
-- Liquibase version: 3.0.8
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE DATABASECHANGELOGLOCK (ID INT NOT NULL, LOCKED BOOLEAN NOT NULL, LOCKGRANTED TIMESTAMP, LOCKEDBY VARCHAR(255), CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID));

-- Initialize Database Lock Table
DELETE FROM DATABASECHANGELOGLOCK;

INSERT INTO DATABASECHANGELOGLOCK (ID, LOCKED) VALUES (1, FALSE);

-- Lock Database
-- Create Database Change Log Table
CREATE TABLE DATABASECHANGELOG (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20));

-- Changeset C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml::1::jszczepankiewicz
CREATE TABLE security_group (id LONG NOT NULL, name VARCHAR(255) NOT NULL, description VARCHAR(1024), CONSTRAINT PK_SECURITY_GROUP PRIMARY KEY (id), UNIQUE (name));

CREATE TABLE security_user (id LONG NOT NULL, login VARCHAR(255) NOT NULL, passhash VARCHAR(100) NOT NULL, creationtime LONG NOT NULL, CONSTRAINT PK_SECURITY_USER PRIMARY KEY (id), UNIQUE (login));

CREATE TABLE security_user_and_group (id_user LONG NOT NULL, id_group LONG NOT NULL, CONSTRAINT PK_SECURITY_USER_AND_GROUP PRIMARY KEY (id_user, id_group));

ALTER TABLE security_user_and_group ADD CONSTRAINT fk_security_user_and_group_id_user FOREIGN KEY (id_user) REFERENCES security_user (id) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE security_user_and_group ADD CONSTRAINT fk_security_user_and_group_id_group FOREIGN KEY (id_group) REFERENCES security_group (id) ON UPDATE RESTRICT ON DELETE CASCADE;

INSERT INTO security_group (id, name) VALUES ('1000', 'admins');

INSERT INTO security_user (id, login, passhash, creationtime) VALUES ('1000', 'admin', 'xyz321', '1');

INSERT INTO security_user_and_group (id_user, id_group) VALUES ('1000', '1000');

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('1', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 1, '7:77b1e0c1011fbf4201cf3f34264a1d49', 'createTable (x3), addForeignKeyConstraint (x2), insert (x3)', '', 'EXECUTED', '3.0.8');

