package rl.position.model

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import rl.time.LocalDateTimeCompanion

/**
  * Message that indicates that there is a new price out there in the market for the given symbol.
  *
  * @author Ricardo Leon
  */
final case class PriceUpdateMessage(
  messageType: MessageType,
  time: LocalDateTime,
  symbol: StockSymbol,
  currentPrice: Money
)

object PriceUpdateMessage extends StrictLogging {
  def unapply(in: String): Option[PriceUpdateMessage] =
    in.split(' ').toList match {
      case messageType :: LocalDateTimeCompanion(dateTime) :: StockSymbol(symbol) :: Money(currentPrice) :: Nil =>
        Some(PriceUpdateMessage(messageType, dateTime, symbol, currentPrice))
      case unrecognized =>
        // If we find an unrecognized patter, log it and continue.
        logger.error("Unrecognized Price Update message: {}", in)
        None
    }
}