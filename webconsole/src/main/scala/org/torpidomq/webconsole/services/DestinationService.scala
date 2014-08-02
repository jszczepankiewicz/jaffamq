package org.torpidomq.webconsole.services

import akka.actor.ActorRef
import akka.util.Timeout
import org.jaffamq.persistence.database.actor.{EntityResponse, GetByIdRequest}
import org.jaffamq.persistence.database.destination.Destination
import org.jaffamq.persistence.database.group.Group
import spray.http.StatusCodes._
import akka.pattern.ask
import spray.httpx.SprayJsonSupport
import spray.routing.{RequestContext, HttpService}
import spray.util.LoggingContext

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created by urwisy on 2014-07-08.
 */
trait DestinationService extends HttpService with SprayJsonSupport {

    def repoActor: ActorRef

    implicit val timeout = Timeout(3000)

    implicit def executionContext: ExecutionContext = actorRefFactory.dispatcher

    def logAndFail(ctx: RequestContext, e: Throwable)(implicit log: LoggingContext) {
        log.error(e, "Request {} could not be handled normally", ctx.request)
        ctx.complete(InternalServerError)
    }

    val destinationServiceRoute = {

        //  convert to implicit trait, this SHOULD NOT be removed, do not optimise this source
        import org.torpidomq.webconsole.json.DestinationJsonProtocol._

        get {
            pathPrefix("api") {
                path("destinations" / "\\w+".r) { id =>
                    get { ctx =>
                        ask(repoActor, new GetByIdRequest(id.toLong))
                                .mapTo[EntityResponse[Destination]]
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
