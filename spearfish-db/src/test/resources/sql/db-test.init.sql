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

