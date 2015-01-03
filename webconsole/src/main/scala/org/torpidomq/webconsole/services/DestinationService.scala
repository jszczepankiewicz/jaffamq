package org.torpidomq.webconsole.services

import akka.actor.ActorRef
import akka.util.Timeout
import org.jaffamq.persistence.database.actor.{EntityListResponse, GetPagedListRequest, EntityResponse, GetByIdRequest}
import org.jaffamq.persistence.database.destination.Destination
import spray.http.StatusCodes
import spray.http.StatusCodes._
import akka.pattern.ask
import spray.httpx.SprayJsonSupport
import spray.routing.{ExceptionHandler, RequestContext, HttpService}
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

  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: NumberFormatException =>
        requestUri { uri =>
          log.warning("Request to {} could not be handled normally due to NumberFormatException", uri)
          complete(StatusCodes.BadRequest, "Invalid request")
        }
    }

    def logAndFail(ctx: RequestContext, e: Throwable)(implicit log: LoggingContext) {
        log.error(e, "Request {} could not be handled normally", ctx.request)
        ctx.complete(InternalServerError)
    }

    val destinationServiceRoute = {

        import org.torpidomq.webconsole.json.DestinationJsonProtocol._

        get {
            pathPrefix("api") {
                path("destinations" / "\\w+".r ) { id =>
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
                }~
                path("destinations" /) {
                  get {
                    parameters('offset ? 0, 'limit ? 50) { (offset, limit) => ctx =>
                      ask(repoActor, new GetPagedListRequest(limit, offset))
                        .mapTo[EntityListResponse[Destination]]
                        .onComplete {
                        case Success(resp) =>
                          if (resp.getPage == null) {
                            ctx.complete(NotFound)
                          }
                          else {
                            ctx.complete(resp.getPage)
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
}
