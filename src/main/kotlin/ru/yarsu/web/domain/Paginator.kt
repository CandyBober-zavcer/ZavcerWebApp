package ru.yarsu.web.domain

import org.http4k.core.Uri
import org.http4k.core.query

const val RANGE = 3

class Paginator(
    private var viewPage: Int,
    private var allPages: Int,
    private val uri: Uri,
) {
    private val nearStart = viewPage - RANGE - 2 < 0
    private val nearEnd = viewPage + RANGE + 3 > allPages

    // Возвращает ссылки на предыдущие страницы
    fun getPrevPageLinks(): Map<Int, Uri> {
        val prevPages: MutableMap<Int, Uri> = mutableMapOf()
        val start = if (!nearStart) viewPage - RANGE else 0
        for (i in start until viewPage) {
            prevPages[i] = uri.query("page", i.toString())
        }
        return prevPages
    }

    // Возвращает ссылки на следующие страницы
    fun getNextPageLinks(): Map<Int, Uri> {
        val nextPages: MutableMap<Int, Uri> = mutableMapOf()
        val end = if (!nearEnd) viewPage + RANGE + 1 else allPages
        for (i in viewPage + 1 until end) {
            nextPages[i] = uri.query("page", i.toString())
        }
        return nextPages
    }

    // Получаем номер текущей страницы
    fun getStartPage(): Int {
        return viewPage
    }

    // Получаем общее количество страниц
    fun getAllPages(): Int {
        return allPages
    }

    // Возвращает ссылку на указанную страницу
    fun getPageUri(pageNumber: Int = 0): Uri {
        val page = if (pageNumber == allPages) (pageNumber - 1) else pageNumber
        return uri.query("page", page.toString())
    }

    // Проверка, что мы близки к началу списка страниц
    fun isNearStart(): Boolean {
        return nearStart
    }

    // Проверка, что мы близки к концу списка страниц
    fun isNearEnd(): Boolean {
        return nearEnd
    }
}
