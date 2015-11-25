package nounou.io.neuralynx.fileObjects

import java.io.File

import nounou.io.neuralynx.headers.NNHeaderNSE

/**
 * Created by ktakagaki on 15/05/28.
 */
class FileNSE(file: File) extends FileNeuralynx[NNHeaderNSE](file, NNHeaderNSE.factory ) {

  override final val recordSize = 112


}

