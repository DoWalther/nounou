var temp = Array.tabulate(5)( _ * 2)
val temp2 = Array.tabulate(5)( (inp: Int) => inp *2 )


val func = (inp: Int) => inp *2
val temp3 = Array.tabulate(5)( func(_) )
val temp4 = Array.tabulate(5)( (inp: Int) => func(inp) )

//temp = temp.map( func(_) )

val func10 = (inp: Int) => inp *10
temp.map( func10(_) )
for( x <- temp ) yield x*10
for( x <- 0 to 5 ) yield x*10
for( x <- (0 to 8 by 2).toArray ) yield {
  //???
  x*10
}

for( x <- 0 until 5;  y <- 0 to 4 )
  yield (y+1)*x


///temp.zip