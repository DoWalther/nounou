import java.text.SimpleDateFormat
import java.util.Calendar

val headerText = "######## Neuralynx Data File Header\n## File Name C:\\CheetahData\\2013-09-11_17-50-10\\Tet4b.ncs\n## Time Opened (m/d/y): 9/11/2013  (h:m:s.ms) 17:50:22.466\n## Time Closed (m/d/y): 9/11/2013  (h:m:s.ms) 19:26:59.171\n-CheetahRev 5.5.1"
val pattOpenDate = ("""Opened.*([0-9]+)/([0-9]+)/([0-9]+)""").r.unanchored
pattOpenDate.findAllIn(headerText).toList
pattOpenDate.findFirstIn(headerText) match {
  case Some(pattOpenDate(m, d, y)) => (m, d, y)
  case _ => "NOT FOUND"
}
val openDate = headerText match {
  case pattOpenDate(m, d, y) => (m.toInt, d.toInt, y.toInt)
  case _ => (1900,1,1)
}
val pattOpenTime = ("""Opened.*([0-9]+):([0-9]+):([0-9]+).?""").r.unanchored
val openTime = headerText match {
  case pattOpenTime(hr, min, sec) => (hr.toInt, min.toInt, sec.toInt)
  case _ => (0,0,0)
}
val date = Calendar.getInstance()
date.set(openDate._1, openDate._2, openDate._3,
            openTime._1, openTime._2, openTime._3)
val format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
format.format(date)

