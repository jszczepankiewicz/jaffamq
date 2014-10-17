package org.torpidomq.webconsole.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import org.jaffamq.persistence.database.actor.{EntityListResponse, EntityResponse, GetByIdRequest, GetPagedListRequest}
import org.jaffamq.persistence.database.user.User
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.routing.{HttpService, RequestContext}
import spray.util.LoggingContext

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created by urwisy on 2014-05-04.
 */
trait UserService extends HttpService with SprayJsonSupport {

  def repoActor: ActorRef

  implicit val timeout = Timeout(3000)

  implicit def executionContext: ExecutionContext = actorRefFactory.dispatcher

  def logAndFail(ctx: RequestContext, e: Throwable)(implicit log: LoggingContext) {
    log.error(e, "Request {} could not be handled normally", ctx.request)
    ctx.complete(InternalServerError)
  }

  val userServiceRoute = {

    import org.torpidomq.webconsole.json.UserJsonProtocol._

    get {
      pathPrefix("api") {
        path("users" / "\\w+".r) { id =>
          get { ctx =>
            ask(repoActor, new GetByIdRequest(id.toLong))
              .mapTo[EntityResponse[User]]
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
          path("users" /) {
            get {
              parameters('offset ? 0, 'limit ? 50) { (offset, limit) => ctx =>
                ask(repoActor, new GetPagedListRequest(limit, offset))
                  .mapTo[EntityListResponse[User]]
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
