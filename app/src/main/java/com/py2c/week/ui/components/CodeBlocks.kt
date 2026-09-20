package com.py2c.week.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.py2c.week.ui.theme.CodeBgDark

private val Keyword = Color(0xFF569CD6)
private val StringLit = Color(0xFFCE9178)
private val Comment = Color(0xFF6A9955)
private val Preproc = Color(0xFFC586C0)
private val TextCol = Color(0xFFD4D4D4)

@Composable
fun CodePane(
    language: String,
    source: String,
    caption: String? = null,
    modifier: Modifier = Modifier,
) {
    val highlighted = remember(source, language) { highlight(source, language) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CodeBgDark,
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    language.uppercase(),
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
                if (caption != null) {
                    Text(caption, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
                }
            }
            SelectionContainer {
                Text(
                    highlighted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = TextCol,
                )
            }
        }
    }
}

@Composable
fun ExamplePane(title: String, source: String, note: String, language: String = "c") {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        CodePane(language, source)
        Text(note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun highlight(source: String, language: String): AnnotatedString {
    return when {
        language.equals("bash", true) || language.equals("sh", true) -> highlightBash(source)
        language.equals("json", true) -> highlightJson(source)
        language.equals("output", true) || language.equals("text", true) -> {
            buildAnnotatedString {
                append(source)
                addStyle(SpanStyle(color = TextCol), 0, source.length)
            }
        }
        else -> highlightC(source)
    }
}

private val bashKeywords = setOf(
    "pwd", "ls", "cd", "mkdir", "touch", "cp", "mv", "rm", "cat", "echo", "clear",
    "chmod", "export", "sudo", "apt", "dnf", "gcc", "clang", "code",
)

private fun highlightBash(source: String): AnnotatedString = buildAnnotatedString {
    append(source)
    addStyle(SpanStyle(color = TextCol), 0, source.length)
    Regex("""#.*""").findAll(source).forEach {
        addStyle(SpanStyle(color = Comment), it.range.first, it.range.last + 1)
    }
    Regex("\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'").findAll(source).forEach {
        addStyle(SpanStyle(color = StringLit), it.range.first, it.range.last + 1)
    }
    Regex("""\b[A-Za-z_]\w*\b""").findAll(source).forEach {
        if (it.value in bashKeywords) {
            addStyle(SpanStyle(color = Keyword, fontWeight = FontWeight.Medium), it.range.first, it.range.last + 1)
        }
    }
}

private fun highlightJson(source: String): AnnotatedString = buildAnnotatedString {
    append(source)
    addStyle(SpanStyle(color = TextCol), 0, source.length)
    Regex("\"(?:\\\\.|[^\"\\\\])*\"").findAll(source).forEach {
        addStyle(SpanStyle(color = StringLit), it.range.first, it.range.last + 1)
    }
}

private val cKeywords = setOf(
    "int", "char", "void", "return", "if", "else", "for", "while", "do", "struct",
    "typedef", "const", "sizeof", "NULL", "long", "double", "float", "short",
    "unsigned", "signed", "break", "continue", "switch", "case", "default",
    "include", "define", "static", "enum",
)

private fun highlightC(source: String): AnnotatedString = buildAnnotatedString {
    append(source)
    addStyle(SpanStyle(color = TextCol), 0, source.length)
    Regex("""/\*[\s\S]*?\*/|//.*""").findAll(source).forEach {
        addStyle(SpanStyle(color = Comment), it.range.first, it.range.last + 1)
    }
    Regex("""#\s*\w+""").findAll(source).forEach {
        addStyle(SpanStyle(color = Preproc), it.range.first, it.range.last + 1)
    }
    Regex("\"(?:\\\\.|[^\"\\\\])*\"").findAll(source).forEach {
        addStyle(SpanStyle(color = StringLit), it.range.first, it.range.last + 1)
    }
    Regex("""'[^']'""").findAll(source).forEach {
        addStyle(SpanStyle(color = StringLit), it.range.first, it.range.last + 1)
    }
    Regex("""\b[A-Za-z_]\w*\b""").findAll(source).forEach {
        if (it.value in cKeywords) {
            addStyle(SpanStyle(color = Keyword, fontWeight = FontWeight.Medium), it.range.first, it.range.last + 1)
        }
    }
}
