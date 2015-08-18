package nounou.io.neuralynx

import java.io.File
import java.math.BigInteger

import breeze.io.RandomAccessFile
import breeze.linalg.DenseVector
import nounou._
import nounou.elements.data.NNDataChannel
import org.scalatest.FunSuite

/**
 * @author ktakagaki
 * //@date 1/30/14.
 */
class FileAdapterNeuralynxHeaderTest extends FunSuite {


   //val testFileTet4a = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()
   val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
   //println(testFileE04LC_CSC1)

   test("file reading") {
     val handle: RandomAccessFile = new RandomAccessFile(new File(testFileE04LC_CSC1) )
     println(handle.getFilePointer)
//     handle.seek(16384)
//     println(handle.readUInt64Shifted())
//     println(handle.readUInt32())
//     println(handle.readUInt32())
//     println(handle.readUInt32())

     handle.seek(16383)
     println(handle.readUInt64Shifted())
     println(handle.readUInt32())
     println(handle.readUInt32())
     println(handle.readUInt32())

   }

   test("headerHandling") {

     val data = FileAdapterNCS.load( testFileE04LC_CSC1 ).apply(0)
     assert( data.isInstanceOf[NNDataChannelNCS] )
     val dataObj = data.asInstanceOf[NNDataChannelNCS]

     assert( dataObj.originalFileHeader.length == 760)
     assert( dataObj.headerBytes == 16384 )

     dataObj.headerAppendText="## Hello Kenta!"
     assert( dataObj.checkValidFile() )

     println( dataObj.fullHeader )

   }



 }
