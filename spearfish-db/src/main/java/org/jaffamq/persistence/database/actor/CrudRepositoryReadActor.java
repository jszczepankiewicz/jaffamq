package org.jaffamq.persistence.database.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.persistence.database.CrudRepository;
import org.jaffamq.persistence.database.sql.JDBCSession;
import scala.Option;

import javax.sql.DataSource;

/**
 * Actor for interaction with underlying CrudRepository that is using RDBMS database. WARNING this actor access
 * SQL database using blocking JDBC invocation!. Every access to this actor should be using timeout.
 */
public abstract class CrudRepositoryReadActor<T> extends UntypedActor {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    protected CrudRepository<T> repository;

    private JDBCSession session;
    private DataSource dataSource;

    public CrudRepositoryReadActor(CrudRepository repository, DataSource dataSource) {
        this.repository = repository;
        this.dataSource = dataSource;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        session = new JDBCSession(dataSource.getConnection());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        session.dispose();
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        super.preRestart(reason, message);
        session.dispose();
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
        session = new JDBCSession(dataSource.getConnection());
    }

    protected JDBCSession getSession() {
        return session;
    }

    protected void handleGetById(GetByIdRequest request) {
        getSender().tell(new EntityResponse(repository.get(getSession(), request.getId())), getSelf());
    }

    protected void handleGetList(GetPagedListRequest request) {
        getSender().tell(new EntityListResponse(repository.list(getSession(), request.getLimit(), request.getOffset())), getSelf());
    }

    protected void handleIsUnique(IsUniqueRequest request) {
        getSender().tell(new IsUniqueResponse(repository.isUnique(getSession(), request.getName())), getSelf());
    }

    protected void opearateUnhandled(Object message) {
        log.warning("Unhandled message: " + message);
        unhandled(message);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof GetByIdRequest) {
            handleGetById((GetByIdRequest) message);
            return;
        } else if (message instanceof GetPagedListRequest) {
            handleGetList((GetPagedListRequest) message);
            return;
        } else if (message instanceof IsUniqueRequest) {
            handleIsUnique((IsUniqueRequest) message);
            return;
        } else {
            opearateUnhandled(message);
        }
    }
}
