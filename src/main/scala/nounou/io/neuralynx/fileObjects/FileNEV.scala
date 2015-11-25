package nounou.io.neuralynx.fileObjects

import java.io.File

import nounou.io.neuralynx.headers.{NNHeaderNEV, NNHeaderNSE}

/**
 * Created by ktakagaki on 15/05/28.
 */
class FileNEV(file: File)
  extends FileNeuralynx[NNHeaderNEV](file, NNHeaderNEV.factory) {

  override final val recordSize = 184


}

