val arr1 = Array(4,5,6,7,8,9,10,15,13,15)
val arr2 = Array(2,3,5)

arr2.flatMap( arr1.slice(0, _) )

Array(arr1, arr2, arr2).flatten