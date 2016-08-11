package rl.position.flink

import java.io.File

import org.apache.flink.streaming.api.functions.sink.WriteSinkFunctionByMillis
import rl.position.model.PNLMessage


/**
  * Write PNL to a file.
  *
  * @author Ricardo Leon
  */
class PNLMessageSink(file: File)
  extends WriteSinkFunctionByMillis[PNLMessage](file.getAbsolutePath, new PNLWriteFormat(), 500l)