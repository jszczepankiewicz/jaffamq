package org.jaffamq.persistence.database.actor.user;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.jaffamq.persistence.database.actor.EntityListResponse;
import org.jaffamq.persistence.database.actor.EntityResponse;
import org.jaffamq.persistence.database.actor.GetByIdRequest;
import org.jaffamq.persistence.database.actor.GetPagedListRequest;
import org.jaffamq.persistence.database.actor.IsUniqueRequest;
import org.jaffamq.persistence.database.actor.IsUniqueResponse;
import org.jaffamq.persistence.database.repository.RepositoryTest;
import org.jaffamq.persistence.database.user.User;
import org.jaffamq.persistence.database.user.UserDefaults;
import org.jaffamq.persistence.database.user.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.jaffamq.persistence.database.repository.IdentifiableMatchers.hasId;
import static org.jaffamq.persistence.database.repository.UserMatchers.hasLogin;
import static org.junit.Assert.assertThat;

public class UserRepositoryReadActorTest extends RepositoryTest {

    private ActorSystem system;
    private ActorRef actor;

    @Rule
    public ExternalResource systemResource = new ExternalResource() {

        @Override
        protected void before() {
            system = ActorSystem.create();
            UserRepository repository = new UserRepository();
            final Props props = Props.create(UserRepositoryReadActor.class, repository, getDataSource());
            actor = system.actorOf(props);
        }

        @Override
        protected void after() {
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };

    @Test
    public void shouldReturnListOfUsers() {

        new JavaTestKit(system) {{

            //  when
            actor.tell(new GetPagedListRequest(2, 0), getRef());

            Object received = receiveOne(duration("1 second"));
            assertThat(received, is(instanceOf(EntityListResponse.class)));
            List<User> groups = ((EntityListResponse) received).getPage();

            //  then
            assertThat(groups.size(), is(equalTo(2)));
            assertThat(groups.get(0), hasId(1L));
            assertThat(groups.get(1), hasId(1000L));
            //  rest is checked in GroupRepository tests


        }};
    }

    @Test
    public void shouldReturnUserFoundForGetById() {

        new JavaTestKit(system) {{

            //  when
            actor.tell(new GetByIdRequest(1L), getRef());

            Object received = receiveOne(duration("1 second"));
            assertThat(received, is(instanceOf(EntityResponse.class)));
            EntityResponse response = (EntityResponse) received;

            //  then
            assertThat((User) response.getEntity(), allOf(
                    hasLogin(UserDefaults.SUPERADMIN_LOGIN),
                    hasId(1L)));

        }};
    }

    @Test
    public void shouldDetectUniqueName() {
        new JavaTestKit(system) {{

            //  when
            actor.tell(new IsUniqueRequest(UserDefaults.SUPERADMIN_LOGIN), getRef());

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