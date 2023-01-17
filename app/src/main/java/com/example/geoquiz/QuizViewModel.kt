package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel: ViewModel() {

    val questionBank = listOf(
        Question(R.string.question_murmansk, true),
        Question(R.string.question_president, true),
        Question(R.string.question_artem, true),
        Question(R.string.question_obama, true),
        Question(R.string.question_prison, false),
        Question(R.string.question_kris, false),
    )

    val questionsAmount = questionBank.size

    var currentIndex = 0

    var score = 0

    var answeredCount = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var currentQuestionAnswered: Boolean = false
        get() = questionBank[currentIndex].isAnswered
        set(value: Boolean) {
            field = value
            questionBank[currentIndex].isAnswered = value
        }

    var resultShown = false



    fun indexPlus() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun indexMinus() {
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
    }

    fun resetQuestions() {
        questionBank.forEach { it.isAnswered = false }
    }
}