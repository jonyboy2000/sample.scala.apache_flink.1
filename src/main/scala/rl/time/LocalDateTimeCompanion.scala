package rl.time

import java.time.temporal.ChronoField
import java.time.{ZoneOffset, LocalDateTime => JavaLocalDateTime}

import rl.logging.TryWithLogging

/**
  * Companion object of LocalDateTime from Java.
  *
  * @author Ricardo Leon
  */
object LocalDateTimeCompanion extends TryWithLogging[JavaLocalDateTime] {

  /**
    * Convert string to LocalDateTime.
    *
    * @param millisecondsEpoch Unix epoch in milliseconds (since January 1, 1970)
    * @return Some local date time if the string could be parsed, None otherwise.
    */
  def unapply(millisecondsEpoch: String): Option[JavaLocalDateTime] =
    tryOrLog {
      val epoch = millisecondsEpoch.toLong
      // the input epoch is given in milliseconds. We need to convert it to seconds.
      val seconds = millisecondsEpoch.toLong / 1000l
      val remainderMilliSeconds = epoch % 1000l
      JavaLocalDateTime.ofEpochSecond(seconds, remainderMilliSeconds.toInt * 1000000, ZoneOffset.UTC)
    }

  /**
    * Convert the given LocalDateTime to epoch in milliseconds
    *
    * @param in date time to convert
    * @return the date time as an epoch in milliseconds
    */
  def toEpochMilliseconds(in: JavaLocalDateTime): Long =
    in.toEpochSecond(ZoneOffset.UTC) * 1000l + in.getLong(ChronoField.MILLI_OF_SECOND)
}
