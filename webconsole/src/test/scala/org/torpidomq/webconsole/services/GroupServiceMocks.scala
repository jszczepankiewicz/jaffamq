package org.torpidomq.webconsole.services

import java.util.{List, ArrayList}

import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import org.jaffamq.persistence.database.actor.{EntityListResponse, GetPagedListRequest, EntityResponse, GetByIdRequest}
import org.jaffamq.persistence.database.group.Group
import org.torpidomq.webconsole.TestDateTime

/**
 * Created by urwisy on 2014-05-11.
 */
object GroupServiceMocks {

    def createGroupList(system:ActorSystem):ActorRef = {
      system.actorOf(Props(
        new Actor {
          def receive = {
            case request: GetPagedListRequest => sender ! new EntityListResponse[Group](
            {val groups:List[Group] = new ArrayList[Group]
              groups.add(new Group.Builder("namefor10").id(10l).creationtime(TestDateTime.A).build())
              groups.add(new Group.Builder("namefor20").id(20l).creationtime(TestDateTime.A).build())
              groups}
            );
          }
        })
      );
    }

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
