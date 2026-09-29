package com.py2c.week.ui.glossary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.py2c.week.data.GlossaryTerm
import com.py2c.week.ui.strings.LocalAppLocale
import com.py2c.week.ui.strings.rememberStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(terms: List<GlossaryTerm>, onOpen: (String) -> Unit) {
    val strings = rememberStrings()
    val locale = LocalAppLocale.current
    var cat by remember(locale) { mutableStateOf(strings.glossaryAll) }
    val cats = remember(terms, strings) { listOf(strings.glossaryAll) + terms.map { it.category }.distinct() }
    val filtered = if (cat == strings.glossaryAll) terms else terms.filter { it.category == cat }
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(strings.glossaryTitle, style = MaterialTheme.typography.headlineMedium)
        Text(strings.glossaryIntro, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cats.forEach { c ->
                FilterChip(selected = cat == c, onClick = { cat = c }, label = { Text(c) })
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.id }) { term ->
                Card(onClick = { onOpen(term.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(term.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(term.termC, style = MaterialTheme.typography.titleMedium)
                        Text(term.termEn, style = MaterialTheme.typography.bodyMedium)
                        Text(term.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermScreen(term: GlossaryTerm, onBack: () -> Unit) {
    val strings = rememberStrings()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(term.termC) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(term.termEn, style = MaterialTheme.typography.titleLarge)
            Text(term.summary, style = MaterialTheme.typography.bodyLarge)
            Text(term.detail, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
