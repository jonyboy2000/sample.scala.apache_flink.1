package rl.logging

import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Try}

/**
  * Use this trait to log errors found while trying to execute a function (e.g. parsing values).
  *
  * @author Ricardo Leon
  */
trait TryWithLogging[U] extends StrictLogging {
  /**
    * Logs a throwable and returns failure.
    */
  private[this] val logThrowable: PartialFunction[Throwable, Try[U]] = {
    case throwable: Throwable =>
      logger.error("Failure", throwable)
      new Failure[U](throwable)
  }

  /**
    * Tries to execute r, logs any error, and returns Some(result) on success or None on failure.
    *
    * @param r function to execute.
    * @return Some[U] if successful, None[U] if failure.
    */
  protected def tryOrLog(r: => U): Option[U] =
    Try[U](r).recoverWith[U](logThrowable).toOption
}
