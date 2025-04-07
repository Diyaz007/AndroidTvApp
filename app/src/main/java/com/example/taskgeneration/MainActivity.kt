package com.example.studyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
            .padding(5.dp)
            .imePadding(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            selectedTasks.forEachIndexed { index, (task, _) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .background(Color(0xFF6200EE))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(task, fontSize = 16.sp, color = Color.White)
                }
                if (index < 2) {
                    Divider(modifier = Modifier.height(60.dp).width(2.dp).background(Color.Black))
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(250.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DrawingCanvas()
                }
                if (it < 2) {
                    Divider(modifier = Modifier.height(250.dp).width(2.dp).background(Color.Black))
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding() // ✅ Учитываем клавиатуру для правильного отображения
        ) {
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
                        label = { Text("Ответ", color = Color.White) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF6200EE),
                            unfocusedContainerColor = Color(0xFF6200EE),
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
                if (index < 2) {
                    Divider(modifier = Modifier.height(130.dp).width(2.dp).background(Color.Black))
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
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
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var isErasing by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .weight(1f)
            .fillMaxSize()
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
                style = Stroke(width = 5f)
            )
        }

        Column(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.White)
                .padding(5.dp)
        ) {
            listOf(Color.Black, Color.Red, Color.Green).forEach { color ->
                Button(
                    onClick = {
                        selectedColor = color
                        isErasing = false
                    },
                    modifier = Modifier
                        .size(25.dp)
                        .padding(4.dp),colors = ButtonDefaults.buttonColors(containerColor = color)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(color)
                    )
                }
            }
            Button(
                onClick = { isErasing = true },
                modifier = Modifier.size(25.dp).padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("\uD83E\uDDFD", color = Color.White)
            }
        }
    }
}
fun generateRandomTasks(): List<Pair<String, String>> {
    val maxValue = 500 // Диапазон корней от -500 до 500
    val maxCoefficient = 1000 // Ограничение на b и c
    val tasks = mutableListOf<Pair<String, String>>()

    repeat(3) {
        var a: Int
        var b: Int
        var c: Int
        var x1: Int
        var x2: Int

        do {
            a = Random.nextInt(1, 10) // Делаем коэффициент "a" небольшим
            x1 = Random.nextInt(-maxValue, maxValue + 1) // Генерируем целые корни
            x2 = Random.nextInt(-maxValue, maxValue + 1)

            // Вычисляем коэффициенты уравнения
            b = -a * (x1 + x2)
            c = a * x1 * x2

        } while (x1 == x2 || b !in -maxCoefficient..maxCoefficient || c !in -maxCoefficient..maxCoefficient)
        // Проверяем, чтобы b и c не выходили за пределы -1000 до 1000

        // Форматируем коэффициенты (негативные значения в скобки)
        fun formatCoefficient(value: Int): String {
            return if (value < 0) "($value)" else value.toString()
        }

        val task = "${formatCoefficient(a)}x² + ${formatCoefficient(b)}x + ${formatCoefficient(c)} = 0"
        val answer = "$x1, $x2" // Ответ без запятых после чисел

        tasks.add(task to answer)
    }
    return tasks
}





@Preview(showBackground = true, device = "spec:width=1920px,height=1080px,dpi=320")
@Composable
fun PreviewStudyBoardScreen() {
    StudyBoardTheme {
        StudyBoardScreen()
    }
}