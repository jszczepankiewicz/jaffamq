package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.sql.JDBCSession;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by urwisy on 12.01.14.
 */
public class RepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryTest.class);

    private JDBCSession session;
    private Connection connection;

    @BeforeClass
    public static void initClass() {

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("Could not load H2 JDBC driver", e);
        }
    }

    private void initializeDDL() throws SQLException {

        LOG.debug("Initializing db DDL...");

        try {
            Statement statement = connection.createStatement();
            statement.execute("RUNSCRIPT FROM 'classpath:sql/torpidomq.init.sql'");
            connection.commit();

        } catch (SQLException e) {
            LOG.error("Error creating DDL for test db", e);
            throw e;
        }

    }

    private void initializeDBData() throws SQLException {

        LOG.debug("Initializing db data...");

        try {
            Statement statement = connection.createStatement();
            statement.execute("RUNSCRIPT FROM 'classpath:sql/db-test.init.sql'");
            connection.commit();

        } catch (SQLException e) {
            LOG.error("Error initializing test db with data", e);
            throw e;
        }

    }

    private void initializeTestData() {

    }


    @Rule
    public ExternalResource jdbcConnectionResource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
            connection = DriverManager.getConnection("jdbc:h2:mem:");
            session = new JDBCSession(connection);

            initializeDDL();
            //initializeDBData();
            initializeTestData();
        }

        @Override
        protected void after() {

            LOG.debug("Disposing db");

            if (session != null) {
                session.dispose();
            }

            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    LOG.warn("Unexpected SQLException while closing sql connection", e);
                }
            }
        }
    };

    protected JDBCSession getSession() {
        return session;
    }


}
