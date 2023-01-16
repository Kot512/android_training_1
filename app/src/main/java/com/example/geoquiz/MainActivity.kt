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

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var resultBarTextView: TextView
    private lateinit var retryButton: Button




    private val questionBank = listOf(
        Question(R.string.question_murmansk, true),
        Question(R.string.question_president, true),
        Question(R.string.question_artem, true),
        Question(R.string.question_obama, true),
        Question(R.string.question_prison, false),
        Question(R.string.question_kris, false),
    )
    private var currentIndex = 0
    private var score = 0
    private var answeredCount = 0




    private fun updateQuestion() {
        Log.d(TAG, "${questionBank[currentIndex].isAnswered}")
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)

        if (!questionBank[currentIndex].isAnswered) {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        } else {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer
        val messageResId = if (correctAnswer == userAnswer)
            R.string.correct_toast.also { score += 1 }
            else R.string.incorrect_toast
        Toast.makeText(
            this,
            messageResId,
            Toast.LENGTH_SHORT
        ).apply {
            setGravity(Gravity.TOP, 0, 500)
            show()
        }

        trueButton.isEnabled = false
        falseButton.isEnabled = false
        questionBank[currentIndex].isAnswered = true
        answeredCount += 1

        if (answeredCount == questionBank.size) {
            showResult()
        }

        Log.d(TAG, "$score")
    }

    private fun nextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
        updateQuestion()
    }

    private fun previousQuestion() {
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
        updateQuestion()
    }

    private fun showResult() {
        val scoreText = "Your score is ${100.0 * score / questionBank.size}%"
        resultBarTextView.text = scoreText
        resultBarTextView.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }

    private fun hideResultAndRestart() {
        resultBarTextView.visibility = View.GONE
        retryButton.visibility = View.GONE
        answeredCount = 0
        score = 0
        currentIndex = 0
        questionBank.forEach { it.isAnswered = false }
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