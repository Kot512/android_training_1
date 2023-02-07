package com.example.geoquiz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
// private const val REQUEST_CODE_CHEAT = 0 - не требуетеся, поскольку новый метод подразумевает
// создание отдельного ActivityResultLauncher, который примет интент для определнной активити
// и будет принимать данные только от нее по выходу из активити

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var resultBarTextView: TextView
    private lateinit var apiTextView: TextView
    private lateinit var retryButton: Button
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
//        ViewModelProviders.of(this).get(QuizViewModel::class.java) - устаревшая форма записи
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val cheatResultLauncher = activityResultLauncher {
        if (it.resultCode == RESULT_OK) {
            val data = it.data
            quizViewModel.currentQuestionCheated =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    //    private val activityResultLauncher: ((ActivityResult) -> Unit) -> ActivityResultLauncher<Intent?>
//            = { someFun ->
//        registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { activityResult -> someFun(activityResult) }
//    }
    private fun activityResultLauncher(
        someFun: (ActivityResult) -> Unit
    ): ActivityResultLauncher<Intent?> {
        return registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult -> someFun(activityResult) }
    }
    //    шаблон для принятия результата из других активити (в виде функции и переменной)


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode != Activity.RESULT_OK) return
//
//        if (requestCode == REQUEST_CODE_CHEAT)
//            quizViewModel.currentQuestionCheated =
//                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
//    }
//    устаревший вместе с startActivityForResult метод


    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.judgement_toast

            correctAnswer == userAnswer ->
                R.string.correct_toast.also { quizViewModel.score++ }

            else -> R.string.incorrect_toast
        }

        Toast.makeText(
            this, messageResId, Toast.LENGTH_SHORT
        ).apply {
            setGravity(Gravity.TOP, 0, 500)
            show()
        }

        trueButton.isEnabled = false
        falseButton.isEnabled = false
        quizViewModel.currentQuestionAnswered = true
        quizViewModel.answeredCount++

        if (quizViewModel.answeredCount == quizViewModel.questionsAmount) {
            showResult()
        }

    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)

        if (!quizViewModel.currentQuestionAnswered) {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        } else {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }

    }

    private fun nextQuestion() {
        quizViewModel.indexPlus()
        updateQuestion()
        Log.d(TAG, "${quizViewModel.currentIndex}")
    }

    private fun previousQuestion() {
        quizViewModel.indexMinus()
        updateQuestion()

    }

    private fun showResult() {
        val scoreText =
            "Your score is ${100.0 * quizViewModel.score / quizViewModel.questionsAmount}%"
        resultBarTextView.text = scoreText
        resultBarTextView.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
        quizViewModel.resultShown = true

    }

    private fun hideResultAndRestart() {
        quizViewModel.resultShown = false
        resultBarTextView.visibility = View.GONE
        retryButton.visibility = View.GONE
        quizViewModel.answeredCount = 0
        quizViewModel.score = 0
        quizViewModel.currentIndex = 0
        quizViewModel.resetQuestions()
        updateQuestion()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?)")
        setContentView(R.layout.activity_main)

        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        backButton = findViewById(R.id.back_button)
        questionTextView = findViewById(R.id.question_text_view)
        retryButton = findViewById(R.id.retry_button)
        resultBarTextView = findViewById(R.id.result_bar)
        cheatButton = findViewById(R.id.cheat_button)
        apiTextView = findViewById(R.id.api_text_view)


        updateQuestion()
        if (quizViewModel.resultShown) showResult()

        val apiLevel = "API level ${Build.VERSION.SDK_INT}"
        apiTextView.text = apiLevel

        trueButton.setOnClickListener {
            checkAnswer(true)
        }
        falseButton.setOnClickListener {
            checkAnswer(false)
        }
        nextButton.setOnClickListener {
            nextQuestion()
        }
        questionTextView.setOnClickListener {
            nextQuestion()
        }
        backButton.setOnClickListener {
            previousQuestion()
        }
        retryButton.setOnClickListener {
            hideResultAndRestart()
        }
        cheatButton.setOnClickListener { buttonView ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // проверям, если версия API устройства
//                подходит для реализации кода (т.е. если API >= 23)
                val options = ActivityOptionsCompat.makeClipRevealAnimation(
                    buttonView,
                    0,
                    0,
                    buttonView.width,
                    buttonView.height,
                )
                cheatResultLauncher.launch(intent, options) // запускаем созданный ActivityResult с доп. настройками
//                (доп. настройки - ActivityOptionsCompat, поскольку ActivityOption не поддерживается методом)
            } else cheatResultLauncher.launch(intent) // запускаем без настроек
//            настраиваем запуск активити и указываем, что CheatActivity должно иметь анимацию
//            отображения из небольшой области View, указанной в параметре (в данном случае из кнопки)
//


//            startActivity(intent) просто создает новый активити, не ожидая получить данные из нее

//            startActivityForResult(intent, REQUEST_CODE_CHEAT) - устаревшая версия
//            по REQUEST_CODE_CHEAT onActivityResult поймет, что будут приниматься данные
//            именно из активити, запущенного именно с этого startActivityForResult


        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
}