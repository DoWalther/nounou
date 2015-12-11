import nounou.io.neuralynx.fileAdapters.FileAdapterNCS
import nounou.io.neuralynx.NNDataChannelFileReadNCS

val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
//new File( "C:\\prog\\_gh\\_kt\\nounou.testfiles\\Neuralynx\\E04LC\\CSC1.ncs" )
val data = FileAdapterNCS.load( testFileE04LC_CSC1 ).apply(0)
assert( data.isInstanceOf[NNDataChannelFileReadNCS] )
val dataObj = data.asInstanceOf[NNDataChannelFileReadNCS]
dataObj.originalFileHeader.length
dataObj.headerBytes
dataObj.headerAppendText="Hello Kenta!"
dataObj.fullHeader
