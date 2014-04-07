package org.torpidomq.webconsole

import org.jaffamq.test.JsonPathScalaAdapter
import spray.http.HttpResponse


/**
 * Matchers for JsonPath
 */
trait JsonPathTest {

    def jsonArray(response: HttpResponse, jsonPath: String): java.util.List[AnyRef] = {
        return JsonPathScalaAdapter.read(response.entity.asString, jsonPath);
    }

    def jsonObject(response: HttpResponse, jsonPath: String): Object = {
        return JsonPathScalaAdapter.readObject(response.entity.asString, jsonPath);
    }
}
