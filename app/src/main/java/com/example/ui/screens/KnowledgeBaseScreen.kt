package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KnowledgeEntity
import com.example.ui.AiViewModel
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SciFiBackground
import com.example.ui.theme.SciFiSurface
import com.example.ui.theme.SciFiSurfaceVariant
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    viewModel: AiViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.knowledgeList.collectAsState()
    val searchQuery by viewModel.knowledgeSearchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<KnowledgeEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<KnowledgeEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SciFiBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "คลังความรู้ AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SciFiSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyanPrimary,
                contentColor = Color(0xFF00363D),
                modifier = Modifier.testTag("add_knowledge_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "เพิ่มความรู้ใหม่")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setKnowledgeSearchQuery(it) },
                placeholder = { Text("ค้นหาคำถามหรือคีย์เวิร์ด...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "ค้นหา",
                        tint = CyanPrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_knowledge_field"),
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SciFiSurface,
                    unfocusedContainerColor = SciFiSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = CyanPrimary,
                    unfocusedIndicatorColor = SciFiSurfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "รายการคำถามที่ AI ตอบได้ (${items.size} หัวข้อ)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Knowledge List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    KnowledgeCard(
                        item = item,
                        onEdit = { editingItem = item },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }
    }

    // Add Knowledge Dialog
    if (showAddDialog) {
        KnowledgeEditDialog(
            title = "เพิ่มคำถามและคำตอบใหม่",
            initialQuestion = "",
            initialAnswer = "",
            initialKeywords = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { q, a, k ->
                viewModel.addKnowledge(q, a, k)
                showAddDialog = false
            }
        )
    }

    // Edit Knowledge Dialog
    editingItem?.let { item ->
        KnowledgeEditDialog(
            title = "แก้ไขคำถามและคำตอบ",
            initialQuestion = item.question,
            initialAnswer = item.answer,
            initialKeywords = item.keywords,
            onDismiss = { editingItem = null },
            onConfirm = { q, a, k ->
                viewModel.updateKnowledge(
                    item.copy(
                        question = q,
                        answer = a,
                        keywords = k
                    )
                )
                editingItem = null
            }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(text = "ยืนยันการลบ", color = TextPrimary) },
            text = { Text(text = "ต้องการลบคำถาม '${item.question}' ออกจากคลังความรู้ใช่หรือไม่?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteKnowledge(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("ลบข้อมูล")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("ยกเลิก", color = TextSecondary)
                }
            },
            containerColor = SciFiSurface
        )
    }
}

@Composable
fun KnowledgeCard(
    item: KnowledgeEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SciFiSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SciFiSurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isSystemDefault) NeonPurple.copy(alpha = 0.2f) else CyanPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (item.isSystemDefault) "ระบบตั้งต้น" else "กำหนดเอง",
                        color = if (item.isSystemDefault) NeonPurple else CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "แก้ไข",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (!item.isSystemDefault) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "ลบ",
                                tint = ErrorRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Q: ${item.question}",
                color = CyanPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "A: ${item.answer}",
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (item.keywords.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "คีย์เวิร์ด: ${item.keywords}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun KnowledgeEditDialog(
    title: String,
    initialQuestion: String,
    initialAnswer: String,
    initialKeywords: String,
    onDismiss: () -> Unit,
    onConfirm: (question: String, answer: String, keywords: String) -> Unit
) {
    var question by remember { mutableStateOf(initialQuestion) }
    var answer by remember { mutableStateOf(initialAnswer) }
    var keywords by remember { mutableStateOf(initialKeywords) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, color = CyanPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("คำถาม (เช่น ทฤษฎีรูหนอน)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_question_field"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("คำตอบของ AI") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_answer_field"),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )

                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("คีย์เวิร์ดเสริม (คั่นด้วยจุลภาค ,)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (question.isNotBlank() && answer.isNotBlank()) {
                        onConfirm(question, answer, keywords)
                    }
                },
                enabled = question.isNotBlank() && answer.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF00363D))
            ) {
                Text("บันทึก")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = TextSecondary)
            }
        },
        containerColor = SciFiSurface
    )
}
