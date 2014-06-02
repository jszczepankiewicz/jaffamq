package org.torpidomq.webconsole.services

import akka.actor.Actor
import org.jaffamq.persistence.database.actor.GetByIdRequest

/**
 * Created by urwisy on 2014-05-11.
 */
object GroupServiceMocks {

    /*val exists = system.actorOf(Props(
        new Actor {

            //  unfortunatelly we can not use here case objects

            def receive() = {
                if (_.isInstanceOf(GetByIdRequest)){
                    sender! new EntityResponse[Group](Group.Builder("name").id(_.entity.id));
                }

                //case GetByIdRequest(id) => sender ! new ShowResponse(Some(Todo(Some(id),"title",false)))

            }
        })

    );*/
}
