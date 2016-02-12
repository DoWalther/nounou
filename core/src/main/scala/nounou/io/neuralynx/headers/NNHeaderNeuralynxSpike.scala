package nounou.io.neuralynx.headers

import java.time.Instant
import nounou.io.neuralynx.fileObjects.{FileNSE, FileNEV}

/**
  * Encapsulates text header for spike NSE file (Neuralynx).
  *
  */
trait NNHeaderNeuralynxSpike extends NNHeaderNeuralynxDAQ {

  /** [Header value NSE/NST/NTT: "WaveformLength"] */
  def getHeaderWaveformLength: Int
  /** [Header value NSE/NST/NTT: "AlignmentPt"] */
  def getHeaderAlignmentPt: Int

  override def getNeuralynxHeaderStringImpl() = {
    super[NNHeaderNeuralynxDAQ].getNeuralynxHeaderStringImpl() +
     "\n" +
    s"-WaveformLength $getHeaderWaveformLength\n" +
    s"-AlignmentPt $getHeaderAlignmentPt\n"
  }

}

abstract class NNHeaderNeuralynxSpikeRead(override val originalHeaderText: String)
  extends NNHeaderNeuralynxDAQRead(originalHeaderText)
  with NNHeaderNSE {

  override lazy val getHeaderFileType = nlxHeaderValueS("FileType", "Spike")
  loggerRequire(getHeaderFileType == "Spike", s"Spike file (*.nse/*.nst/*.ntt) with non-standard record type: $getHeaderFileType")

  override lazy val getHeaderWaveformLength = nlxHeaderValueI("WaveformLength", "0")
  override lazy val getHeaderAlignmentPt = nlxHeaderValueI("AlignmentPt", "-1")

}
