package org.jaffamq.persistence.database.sql;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by urwisy on 06.01.14.
 */
public class JDBCSessionTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateAndCompilePreparedStatementOnDemand() throws Exception {

        //  given
        String sql = "Select * from torpidomq";
        String SELECT_NAME = "SELECT_FROM_TORPIDOMQ";

        SelectOperation query = new SelectOperation(SELECT_NAME, sql){
            @Override
            protected Object mapResult(ResultSet rs, int rowNumber) throws SQLException {
                return null;
            }
        };

        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        JDBCSession session = new JDBCSession(connection);

        //  when
        PreparedStatement fromProductionCode = session.getCompiledStatement(query);

        //  then
        assertThat(fromProductionCode, is(equalTo(preparedStatement)));

    }

    @Test
    public void shouldCloseAllPreparedStatementsOnDispose() throws Exception {

        //  given
        String sql = "Select * from torpidomq";
        String SELECT_NAME = "SELECT_FROM_TORPIDOMQ";

        SelectOperation query = new SelectOperation(SELECT_NAME, sql){

            @Override
            protected Object mapResult(ResultSet rs, int rowNumber) throws SQLException {
                return null;
            }
        };

        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(connection.prepareStatement(sql)).thenReturn(preparedStatement);

        JDBCSession session = new JDBCSession(connection);

        //  when
        session.getCompiledStatement(query);
        session.dispose();

        //  then
        Mockito.verify(preparedStatement).close();
    }

}
