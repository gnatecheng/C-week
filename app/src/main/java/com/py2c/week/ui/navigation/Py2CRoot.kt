package com.py2c.week.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.py2c.week.data.AppContainer
import com.py2c.week.data.completedCourseDayIds
import com.py2c.week.data.learningReport
import com.py2c.week.ui.ProgressViewModel
import com.py2c.week.ui.day.DayScreen
import com.py2c.week.ui.glossary.GlossaryScreen
import com.py2c.week.ui.glossary.TermScreen
import com.py2c.week.ui.home.HomeScreen
import com.py2c.week.ui.lab.LabListScreen
import com.py2c.week.ui.lab.LabScreen
import com.py2c.week.ui.lesson.DemoScreen
import com.py2c.week.ui.lesson.LessonScreen
import com.py2c.week.ui.quiz.QuizScreen
import com.py2c.week.ui.report.ReportScreen
import com.py2c.week.ui.wrongbook.WrongBookScreen

val LocalContainer = compositionLocalOf<AppContainer> {
    error("AppContainer 未提供")
}

private data class TopTab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun Py2CRoot(container: AppContainer) {
    CompositionLocalProvider(LocalContainer provides container) {
        val nav = rememberNavController()
        val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.factory(container))
        val progress by vm.progress.collectAsState()
        val curriculum = container.curriculum
        LaunchedEffect(progress) {
            container.progressStore.syncCompletedCourseDays(progress.completedCourseDayIds(curriculum))
        }
        val back by nav.currentBackStackEntryAsState()
        val route = back?.destination?.route
        val tabs = remember {
            listOf(
                TopTab("home", "本周课程", Icons.Outlined.Home),
                TopTab("labs", "代码实验", Icons.Outlined.Terminal),
                TopTab("wrongs", "错题本", Icons.Outlined.AutoStories),
                TopTab("glossary", "词汇表", Icons.AutoMirrored.Outlined.MenuBook),
            )
        }
        val showBar = route in setOf("home", "labs", "glossary", "wrongs")
        val openWrongs = progress.wrongItems.count { !it.resolved }

        Scaffold(
            bottomBar = {
                if (showBar) {
                    NavigationBar {
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                selected = route == tab.route,
                                onClick = {
                                    nav.navigate(tab.route) {
                                        popUpTo(nav.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    if (tab.route == "wrongs" && openWrongs > 0) {
                                        BadgedBox(badge = { Badge { Text("$openWrongs") } }) {
                                            Icon(tab.icon, contentDescription = tab.label)
                                        }
                                    } else {
                                        Icon(tab.icon, contentDescription = tab.label)
                                    }
                                },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = "home",
                modifier = Modifier.padding(padding),
            ) {
                composable("home") {
                    HomeScreen(
                        curriculum = curriculum,
                        progress = progress,
                        onOpenDay = { nav.navigate("day/$it") },
                        onOpenWrongBook = { nav.navigate("wrongs") },
                        onOpenReport = { nav.navigate("report") },
                        onReset = { /* handled inside */ },
                    )
                }
                composable("labs") {
                    LabListScreen(
                        curriculum = curriculum,
                        progress = progress,
                        onOpen = { nav.navigate("lab/$it") },
                    )
                }
                composable("wrongs") {
                    WrongBookScreen(
                        curriculum = curriculum,
                        progress = progress,
                        onRedoQuiz = { dayId, qid -> nav.navigate("quiz/$dayId?only=$qid") },
                        onRedoLab = { nav.navigate("lab/$it") },
                        onOpenReport = { nav.navigate("report") },
                    )
                }
                composable("report") {
                    ReportScreen(
                        curriculum = curriculum,
                        report = progress.learningReport(curriculum),
                        onBack = { nav.popBackStack() },
                    )
                }
                composable("glossary") {
                    GlossaryScreen(
                        terms = curriculum.glossary,
                        onOpen = { nav.navigate("term/$it") },
                    )
                }
                composable(
                    "day/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val id = entry.arguments?.getInt("id") ?: 1
                    val day = curriculum.days.first { it.id == id }
                    DayScreen(
                        day = day,
                        progress = progress,
                        onBack = { nav.popBackStack() },
                        onLesson = { nav.navigate("lesson/$it") },
                        onQuiz = { nav.navigate("quiz/${day.id}?only=") },
                        onLab = { nav.navigate("lab/${day.lab.id}") },
                    )
                }
                composable(
                    "lesson/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    val day = curriculum.days.first { d -> d.lessons.any { it.id == id } }
                    val lesson = day.lessons.first { it.id == id }
                    LessonScreen(
                        day = day,
                        lesson = lesson,
                        demos = curriculum.demos,
                        videos = container.videos,
                        completed = progress.completedLessons.contains(lesson.id),
                        onBack = { nav.popBackStack() },
                        onOpenDemo = { nav.navigate("demo/$it") },
                        onMarkDone = { container.progressStore.markLesson(lesson.id) },
                    )
                }
                composable(
                    "quiz/{id}?only={only}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.IntType },
                        navArgument("only") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                    ),
                ) { entry ->
                    val id = entry.arguments?.getInt("id") ?: 1
                    val only = entry.arguments?.getString("only").orEmpty()
                    val day = curriculum.days.first { it.id == id }
                    val questions = if (only.isBlank()) day.quiz else day.quiz.filter { it.id == only }.ifEmpty { day.quiz }
                    val redo = only.isNotBlank()
                    QuizScreen(
                        day = day,
                        questions = questions,
                        alreadyDone = progress.completedQuizzes.contains(id),
                        lastScore = progress.quizScores[id],
                        redoMode = redo,
                        onBack = { nav.popBackStack() },
                        onSubmit = { score, answers ->
                            if (!redo) container.progressStore.markQuiz(id, score)
                            container.progressStore.recordQuizResults(day, answers)
                        },
                    )
                }
                composable(
                    "lab/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    val day = curriculum.days.first { it.lab.id == id }
                    LabScreen(
                        day = day,
                        lab = day.lab,
                        progress = progress,
                        store = container.progressStore,
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(
                    "demo/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    val demo = curriculum.demos.getValue(id)
                    val video = container.videos[id]
                    DemoScreen(demo = demo, video = video, onBack = { nav.popBackStack() })
                }
                composable(
                    "term/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    val term = curriculum.glossary.first { it.id == id }
                    TermScreen(term = term, onBack = { nav.popBackStack() })
                }
            }
        }
    }
}
