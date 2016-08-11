package rl.position

import java.io.File

import org.apache.flink.api.java.utils.ParameterTool
import rl.position.flink.{FillMessageSource, PNLMessageSink, PriceUpdateMessageSource}

/**
  * Entry point for command-line-based execution.
  *
  * @author Ricardo Leon
  */
object MainRunner {
  /**
    * The first 2 parameters are the input files.  The third one is the output file.  Any parameters remaining will be
    * used to configure Flink.
    */
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      System.err.println("Usage:")
      System.err.println(" java -jar PositionService.jar fillMessagesFile priceUpdateMessagesFile outputFile")
      System.exit(1)
    }
    val fillMessages = new File(args(0))
    if (!fillMessages.exists || !fillMessages.canRead) {
      System.err.println(s"File [${args(0)}] could not be found or cannot be read")
      System.exit(2)
    }
    val priceUpdateMessages = new File(args(1))
    if (!priceUpdateMessages.exists || !priceUpdateMessages.canRead) {
      System.err.println(s"File [${args(1)}] could not be found or cannot be read")
      System.exit(3)
    }
    val outputFile = new File(args(2))
    val fillMessagesSource = new FillMessageSource(fillMessages)
    val priceUpdateMessagesSource = new PriceUpdateMessageSource(priceUpdateMessages)
    val pnlOutput = new PNLMessageSink(outputFile)
    val parameters = ParameterTool.fromArgs(args.drop(3))
    new Job(fillMessagesSource, priceUpdateMessagesSource, pnlOutput).run(parameters)
  }

}
