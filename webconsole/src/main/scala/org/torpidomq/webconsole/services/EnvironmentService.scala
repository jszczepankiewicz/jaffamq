package org.torpidomq.webconsole.services

import spray.routing.HttpService
import org.torpidomq.webconsole.system.EnvironmentInfo
import spray.httpx.SprayJsonSupport

/**
 * Created by urwisy on 30.03.14.
 */
trait EnvironmentService extends HttpService with SprayJsonSupport {

    var env = new EnvironmentInfo()

    val route = {

        //  convert to implicit trait see
        import org.torpidomq.webconsole.JsonProtocol._

        get {
            pathPrefix("api" / "environment") {
                path("jvm") {

                    complete {
                        env.javaProperties
                    }
                }~
                path("system") {

                    complete {
                        env.envProperties
                    }
                }

            }
        }
    }
}
