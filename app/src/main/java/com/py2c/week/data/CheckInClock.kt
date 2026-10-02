package com.py2c.week.data

import java.time.LocalDate

/** Fixed \"today\" for Roborazzi and unit tests; null uses the system clock. */
object CheckInClock {
    @Volatile
    var fixedToday: LocalDate? = null

    fun today(): LocalDate = fixedToday ?: LocalDate.now()

    fun clearFixedToday() {
        fixedToday = null
    }
}
