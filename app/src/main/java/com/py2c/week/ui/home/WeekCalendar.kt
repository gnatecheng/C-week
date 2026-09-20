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
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.WeekCurriculum
import com.py2c.week.data.isFullyComplete
import com.py2c.week.data.nextOpenDay
import com.py2c.week.data.shortWeekdayZh
import com.py2c.week.data.thisCalendarWeek
import com.py2c.week.data.todayIso
import java.time.LocalDate

@Composable
fun WeekCheckInCard(
    curriculum: WeekCurriculum,
    progress: ProgressSnapshot,
    onOpenDay: (Int) -> Unit,
    onCheckInToday: () -> Unit,
) {
    val lit = progress.checkedCourseDays + curriculum.days.filter { it.isFullyComplete(progress) }.map { it.id }
    val litCount = curriculum.days.count { it.id in lit }
    val streak = progress.streak
    val todayChecked = progress.checkinDates.contains(todayIso())
    val next = progress.nextOpenDay(curriculum)
    val nudge = when {
        litCount >= 7 -> "七关全亮，本周毕业。可以把 Dijkstra 再在电脑 gcc 里跑一遍。"
        next != null -> "一关一天：下一关是第 ${next.id} 天「${next.title}」。点亮 7 格就毕业。"
        else -> "把剩下的实验和测验做完，整周格子就会亮满。"
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("学习日历", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            Text(
                "连续打卡 $streak 天 · 已点亮 $litCount / 7 关",
                style = MaterialTheme.typography.titleMedium,
            )
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
            Text("本周日历（按真实日期）", style = MaterialTheme.typography.labelLarge)
            CalendarStrip(checkinDates = progress.checkinDates)
            Button(
                onClick = onCheckInToday,
                enabled = !todayChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(if (todayChecked) "今日已打卡" else "今日打卡")
            }
            Text(
                if (todayChecked) "今天的格子已经盖章。做完一关课文/实验/测验也会自动记入连续天数。"
                else "点一下给今天盖章；完成任意课文、实验或测验也会自动打卡。",
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
            .semantics { contentDescription = if (done) "第${dayId}天已完成" else "第${dayId}天未完成" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("D$dayId", color = fg, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(if (done) "亮" else "关", color = fg, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun CalendarStrip(checkinDates: Set<String>) {
    val week = thisCalendarWeek()
    val today = LocalDate.now()
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
                Text(date.shortWeekdayZh(), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
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
