package org.torpidomq.webconsole

import spray.routing.{Route, ExceptionHandler}
import spray.util.LoggingContext

/**
 * Created by urwisy on 2015-01-03. Unfinished.
 */
object  ExceptionHandling {

  implicit object DefaultExceptionHandler extends ExceptionHandler{

    override def isDefinedAt(x: Throwable): Boolean = ???

    override def apply(v1: Throwable): Route = ???
  }

}
