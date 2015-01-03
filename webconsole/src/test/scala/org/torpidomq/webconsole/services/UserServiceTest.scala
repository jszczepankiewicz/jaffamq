package org.torpidomq.webconsole.services

import akka.actor.ActorRef
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.torpidomq.webconsole.services.UserService
import spray.http.StatusCodes
import spray.testkit.ScalatestRouteTest

/**
 * Created by urwisy on 2014-05-10.
 */

class UserServiceTest  extends FlatSpec with Matchers with ScalatestRouteTest with UserService {

  def actorRefFactory = system

  // connect the DSL to the test ActorSystem
  val route = userServiceRoute
  implicit val _system = system
  var _repoActor: ActorRef = _repoActor

  def repoActor = _repoActor

  //  in this context we will need unmarshallers declared as implicits

  import org.torpidomq.webconsole.json.UserJsonProtocol._

  "UserService" should "return 400 for GET request with id provided as non integer" in {

    //  given
    _repoActor = UserServiceMocks.createUserNotExist(system)

    //  when
    Get("/api/users/a") ~> route ~> check {
      response.status should be(StatusCodes.BadRequest)
    }
  }

}
