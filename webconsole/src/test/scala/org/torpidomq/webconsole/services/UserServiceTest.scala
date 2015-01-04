package org.torpidomq.webconsole.services

import akka.actor.ActorRef
import org.jaffamq.persistence.database.CalendarUtils
import org.jaffamq.persistence.database.destination.Destination
import org.jaffamq.persistence.database.user.User
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.torpidomq.webconsole.TestDateTime
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

  it should "return 404 for GET request to retrieve non-existing user" in {

    //  given
    _repoActor = UserServiceMocks.createUserNotExist(system)

    //  when
    Get("/api/users/999") ~> route ~> check {
      response.status should be(StatusCodes.NotFound)
    }
  }

  it should "return User for GET request to retrieve existing user id" in {

    //  given
    _repoActor = UserServiceMocks.createUserExist(system)

    //  when
    Get("/api/users/2") ~> route ~> check {
      response.status should be(StatusCodes.OK)
      val user = responseAs[User]
      user.getId should be (2)
      user.getLogin should be ("nameForExists")
      user.getCreationTime.toDateTime(CalendarUtils.DB_TIMEZONE) should be(TestDateTime.A)
    }
  }

}
