package rl.position.flink

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.watermark.Watermark
import rl.position.model.PriceUpdateMessage
import rl.time.LocalDateTimeCompanion._

/**
  * Generates messages based on a list of strings
  *
  * @author Ricardo Leon
  */
class PriceUpdateMessageTestSource(lines: Seq[String]) extends SourceFunction[PriceUpdateMessage] {
  @volatile
  private var cancelJob = false

  override def cancel(): Unit = {
    cancelJob = false
  }

  override def run(ctx: SourceContext[PriceUpdateMessage]): Unit = {
    val iter = lines.iterator
    while (iter.hasNext && !cancelJob) {
      iter.next() match {
        case PriceUpdateMessage(message) =>
          ctx.collectWithTimestamp(message, toEpochMilliseconds(message.time))
          ctx.emitWatermark(new Watermark(toEpochMilliseconds(message.time)))
      }
    }
  }
}
