package rl.position.flink

import java.io.{BufferedReader, File, FileReader}

import com.typesafe.scalalogging.StrictLogging
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.watermark.Watermark
import rl.position.model.FillMessage
import rl.time.LocalDateTimeCompanion.toEpochMilliseconds

import scala.util.{Failure, Try}

/**
  * Read the contents as the file as FillMessages
  *
  * @author Ricardo Leon
  */
class FillMessageSource(file: File) extends SourceFunction[FillMessage] with StrictLogging {

  /**
    * This assumes that no message will arrive with more than one minute delay.
    */
  private val watermarkDistanceMS = 60000l
  @volatile
  private var cancelJob           = false
  private var maxTimestampMS      = 0l

  override def cancel(): Unit = {
    cancelJob = true
  }

  override def run(ctx: SourceContext[FillMessage]): Unit = {
    val buffer = new BufferedReader(new FileReader(file))
    try {
      val iter = buffer.lines().iterator()
      while (iter.hasNext && !cancelJob) {
        iter.next() match {
          case FillMessage(message) =>
            val timestamp = toEpochMilliseconds(message.time)
            ctx.collectWithTimestamp(message, timestamp)
            if (maxTimestampMS == 0l) {
              maxTimestampMS = timestamp
              ctx.emitWatermark(new Watermark(timestamp + watermarkDistanceMS))
            } else if (timestamp - maxTimestampMS > watermarkDistanceMS) {
              ctx.emitWatermark(new Watermark(timestamp + watermarkDistanceMS))
              maxTimestampMS = timestamp
            }
          case _ => // skip.  This case will be logged by the unapply function.
        }
      }
    } finally {
      Try(buffer.close()).recoverWith {
        case exception =>
          logger.error("Error closing FillMessage file" + file, exception)
          Failure(exception)
      }
    }
  }
}
