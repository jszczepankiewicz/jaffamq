package org.torpidomq.webconsole.services

import spray.routing.HttpService
import spray.httpx.SprayJsonSupport

/**
 * Created by urwisy on 2014-05-04.
 */
trait UserService extends HttpService with SprayJsonSupport {

    /*val route = {

        //  convert to implicit trait see

        get {
            pathPrefix("api" / "groups") {
                path("jvm") {

                    complete {
                        env.javaProperties
                    }
                } ~
                        path("system") {

                            complete {
                                env.envProperties
                            }
                        }

            }
        }
    }*/
}
