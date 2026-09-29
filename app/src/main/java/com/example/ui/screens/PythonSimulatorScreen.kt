package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.AiViewModel
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HologramCyan
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
fun PythonSimulatorScreen(
    viewModel: AiViewModel,
    modifier: Modifier = Modifier
) {
    val simState by viewModel.pythonSimState.collectAsState()
    val defaultCode = viewModel.currentPasscode.collectAsState().value

    var testCodeInput by remember { mutableStateOf(defaultCode) }
    var testQuestionInput by remember { mutableStateOf("ทฤษฎีรูหนอนเป็นยังไง") }

    val pythonSourceCode = """
# โค้ดต้นฉบับภาษา Python (Akin AI Prototype)
A = $defaultCode  # รหัสผ่านเริ่มต้นสำหรับปลดล็อก

d = int(input("โปรดป้อนหมายเลข: "))
if d == A:
    print('ยินดีต้อนรับค่ะเจ้านาย')
    
    # คำถามทฤษฎีรูหนอน (ไม่ต้องเรียงลำดับ)
    DC = "ทฤษฎีรูหนอนเป็นยังไง"
    jo = input("?")
    if jo == DC:
        print('ทฤษฎีรูหนอน เกิดจากการที่ มีสิ่งของที่มีมวลติดลบ...')
    else:
        print('ไม่พบข้อมูล')
        
    # คำถามเด็กที่ฉลาดที่สุด
    disapong = "เด็กที่ฉลาดที่สุด"
    BRO = input("?")
    if disapong == BRO:
        print('อากิ้น')
    else:
        print('อากิ้นฉลาดที่สุด')
else:
    print('รหัสผิด')
    """.trimIndent()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SciFiBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = TerminalGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ห้องทดสอบ Python Simulator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SciFiSurface),
                actions = {
                    IconButton(
                        onClick = { viewModel.resetPythonSim() },
                        modifier = Modifier.testTag("reset_python_sim_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "รีเซ็ตเทอร์มินัล",
                            tint = CyanPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explanation Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = SciFiSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "การทำงานแบบไม่ต้องเรียงลำดับ (Non-sequential)",
                            color = NeonPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ในโค้ด Python เดิมคำสั่ง input() จะทำงานตามลำดับทีละบรรทัด แต่ในแอพ Android นี้ ระบบได้แปลงเป็น Event-Driven AI ทำให้เมื่อป้อนรหัส A ($defaultCode) ถูกต้องแล้ว เจ้านายสามารถเลือกถามคำถามใดก่อนก็ได้ หรือพิมพ์ถามซ้ำได้อย่างอิสระค่ะ!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            // Interactive Execution Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = SciFiSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ทดลองรันสคริปต์ Python",
                        color = CyanPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testCodeInput,
                        onValueChange = { testCodeInput = it },
                        label = { Text("ค่าตัวแปร d (input หมายเลข)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sim_input_code"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SciFiSurfaceVariant,
                            unfocusedContainerColor = SciFiSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testQuestionInput,
                        onValueChange = { testQuestionInput = it },
                        label = { Text("คำถาม jo / BRO") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sim_input_question"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SciFiSurfaceVariant,
                            unfocusedContainerColor = SciFiSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = {
                                testCodeInput = defaultCode
                                testQuestionInput = "ทฤษฎีรูหนอนเป็นยังไง"
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = SciFiSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "ทดสอบรูหนอน",
                                fontSize = 11.sp,
                                color = HologramCyan,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 1
                            )
                        }

                        Surface(
                            onClick = {
                                testCodeInput = defaultCode
                                testQuestionInput = "เด็กที่ฉลาดที่สุด"
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = SciFiSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "ทดสอบเด็กฉลาดสุด",
                                fontSize = 11.sp,
                                color = HologramCyan,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 1
                            )
                        }

                        Surface(
                            onClick = {
                                testCodeInput = "12345"
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = SciFiSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "ทดสอบรหัสผิด",
                                fontSize = 11.sp,
                                color = ErrorRed,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.runPythonScript(testCodeInput, testQuestionInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("run_python_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TerminalGreen,
                            contentColor = Color(0xFF003923)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "รันโค้ด")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("รันโค้ด Python (Execute)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Terminal Console Output Box
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF050811)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(ErrorRed, shape = RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFEAB308), shape = RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(modifier = Modifier.size(8.dp).background(TerminalGreen, shape = RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terminal Output",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "Python 3.12",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    simState.logs.forEach { line ->
                        val lineColor = when {
                            line.contains("ยินดีต้อนรับ") -> TerminalGreen
                            line.contains("รหัสผิด") -> ErrorRed
                            line.contains(">>>") -> CyanPrimary
                            line.contains("?:") || line.contains("โปรดป้อน") -> HologramCyan
                            else -> TextPrimary
                        }

                        Text(
                            text = line,
                            color = lineColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }

            // Python Code Display Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SciFiSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SciFiSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "โค้ดต้นฉบับ (Source Code)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF070B14), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = pythonSourceCode,
                            color = HologramCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
