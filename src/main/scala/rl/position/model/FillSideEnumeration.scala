package rl.position.model

import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success, Try}

/**
  * Operation done when filling the order: Buy or Sell.
  *
  * @author Ricardo Leon
  */
object FillSideEnumeration extends Enumeration with StrictLogging {
  type FillSide = Value
  val Buy  = Value("B")
  val Sell = Value("S")

  /**
    * Parse Fill Side using the "names" (e.g. "B" or "S")
    *
    * @param in "B" or "S"
    * @return FillSide if found by the name
    */
  def unapply(in: String): Option[FillSide] =
    Try(withName(in)) match {
      case Success(fillSide) =>
        Some(fillSide)
      case Failure(exception) =>
        logger.error("Could not find fill side enumeration", exception)
        None
    }
}
