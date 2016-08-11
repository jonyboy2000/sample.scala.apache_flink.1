package rl.position.model

import java.time.LocalDateTime

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class PNLMessageSpec extends FlatSpec with MustMatchers {
  behavior of "PNLMessage"

  it should "print the example messages" in {
    val expected = Seq(
      "PNL 1420063200000 MSFT 48100 -61888.51\n",
      "PNL 1420063200000 AAPL 48100 -103147.52\n",
      "PNL 1420066800000 MSFT 49400 -174554.88\n"
    )
    val actual = Seq(
      PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 22, 0), StockSymbol("MSFT"), SharesSize(48100), Money.zero, -Money(-61888.51), printable = true),
      PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 22, 0), StockSymbol("AAPL"), SharesSize(48100), Money.zero, -Money(-103147.52), printable = true),
      PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 23, 0), StockSymbol("MSFT"), SharesSize(49400), Money.zero, -Money(-174554.88), printable = true)
    ).map(message => PNLMessage.print(message, new StringBuffer).toString)
    actual must contain theSameElementsInOrderAs expected
  }

  it should "add base + something = something" in {
    val input = PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 22, 0), StockSymbol("MSFT"), SharesSize(48100), Money.zero, -Money(-61888.51), printable = true)
    PNLMessage.base.aggregate(input) mustBe input
  }

  it should "a merge b = c" in {
    val a = PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 22, 0), StockSymbol("MSFT"), SharesSize(48100), Money(1), -Money(1), printable = true)
    val b = PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 23, 0), StockSymbol("MSFT"), SharesSize(49400), Money(5), -Money(2), printable = true)
    val c = PNLMessage("PNL", LocalDateTime.of(2014, 12, 31, 23, 0), StockSymbol("MSFT"), SharesSize(49400+48100), Money(5), -Money(3), printable = true)
    a.aggregate(b) mustBe c
  }

}
