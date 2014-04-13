package org.torpidomq.webconsole.services

import org.scalatest.{Matchers, FlatSpec}
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes.OK
import org.torpidomq.webconsole.JsonPathSupport
;

/**
 * Created by urwisy on 30.03.14.
 */
class EnvironmentServiceTest extends FlatSpec with EnvironmentService with ScalatestRouteTest with Matchers with JsonPathSupport{

    def actorRefFactory = system

    "EnvironmentService" should "return non-empty java properties" in{
        Get("/api/environment/jvm") ~> route ~> check {
            status should equal(OK)
            jsonArray("$.*").size should be > 10
            val ref = jsonObject("$.[?(@.property=='java.specification.name')]")
            //  TODO: add machers for ref

        }
    }

    it should "return non-empty env properties" in{
        Get("/api/environment/system") ~> route ~> check {
            status should equal(OK)
            jsonArray("$.*").size should be > 10
            val ref = jsonObject("$.[?(@.property=='PROCESSOR_LEVEL')]")
            //  TODO: add machers for ref
        }
    }
}
