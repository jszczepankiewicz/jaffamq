package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.SingleConnectionInMemoryDataSource;
import org.jaffamq.persistence.database.sql.JDBCSession;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base class for tests that use in-memory h2 database with DDL and content initialization.
 */
public class RepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryTest.class);

    private JDBCSession session;
    private Connection connection;
    private SingleConnectionInMemoryDataSource dataSource;

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

    private void initializeTestData() throws SQLException {

        LOG.debug("Initializing test data...");

        try {
            Statement statement = connection.createStatement();
            statement.execute("RUNSCRIPT FROM 'classpath:sql/db-test.init.sql'");
            connection.commit();

        } catch (SQLException e) {
            LOG.error("Error initializing test db with data", e);
            throw e;
        }
    }


    @Rule
    public ExternalResource jdbcConnectionResource = new ExternalResource() {

        @Override
        protected void before() throws SQLException {

            dataSource = new SingleConnectionInMemoryDataSource();
            connection = dataSource.getConnection();
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

            /*try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }*/

        }
    };

    protected JDBCSession getSession() {
        return session;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    ;


}
