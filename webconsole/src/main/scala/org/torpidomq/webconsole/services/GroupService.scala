package org.torpidomq.webconsole.services

import spray.http.StatusCodes
import StatusCodes._
import spray.routing.{RequestContext, HttpService}
import spray.httpx.SprayJsonSupport
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import spray.util.LoggingContext
import scala.util.Failure
import scala.util.Success
import org.jaffamq.persistence.database.actor.GetByIdRequest
import org.jaffamq.persistence.database.group.Group
import org.jaffamq.persistence.database.actor.EntityResponse
import scala.concurrent.ExecutionContext


/**
 * Created by urwisy on 2014-05-11.
 */
trait GroupService extends HttpService with SprayJsonSupport {

    def repoActor: ActorRef

    implicit val timeout = Timeout(3000)

    implicit def executionContext: ExecutionContext = actorRefFactory.dispatcher

    def logAndFail(ctx: RequestContext, e: Throwable)(implicit log: LoggingContext) {
        log.error(e, "Request {} could not be handled normally", ctx.request)
        ctx.complete(InternalServerError)
    }


    val groupServiceRoute = {

        //  convert to implicit trait see

        get {
            pathPrefix("api") {
                path("groups" / "\\w+".r) { id =>
                    get { ctx =>
                        ask(repoActor, new GetByIdRequest(id.toLong))
                                .mapTo[EntityResponse[Group]]
                                .onComplete {
                            case Success(resp) =>
                                if (resp.getEntity == null) {
                                    ctx.complete(NotFound)
                                }
                                else {
                                    ctx.complete(resp.getEntity)
                                }

                            case Failure(e) =>
                                logAndFail(ctx, e)
                        }
                    }
                }
            }
        }
    }
}
