package org.torpidomq.webconsole.services

import org.scalatest.{Matchers, FlatSpec}
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes.OK
import org.torpidomq.webconsole.JsonPathTest
;

/**
 * Created by urwisy on 30.03.14.
 */
class EnvironmentServiceTest extends FlatSpec with EnvironmentService with ScalatestRouteTest with Matchers with JsonPathTest{

    def actorRefFactory = system

    "EnvironmentService" should "return non-empty java properties" in{
        Get("/api/environment/jvm") ~> route ~> check {
            status should equal(OK)
            jsonArray(response, "$.*").size should be > 10
            val ref = jsonObject(response, "$.[?(@.property=='java.specification.name')]")
        }
    }

    it should "return non-empty env properties" in{
        Get("/api/environment/system") ~> route ~> check {
            status should equal(OK)
            jsonArray(response, "$.*").size should be > 10
            val ref = jsonObject(response, "$.[?(@.property=='PROCESSOR_LEVEL')]")
        }
    }
}
