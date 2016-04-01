package nounou.io.neuroplex

import java.io.File

import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import breeze.linalg.DenseVector
import nounou.elements.NNElement
import nounou.elements.data.{NNDataNeighbor, NNDataPreloaded}
import nounou.elements.traits.{NNScaling, NNTiming}
import nounou.io.FileLoader

/**
  * Created by ktakagaki on 16/03/29.
  */
class FileReadNeuroplexDa extends FileLoader {

  override val canLoadExtensions: Array[String] = Array("da")

  /**
    * '''__MUST OVERRIDE__''' Actual loading of file.
    *
    */
  override def loadImpl(file: File): Array[NNElement] = {
        val _handle: RandomAccessFile = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

    //ToDo 2: expand to general .da format
    val header = _handle.readInt16(2560)
      //the 5th header element returns the number of frames
      val frameCount = if (header(5-1) < 0) -header(5-1) * 1024 else header(5-1)
      //ToDo Get fps from header
      val sampleRate =1600d
      //ToDo Get other header values into fields
      // ToDo I think neuroplex adds footer as well.
      val scaleToMv = 0.305175781250000d
      val channelCount = 472

      val tempData: Array[DenseVector[Double]] = (
          for(det <- 0 until 472) yield DenseVector( _handle.readInt16(frameCount).map( _.toDouble * scaleToMv ) )
        ).toArray

    val timing = new NNTiming(sampleRate = sampleRate, totalLength = frameCount)
    val scaling = new NNScaling(unit = "mV")

    val tempReturn = new NNDataPreloaded(tempData, timing = timing, scalingInput = scaling) with NNDataNeighbor
    tempReturn.setLayout( new NNLayout464ii() )

    Array( tempReturn )
  }


}
