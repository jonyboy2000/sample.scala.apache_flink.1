package rl.position

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import rl.position.model.{FillMessage, PNLMessage, PriceUpdateMessage, StockSymbol}
import rl.time.LocalDateTimeCompanion


/**
  * Apache Flink Job.
  *
  * You can also generate a .jar file that you can submit on your Flink
  * cluster. Just type
  * {{{
  *   sbt clean assembly
  * }}}
  * in the projects root directory. You will find the jar in
  * target/scala-2.11/Flink\ Project-assembly-0.1-SNAPSHOT.jar
  */
class Job(fillMessageSource: SourceFunction[FillMessage],
  priceUpdateMessageSource: SourceFunction[PriceUpdateMessage],
  output: SinkFunction[PNLMessage]
) {
  /**
    * 1. Setup Apache Flink's enviroment
    * 2. Set the "Time Characteristic" to event time.
    * 3. Create two streams for Fill Messages and Price Update Messages
    * 4. Convert the two messages into a common type that will be used as state
    * 5. Update the objects based on the state
    * 6. Print them
    */
  def run(parameters: ParameterTool): Unit = {
    val env = StreamExecutionEnvironment.createLocalEnvironment()
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setGlobalJobParameters(parameters)

    val fillStream = addFillStream(env)
      .map(PNLMessage.apply(_)) // transform the fill messages into PNL messages for aggregation

    val priceUpdateStream = addPriceUpdate(env)
      .map(PNLMessage.apply(_)) // transform the fill messages into PNL messages for aggregation

    val coGroupedStream = fillStream.union(priceUpdateStream)
      .keyBy(_.symbol)
      .timeWindow(window)
      .apply[PNLMessage](windowFunction)
      .keyBy(_.symbol)
      .mapWithState[PNLMessage, PNLMessage](accumulatePosition)
      .filter(_.printable)
      .addSink(output)

    env.execute("Position Service")
  }

  /**
    * Update the current balance with the previous value.
    */
  private def accumulatePosition = (message: PNLMessage, stateOpt: Option[PNLMessage]) =>
    (message, stateOpt) match {
      case (pnlMessage, None) =>
        (pnlMessage, Some(pnlMessage))
      case (pnlMessage, Some(state)) =>
        val newPNL = state.aggregate(pnlMessage)
        (newPNL, Some(newPNL))
    }

  /**
    * The messages are accumulated every N seconds.
    * This makes sure the messages within those N seconds are sorted by time so that we can have an accurate position size
    * we generate the updated PNL based on the price updates.
    */
  private def windowFunction =
    new WindowFunction[PNLMessage, PNLMessage, StockSymbol, TimeWindow] {

      override def apply(key: StockSymbol, window: TimeWindow, input: Iterable[PNLMessage], out: Collector[PNLMessage]): Unit = {
        // Sorting the message tries to make sure that the Fill position messages are NOT processed AFTER the Price Update messages are processed.
        // This is not guaranteed because the process only waits a few seconds to receive outdated fill messages.
        val sortedMessages = input.toSeq.sortBy(message => LocalDateTimeCompanion.toEpochMilliseconds(message.time))
        var previousMessage = PNLMessage.base
        sortedMessages.foreach { message =>
          val mergedMessage = previousMessage.aggregate(message)
          previousMessage = message
          if (mergedMessage.printable) {
            out.collect(mergedMessage)
          }
        }
        // If this is the last message and it's not printable, then let's send it to the stream to keep the last state.
        if (!previousMessage.printable) {
          out.collect(previousMessage)
        }
      }
    }

  /**
    * The time we're going to give the messages to get to our platform before we issue the next
    */
  private def window = Time.seconds(10)

  protected def addFillStream(env: StreamExecutionEnvironment): DataStream[FillMessage] =
    env.addSource[FillMessage](fillMessageSource)

  protected def addPriceUpdate(env: StreamExecutionEnvironment) =
    env.addSource[PriceUpdateMessage](priceUpdateMessageSource)
}