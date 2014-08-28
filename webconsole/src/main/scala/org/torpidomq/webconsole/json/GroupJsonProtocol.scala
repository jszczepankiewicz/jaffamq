package org.torpidomq.webconsole.json

import org.jaffamq.persistence.database.group.Group
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spray.json._

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Created by urwisy on 2014-06-01.
 */
object GroupJsonProtocol extends DefaultJsonProtocol {


  implicit object GroupJsonFormat extends RootJsonFormat[Group] {

    def write(group: Group) = {

      JsObject(
        "id" -> JsNumber(group.getId),
        "name" -> JsString(group.getName),
        "creationtime" -> JsString(ISODateTimeFormat.dateTime.print(group.getCreationtime()))
      )
    }

    def read(json: JsValue) = {

      json.asJsObject.getFields("name", "id", "creationtime") match {
        case Seq(JsString(name)) => new Group.Builder(name).build()
        case Seq(JsString(name), JsNumber(id)) => new Group.Builder(name).id(id.toInt).build()
        case Seq(JsString(name), JsNumber(id), JsString(creationtime)) => new Group.Builder(name).id(id.toInt).creationtime(new DateTime(creationtime)).build()
        case _ => deserializationError("Expected fields: 'name' (string), optionally 'id' (number) and 'creationstamp' (date as ISO string)")

      }
    }
  }

  implicit object GroupListJsonFormat extends RootJsonFormat[java.util.List[Group]] {

    override def read(json: JsValue): java.util.List[Group] = null

    override def write(lst: java.util.List[Group]): JsValue = {
      var groups = new ListBuffer[JsValue]()

      for(group <- lst){
        groups+=group.toJson
      }

      return groups.toList.toJson

    }
  }

}
