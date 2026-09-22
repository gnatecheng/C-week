package com.py2c.week.data

import com.py2c.week.data.curriculum.buildCurriculum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LabGraderTest {
    private val curriculum = buildCurriculum()
    private fun lab(dayId: Int) = curriculum.days.first { it.id == dayId }.lab

    @Test
    fun solutionsPassAllCases() {
        curriculum.days.forEach { day ->
            val eval = gradeLab(day.lab.solutionCode, day.lab)
            assertTrue("day ${day.id} solution should pass, got ${eval.failedHints}", eval.passed)
            assertEquals(100, eval.scorePercent)
            assertTrue(eval.totalCases >= 2)
            assertEquals(eval.totalCases, eval.passedCases)
        }
    }

    @Test
    fun day2WrongDivisionGivesPartialCredit() {
        val lab = lab(2)
        val code = lab.solutionCode.replace("c * 9 / 5 + 32", "c / 5 * 9 + 32")
        val eval = gradeLab(code, lab)
        assertFalse(eval.passed)
        assertTrue("expected partial cases, got ${eval.passedCases}/${eval.totalCases} ${eval.caseOutcomes}", eval.passedCases in 1 until eval.totalCases)
        assertTrue(eval.scorePercent in 1..99)
        // 100 is divisible by 5 so both orders print 212; 37 is the distinguishing case.
        val body = eval.caseOutcomes.first { it.name == "体温" }
        assertEquals("95", body.actual)
        assertFalse(body.passed)
        val one = eval.caseOutcomes.first { it.name == "一摄氏" }
        assertEquals("32", one.actual)
        assertFalse(one.passed)
        assertTrue(eval.gradeHints.any { it.kind == HintKind.FORMULA })
    }

    @Test
    fun day3StubFailsPrimeCases() {
        val eval = gradeLab(lab(3).starterCode, lab(3))
        assertFalse(eval.passed)
        assertTrue(eval.totalCases > eval.passedCases)
        assertTrue(eval.gradeHints.any { it.kind == HintKind.TODO || it.kind == HintKind.STUB })
    }

    @Test
    fun day7BfsDoesNotPassMainCase() {
        val lab = lab(7)
        val code = lab.solutionCode
            .replace("dist[v] > dist[u] + w[e]", "dist[v] > dist[u] + 1")
            .replace("dist[v] = dist[u] + w[e]", "dist[v] = dist[u] + 1")
        val eval = gradeLab(code, lab)
        assertFalse(eval.passed)
        val main = eval.caseOutcomes.first { it.name == "主测" }
        assertFalse(main.passed)
        assertEquals("0 1 1 2", main.actual?.trim())
        assertTrue(eval.gradeHints.any { it.kind == HintKind.BFS_VS_DIJKSTRA })
    }

    @Test
    fun stdoutMatchIgnoresExtraSpaces() {
        assertTrue(stdoutMatches("0 2 1 3 \n", "0 2 1 3"))
        assertFalse(stdoutMatches("0 1 1 2", "0 2 1 3"))
    }
}

class WrongBookTest {
    @Test
    fun roundTrip() {
        val item = WrongItem(
            source = WrongSource.QUIZ,
            dayId = 2,
            questionId = "d2-q1",
            prompt = "scanf 漏了 &？",
            userAnswer = "自动补上地址",
            correctAnswer = "把 n 的值当作指针写入",
            hintCategory = "指针",
            resolved = false,
            updatedAtMs = 42L,
        )
        val parsed = parseWrongItem(item.serialize())
        assertEquals(item, parsed)
    }

    @Test
    fun groupByDay() {
        val items = listOf(
            WrongItem(WrongSource.LAB, 3, "lab-d3", "is_prime", "No", "Yes", "空壳"),
            WrongItem(WrongSource.QUIZ, 1, "d1-q1", "gcc?", "cat", "gcc hello.c -o hello", "编译命令"),
        )
        val grouped = items.groupedByDay()
        assertEquals(listOf(1, 3), grouped.keys.toList())
    }
}
