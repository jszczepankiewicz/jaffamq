--  IMPORTANT INFORMATION ABOUT PK
--  test entities values should be picked from the range 1000 - 99999
--  this will prevent collisions with production initialization data (below 1000) and real normal-working data (above 10.000)

--  security ===========================================================================================================


--  move this to init db.
INSERT INTO security_user(id, login, passhash, creationtime) VALUES
    (1000, 'someuser1', 'notimportant', 1);
INSERT INTO security_user(id, login, passhash, creationtime) VALUES
    (1001, 'someuser2', 'notimportant', 1);
INSERT INTO security_user(id, login, passhash, creationtime) VALUES
    (1002, 'someuser3', 'notimportant', 1);
INSERT INTO security_user(id, login, passhash, creationtime) VALUES
    (1003, 'someuser4', 'notimportant', 1);
INSERT INTO security_user(id, login, passhash, creationtime) VALUES
    (1004, 'someuser5', 'notimportant', 1);

-- groups
INSERT INTO security_group(id, name) VALUES
    (1000, 'test0');

INSERT INTO security_group(id, name) VALUES
    (1001, 'test1');

INSERT INTO security_group(id, name) VALUES
    (1002, 'test2');
INSERT INTO security_group(id, name) VALUES
    (1003, 'test3');
INSERT INTO security_group(id, name) VALUES
    (1004, 'test4');
INSERT INTO security_group(id, name) VALUES
    (1005, 'test5');
--  users and groups

INSERT INTO security_user_and_group(id_user, id_group) VALUES (1003, 1003);
INSERT INTO security_user_and_group(id_user, id_group) VALUES (1004, 1003);

-- destinations
INSERT INTO destination(id, name, creationtime) VALUES
    (1000, 'queue/something1', 1);

INSERT INTO destination(id, name, creationtime) VALUES
    (1001, 'queue/something2', 1);

INSERT INTO destination(id, name, creationtime) VALUES
    (1002, 'queue/something3', 1);

-- destination and users

INSERT INTO destination_and_group_with_read (id_group, id_destination) VALUES  (1002,1002);
INSERT INTO destination_and_group_with_write (id_group, id_destination) VALUES (1003,1002);
INSERT INTO destination_and_group_with_admin (id_group, id_destination) VALUES (1004,1002);
INSERT INTO destination_and_group_with_admin (id_group, id_destination) VALUES (1005,1002);