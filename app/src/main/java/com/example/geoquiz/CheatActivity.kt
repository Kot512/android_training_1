package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var answerIsTrue = false

    private val cheatViewModel by lazy {
        ViewModelProvider(this)[CheatViewModel::class.java]
    }

//    мы создаем новый интент в main и засовываем в него экстру с ключом в виде константы, к которой
//    также должна иметь доступ и cheat, чтобы эту экстру считать;
//    поэтому, вместо того, чтобы сделать публичную константу в main (чтобы была доступна и в cheat),
//    для реализации интента так же в main, мы создаем приватную константу для экстры сразу в cheat
//    и реализуем компанейский объект в cheat, который содержит реализацию функции, автоматически
//    создающей нужный интент и кладущей туда нужную экстру под созданной здесь же приватной константой;
//    таким образом: 1) мы не перегружаем main реализацией создания интента и просто вызываем функцию
//    через объекта-компаньона; 2) приватная константа используется только в cheat для считывания данных,
//    а потому делать ее публичной необязательно (с помощью компаньона, функция которого имеет доступ
//    к этой константе, в main забиваем экстру на эту константу (через функцию))
    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }


    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data) // функция упаковывает экстру в интент и готовит к отправке
    }

    private fun updateAnswerText() {
        val answerText =
            if (answerIsTrue) R.string.true_button
            else R.string.false_button
        answerTextView.setText(answerText)
    }

    private fun answerChecked() {
        updateAnswerText()
        setAnswerShownResult(true)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)


        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)


        if (cheatViewModel.cheatAnswerShown) answerChecked()


        showAnswerButton.setOnClickListener {
            answerChecked()
            cheatViewModel.cheatAnswerShown = true
        }


    }
}