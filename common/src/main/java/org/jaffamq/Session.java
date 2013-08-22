package org.jaffamq;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 17.08.13
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class Session extends UntypedActor{
    final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    public enum State{
        BLANK,
        CONNECTED,
        DISCONNECTED
    }

    protected void setState(State s) {
        if (state != s) {
            transition(state, s);
            state = s;
        }
    }

    private void transition(State old, State next){
        state = next;
    }

    private State state = State.BLANK;
    @Override
    public void onReceive(Object o) throws Exception {
        log.debug("Session.onReceive: {}", o);
    }
}
