import java.text.DecimalFormat

"%6.3e".format(3.05185e-008)
"%6.3E".format(3.05185e-008)
"%8.5e".format(3.05185e-008)
("%10.5e").format(3.05185e-008)

val format = new DecimalFormat("0.#####E000;-0.#####E000")
format.format(3.05185e-008)
format.format(3.05185e-008).replace("E", "e")
3.05185e-008