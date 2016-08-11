package rl.position.model

import rl.logging.TryWithLogging

/**
  * Currency-less money amount with arbitrary precision.
  * Can take negative numbers.
  *
  * @author Ricardo Leon
  * @param figure amount
  */
case class Money(figure: BigDecimal) {
  def unary_- : Money =
    Money(-figure)

  def *(owned: SharesSize): Money =
    Money(figure * owned.size)

  def +(other: Money) =
    Money(this.figure + other.figure)

  def -(other: Money) =
    Money(this.figure - other.figure)
}

object Money extends TryWithLogging[Money] {
  final val zero = Money(BigDecimal(0))

  def unapply(in: String): Option[Money] =
    tryOrLog(Money(BigDecimal(in)))

}