package org.torpidomq.webconsole.json

import org.scalatest.{Matchers, FlatSpec}
import spray.json.{JsonParser, DefaultJsonProtocol, DeserializationException}

import org.jaffamq.persistence.database.group.Group
import org.torpidomq.webconsole.TestDateTime


/**
 * Test for Group Json marshalling/unmarshalling.
 */
class GroupJsonProtocolTest extends FlatSpec with Matchers with DefaultJsonProtocol {

    import org.torpidomq.webconsole.json.GroupJsonProtocol._

    "GroupJsonFormat" should "supports full round-trip in (de)serialization for Group" in {

        val group = new Group.Builder("nameOfTheGroup1").id(999).creationtime(TestDateTime.A).build()
        GroupJsonFormat.write(group).convertTo[Group] shouldEqual group
    }

    it should "unmarshalls Group when name and id provided" in {

        val expected = new Group.Builder("nameOfTheGroup1").id(999).build()
        JsonParser( """{ "name": "nameOfTheGroup1", "id": 999 }""").convertTo[Group] shouldEqual expected
    }

    it should "unmarshalls Group when name provided" in {

        val expected = new Group.Builder("nameOfTheGroup2").build()
        JsonParser( """{ "name": "nameOfTheGroup2"}""").convertTo[Group] shouldEqual expected
    }

    it should "marshall Group with id, name and creationstamp in ISO format" in {

        val expected = """{"id":999,"name":"nameOfTheGroup1","creationtime":"2014-04-20T15:38:02.884Z"}"""
        val group = new Group.Builder("nameOfTheGroup1").id(999).creationtime(TestDateTime.A).build()

        GroupJsonFormat.write(group).compactPrint shouldEqual expected
    }

    it should "throw DeserializationException when unrecognized body passed to unmarshall" in {

        val ex = intercept[DeserializationException] {
            JsonParser( """{ "namex": "x"}""").convertTo[Group]
        }

        ex.getMessage shouldEqual "Expected fields: 'name' (string), optionally 'id' (number) and 'creationstamp' (date as ISO string)"
    }
}
