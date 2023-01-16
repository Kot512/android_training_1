package com.example.geoquiz

import androidx.annotation.StringRes

data class Question(
    @StringRes val textResId: Int,
    val answer: Boolean,
    var isAnswered: Boolean = false,
)

//аннотация @StringRes помогает встроенному инспектору кода Lint проверить во время компиляции,
//что используется именно строковой идентификатор ресурса