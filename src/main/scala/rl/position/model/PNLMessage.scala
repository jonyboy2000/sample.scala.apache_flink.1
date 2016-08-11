package rl.position.model

import java.time.LocalDateTime

import rl.time.LocalDateTimeCompanion

/**
  * Profit & Loss message.
  *
  * Use this to aggregate the data that comes as Fill and Price Update messages.
  *
  * @author Ricardo Leon
  * @param messageType       Should be P always
  * @param time              when was this originated
  * @param symbol            the stock ticker
  * @param sharesOwned       How many net shares are being held
  * @param lastPricePerShare the last price we know about
  * @param outlay            How much money was spent buying these instruments.
  * @param printable         Should this be printed?
  */
case class PNLMessage(
  messageType: MessageType,
  time: LocalDateTime,
  symbol: StockSymbol,
  sharesOwned: SharesSize,
  lastPricePerShare: Money,
  outlay: Money,
  printable: Boolean
) {

  /**
    * @return How much money your stock is worth minus how much you had to pay for it
    */
  def markToMarketPNL: Money =
    (lastPricePerShare * sharesOwned) - outlay

  /**
    * Use this to aggregate PNL messages that will represent the final state.
    * Note: this aggregation function is NOT commutative.  a.aggregate(b) ≠ b.aggregate(a)
    *
    * @param other message that belongs to the same symbol.
    * @return aggregate data from the first PNL and the second, but using the "other" price information and the "other" time information
    */
  def aggregate(other: PNLMessage) = {
    // Make sure we don't mix Apples with Pears
    require(symbol.name == "" || symbol == other.symbol, "Trying to mix " + symbol.name + " with " + other.symbol.name)
    PNLMessage(messageType,
      other.time,
      other.symbol,
      sharesOwned + other.sharesOwned,
      other.lastPricePerShare,
      outlay + other.outlay,
      printable = other.printable
    )
  }
}

object PNLMessage {

  final private val messageType      = "PNL"
  final private val fieldSeparator   = ' '
  final private val messageSeparator = '\n'
  val base = PNLMessage(
    messageType,
    LocalDateTime.MIN,
    StockSymbol.empty,
    SharesSize.zero,
    Money.zero,
    Money.zero,
    printable = false)

  /**
    * Print the message using this format:
    * "Message type" "Milliseconds" "Symbol" "Size Owned" "Mark to Market P&L"\n
    * Examples:
    * PNL 1420063200000 MSFT 48100 -61888.51
    * PNL 1420063200000 AAPL 48100 -103147.52
    *
    * @param message message to print
    * @param out     where to print
    * @return the "out" parameter for chaining
    */
  def print[T <: Appendable](message: PNLMessage, out: T): T = {
    import message._
    out.append(new StringBuffer(50)
      .append(message.messageType).append(fieldSeparator)
      .append(LocalDateTimeCompanion.toEpochMilliseconds(time).toString).append(fieldSeparator)
      .append(symbol.name).append(fieldSeparator)
      .append(sharesOwned.size.toString).append(fieldSeparator)
      .append(markToMarketPNL.figure.toString)
      .append(messageSeparator))
    out
  }

  /**
    * Convert a Fill message into a PNL message
    *
    * @param fillMessage base data for the PNL message
    * @return PNL message with the same information from the fill message
    */
  def apply(fillMessage: FillMessage): PNLMessage = {
    val (newSize, newOutlay) = fillMessage.fillSide match {
      case FillSideEnumeration.Buy => (fillMessage.fillSize, fillMessage.price * fillMessage.fillSize)
      case FillSideEnumeration.Sell => (-fillMessage.fillSize, -fillMessage.price * fillMessage.fillSize)
    }
    PNLMessage(
      messageType = PNLMessage.messageType,
      time = fillMessage.time,
      symbol = fillMessage.symbol,
      sharesOwned = newSize,
      lastPricePerShare = fillMessage.price,
      outlay = newOutlay,
      printable = false)
  }

  /**
    * Convert a Price Update message into a PNL message
    *
    * @param priceUpdateMessage base data for the PNL message
    * @return PNL message without any stock purchase data
    */
  def apply(priceUpdateMessage: PriceUpdateMessage): PNLMessage =
    PNLMessage(
      messageType = PNLMessage.messageType,
      time = priceUpdateMessage.time,
      symbol = priceUpdateMessage.symbol,
      sharesOwned = SharesSize.zero,
      lastPricePerShare = priceUpdateMessage.currentPrice,
      outlay = Money.zero,
      printable = true
    )
}