package org.torpidomq.webconsole.services

import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{Actor, Props, ActorRef}
import org.torpidomq.webconsole.TestDateTime
import akka.util.Timeout
import org.jaffamq.persistence.database.actor.{GetByIdRequest, EntityResponse}
import org.jaffamq.persistence.database.group.Group
import org.jaffamq.persistence.database.CalendarUtils

/**
 * Created by urwisy on 2014-05-11.
 */
//@RunWith(classOf[JUnitRunner])
class GroupServiceTest extends FlatSpec with ShouldMatchers with ScalatestRouteTest with GroupService {

    def actorRefFactory = system

    // connect the DSL to the test ActorSystem
    val route = groupServiceRoute
    implicit val _system = system
    var _repoActor: ActorRef = Mocks.exists

    def repoActor = _repoActor

    object Mocks {
        implicit val askTimeout: Timeout = Timeout(3000)

        val exists = system.actorOf(Props(
            new Actor {

                def receive = {
                    case request: GetByIdRequest => sender ! new EntityResponse[Group](
                        new Group.Builder("nameForExists").id(request.getId).creationtime(TestDateTime.A).build());

                }
            })

        );
    };

    //  in this context we will need unmarshallers declared as implicits

    import org.torpidomq.webconsole.json.GroupJsonProtocol._

    "GroupService" should "return Group for GET request with id provided" in {

        //  given
        _repoActor = Mocks.exists

        //  when
        Get("/api/groups/99") ~> route ~> check {
            val group = responseAs[Group]

            //  then
            group.getName() should be("nameForExists");
            group.getId() should be(99);
            val i = group.getCreationtime().toDateTime(CalendarUtils.DB_TIMEZONE)
            i should be(TestDateTime.A)
        }
    }

    /*describe("the service show") {
        it("should return a group if it exists") {
            _repoActor = Mocks.exists

            Get("/api/todo/TEST") ~> route ~> check {
                val todo = entityAs[String]
                todo should include("title")
                todo should include("TEST")
            }
        }
    }*/

}