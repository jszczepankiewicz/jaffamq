package org.torpidomq.webconsole.services

import akka.actor.ActorRef
import org.jaffamq.persistence.database.CalendarUtils
import org.jaffamq.persistence.database.destination.Destination
import org.scalatest.{Matchers, FlatSpec}
import org.torpidomq.webconsole.TestDateTime
import spray.http.StatusCodes
import spray.testkit.ScalatestRouteTest

/**
 * Created by urwisy on 2014-08-03.
 */
class DestinationServiceTest  extends FlatSpec with Matchers with ScalatestRouteTest with DestinationService{

  def actorRefFactory = system

  // connect the DSL to the test ActorSystem
  val route = destinationServiceRoute
  implicit val _system = system
  var _repoActor: ActorRef = _repoActor

  def repoActor = _repoActor

  //  in this context we will need unmarshallers declared as implicits

  import org.torpidomq.webconsole.json.DestinationJsonProtocol._

  "DestinationService" should "return Destination for GET request with id provided" in {

    //  given
    _repoActor = DestinationServiceMocks.createDestinationExist(system)

    //  when
    Get("/api/destinations/99") ~> route ~> check {
      response.status should be(StatusCodes.OK)
      val destination = responseAs[Destination]

      //  then
      destination.getName() should be("nameForExists");
      destination.getId() should be(99);
      destination.getType() should be (Destination.Type.QUEUE)
      destination.getCreationTime.toDateTime(CalendarUtils.DB_TIMEZONE) should be(TestDateTime.A)
    }
  }

  it should "return 404 for GET request with id provided for not existing id" in {

    //  given
    _repoActor = DestinationServiceMocks.createDestinationNotExist(system)

    //  when
    Get("/api/destinations/99999") ~> route ~> check {
      response.status should be(StatusCodes.NotFound)
    }
  }

  it should "return 400 for GET request with id provided as non integer" in {

    //  given
    _repoActor = DestinationServiceMocks.createDestinationNotExist(system)

    //  when
    Get("/api/destinations/a") ~> route ~> check {
      response.status should be(StatusCodes.BadRequest)
    }
  }

}
