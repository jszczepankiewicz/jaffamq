package org.torpidomq.webconsole.services

import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import org.jaffamq.persistence.database.actor.{EntityResponse, GetByIdRequest}
import org.jaffamq.persistence.database.user.User
import org.torpidomq.webconsole.TestDateTime


/**
 * Created by urwisy on 2014-05-10.
 */
object UserServiceMocks {

    def createUserExist(system: ActorSystem):ActorRef = {

        system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[User](
                        new User.Builder("nameForExists").id(request.getId).creationtime(TestDateTime.A).build());
                }
            })
        );
    }

    def createUserNotExist(system: ActorSystem) {

        system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[User](
                        null)
                }
            })
        );
    }
}
