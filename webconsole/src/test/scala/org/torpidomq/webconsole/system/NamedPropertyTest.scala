package org.torpidomq.webconsole.system

import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by urwisy on 30.03.14.
 */
class NamedPropertyTest extends FlatSpec with Matchers {

    "The NamedProperty" should "return non-null values passed in constructor" in {
        val property = new NamedProperty("key1", "value1")
        property.name should equal ("key1")
        property.value should equal ("value1")
    }

    it should "return null values passed in constructor" in{
        val property = new NamedProperty(null, null)
        property.name should equal (null)
        property.value should equal (null)
    }

    it should "return hashcode equals to 0 for nulled name" in{
        val property = new NamedProperty(null, "some")
        property.hashCode should be (0)
    }

    it should "return hashcode not equal to 0 for non-nulled name" in{
        val property = new NamedProperty("key", "some")
        property.hashCode() should be > 0
    }
}
