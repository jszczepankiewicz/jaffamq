package org.jaffamq.persistence.database;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * It is hard to use in-memory database for testing by propagating datasource thats why this datasource was introduced. It is NOT
 * threadsafe cause it may return one connection to different threads. DataSource is using in memory h2 database.
 */
public class SingleConnectionInMemoryDataSource implements DataSource {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SingleConnectionInMemoryDataSource.class);

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unexpected ClassNotFoundException during JDBC initialization. Check if H2 driver is in classpath.");
        }
    }

    private Connection connection;

    public SingleConnectionInMemoryDataSource() {
        try {

            connection = DriverManager.
                    getConnection("jdbc:h2:mem:", "", "");
        } catch (SQLException e) {
            throw new IllegalStateException("Unexpected SQLException while trying to open in-memory database");
        }

    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public void destroy() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Unexpected SQLException while trying to close database connection", e);
            }
        }
    }
}
