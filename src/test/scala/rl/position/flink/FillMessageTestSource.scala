package rl.position.flink

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.watermark.Watermark
import rl.position.model.FillMessage
import rl.time.LocalDateTimeCompanion.toEpochMilliseconds

/**
  * Generates Fill messages based on a list of strings
  *
  * @author Ricardo Leon
  */
class FillMessageTestSource(lines: Seq[String]) extends SourceFunction[FillMessage] {
  @volatile
  private var cancelJob = false

  override def cancel(): Unit = {
    cancelJob = false
  }

  override def run(ctx: SourceContext[FillMessage]): Unit = {
    val iter = lines.iterator
    while (iter.hasNext && !cancelJob) {
      iter.next() match {
        case FillMessage(message) =>
          ctx.collectWithTimestamp(message, toEpochMilliseconds(message.time))
          ctx.emitWatermark(new Watermark(toEpochMilliseconds(message.time)))
      }
    }
  }
}
