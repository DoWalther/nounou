val valC = 5L

println( valC )
println( valC.toString )
println( "some string " + valC )

val valCBoxed = new java.lang.Long( valC )

println( valCBoxed )
println( valCBoxed.toString )

println( "some string " + valCBoxed )

//println( valCBoxed.asInstanceOf[String] )
//println( valC.asInstanceOf[String] )


val first5 = 5
val second5 = 5

first5 == second5
new Integer(first5) == new Integer(second5)