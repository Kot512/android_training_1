package com.example.geoquiz

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var resultBarTextView: TextView
    private lateinit var retryButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }




    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId =
            if (correctAnswer == userAnswer)
            R.string.correct_toast.also { quizViewModel.score++ }
            else R.string.incorrect_toast

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
        Log.d(TAG, "${quizViewModel.currentQuestionText}, ${quizViewModel.questionBank[quizViewModel.currentIndex]}")
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
        val scoreText = "Your score is ${100.0 * quizViewModel.score / quizViewModel.questionsAmount}%"
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

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        backButton = findViewById(R.id.back_button)
        questionTextView = findViewById(R.id.question_text_view)
        retryButton = findViewById(R.id.retry_button)
        resultBarTextView = findViewById(R.id.result_bar)

        updateQuestion()
        if (quizViewModel.resultShown) showResult()

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