val testString = " #ABCDEFG\n HI "
testString.padTo(20, "x")
testString.padTo(20, 100.toChar).toString

testString.take(5)
testString.take(20).toCharArray
testString.trim().toCharArray
testString.stripMargin.toCharArray

"line1\nline2\nline3".split("\n").map("sp-"+ _).mkString("\n")