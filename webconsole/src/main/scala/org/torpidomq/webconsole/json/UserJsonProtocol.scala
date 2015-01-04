package org.torpidomq.webconsole.json


import org.jaffamq.persistence.database.user.User
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.collection.JavaConversions._
import spray.json._

import scala.collection.mutable.ListBuffer

/**
 * Created by urwisy on 2014-06-08.
 */
object UserJsonProtocol extends DefaultJsonProtocol {

  implicit object UserJsonFormat extends RootJsonFormat[User] {
    override def write(user: User): JsValue = {

      val links = JsObject(
        "self" -> JsObject(
          "href" -> JsString("/api/users/" + user.getId)),
        "groups" -> JsObject(
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
      json.asJsObject.getFields("id", "login", "creationtime" , "password") match {
        case Seq(JsNumber(id), JsString(login), JsString(creationtime)) => new User.Builder(login).id(id.toInt).creationtime(new DateTime(creationtime)).build()
        case Seq(JsString(login), JsString(password)) => new User.Builder(login).password(password).build()
        case _ => deserializationError("Expected fields: 'login' (string), 'password' (string)")
      }
    }
  }

  implicit object UserListJsonFormat extends RootJsonFormat[java.util.List[User]] {

    override def read(json: JsValue): java.util.List[User] = null

    override def write(lst: java.util.List[User]): JsValue = {
      var users = new ListBuffer[JsValue]()

      for (user <- lst) {
        users += user.toJson
      }

      return users.toList.toJson
    }
  }

}
