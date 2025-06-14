package ru.yarsu.web.funs

import ru.yarsu.web.domain.INPAGE
import kotlin.math.ceil

fun pageAmountArticle(size: Int): Int {
    return ceil(size.toDouble() / INPAGE).toInt()
}
