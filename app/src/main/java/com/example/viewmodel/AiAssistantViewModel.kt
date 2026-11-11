package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AiAssistantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isStreaming: Boolean = false
)

class AiAssistantViewModel(
    private val aiRepository: AiAssistantRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Akwaaba! I am your Ghana GrantFinder AI Application Assistant. I can help you draft compelling proposals, structure project objectives, prepare grant budgets, and review required Ghanaian documentation. What would you like assistance with today?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val quickPrompts = listOf(
        "1-Page Proposal Outline",
        "Grant Budget Justification",
        "SMART Project Objectives",
        "Ghana Card & Tax Compliance",
        "Letter of Support Advice"
    )

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _isGenerating.value) return

        _errorMessage.value = null

        // Add user message and placeholder for AI streaming
        val currentList = _messages.value.toMutableList()
        currentList.add(ChatMessage(text = trimmed, isUser = true))
        currentList.add(ChatMessage(text = "", isUser = false, isStreaming = true))
        _messages.value = currentList
        _isGenerating.value = true

        viewModelScope.launch {
            val responseAccumulator = StringBuilder()
            try {
                aiRepository.streamAdvice(trimmed).collect { chunk ->
                    responseAccumulator.append(chunk)
                    val updated = _messages.value.toMutableList()
                    val lastIndex = updated.lastIndex
                    if (lastIndex >= 0) {
                        updated[lastIndex] = ChatMessage(
                            text = responseAccumulator.toString(),
                            isUser = false,
                            isStreaming = true
                        )
                        _messages.value = updated
                    }
                }

                // Finalize streaming
                val finalized = _messages.value.toMutableList()
                val lastIndex = finalized.lastIndex
                if (lastIndex >= 0) {
                    finalized[lastIndex] = ChatMessage(
                        text = responseAccumulator.toString(),
                        isUser = false,
                        isStreaming = false
                    )
                    _messages.value = finalized
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to generate AI response. Please try again."
                val errorList = _messages.value.toMutableList()
                if (errorList.isNotEmpty() && errorList.last().isStreaming) {
                    errorList[errorList.lastIndex] = ChatMessage(
                        text = "I encountered an issue connecting to the AI service. Please check your network and try again.",
                        isUser = false,
                        isStreaming = false
                    )
                    _messages.value = errorList
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                text = "Akwaaba! What grant or application would you like assistance with?",
                isUser = false
            )
        )
    }
}
