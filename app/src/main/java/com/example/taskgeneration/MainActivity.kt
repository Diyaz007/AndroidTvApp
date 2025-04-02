package com.example.studyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consumeAllChanges
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
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .imePadding(),
        horizontalAlignment = Alignment.Start
    ) {
        // Заголовок с заданиями (увеличенные размеры)
        Row(modifier = Modifier.fillMaxWidth()) {
            selectedTasks.forEachIndexed { index, (task, _) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp) // Увеличенная высота
                        .background(Color(0xFF6200EE))
                        .padding(32.dp), // Увеличенный padding
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        task,
                        fontSize = 35.sp, // Увеличенный размер шрифта
                        color = Color.White
                    )
                }
                if (index < 2) {
                    Divider(
                        modifier = Modifier
                            .height(120.dp)
                            .width(4.dp) // Более толстый разделитель
                            .background(Color.Black)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(0.dp))

        // Область для рисования (увеличенные размеры)
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(650.dp) // Увеличенная высота
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DrawingCanvas()
                }
                if (it < 2) {
                    Divider(
                        modifier = Modifier
                            .height(650.dp)
                            .width(4.dp)
                            .background(Color.Black)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(0.dp))

        // Область ввода (увеличенные размеры)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            selectedTasks.forEachIndexed { index, (_, correctAnswer) ->
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .background(Color(0xFF6200EE))
                        .padding(16.dp), // Увеличенный padding
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = userAnswers[index],
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() || it.isWhitespace() }) {
                                userAnswers = userAnswers.toMutableList().also { it[index] = newValue }
                            }
                        },
                        label = {
                            Text(
                                "Answer: x1,x2",
                                color = Color.White,
                                fontSize = 30.sp,
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF6200EE),
                            unfocusedContainerColor = Color(0xFF6200EE),
                            focusedIndicatorColor = answerColors[index],
                            unfocusedIndicatorColor = answerColors[index]
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 30.sp), // Больший размер текста
                        modifier = Modifier.height(80.dp).width(550.dp) // Высота поля ввода
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            answerColors = answerColors.toMutableList().also {
                                it[index] = if (userAnswers[index] == correctAnswer) Color.Green else Color.Red
                            }
                        },
                        modifier = Modifier
                            .height(80.dp) // Высота кнопки
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Check",
                            fontSize = 30.sp // Увеличенный размер шрифта
                        )
                    }
                }
                if (index < 2) {
                    Divider(
                        modifier = Modifier
                            .height(200.dp) // Высота разделителя
                            .width(4.dp)
                            .background(Color.Black)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Кнопка генерации (увеличенные размеры)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    selectedTasks = generateRandomTasks()
                    userAnswers = List(3) { "" }
                    answerColors = List(3) { Color.Gray}
                },
                modifier = Modifier
                    .height(100.dp) // Высота кнопки
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    "Generate new tasks",
                    fontSize = 35.sp // Увеличенный размер шрифта
                )
            }
        }
    }
}

@Composable
fun DrawingCanvas() {
    var path by remember { mutableStateOf(Path()) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var isErasing by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .weight(1f).height(640.dp)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consumeAllChanges()
                        path = Path().apply {
                            addPath(path)
                            lineTo(change.position.x, change.position.y)
                        }
                    }
                }
        ) {
            drawPath(
                path,
                color = if (isErasing) Color.White else selectedColor,
                style = Stroke(width = 10f) // Более толстая линия
            )
        }

        Column(
            modifier = Modifier
                .padding(10.dp)
                .background(Color.White)
                .padding(10.dp)
        ) {
            listOf(Color.Black, Color.Red, Color.Green).forEach { color ->
                Button(
                    onClick = {
                        selectedColor = color
                        isErasing = false
                    },
                    modifier = Modifier
                        .size(50.dp) // Увеличенные кнопки
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(color)
                    )
                }
            }

            Button(
                onClick = { isErasing = true },
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(
                    "\uD83E\uDDFD",
                    color = Color.White,
                    fontSize = 24.sp // Увеличенный размер иконки
                )
            }
        }
    }
}

fun generateRandomTasks(): List<Pair<String, String>> {
    val maxValue = 500
    val maxCoefficient = 1000
    val tasks = mutableListOf<Pair<String, String>>()

    repeat(3) {
        var a: Int
        var b: Int
        var c: Int
        var x1: Int
        var x2: Int

        do {
            a = Random.nextInt(1, 10)
            x1 = Random.nextInt(-maxValue, maxValue + 1)
            x2 = Random.nextInt(-maxValue, maxValue + 1)

            b = -a * (x1 + x2)
            c = a * x1 * x2

        } while (x1 == x2 || b !in -maxCoefficient..maxCoefficient || c !in -maxCoefficient..maxCoefficient)

        fun formatCoefficient(value: Int): String {
            return if (value < 0) "($value)" else value.toString()
        }

        val task = "Solve: ${formatCoefficient(a)}x² + ${formatCoefficient(b)}x + ${formatCoefficient(c)} = 0"
        val answer = "$x1,$x2"

        tasks.add(task to answer)
    }
    return tasks
}

@Preview(showBackground = true, device = "spec:width=3840px,height=2160px,dpi=320")
@Composable
fun PreviewStudyBoardScreen() {
    StudyBoardTheme {
        StudyBoardScreen()
    }
}