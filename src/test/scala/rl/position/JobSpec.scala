package rl.position

import java.util

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.runtime.tasks.StreamTaskException
import org.scalatest.{FlatSpec, MustMatchers}
import rl.position.model.PNLMessage

/**
  * Testing the flink job.
  *
  * @author Ricardo Leon
  */
class JobSpec extends FlatSpec with MustMatchers {
  behavior of "Job"

  it should "run and give me results" in {
    // The order in these sequences shouldn't matter.
    // The result should be the same if you shuffle them because it should use the timestamp.
    val fills = Seq(
      "F 1388534404004 MSFT 1.00 2 S", // outlay: +2, total outlay: -13, total shares 1
      "F 1388534401001 MSFT 5.00 3 B" // outlay: -15, shares 3
    )
    val priceUpdates = Seq(
      "P 1388534406006 MSFT 11.00", // PNL: 1*11 - 13 = -2, shares: 1
      "P 1388534403003 MSFT 7.00", // PNL: 3*7.00 - 15 = 6, shares: 3
      "P 1388534407007 MSFT 13.00" // PNL: 1*13-13=0, shares 1
    )
    val dummy = new SinkFunction[PNLMessage] {
      override def invoke(value: PNLMessage): Unit = {
        PNLMessage.print(value, System.out)
      }
    }
    val tested = new TestableJob(fills, priceUpdates, dummy)
    try {
      val emptyConfig = ParameterTool.fromMap(new util.HashMap[String, String]())
      tested.run(emptyConfig)
    } catch {
      case e: StreamTaskException =>
        // It seems like the ScalaTest framework doesn't work well with Flink's serialization.
        cancel(s"Run me using 'testOnly ${this.getClass.getCanonicalName}'", e)
    }
    // TODO: Get back results and make sure they're correct.  Right now they're printed to STDOUT.
  }

}
