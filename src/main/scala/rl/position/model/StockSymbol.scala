package rl.position.model

import rl.logging.TryWithLogging

/**
  * Stock exchange symbol.
  *
  * @author Ricardo Leon
  * @param name E.g. "AAPL".
  */
case class StockSymbol(name: String)

object StockSymbol extends TryWithLogging[StockSymbol] {
  val empty: StockSymbol = StockSymbol("")

  /**
    * Parse Symbol.
    *
    * @param in any string that represents a symbol
    * @return Some Symbol if value is correct.
    */
  def unapply(in: String): Option[StockSymbol] =
    tryOrLog(StockSymbol(in))
}