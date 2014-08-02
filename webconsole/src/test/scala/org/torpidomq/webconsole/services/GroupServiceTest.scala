package org.torpidomq.webconsole.services

import org.scalatest.{Matchers, FlatSpec}
import spray.testkit.ScalatestRouteTest
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{Actor, Props, ActorRef}
import org.torpidomq.webconsole.TestDateTime
import spray.http.StatusCodes
import akka.util.Timeout
import org.jaffamq.persistence.database.actor.{GetByIdRequest, EntityResponse}
import org.jaffamq.persistence.database.group.Group
import org.jaffamq.persistence.database.CalendarUtils

/**
 * Created by urwisy on 2014-05-11.
 */
//@RunWith(classOf[JUnitRunner])
class GroupServiceTest extends FlatSpec with Matchers with ScalatestRouteTest with GroupService {

    def actorRefFactory = system

    // connect the DSL to the test ActorSystem
    val route = groupServiceRoute
    implicit val _system = system
    var _repoActor: ActorRef = _repoActor

    def repoActor = _repoActor

    //  in this context we will need unmarshallers declared as implicits

    import org.torpidomq.webconsole.json.GroupJsonProtocol._

    "GroupService" should "return Group for GET request with id provided" in {

        //  given
        _repoActor = GroupServiceMocks.createGroupExist(system)

        //  when
        Get("/api/groups/99") ~> route ~> check {
            val group = responseAs[Group]

            //  then
            group.getName() should be("nameForExists");
            group.getId() should be(99);
            group.getCreationtime().toDateTime(CalendarUtils.DB_TIMEZONE) should be(TestDateTime.A)
        }
    }

    it should "return 404 for GET request with id provided for not existing id" in {

        //  given
        _repoActor = GroupServiceMocks.createGroupNotExist(system)

        //  when
        Get("/api/groups/99") ~> route ~> check {
            response.status should be(StatusCodes.NotFound)
        }
    }


}