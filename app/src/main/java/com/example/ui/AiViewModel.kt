package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.KnowledgeEntity
import com.example.data.local.UserPreferences
import com.example.data.repository.AiRepository
import com.example.data.repository.QuerySource
import com.example.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PythonSimState(
    val step: Int = 0, // 0: Idle/Start, 1: Waiting Code, 2: Code Verified, 3: Asking Question
    val logs: List<String> = emptyList(),
    val isSuccess: Boolean? = null
)

class AiViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AiRepository(database.knowledgeDao())
    val userPrefs = UserPreferences(application)
    val ttsManager = TtsManager(application)

    // Security & Auth State
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _currentPasscode = MutableStateFlow(userPrefs.passcode)
    val currentPasscode: StateFlow<String> = _currentPasscode.asStateFlow()

    private val _enteredPasscode = MutableStateFlow("")
    val enteredPasscode: StateFlow<String> = _enteredPasscode.asStateFlow()

    private val _passcodeError = MutableStateFlow<String?>(null)
    val passcodeError: StateFlow<String?> = _passcodeError.asStateFlow()

    // Navigation & Screen State
    private val _activeTab = MutableStateFlow(AppTab.CHAT)
    val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

    // Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "ยินดีต้อนรับค่ะเจ้านาย พร้อมให้บริการแล้วค่ะ สามารถถามคำถามใดก็ได้โดยไม่ต้องเรียงลำดับค่ะ",
                isFromUser = false,
                source = QuerySource.EXACT_LOCAL
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Knowledge Base State
    private val _knowledgeSearchQuery = MutableStateFlow("")
    val knowledgeSearchQuery: StateFlow<String> = _knowledgeSearchQuery.asStateFlow()

    val knowledgeList: StateFlow<List<KnowledgeEntity>> = _knowledgeSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllKnowledgeFlow()
            } else {
                repository.searchKnowledge(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings
    private val _isTtsEnabled = MutableStateFlow(userPrefs.isTtsEnabled)
    val isTtsEnabled: StateFlow<Boolean> = _isTtsEnabled.asStateFlow()

    private val _matchingMode = MutableStateFlow(userPrefs.matchingMode)
    val matchingMode: StateFlow<String> = _matchingMode.asStateFlow()

    // Python Simulator State
    private val _pythonSimState = MutableStateFlow(PythonSimState(
        step = 0,
        logs = listOf(">>> Python 3.12 AI Test Script initialized", ">>> A = ${userPrefs.passcode}")
    ))
    val pythonSimState: StateFlow<PythonSimState> = _pythonSimState.asStateFlow()

    fun onPasscodeDigit(digit: String) {
        if (_enteredPasscode.value.length < 10) {
            _enteredPasscode.value += digit
            _passcodeError.value = null
        }
    }

    fun onPasscodeBackspace() {
        if (_enteredPasscode.value.isNotEmpty()) {
            _enteredPasscode.value = _enteredPasscode.value.dropLast(1)
            _passcodeError.value = null
        }
    }

    fun onPasscodeClear() {
        _enteredPasscode.value = ""
        _passcodeError.value = null
    }

    fun onPasscodeSetText(text: String) {
        _enteredPasscode.value = text
        _passcodeError.value = null
    }

    fun verifyPasscode(): Boolean {
        val target = _currentPasscode.value
        val entered = _enteredPasscode.value.trim()

        if (entered == target) {
            _isUnlocked.value = true
            _passcodeError.value = null
            if (_isTtsEnabled.value) {
                ttsManager.speak("ยินดีต้อนรับค่ะเจ้านาย")
            }
            return true
        } else {
            _passcodeError.value = "รหัสผิด"
            if (_isTtsEnabled.value) {
                ttsManager.speak("รหัสผิด")
            }
            return false
        }
    }

    fun lockApp() {
        _isUnlocked.value = false
        _enteredPasscode.value = ""
        _passcodeError.value = null
        ttsManager.stop()
    }

    fun updateMasterPasscode(newCode: String) {
        if (newCode.isNotBlank()) {
            userPrefs.passcode = newCode.trim()
            _currentPasscode.value = newCode.trim()
        }
    }

    fun resetPasscodeToDefault() {
        userPrefs.resetPasscodeToDefault()
        _currentPasscode.value = UserPreferences.DEFAULT_PASSCODE
    }

    fun setTab(tab: AppTab) {
        _activeTab.value = tab
    }

    fun toggleTts() {
        val newState = !_isTtsEnabled.value
        _isTtsEnabled.value = newState
        userPrefs.isTtsEnabled = newState
        if (!newState) {
            ttsManager.stop()
        }
    }

    fun setMatchingMode(mode: String) {
        _matchingMode.value = mode
        userPrefs.matchingMode = mode
    }

    fun setKnowledgeSearchQuery(query: String) {
        _knowledgeSearchQuery.value = query
    }

    fun askQuestion(rawQuestion: String) {
        val question = rawQuestion.trim()
        if (question.isBlank()) return

        // 1. Post user message
        val userMsg = ChatMessage(text = question, isFromUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        // 2. Query repository
        viewModelScope.launch {
            _isAiThinking.value = true
            val result = repository.answerQuestion(question, _matchingMode.value)
            _isAiThinking.value = false

            val aiMsg = ChatMessage(
                text = result.answer,
                isFromUser = false,
                source = result.source
            )
            _chatMessages.value = _chatMessages.value + aiMsg

            // Speak answer if TTS enabled
            if (_isTtsEnabled.value) {
                ttsManager.speak(result.answer)
            }
        }
    }

    fun clearChat() {
        ttsManager.stop()
        _chatMessages.value = listOf(
            ChatMessage(
                text = "ยินดีต้อนรับค่ะเจ้านาย เริ่มบทสนทนาใหม่ได้เลยค่ะ",
                isFromUser = false,
                source = QuerySource.EXACT_LOCAL
            )
        )
    }

    fun addKnowledge(question: String, answer: String, keywords: String) {
        viewModelScope.launch {
            repository.addKnowledge(question, answer, keywords)
        }
    }

    fun updateKnowledge(entity: KnowledgeEntity) {
        viewModelScope.launch {
            repository.updateKnowledge(entity)
        }
    }

    fun deleteKnowledge(id: Long) {
        viewModelScope.launch {
            repository.deleteKnowledge(id)
        }
    }

    // Python Simulator Methods
    fun runPythonScript(inputCode: String, question: String = "") {
        val codeTarget = _currentPasscode.value
        val newLogs = mutableListOf<String>()
        newLogs.add(">>> d = int(input(\"โปรดป้อนหมายเลข\"))")
        newLogs.add("โปรดป้อนหมายเลข: $inputCode")

        if (inputCode.trim() == codeTarget) {
            newLogs.add("ยินดีต้อนรับค่ะเจ้านาย")
            if (question.isNotBlank()) {
                newLogs.add(">>> jo = input(\"?\")")
                newLogs.add("?: $question")

                if (question.trim() == "ทฤษฎีรูหนอนเป็นยังไง") {
                    newLogs.add("ทฤษฎีรูหนอน เกิดจากการที่ มีสิ่งของที่มีมวลติดลบ จึงทำให้เกิดการยุบตัวของเวลา  และความโค้งของอวกาศ จึงทำให้เกิดแรงผลักอย่างมาก")
                } else if (question.trim() == "เด็กที่ฉลาดที่สุด") {
                    newLogs.add("อากิ้น")
                } else if (question.contains("อากิ้น")) {
                    newLogs.add("อากิ้นฉลาดที่สุด")
                } else {
                    newLogs.add("ไม่พบข้อมูล")
                }
            }
            _pythonSimState.value = PythonSimState(
                step = 2,
                logs = _pythonSimState.value.logs + newLogs,
                isSuccess = true
            )
        } else {
            newLogs.add("รหัสผิด")
            _pythonSimState.value = PythonSimState(
                step = 1,
                logs = _pythonSimState.value.logs + newLogs,
                isSuccess = false
            )
        }
    }

    fun resetPythonSim() {
        _pythonSimState.value = PythonSimState(
            step = 0,
            logs = listOf(
                ">>> Python 3.12 AI Test Script initialized",
                ">>> A = ${_currentPasscode.value} # กำหนดรหัสผ่านเริ่มต้น",
                ">>> พร้อมเริ่มการทดสอบ จำลอง input() และ output"
            ),
            isSuccess = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
