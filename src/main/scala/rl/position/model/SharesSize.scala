package rl.position.model

import rl.logging.TryWithLogging

/**
  * Signed position size.
  *
  * @author Ricardo Leon
  */
case class SharesSize(size: Long) {
  def negate =
    SharesSize(-this.size)

  def +(other: SharesSize) =
    SharesSize(this.size + other.size)

  def -(other: SharesSize) =
    SharesSize(this.size - other.size)

  def unary_- : SharesSize =
    SharesSize(-this.size)
}

object SharesSize extends TryWithLogging[SharesSize] {
  final val zero: SharesSize = SharesSize(0)

  def unapply(in: String): Option[SharesSize] =
    tryOrLog(SharesSize(in.toLong))
}