package org.torpidomq.webconsole.system

import org.scalatest.{FlatSpec, Matchers, BeforeAndAfter}

/**
 * Created by urwisy on 29.03.14.
 */
class EnvironmentInfoTest extends FlatSpec with BeforeAndAfter with Matchers {

  private var env:EnvironmentInfo = _

  before{
    env = new EnvironmentInfo()
  }

  "EnvironmentInfo" should "return non-empty java properties" in{
    val props = env.javaProperties
    props.size should be > 1
  }

  it should "return non-empty env properties" in{
    val props = env.envProperties
    props.size should be > 1
  }

}
