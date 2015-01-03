package org.torpidomq.webconsole.services

import spray.http.StatusCodes
import StatusCodes._
import spray.routing.{ExceptionHandler, RequestContext, HttpService}
import spray.httpx.SprayJsonSupport
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import spray.util.LoggingContext
import scala.util.Failure
import scala.util.Success
import org.jaffamq.persistence.database.actor.{EntityListResponse, GetPagedListRequest, GetByIdRequest, EntityResponse}
import org.jaffamq.persistence.database.group.Group
import scala.concurrent.ExecutionContext


/**
 * Created by urwisy on 2014-05-11.
 */
trait GroupService extends HttpService with SprayJsonSupport {

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


  val groupServiceRoute = {

    //  convert to implicit trait, this SHOULD NOT be removed, do not optimise this source
    import org.torpidomq.webconsole.json.GroupJsonProtocol._

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
        } ~
          path("groups" /) {
            get {
              parameters('offset ? 0, 'limit ? 50) { (offset, limit) => ctx =>
                ask(repoActor, new GetPagedListRequest(limit, offset))
                  .mapTo[EntityListResponse[Group]]
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
