package com.py2c.week.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.py2c.week.data.CheckInClock
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.WeekCurriculum
import com.py2c.week.data.isFullyComplete
import com.py2c.week.data.nextOpenDay
import com.py2c.week.data.shortWeekday
import com.py2c.week.data.thisCalendarWeek
import com.py2c.week.data.todayIso
import com.py2c.week.ui.strings.LocalAppLocale
import com.py2c.week.ui.strings.rememberStrings
import java.time.LocalDate

@Composable
fun WeekCheckInCard(
    curriculum: WeekCurriculum,
    progress: ProgressSnapshot,
    onOpenDay: (Int) -> Unit,
    onCheckInToday: () -> Unit,
) {
    val strings = rememberStrings()
    val locale = LocalAppLocale.current
    val lit = progress.checkedCourseDays + curriculum.days.filter { it.isFullyComplete(progress) }.map { it.id }
    val litCount = curriculum.days.count { it.id in lit }
    val streak = progress.streak
    val todayChecked = progress.checkinDates.contains(todayIso())
    val next = progress.nextOpenDay(curriculum)
    val nudge = when {
        litCount >= 7 -> strings.calendarNudgeAllDone()
        next != null -> strings.calendarNudgeNext(next.id, next.title)
        else -> strings.calendarNudgeFinish
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(strings.calendarTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            Text(strings.calendarStreak(streak, litCount), style = MaterialTheme.typography.titleMedium)
            Text(nudge, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                curriculum.days.forEach { day ->
                    val done = day.id in lit
                    CourseDayCell(
                        dayId = day.id,
                        done = done,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenDay(day.id) },
                    )
                }
            }
            Text(strings.calendarWeekLabel, style = MaterialTheme.typography.labelLarge)
            CalendarStrip(checkinDates = progress.checkinDates, locale = locale)
            Button(
                onClick = onCheckInToday,
                enabled = !todayChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(if (todayChecked) strings.calendarCheckedInToday else strings.calendarCheckInToday)
            }
            Text(
                if (todayChecked) strings.calendarCheckInHintChecked else strings.calendarCheckInHintUnchecked,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun CourseDayCell(
    dayId: Int,
    done: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val strings = rememberStrings()
    val scheme = MaterialTheme.colorScheme
    val bg = if (done) scheme.primary else scheme.surface
    val fg = if (done) scheme.onPrimary else scheme.onSurface
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, if (done) scheme.primary else scheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp)
            .semantics {
                contentDescription = if (done) strings.calendarDayDone(dayId) else strings.calendarDayNotDone(dayId)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("D$dayId", color = fg, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(if (done) strings.calendarCellOn else strings.calendarCellOff, color = fg, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun CalendarStrip(checkinDates: Set<String>, locale: com.py2c.week.data.AppLocale) {
    val week = thisCalendarWeek()
    val today = CheckInClock.today()
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        week.forEach { date ->
            val iso = date.toString()
            val stamped = checkinDates.contains(iso)
            val isToday = date == today
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when {
                            stamped -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                            isToday -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        },
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(date.shortWeekday(locale), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                stamped -> MaterialTheme.colorScheme.primary
                                isToday -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.outlineVariant
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        date.dayOfMonth.toString(),
                        color = if (stamped || isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
