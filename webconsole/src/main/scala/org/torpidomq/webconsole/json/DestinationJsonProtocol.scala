package org.torpidomq.webconsole.json

import org.jaffamq.persistence.database.destination.Destination
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import scala.collection.JavaConversions._
import spray.json._

import scala.collection.mutable.ListBuffer

/**
 * Created by urwisy on 2014-06-29.
 */
object DestinationJsonProtocol extends DefaultJsonProtocol {

  implicit object DestinationJsonFormat extends RootJsonFormat[Destination] {

    override def write(destination: Destination): JsValue = {

      val links = JsObject(
        "self" -> JsObject(
          "href" -> JsString("/api/destinations/" + destination.getId)),
        "readAuthorizedGroups" -> JsObject(
          "href" -> JsString("/api/destinations/" + destination.getId + "/readAuthorizedGroups")),
        "writeAuthorizedGroups" -> JsObject(
          "href" -> JsString("/api/destinations/" + destination.getId + "/writeAuthorizedGroups")),
        "adminAuthorizedGroups" -> JsObject(
          "href" -> JsString("/api/destinations/" + destination.getId + "/adminAuthorizedGroups"))
      )

      JsObject(
        "id" -> JsNumber(destination.getId),
        "name" -> JsString(destination.getName),
        "nature" -> JsString(destination.getType.toValue.toString),
        "creationtime" -> JsString(ISODateTimeFormat.dateTime.print(destination.getCreationTime)),
        "_links" -> links
      )
    }

    override def read(json: JsValue): Destination = {
      json.asJsObject.getFields("name", "id", "nature", "creationtime") match {
        case Seq(JsString(name), JsNumber(id), JsString(nature), JsString(creationtime)) => new Destination.Builder(name, nature).id(id.toInt).creationtime(new DateTime(creationtime)).build()
        case _ => deserializationError("Expected fields: 'name' (string), 'nature' (string)")
      }
    }
  }

  implicit object DestinationListJsonFormat extends RootJsonFormat[java.util.List[Destination]] {

    override def read(json: JsValue): java.util.List[Destination] = null

    override def write(lst: java.util.List[Destination]): JsValue = {
      var destinations = new ListBuffer[JsValue]()

      for (destination <- lst) {
        destinations += destination.toJson
      }

      return destinations.toList.toJson

    }
  }

}
