-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/db.changelog-master.xml
-- Ran at: 20.04.14 19:09
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
CREATE SEQUENCE user_seq START WITH 10000 INCREMENT BY 1;

CREATE SEQUENCE group_seq START WITH 10000 INCREMENT BY 1;

CREATE TABLE security_group (id BIGINT NOT NULL, name VARCHAR(255) NOT NULL, description VARCHAR(1024), CONSTRAINT PK_SECURITY_GROUP PRIMARY KEY (id), UNIQUE (name));

CREATE TABLE security_user (id BIGINT NOT NULL, login VARCHAR(255) NOT NULL, passhash VARCHAR(100) NOT NULL, creationtime BIGINT NOT NULL, CONSTRAINT PK_SECURITY_USER PRIMARY KEY (id), UNIQUE (login));

CREATE TABLE security_user_and_group (id_user BIGINT NOT NULL, id_group BIGINT NOT NULL, CONSTRAINT PK_SECURITY_USER_AND_GROUP PRIMARY KEY (id_user, id_group));

ALTER TABLE security_user_and_group ADD CONSTRAINT fk_security_user_and_group_id_user FOREIGN KEY (id_user) REFERENCES security_user (id) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE security_user_and_group ADD CONSTRAINT fk_security_user_and_group_id_group FOREIGN KEY (id_group) REFERENCES security_group (id) ON UPDATE RESTRICT ON DELETE CASCADE;

INSERT INTO security_group (id, name) VALUES ('1', 'admins');

INSERT INTO security_user (id, login, passhash, creationtime) VALUES ('1', 'admin', '8be94e85158147d335c31e7401565942785e79d7d446cc41f6427422d6755371', '1');

INSERT INTO security_user_and_group (id_user, id_group) VALUES ('1', '1');

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('1', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 1, '7:98bf4db6cdb5122b73e7e547ea77bf69', 'createSequence (x2), createTable (x3), addForeignKeyConstraint (x2), insert (x3)', '', 'EXECUTED', '3.0.8');

-- Changeset C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml::100::jszczepankiewicz
-- destination table and the rest of relations
CREATE TABLE destination (id BIGINT NOT NULL, name VARCHAR(4096) NOT NULL, creationtime BIGINT NOT NULL, CONSTRAINT PK_DESTINATION PRIMARY KEY (id), UNIQUE (name));

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('100', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 2, '7:b212f1acd1a160f9b00b76c86889a846', 'createTable', 'destination table and the rest of relations', 'EXECUTED', '3.0.8');

-- Changeset C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml::101::jszczepankiewicz
-- destination and groups with read authorities
CREATE TABLE destination_and_group_with_read (id_group BIGINT NOT NULL, id_destination BIGINT NOT NULL, CONSTRAINT PK_DESTINATION_AND_GROUP_WITH_READ PRIMARY KEY (id_group, id_destination));

ALTER TABLE destination_and_group_with_read ADD CONSTRAINT fk_destination_and_group_with_read_id_group FOREIGN KEY (id_group) REFERENCES security_group (id) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE destination_and_group_with_read ADD CONSTRAINT fk_destination_and_group_with_read_id_destination FOREIGN KEY (id_destination) REFERENCES destination (id) ON UPDATE RESTRICT ON DELETE CASCADE;

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('101', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 3, '7:5ab97aa25b7d5e499a50e34b7aa78fbf', 'createTable, addForeignKeyConstraint (x2)', 'destination and groups with read authorities', 'EXECUTED', '3.0.8');

-- Changeset C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml::102::jszczepankiewicz
-- destination and groups with write authorities
CREATE TABLE destination_and_group_with_write (id_group BIGINT NOT NULL, id_destination BIGINT NOT NULL, CONSTRAINT PK_DESTINATION_AND_GROUP_WITH_WRITE PRIMARY KEY (id_group, id_destination));

ALTER TABLE destination_and_group_with_write ADD CONSTRAINT fk_destination_and_group_with_write_id_group FOREIGN KEY (id_group) REFERENCES security_group (id) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE destination_and_group_with_write ADD CONSTRAINT fk_destination_and_group_with_write_id_destination FOREIGN KEY (id_destination) REFERENCES destination (id) ON UPDATE RESTRICT ON DELETE CASCADE;

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('102', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 4, '7:96127363355194217ed92406b14421f2', 'createTable, addForeignKeyConstraint (x2)', 'destination and groups with write authorities', 'EXECUTED', '3.0.8');

-- Changeset C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml::103::jszczepankiewicz
-- destination and groups with admin authorities
CREATE TABLE destination_and_group_with_admin (id_group BIGINT NOT NULL, id_destination BIGINT NOT NULL, CONSTRAINT PK_DESTINATION_AND_GROUP_WITH_ADMIN PRIMARY KEY (id_group, id_destination));

ALTER TABLE destination_and_group_with_admin ADD CONSTRAINT fk_destination_and_group_with_admin_id_group FOREIGN KEY (id_group) REFERENCES security_group (id) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE destination_and_group_with_admin ADD CONSTRAINT fk_destination_and_group_with_admin_id_destination FOREIGN KEY (id_destination) REFERENCES destination (id) ON UPDATE RESTRICT ON DELETE CASCADE;

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('103', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 5, '7:b5cd0132d2620f2a7436287e531129f3', 'createTable, addForeignKeyConstraint (x2)', 'destination and groups with admin authorities', 'EXECUTED', '3.0.8');

-- Changeset C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml::104::jszczepankiewicz
-- Sequence for destination table
CREATE SEQUENCE destination_seq START WITH 10000 INCREMENT BY 1;

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('104', 'jszczepankiewicz', 'C:/dev/intelij_workspace/JaffaMQ/spearfish-db/src/main/resources/org/torpidomq/db/changelog/1.x/db.changelog-1.0.xml', NOW(), 6, '7:188ec8ce3f2e40493e7943eeb8bd1b44', 'createSequence', 'Sequence for destination table', 'EXECUTED', '3.0.8');

