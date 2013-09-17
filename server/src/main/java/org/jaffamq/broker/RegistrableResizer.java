package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.routing.Resizer;
import akka.routing.RouteeProvider;

import java.util.LinkedList;
import java.util.List;

/**
 * Resizer that has the ability to add / remove subscribers during the router lifecycle.
 */
public class RegistrableResizer implements Resizer {

    private boolean needsResize = false;

    private List<ActorRef> routeesToRegister = new LinkedList<>();
    private List<ActorRef> routeesToUnregister = new LinkedList<>();

    public synchronized void registerRoutee(ActorRef actor) {
        routeesToUnregister.remove(actor);
        routeesToRegister.add(actor);
        needsResize = true;
    }

    public synchronized void unregisterRoutee(ActorRef actor) {
        routeesToRegister.remove(actor);
        routeesToUnregister.add(actor);
        needsResize = true;
    }

    @Override
    public boolean isTimeForResize(long messageCounter) {
        return needsResize;
    }

    @Override
    public synchronized void resize(RouteeProvider routeeProvider) {

        if (routeesToRegister.size() > 0) {
            routeeProvider.registerRoutees(routeesToRegister);
            routeesToRegister = new LinkedList<>();
        }

        if (routeesToUnregister.size() > 0) {
            routeeProvider.unregisterRoutees(routeesToUnregister);
            routeesToUnregister = new LinkedList<>();
        }

        needsResize = false;
    }
}
