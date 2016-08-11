package rl.position.model

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class SharesSizeSpec extends FlatSpec with MustMatchers {
  behavior of "SharesSize"
  it should "add" in {
    val a = SharesSize(1)
    val b = SharesSize(-2)
    val c = SharesSize(-1)
    (a + b) mustBe c
  }

  it should "subtract" in {
    val a = SharesSize(1)
    val b = SharesSize(-2)
    val c = SharesSize(3)
    (a - b) mustBe c
  }

  it should "negate" in {
    val a = SharesSize(1)
    val b = SharesSize(-1)
    (-a) mustBe b
  }

}
