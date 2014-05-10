package org.jaffamq.persistence.database.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.jaffamq.persistence.database.actor.destination.DestinationRepositoryReadActor;
import org.jaffamq.persistence.database.destination.Destination;
import org.jaffamq.persistence.database.destination.DestinationRepository;
import org.jaffamq.persistence.database.repository.RepositoryTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.hasName;
import static org.jaffamq.persistence.database.repository.IdentifiableMatchers.hasId;
import static org.junit.Assert.assertThat;

public class DestinationRepositoryReadActorTest extends RepositoryTest {

    private ActorSystem system;
    private ActorRef actor;

    @Rule
    public ExternalResource systemResource = new ExternalResource() {

        @Override
        protected void before() {
            system = ActorSystem.create();
            DestinationRepository repository = new DestinationRepository();
            final Props props = Props.create(DestinationRepositoryReadActor.class, repository, getDataSource());
            actor = system.actorOf(props);
        }

        @Override
        protected void after() {
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };

    @Test
    public void shouldReturnListOfDestinations() {

        new JavaTestKit(system) {{

            //  when
            actor.tell(new GetPagedListRequest(2, 0), getRef());

            Object received = receiveOne(duration("1 second"));
            assertThat(received, is(instanceOf(EntityListResponse.class)));
            List<Destination> groups = ((EntityListResponse) received).getPage();

            //  then
            assertThat(groups.size(), is(equalTo(2)));
            assertThat(groups.get(0), hasId(1000L));
            assertThat(groups.get(1), hasId(1002L));
            //  rest is checked in DestinationRepository tests


        }};
    }

    @Test
    public void shouldReturnDestinationFoundForGetById() {

        new JavaTestKit(system) {{

            //  when
            actor.tell(new GetByIdRequest(1000L), getRef());

            Object received = receiveOne(duration("1 second"));
            assertThat(received, is(instanceOf(EntityResponse.class)));
            EntityResponse response = (EntityResponse) received;

            //  then
            assertThat((Destination) response.getEntity(), allOf(
                    hasName("queue/something1"),
                    hasId(1000L)));


        }};
    }

    @Test
    public void shouldDetectUniqueName() {
        new JavaTestKit(system) {{

            //  when
            actor.tell(new IsUniqueRequest("queue/something1"), getRef());

            Object received = receiveOne(duration("1 second"));
            assertThat(received, is(instanceOf(IsUniqueResponse.class)));
            IsUniqueResponse response = (IsUniqueResponse) received;

            //  then
            assertThat(response.isUnique(), is(false));

        }};
    }

    @Test
    public void shouldDetectNonUniqueName() {
        new JavaTestKit(system) {{

            //  when
            actor.tell(new IsUniqueRequest("thereisonlyonechucknorris"), getRef());

            Object received = receiveOne(duration("1 second"));
            assertThat(received, is(instanceOf(IsUniqueResponse.class)));
            IsUniqueResponse response = (IsUniqueResponse) received;

            //  then
            assertThat(response.isUnique(), is(true));

        }};
    }

}