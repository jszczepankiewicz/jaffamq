package org.torpidomq.webconsole.system

import collection.mutable.HashMap
import scala.collection.JavaConversions._ //  for filter to work correctly
/**
 * Created by urwisy on 22.03.14.
 */
class EnvironmentInfo {

  def javaProperties:Map[String,NamedProperty] = {

    val retval = new HashMap[String, NamedProperty]()
    val environmentVars = System.getProperties

    for ((k,v) <- environmentVars){
      retval += (k -> new NamedProperty(k,v))
    }

    return retval.toMap
  }

  def envProperties:Map[String, NamedProperty] = {
    val retval = new HashMap[String, NamedProperty]()

    for ((k,v) <- System.getenv()){
      retval += (k -> new NamedProperty(k,v))
    }

    return retval.toMap
  }
}
