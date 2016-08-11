package rl.position.model

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import rl.time.LocalDateTimeCompanion

/**
  * Simple class to encapsulate a fill message.
  *
  * @author Ricardo Leon
  * @param messageType Always F for this type.
  * @param time        Fill time
  * @param symbol      Symbol name
  * @param price       Fill price
  * @param fillSize    Fill size
  * @param fillSide    Buy or Sell
  */
final case class FillMessage(
  messageType: MessageType,
  time: LocalDateTime,
  symbol: StockSymbol,
  price: Money,
  fillSize: SharesSize,
  fillSide: FillSideEnumeration.FillSide
)

object FillMessage extends StrictLogging {
  /**
    * Try to parse the PNL message from a string
    *
    * @param in Something like "F 1388534400000 AAPL 181.26 300 B"
    * @return fill message based on the string.
    */
  def unapply(in: String): Option[FillMessage] =
    in.split(' ').toList match {
      case messageType :: LocalDateTimeCompanion(dateTime) :: StockSymbol(symbol) :: Money(price) :: SharesSize(amount) :: FillSideEnumeration(side) :: Nil =>
        Some(FillMessage(messageType, dateTime, symbol, price, amount, side))
      case unrecognized =>
        // If we find an unrecognized patter, log it and continue.
        logger.error("Unrecognized PNL message: {}", in)
        None
    }
}