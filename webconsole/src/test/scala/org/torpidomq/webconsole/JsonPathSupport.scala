package org.torpidomq.webconsole

import spray.testkit.RouteTest
import org.jaffamq.test.JsonPathScalaAdapter

/**
 * Utilities supporting JsonPath
 */
trait JsonPathSupport { this: RouteTest =>
    def jsonArray(jsonPath: String) = JsonPathScalaAdapter.read(entity.asString, jsonPath)
    def jsonObject(jsonPath: String) = JsonPathScalaAdapter.readObject(entity.asString, jsonPath);
}
