import java.io.File

import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}

val testFileE04LC_CSC1 =
  "C:/prog/nounou-resources/resources/nounou/Neuralynx/E04LC/CSC1.ncs"
val handle =
  new RandomAccessFile(new File(testFileE04LC_CSC1) )(
      ByteConverterLittleEndian)
val headerBytes = 16384
handle.getFilePointer
//Read header
handle.seek(0)
val headerText=
  new String(handle.readUInt8(headerBytes).map(_.toChar))
handle.getFilePointer
//Timestamp by hand
handle.seek(16384)
handle.getFilePointer
handle.readUInt8(8).toList
var timestamp = 0L
////First page
//handle.seek(16384)
//handle.getFilePointer
//timestamp = handle.readInt64() //
//First page
handle.seek(16384)
//handle.getFilePointer
timestamp = handle.readUInt64Shifted() //-6691465987522092253
BigInt(timestamp) + BigInt( 9223372036854775807L ) + 1 //2531906049332683555
handle.readUInt32()
handle.readUInt32()
handle.readUInt32()
handle.getFilePointer
//handle.readUInt32()
handle.readInt16()
handle.readInt16()
handle.readInt16()
handle.readInt16()
handle.getFilePointer

