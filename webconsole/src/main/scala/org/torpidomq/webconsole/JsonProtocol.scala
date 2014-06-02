package org.torpidomq.webconsole

import spray.json._

//import spray.json.JsField;

import org.torpidomq.webconsole.system.NamedProperty

/**
 * Created by urwisy on 30.03.14.
 */
object JsonProtocol extends DefaultJsonProtocol {

    import spray.json._


    implicit object NamedPropertiesMapJsonFormat extends RootJsonFormat[Map[String, NamedProperty]] {

        def write(map: Map[String, NamedProperty]) = {

            val listToConvert = for ((k, v) <- map) yield {
                JsObject(
                    "property" -> JsString(k),
                    "value" -> JsString(v.value)
                )
            }

            listToConvert.toJson
        }

        def read(json: JsValue) = null

    }

}
