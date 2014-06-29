package org.torpidomq.webconsole.json

import org.jaffamq.persistence.database.destination.Destination
import org.joda.time.format.ISODateTimeFormat
import spray.json._

/**
 * Created by urwisy on 2014-06-29.
 */
object DestinationJsonProtocol extends DefaultJsonProtocol{

    implicit object DestinationJsonFormat extends RootJsonFormat[Destination]{

        override def write(destination: Destination): JsValue = {

            val links = JsObject(
                "self" ->JsObject(
                    "href" -> JsString("/api/destinations/" + destination.getId)),
                "readAuthorizedGroups" ->JsObject(
                    "href" -> JsString("/api/destinations/" + destination.getId + "/readAuthorizedGroups")),
                "writeAuthorizedGroups" ->JsObject(
                    "href" -> JsString("/api/destinations/" + destination.getId + "/writeAuthorizedGroups")),
                "adminAuthorizedGroups" ->JsObject(
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
            json.asJsObject.getFields("name", "nature") match {
                case Seq(JsString(name), JsString(nature)) => new Destination.Builder(name, nature).build()
                case _ => deserializationError("Expected fields: 'name' (string), 'nature' (string)")
            }
        }
    }

}
