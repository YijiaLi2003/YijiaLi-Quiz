package com.example.yijiali_quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardsQuizTheme {
                FlashcardsQuizApp()
            }
        }
    }
}

data class Flashcard(
    val question: String,
    val answer: String
)

@Composable
fun FlashcardsQuizApp() {
    val flashcards = listOf(
        Flashcard("What is 5 + 3?", "8"),
        Flashcard("What is 7 - 2?", "5"),
        Flashcard("What is 9 x 3?", "27"),
        Flashcard("What is 12 / 4?", "3")
    )

    // State variables
    // I manage to get current question indirectly through currentQuestionIndex.
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isQuizComplete by remember { mutableStateOf(false) }
    var attemptCount by remember { mutableStateOf(0) } // New state variable
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isQuizComplete) {
                Text(
                    text = "Quiz Completed!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    currentQuestionIndex = 0
                    isQuizComplete = false
                    userAnswer = ""
                    attemptCount = 0
                }) {
                    Text("Restart Quiz")
                }
            } else {
                val currentFlashcard = flashcards[currentQuestionIndex]
                QuestionCard(question = currentFlashcard.question)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Submit Button
                Button(onClick = {
                    val correctAnswer = currentFlashcard.answer.trim()
                    if (userAnswer.trim() == correctAnswer) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Correct!")
                        }
                        if (currentQuestionIndex < flashcards.size - 1) {
                            currentQuestionIndex++
                            userAnswer = ""
                            attemptCount = 0
                        } else {
                            isQuizComplete = true
                        }
                    } else {
                        attemptCount++
                        if (attemptCount < 3) {
                            val remainingAttempts = 3 - attemptCount
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Incorrect. You have $remainingAttempts more attempt(s)."
                                )
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Incorrect. Moving to next question."
                                )
                            }
                            if (currentQuestionIndex < flashcards.size - 1) {
                                currentQuestionIndex++
                                userAnswer = ""
                                attemptCount = 0 // Reset attempt count
                            } else {
                                isQuizComplete = true
                            }
                        }
                    }
                }) {
                    Text("Submit Answer")
                }
            }
        }
    }
}

@Composable
fun QuestionCard(question: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun FlashcardsQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}