import java.io.File
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import nounou.io.neuralynx.fileObjects.FileReadNEV
////Tests reading by hand
//val file = new File( getClass.getResource("/nounou/Neuralynx/t130911/Events.nev").getPath() )
//val fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)
//fHand.seek(0)
//val string = new String(fHand.readUInt8((16384/*file.headerBytes*/)).map(_.toChar))
//val originalHeaderText: String = {
//  println(string)
//  string.replaceAll("""(?m)[\s\x00]+$""","")
//}
//Using FileNEV
val fileNEV = new FileReadNEV(
  getClass.getResource("/nounou/Neuralynx/t130911/Events.nev").getPath()
)
fileNEV.handle.seek(fileNEV.headerBytes)
//nstx, npkt_id, npkt_data_size (2)
fileNEV.handle.readInt16(3)
fileNEV.handle.readUInt64()
//nevent_id, nttl, ncrc, ndummy1, ndummy2
fileNEV.handle.readInt16(5).map(_.toInt)
fileNEV.handle.readInt32(8)
val tempCharArray = fileNEV.handle.readUInt8(128)
new String( tempCharArray.map(_.toChar) )
//tempCharArray.map(_.toChar).map(_.toShort)

//dnExtra
//fHand.readChar(128)
//val tempstring = new String(fileNEV.handle.readUInt8(128).map(_.toChar))
//tempstring.toCharArray.slice(16, 19)
//tempstring.toCharArray.slice(18, 25).map(_.toShort)
//fileNEV.handle.readInt16(3)
//fileNEV.handle.readUInt64Shifted()
//fileNEV.handle.readInt16(5)  //nevent_id, nttl, ncrc, ndummy1, ndummy2
//fileNEV.handle.readInt32(8) //dnExtra
//fHand.readChar(128)
//new String(fileNEV.handle.readUInt8(128).filterNot( _ == 0 ).map(_.toChar))
//new String(fHand.readUInt8(128).map(_.toChar))
//21412881115L
//9223372036854775807L
//21412881115L - 9223372036854775807L -1

