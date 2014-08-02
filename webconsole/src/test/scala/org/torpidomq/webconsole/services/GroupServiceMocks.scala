package org.torpidomq.webconsole.services

import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import org.jaffamq.persistence.database.actor.{EntityResponse, GetByIdRequest}
import org.jaffamq.persistence.database.group.Group
import org.torpidomq.webconsole.TestDateTime

/**
 * Created by urwisy on 2014-05-11.
 */
object GroupServiceMocks {

    def createGroupExist(system: ActorSystem):ActorRef = {

        system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[Group](
                        new Group.Builder("nameForExists").id(request.getId).creationtime(TestDateTime.A).build());
                }
            })
        );
    }

    def createGroupNotExist(system: ActorSystem):ActorRef = {

        system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[Group](
                        null)
                }
            })
        );
    }

}
