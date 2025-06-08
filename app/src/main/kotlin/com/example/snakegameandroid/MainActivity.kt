package com.example.snakegameandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.snakegameandroid.ui.theme.SnakeGameAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class State(
    val food: Offset,
    val snake: List<Offset>,
    val direction: Direction,
    val score: Int = 0,
    val isGameOver: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGameAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SnakeGame()
                }
            }
        }
    }
}

@Composable
fun SnakeGame() {
    val boardSize = 20
    val cellSize = 20.dp
    val snakeColor = Color.Green
    val foodColor = Color.Red
    val initialSnake = listOf(Offset(boardSize / 2f, boardSize / 2f))
    val initialFood = Offset(Random.nextInt(0, boardSize).toFloat(), Random.nextInt(0, boardSize).toFloat())

    val state = remember {
        MutableStateFlow(State(initialFood, initialSnake, Direction.RIGHT))
    }

    val scope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(150)
                state.update { currentState ->
                    if (currentState.isGameOver) {
                        return@update currentState
                    }

                    val newHead = when (currentState.direction) {
                        Direction.UP -> Offset(currentState.snake.first().x, (currentState.snake.first().y - 1 + boardSize) % boardSize)
                        Direction.DOWN -> Offset(currentState.snake.first().x, (currentState.snake.first().y + 1) % boardSize)
                        Direction.LEFT -> Offset((currentState.snake.first().x - 1 + boardSize) % boardSize, currentState.snake.first().y)
                        Direction.RIGHT -> Offset((currentState.snake.first().x + 1) % boardSize, currentState.snake.first().y)
                    }

                    if (currentState.snake.contains(newHead)) {
                        return@update currentState.copy(isGameOver = true)
                    }

                    val newSnake = mutableListOf(newHead) + currentState.snake.dropLast(1)
                    var newFood = currentState.food
                    var newScore = currentState.score

                    if (newHead == currentState.food) {
                        newSnake.add(currentState.snake.last())
                        newFood = Offset(Random.nextInt(0, boardSize).toFloat(), Random.nextInt(0, boardSize).toFloat())
                        newScore = currentState.score + 10
                    }

                    currentState.copy(snake = newSnake, food = newFood, score = newScore)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Score: ${state.collectAsState().value.score}")
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(cellSize * boardSize)
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            val x = offset.x
                            val y = offset.y
                            val boxWidth = size.width / 3
                            val boxHeight = size.height / 3

                            val currentState = state.value

                            if (x < boxWidth && y > boxHeight && y < 2 * boxHeight && currentState.direction != Direction.RIGHT) {
                                state.update { it.copy(direction = Direction.LEFT) }
                            } else if (x > 2 * boxWidth && y > boxHeight && y < 2 * boxHeight && currentState.direction != Direction.LEFT) {
                                state.update { it.copy(direction = Direction.RIGHT) }
                            } else if (y < boxHeight && x > boxWidth && x < 2 * boxWidth && currentState.direction != Direction.DOWN) {
                                state.update { it.copy(direction = Direction.UP) }
                            } else if (y > 2 * boxHeight && x > boxWidth && x < 2 * boxWidth && currentState.direction != Direction.UP) {
                                state.update { it.copy(direction = Direction.DOWN) }
                            }
                        }
                    )
                }
        ) {
            val currentState by state.collectAsState()

            Canvas(modifier = Modifier.fillMaxSize()) {
                currentState.snake.forEach {
                    drawRect(color = snakeColor, topLeft = Offset(it.x * cellSize.toPx(), it.y * cellSize.toPx()), size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx()))
                }
                drawRect(color = foodColor, topLeft = Offset(currentState.food.x * cellSize.toPx(), currentState.food.y * cellSize.toPx()), size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx()))
            }

            if (currentState.isGameOver) {
                Text("Game Over! Score: ${currentState.score}", color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isPlaying) {
            Button(onClick = {
                isPlaying = true
                state.update { State(initialFood, initialSnake, Direction.RIGHT) }
            }) {
                Text("Start Game")
            }
        } else if (state.collectAsState().value.isGameOver) {
            Button(onClick = {
                isPlaying = true
                state.update { State(initialFood, initialSnake, Direction.RIGHT) }
            }) {
                Text("Restart Game")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SnakeGameAndroidTheme {
        SnakeGame()
    }
}
