package rl.position

import org.apache.flink.streaming.api.functions.sink.SinkFunction
import rl.position.flink.{FillMessageTestSource, PriceUpdateMessageTestSource}
import rl.position.model.PNLMessage

/**
  * Simple class to test the underlying Job class.
  *
  * @author Ricardo Leon
  */
class TestableJob(testFillMessages: Seq[String],
  testPriceUpdateMessages: Seq[String],
  sink: SinkFunction[PNLMessage]
) extends
  Job(new FillMessageTestSource(testFillMessages),
    new PriceUpdateMessageTestSource(testPriceUpdateMessages),
    sink)
  with Serializable


