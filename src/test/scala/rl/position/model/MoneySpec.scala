package rl.position.model

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class MoneySpec extends FlatSpec with MustMatchers {
  behavior of "MoneyAmount"

  it should "parse String as currency" in {
    val inOut = Seq(
      "1" -> BigDecimal.decimal(1),
      "-1" -> BigDecimal.decimal(-1),
      "0" -> BigDecimal.decimal(0),
      "0.1" -> BigDecimal.decimal(0.1),
      "-0.1" -> BigDecimal.decimal(-0.1),
      "3.45" -> BigDecimal.decimal(3.45),
      "3.456" -> BigDecimal.decimal(3.456)
    )

    inOut.foreach {
      case (input, decimal) =>
        val expected = Money(decimal)
        Money.unapply(input) must contain(expected)
    }
  }
  it should "add" in {
    val a = Money(1)
    val b = Money(-2)
    val c = Money(-1)
    (a + b) mustBe c
  }

  it should "subtract" in {
    val a = Money(1)
    val b = Money(2)
    val c = Money(-1)
    (a - b) mustBe c
  }

  it should "negate" in {
    val a = Money(1)
    val b = Money(-1)
    (-a) mustBe b
  }

}
