package rl.position.model

import java.time.LocalDateTime

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class FillMessageSpec extends FlatSpec with MustMatchers {
  behavior of "FillMessage"

  it should "parse FillMessage" in {
    val input = Seq(
      "F 1388534400000 MSFT 42.43 300 B",
      "F 1388534400000 AAPL 181.26 300 B",
      "F 1388534472953 MSFT 42.93 300 S"
    )
    val actual = input.map(FillMessage.unapply)
    val expected = Seq(
      FillMessage("F", LocalDateTime.of(2014, 1, 1, 0, 0), StockSymbol("MSFT"), Money(42.43), SharesSize(300), FillSideEnumeration.Buy),
      FillMessage("F", LocalDateTime.of(2014, 1, 1, 0, 0), StockSymbol("AAPL"), Money(181.26), SharesSize(300), FillSideEnumeration.Buy),
      FillMessage("F", LocalDateTime.of(2014, 1, 1, 0, 1, 12, 953000000), StockSymbol("MSFT"), Money(42.93), SharesSize(300), FillSideEnumeration.Sell)
    ).map(Some.apply)
    actual must contain theSameElementsInOrderAs expected
  }

  it should "ignore invalid strings" in {
    val input = "AAABBCC 555 DDDD"
    // The following line might log an error message.  This is expected.
    val actual = FillMessage.unapply(input)
    actual mustBe empty
  }
}
