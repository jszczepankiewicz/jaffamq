package org.jaffamq.broker;


import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.routing.RouteeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;

/**
 * Unit test for RegistrableResizer using mocks. There is also integration test for that.
 */
public class RegistrableResizerTest {

    private RegistrableResizer resizer;

    @Before
    public void init(){
        resizer = new RegistrableResizer();
    }

    @Test
    public void shouldShowNoTimeForResizeWithoutRoutees(){

        //  then
        assertThat(resizer.isTimeForResize(0), is(false));
    }

    @Test
    public void shouldChangeIsTimeForResizeAfterRouteeRegister(){

        //  given
        ActorRef actor1 = Mockito.mock(ActorRef.class);

        //  when
        resizer.registerRoutee(actor1);

        //  then
        assertThat(resizer.isTimeForResize(1), is(true));

    }

    @Test
    public void shouldChangeIsTimeForResizeAfterRoutee(){

        //  given
        ActorRef actor1 = Mockito.mock(ActorRef.class);

        //  when
        resizer.unregisterRoutee(actor1);

        //  then
        assertThat(resizer.isTimeForResize(1), is(true));
    }

    @Test
    public void shouldInvokeRegisterOnRouterAfterRemoveRoutees(){

        //  given
        ActorRef actor1 = Mockito.mock(ActorRef.class);
        resizer.registerRoutee(actor1);
        RouteeProvider provider = Mockito.mock(RouteeProvider.class);
        ArgumentCaptor<Iterable> listCaptor = ArgumentCaptor.forClass(Iterable.class);

        //  when
        resizer.resize(provider);

        //  then
        assertThat(resizer.isTimeForResize(1), is(false));
        verify(provider).registerRoutees(listCaptor.capture());
        Iterator iterator = listCaptor.getValue().iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        Object listElement = iterator.next();
        assertThat(listElement instanceof ActorRef, is(true));
    }

    @Test
    public void shouldInvokeUnregisterOnRouterAfterAddRoutees(){

        //  given
        ActorRef actor1 = Mockito.mock(ActorRef.class);
        resizer.unregisterRoutee(actor1);
        RouteeProvider provider = Mockito.mock(RouteeProvider.class);
        ArgumentCaptor<Iterable> listCaptor = ArgumentCaptor.forClass(Iterable.class);

        //  when
        resizer.resize(provider);

        //  then
        assertThat(resizer.isTimeForResize(1), is(false));
        verify(provider).unregisterRoutees(listCaptor.capture());
        Iterator iterator = listCaptor.getValue().iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        Object listElement = iterator.next();
        assertThat(listElement instanceof ActorRef, is(true));
    }

}
