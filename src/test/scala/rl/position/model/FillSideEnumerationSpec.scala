package rl.position.model

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class FillSideEnumerationSpec extends FlatSpec with MustMatchers {
  behavior of "FillSideEnumeration"

  it should "ignore unexpected strings" in {
    // The following line might log an error message. This is expected.
    val actual = FillSideEnumeration.unapply("JJJJJJJJ")
    actual mustBe empty
  }
}
