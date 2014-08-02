package org.torpidomq.webconsole.services

import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import org.jaffamq.persistence.database.actor.{EntityResponse, GetByIdRequest}
import org.jaffamq.persistence.database.group.Group
import org.jaffamq.persistence.database.destination.Destination
import org.torpidomq.webconsole.TestDateTime

/**
 * Created by urwisy on 2014-07-08.
 */
object DestinationServiceMocks {

    def createDestinationExist(system: ActorSystem):ActorRef = {

        system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[Destination](
                        new Destination.Builder("nameForExists", Destination.Type.QUEUE).id(request.getId).creationtime(TestDateTime.A).build());
                }
            })
        );
    }

    def createGroupNotExist(system: ActorSystem):ActorRef = {

        system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[Destination](
                        null)
                }
            })
        );
    }
}
