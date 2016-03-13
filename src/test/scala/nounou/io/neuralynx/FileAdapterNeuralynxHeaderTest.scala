package nounou.io.neuralynx

import java.io.File

import breeze.io.RandomAccessFile
import nounou.io.neuralynx.fileAdapters.FileAdapterNCS
import org.scalatest.FunSuite

/**
 * @author ktakagaki
 * //@date 1/30/14.
 */
class FileAdapterNeuralynxHeaderTest extends FunSuite {


   //val testFileTet4a = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()
   val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
   //println(testFileE04LC_CSC1)

//   test("manual test of file reading") {
//     val handle: RandomAccessFile = new RandomAccessFile(new File(testFileE04LC_CSC1) )
//     //println(handle.getFilePointer)
////     handle.seek(16384)
////     println(handle.readUInt64Shifted())
////     println(handle.readUInt32())
////     println(handle.readUInt32())
////     println(handle.readUInt32())
//
////     handle.seek(16383)
////     println(handle.readUInt64Shifted())
////     println(handle.readUInt32())
////     println(handle.readUInt32())
////     println(handle.readUInt32())
//
//   }

   test("headerHandling") {

     val data = FileAdapterNCS.load( testFileE04LC_CSC1 ).apply(0)
     assert( data.isInstanceOf[NNChannelFileReadNCS] )
     val dataObj = data.asInstanceOf[NNChannelFileReadNCS]

     //assert( dataObj.header.originalFileHeader.length == 760)
     assert( dataObj.headerBytes == 16384 )

     //dataObj.header.headerAppendText="## Hello Kenta!"
     //assert( dataObj.isValidFile() )

     //println( dataObj.fullHeader )

   }


//  ######## Neuralynx Data File Header
//  ## File Name C:\CheetahData\2013-12-02_17-07-31\Events.nev
//  ## Time Opened (m/d/y): 12/2/2013  (h:m:s.ms) 17:7:31.938
//  ## Time Closed (m/d/y): 12/2/2013  (h:m:s.ms) 18:33:52.919
//  -CheetahRev 5.5.1
//
//  -FileType Event
//    -RecordSize 184


//  ######## Neuralynx Data File Header
//  ## File Name C:\CheetahData\2013-12-02_17-07-31\STet3a.nse
//  ## Time Opened (m/d/y): 12/2/2013  (h:m:s.ms) 17:7:45.587
//  ## Time Closed (m/d/y): 12/2/2013  (h:m:s.ms) 18:33:52.911
//  -CheetahRev 5.5.1
//
//  -AcqEntName STet3a
//    -FileType Spike
//    -RecordSize 112
//
//  -HardwareSubSystemName AcqSystem1
//    -HardwareSubSystemType DigitalLynxSX
//    -SamplingFrequency 32000
//  -ADMaxValue 32767
//  -ADBitVolts 3.05185e-009
//
//  -NumADChannels 1
//  -ADChannel 8
//  -InputRange 100
//  -InputInverted False
//    -DSPLowCutFilterEnabled True
//    -DspLowCutFrequency 600
//  -DspLowCutNumTaps 64
//  -DspLowCutFilterType FIR
//    -DSPHighCutFilterEnabled True
//    -DspHighCutFrequency 6000
//  -DspHighCutNumTaps 32
//  -DspHighCutFilterType FIR
//    -DspDelayCompensation Enabled
//    -DspFilterDelay_µs 1468
//  -DisabledSubChannels
//  -WaveformLength 32
//  -AlignmentPt 8
//  -ThreshVal -23
//  -MinRetriggerSamples 24
//  -SpikeRetriggerTime 750
//  -DualThresholding False
//
//  -Feature Peak 0 0
//  -Feature Valley 1 0
//  -Feature Energy 2 0
//  -Feature Height 3 0
//  -Feature NthSample 4 0 4
//  -Feature NthSample 5 0 16
//  -Feature NthSample 6 0 24
//  -Feature NthSample 7 0 28


//  ######## Neuralynx Data File Header
//  ## File Name C:\CheetahData\2013-12-02_17-07-31\Tet3a.ncs
//  ## Time Opened (m/d/y): 12/2/2013  (h:m:s.ms) 17:7:45.617
//  ## Time Closed (m/d/y): 12/2/2013  (h:m:s.ms) 18:33:52.914
//  -CheetahRev 5.5.1
//
//  -AcqEntName Tet3a
//    -FileType CSC
//    -RecordSize 1044
//
//  -HardwareSubSystemName AcqSystem1
//    -HardwareSubSystemType DigitalLynxSX
//    -SamplingFrequency 32000
//  -ADMaxValue 32767
//  -ADBitVolts 3.05185e-008
//
//  -NumADChannels 1
//  -ADChannel 8
//  -InputRange 1000
//  -InputInverted False
//    -DSPLowCutFilterEnabled True
//    -DspLowCutFrequency 1
//  -DspLowCutNumTaps 0
//  -DspLowCutFilterType DCO
//    -DSPHighCutFilterEnabled True
//    -DspHighCutFrequency 9000
//  -DspHighCutNumTaps 32
//  -DspHighCutFilterType FIR
//    -DspDelayCompensation Enabled
//    -DspFilterDelay_µs 484


}
