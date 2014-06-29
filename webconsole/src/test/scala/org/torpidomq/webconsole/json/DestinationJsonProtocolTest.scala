package org.torpidomq.webconsole.json

import org.jaffamq.persistence.database.destination.Destination
import org.scalatest.{FlatSpec, Matchers}
import org.torpidomq.webconsole.TestDateTime
import spray.json.{DefaultJsonProtocol, JsonParser}

/**
 * Created by urwisy on 2014-06-29.
 */
class DestinationJsonProtocolTest extends FlatSpec with Matchers with DefaultJsonProtocol {

    import org.torpidomq.webconsole.json.DestinationJsonProtocol._

    "DestinationJsonProtocol" should "marshall Destination with id, name, type, creationtime, links to self and all groups" in {

        val expected = """{"id":999,"name":"karma","nature":"Q","creationtime":"2014-04-20T15:38:02.884Z","_links":{"self":{"href":"/api/destinations/999"},"readAuthorizedGroups":{"href":"/api/destinations/999/readAuthorizedGroups"},"writeAuthorizedGroups":{"href":"/api/destinations/999/writeAuthorizedGroups"},"adminAuthorizedGroups":{"href":"/api/destinations/999/adminAuthorizedGroups"}}}"""
        val destination = new Destination.Builder("karma", "Q").id(999).creationtime(TestDateTime.A).build()
        System.out.println(DestinationJsonFormat.write(destination).compactPrint)
        DestinationJsonFormat.write(destination).compactPrint shouldEqual expected
    }

    it should "unmarshall Destination with name and type" in {

        //  given
        val expected = new Destination.Builder("karma", "Q").build()

        //  when
        val marshalled = JsonParser( """{ "name": "karma", "nature": "Q" }""").convertTo[Destination]

        //  then
        /*
            Because under the hood creationtime is being set up we can not use shouldEqual on whole objects
            we need to test the fields individually
         */
        marshalled.getName should be("karma")
        marshalled.getType.toValue should be('Q')
        marshalled.getId should be(null)
        marshalled.getAdminAuthorizedGroups.size() should be(0)
        marshalled.getReadAuthorizedGroups.size() should be(0)
        marshalled.getWriteAuthorizedGroups.size() should be(0)
        marshalled.getCreationTime should not be (null)
    }


}
