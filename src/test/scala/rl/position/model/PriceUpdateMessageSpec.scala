package rl.position.model

import java.time.LocalDateTime

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class PriceUpdateMessageSpec extends FlatSpec with MustMatchers {
  behavior of "PriceUpdateMessage"

  it should "parse sample text" in {
    val input = Seq(
      "P 1388534400000 MSFT 42.43",
      "P 1388534400000 AAPL 181.26"
    )
    val actual = input.map(PriceUpdateMessage.unapply)
    val expected = Seq(
      PriceUpdateMessage("P", LocalDateTime.of(2014, 1, 1, 0, 0), StockSymbol("MSFT"), Money(42.43)),
      PriceUpdateMessage("P", LocalDateTime.of(2014, 1, 1, 0, 0), StockSymbol("AAPL"), Money(181.26))
    ).map(Some.apply)
    actual must contain theSameElementsInOrderAs expected
  }

  it should "ignore invalid strings" in {
    val input = "P 1 AAAAAMMMM333"
    // The following line might log an error message.  This is expected.
    val actual = PriceUpdateMessage.unapply(input)
    actual mustBe empty
  }
}
