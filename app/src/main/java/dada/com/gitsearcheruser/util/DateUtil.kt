package dada.com.gitsearcheruser.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object{
         fun getDate(timeStr: String): String? {
             val time = timeStr.toLong()
            val calendar = Calendar.getInstance()
            val sdf =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentTimeZone = Date(time * 1000)
            calendar.time = currentTimeZone
            calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY)+8)
            return  sdf.format(calendar.time)
        }
    }
}