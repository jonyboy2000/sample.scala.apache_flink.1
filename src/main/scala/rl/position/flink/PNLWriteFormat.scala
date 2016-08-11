package rl.position.flink

import java.io._
import java.util

import org.apache.flink.streaming.api.functions.sink.WriteFormat
import rl.position.model.PNLMessage

/**
  * Simple file writer for the PNLMessage
  * Note: Flink didn't let me mute stdout.  We have to use files for the output.
  *
  * @author Ricardo Leon
  */
class PNLWriteFormat extends WriteFormat[PNLMessage] {

  override def write(path: String, tuples: util.ArrayList[PNLMessage]): Unit = {
    val outStream = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))
    try {
      var i = 0
      while (i < tuples.size()) {
        PNLMessage.print(tuples.get(i), outStream)
        i += 1
      }
    } catch {
      case e: IOException =>
        throw new RuntimeException("Exception occurred while writing file " + path, e)
    } finally {
      outStream.close()
    }
  }
}
