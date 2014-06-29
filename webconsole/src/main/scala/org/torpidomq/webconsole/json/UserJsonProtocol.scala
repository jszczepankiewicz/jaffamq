package org.torpidomq.webconsole.json


import spray.json._
import org.jaffamq.persistence.database.user.User
import org.joda.time.format.ISODateTimeFormat

/**
 * Created by urwisy on 2014-06-08.
 */
object UserJsonProtocol extends DefaultJsonProtocol {

    implicit object UserJsonFormat extends RootJsonFormat[User]{
        override def write(user: User): JsValue = {

            val links = JsObject(
                "self" ->JsObject(
                    "href" -> JsString("/api/users/" + user.getId)),
                "groups" ->JsObject(
                    "href" -> JsString("/api/users/" + user.getId + "/groups"))
            )

            JsObject(
                "id" -> JsNumber(user.getId),
                "login" -> JsString(user.getLogin),
                "creationtime" -> JsString(ISODateTimeFormat.dateTime.print(user.getCreationTime)),
                "_links" -> links
            )
        }

        override def read(json: JsValue): User = {
            json.asJsObject.getFields("login", "password") match {
                case Seq(JsString(login), JsString(password)) => new User.Builder(login).password(password).build()
                case _ => deserializationError("Expected fields: 'login' (string), 'password' (string)")
            }
        }
    }
}
