package com.example.quizapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Question(
    val emoji: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
)

enum class Screen {
    MENU,
    GAME,
    RESULT,
}

data class GameUiState(
    val screen: Screen = Screen.MENU,
    val playerName: String = "Игрок",
    val questionIndex: Int = 0,
    val questionCount: Int = 0,
    val score: Int = 0,
    val timeLeft: Int = 15,
    val currentQuestion: Question? = null,
    val selectedAnswer: String? = null,
    val isAnswered: Boolean = false,
    val showReveal: Boolean = false,
    val lastResultText: String = "",
    val isCorrect: Boolean = false,
)

class QuizViewModel : ViewModel() {
    private val allQuestions = listOf(
        Question("🧠", "2, 4, 8, 16. Келесі сан қандай?", listOf("24", "30", "32", "36"), "32"),
        Question("⚽", "Футбол командасы алаңда неше ойыншымен ойнайды?", listOf("9", "10", "11", "12"), "11"),
        Question("🎬", "«Frozen» мультфильмінде сөйлейтін аққала кейіпкер кім?", listOf("Симба", "Олаф", "Немо", "Бэмби"), "Олаф"),
        Question("🎵", "Музыкада негізгі ноталар неше?", listOf("5", "6", "7", "8"), "7"),
        Question("⚙️", "1 байтта неше бит бар?", listOf("4", "8", "10", "16"), "8"),
        Question("🧠", "1 кг мақта мен 1 кг темірдің қайсысы ауыр?", listOf("Мақта", "Темір", "Бірдей", "Айту мүмкін емес"), "Бірдей"),
        Question("⚽", "Баскетболда алаңда бір команданың неше ойыншысы болады?", listOf("4", "5", "6", "7"), "5"),
        Question("🎬", "«Том мен Джерри» мультфильмінде Том қандай жануар?", listOf("Ит", "Мысық", "Тышқан", "Қоян"), "Мысық"),
        Question("🎵", "Домбыраның неше ішегі ба��?", listOf("2", "3", "4", "6"), "2"),
        Question("⚙️", "Компьютердің «миы» қалай аталады?", listOf("Монитор", "Процессор", "Тінтуір", "Пернетақта"), "Процессор"),
    )

    private var questions: List<Question> = allQuestions.shuffled()

    var uiState by mutableStateOf(
        GameUiState(
            screen = Screen.MENU,
            playerName = "Игрок",
            questionCount = questions.size,
            timeLeft = 15,
        )
    )
        private set

    fun startGame(name: String) {
        questions = allQuestions.shuffled()
        uiState = GameUiState(
            screen = Screen.GAME,
            playerName = name.ifBlank { "Игрок" }.trim(),
            questionIndex = 0,
            questionCount = questions.size,
            score = 0,
            timeLeft = 15,
            currentQuestion = questions[0],
        )
    }

    fun answer(option: String) {
        val q = uiState.currentQuestion ?: return
        if (uiState.isAnswered || uiState.screen != Screen.GAME) return

        val isCorrect = option == q.correctAnswer
        val reward = if (isCorrect) 100 + uiState.timeLeft * 10 else 0
        val newScore = uiState.score + reward

        uiState = uiState.copy(
            selectedAnswer = option,
            isAnswered = true,
            showReveal = true,
            isCorrect = isCorrect,
            score = newScore,
            lastResultText = if (isCorrect) {
                "✅ Дұрыс! +$reward ұпай"
            } else {
                "❌ Қате. Дұрыс жауап: ${q.correctAnswer}"
            }
        )
    }

    fun onTimerTick() {
        if (uiState.screen != Screen.GAME || uiState.isAnswered || uiState.showReveal) return

        if (uiState.timeLeft <= 1) {
            val q = uiState.currentQuestion ?: return
            uiState = uiState.copy(
                isAnswered = true,
                showReveal = true,
                isCorrect = false,
                selectedAnswer = null,
                lastResultText = "⏰ Уақыт бітті! Дұрыс жауап: ${q.correctAnswer}"
            )
            return
        }

        uiState = uiState.copy(timeLeft = uiState.timeLeft - 1)
    }

    fun nextQuestion() {
        val nextIndex = uiState.questionIndex + 1
        if (nextIndex >= questions.size) {
            uiState = uiState.copy(screen = Screen.RESULT)
            return
        }

        uiState = uiState.copy(
            screen = Screen.GAME,
            questionIndex = nextIndex,
            timeLeft = 15,
            currentQuestion = questions[nextIndex],
            selectedAnswer = null,
            isAnswered = false,
            showReveal = false,
            isCorrect = false,
            lastResultText = ""
        )
    }

    fun goToMenu() {
        uiState = GameUiState(screen = Screen.MENU, playerName = uiState.playerName)
    }
}
