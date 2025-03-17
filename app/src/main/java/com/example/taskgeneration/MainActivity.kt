package com.example.studyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyboard.ui.theme.StudyBoardTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyBoardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyBoardScreen()
                }
            }
        }
    }
}

@Composable
fun StudyBoardScreen() {
    var selectedTasks by remember { mutableStateOf(generateRandomTasks()) }
    var userAnswers by remember { mutableStateOf(List(3) { "" }) }
    var answerColors by remember { mutableStateOf(List(3) { Color.Gray }) }

    Column(
        modifier = Modifier.fillMaxSize().padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            selectedTasks = generateRandomTasks()
            userAnswers = List(3) { "" }
            answerColors = List(3) { Color.Gray }
        }) {
            Text("")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            selectedTasks.forEachIndexed { index, (task, _) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .background(Color(0xFF6200EE))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(task, fontSize = 16.sp, color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DrawingCanvas()
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            selectedTasks.forEachIndexed { index, (_, correctAnswer) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF6200EE))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = userAnswers[index],
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() || it.isWhitespace() }) {
                                userAnswers = userAnswers.toMutableList().also { it[index] = newValue }
                            }
                        },
                        label = { Text("Ответ",color=Color(0xFF6200EE))},
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF6200EE),
                            unfocusedTextColor = Color(0xFF6200EE),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = answerColors[index],
                            unfocusedIndicatorColor = answerColors[index]
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Button(onClick = {
                        answerColors = answerColors.toMutableList().also {
                            it[index] = if (userAnswers[index] == correctAnswer) Color.Green else Color.Red
                        }
                    }) {
                        Text("Проверить")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                selectedTasks = generateRandomTasks()
                userAnswers = List(3) { "" }
                answerColors = List(3) { Color.Gray }
            }) {
                Text("Сгенерировать новые задания")
            }
        }
    }
}

@Composable
fun DrawingCanvas() {
    var path by remember { mutableStateOf(Path()) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consumeAllChanges()
                path = Path().apply {
                    addPath(path)
                    lineTo(change.position.x, change.position.y)
                }
            }
        }
    ) {
        drawPath(path, color = Color.Black, style = Stroke(width = 5f))
    }
}

fun generateRandomTasks(): List<Pair<String, String>> {
    val operators = listOf("+", "-", "*", "/")
    val difficulties = listOf(100 to "Легкий", 500 to "Средний", 1000 to "Сложный")
    val tasks = mutableListOf<Pair<String, String>>()

    for ((maxValue, _) in difficulties) {
        val a = Random.nextInt(1, maxValue)
        val b = Random.nextInt(1, maxValue)
        val operator = operators.random()
        val task = "Решите: $a $operator $b"
        val answer = when (operator) {
            "+" -> (a + b).toString()
            "-" -> (a - b).toString()
            "*" -> (a * b).toString()
            "/" -> if (b != 0) (a / b).toString() else "0"
            else -> "?"
        }
        tasks.add(task to answer)
    }
    return tasks
}
@Preview(showBackground = true,
    device = "spec:width=1920px,height=1080px,dpi=320"
)
@Composable
fun PreviewStudyBoardScreen() {
    StudyBoardTheme {
        StudyBoardScreen()
    }
}
