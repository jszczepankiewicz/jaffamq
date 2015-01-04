package org.torpidomq.webconsole.json


import org.jaffamq.persistence.database.CalendarUtils
import org.jaffamq.persistence.database.user.User
import org.scalatest.{Matchers, FlatSpec}
import org.torpidomq.webconsole.TestDateTime
import spray.json.{JsonParser, DefaultJsonProtocol}

/**
 * Created by urwisy on 2014-06-15.
 */
class UserJsonProtocolTest extends FlatSpec with Matchers with DefaultJsonProtocol {

    import org.torpidomq.webconsole.json.UserJsonProtocol._

    "UserJsonProtocol" should "marshall User with id, login, creationtime, links to self and groups" in {

        val expected = """{"id":999,"login":"jamesbond","creationtime":"2014-04-20T15:38:02.884Z","_links":{"self":{"href":"/api/users/999"},"groups":{"href":"/api/users/999/groups"}}}"""
        val user = new User.Builder("jamesbond").id(999).creationtime(TestDateTime.A).build()

        UserJsonFormat.write(user).compactPrint shouldEqual expected
    }

    it should "unmarshall User with login and password as expected during create" in {

        //  given
        val expected = new User.Builder("spiderman").password("deathToBatman!").build()

        //  when
        val marshalled = JsonParser( """{ "login": "spiderman", "password": "deathToBatman!" }""").convertTo[User]

        //  then
        /*
            Because under the hood creationtime is being set up we can not use shouldEqual on whole objects
            we need to test the fields individually
         */
        marshalled.getLogin should be ("spiderman")
        marshalled.getPassword should be ("deathToBatman!")
        marshalled.getId should be (null)
        marshalled.getGroups.size() should be (0)
        marshalled.getPasswordhash should be (null)
        marshalled.getCreationTime should not be (null)
    }

    it should "unmarshall User with id, login, creationtime as expected during GET (by id)" in {

        //  given
        val expected = new User.Builder("spiderman").password("deathToBatman!").build()

        //  when
        val marshalled = JsonParser( """{ "id": 2, "login": "somelogin", "creationtime":"2014-04-20T15:38:02.884Z" }""").convertTo[User]

        //  then
        /*
            Because under the hood creationtime is being set up we can not use shouldEqual on whole objects
            we need to test the fields individually
         */
        marshalled.getLogin should be ("somelogin")
        marshalled.getPassword should be (null)
        marshalled.getId should be (2)
        marshalled.getGroups.size() should be (0)
        marshalled.getPasswordhash should be (null)
        marshalled.getCreationTime.toDateTime(CalendarUtils.DB_TIMEZONE) should be(TestDateTime.A)
    }



}
